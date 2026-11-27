import { api } from '../../shared/api/client'

export interface User {
  id: number
  loginId: string
  nickname: string
}

export interface TestAccount {
  loginId: string
  nickname: string
}

export interface SignupInput {
  loginId: string
  password: string
  nickname: string
}

export interface LoginInput {
  loginId: string
  password: string
}

export const authApi = {
  me: () => api<User>('/api/auth/me'),
  signup: (input: SignupInput) => api<User>('/api/auth/signup', { method: 'POST', body: input }),
  login: (input: LoginInput) => api<User>('/api/auth/login', { method: 'POST', body: input }),
  logout: () => api<void>('/api/auth/logout', { method: 'POST' }),
  testAccounts: () => api<TestAccount[]>('/api/auth/test-accounts'),
  testLogin: (loginId: string) =>
    api<User>('/api/auth/test-login', { method: 'POST', body: { loginId } }),
}
