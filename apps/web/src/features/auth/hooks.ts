import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ApiError } from '../../shared/api/client'
import { authApi, type LoginInput, type SignupInput, type User } from './api'

const ME_KEY = ['auth', 'me'] as const

/** 현재 로그인 사용자. 비로그인이면 null이다. */
export function useMe() {
  return useQuery({
    queryKey: ME_KEY,
    queryFn: async (): Promise<User | null> => {
      try {
        return await authApi.me()
      } catch (error) {
        if (error instanceof ApiError && error.status === 401) return null
        throw error
      }
    },
    staleTime: 60_000,
  })
}

export function useLogin() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (input: LoginInput) => authApi.login(input),
    onSuccess: (user) => queryClient.setQueryData(ME_KEY, user),
  })
}

export function useTestLogin() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (loginId: string) => authApi.testLogin(loginId),
    onSuccess: (user) => queryClient.setQueryData(ME_KEY, user),
  })
}

/** 회원가입 후 같은 정보로 바로 로그인한다. */
export function useSignup() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (input: SignupInput) => {
      await authApi.signup(input)
      return authApi.login({ loginId: input.loginId, password: input.password })
    },
    onSuccess: (user) => queryClient.setQueryData(ME_KEY, user),
  })
}

export function useLogout() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      queryClient.setQueryData(ME_KEY, null)
      return queryClient.invalidateQueries()
    },
  })
}

export function useTestAccounts() {
  return useQuery({
    queryKey: ['auth', 'test-accounts'],
    queryFn: () => authApi.testAccounts(),
    staleTime: Infinity,
  })
}
