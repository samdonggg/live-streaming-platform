import type { ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router'
import { useMe } from './hooks'

/** 비로그인 사용자를 로그인 화면으로 보내고, 로그인 후 원래 화면으로 돌아오게 한다. */
export function RequireAuth({ children }: { children: ReactNode }) {
  const { data: user, isPending } = useMe()
  const location = useLocation()

  if (isPending) return <p className="muted">불러오는 중…</p>
  if (!user) {
    const redirect = encodeURIComponent(location.pathname + location.search)
    return <Navigate to={`/login?redirect=${redirect}`} replace />
  }
  return children
}
