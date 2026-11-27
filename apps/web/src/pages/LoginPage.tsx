import { type FormEvent, useState } from 'react'
import { Link, Navigate, useNavigate, useSearchParams } from 'react-router'
import { useLogin, useMe, useTestAccounts, useTestLogin } from '../features/auth/hooks'
import { safeRedirect } from '../features/auth/redirect'
import { ErrorMessage } from '../shared/ui/ErrorMessage'

export function LoginPage() {
  const [searchParams] = useSearchParams()
  const redirect = safeRedirect(searchParams.get('redirect'))
  const navigate = useNavigate()

  const { data: user } = useMe()
  const login = useLogin()
  const testLogin = useTestLogin()
  const { data: testAccounts = [] } = useTestAccounts()

  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')

  if (user) return <Navigate to={redirect} replace />

  const goBack = () => navigate(redirect, { replace: true })

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    login.mutate({ loginId, password }, { onSuccess: goBack })
  }

  return (
    <section className="card">
      <h1>로그인</h1>
      <form onSubmit={handleSubmit} className="form">
        <label>
          아이디
          <input
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
            autoComplete="username"
            required
          />
        </label>
        <label>
          비밀번호
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>
        <ErrorMessage error={login.error} />
        <button type="submit" className="primary" disabled={login.isPending}>
          로그인
        </button>
      </form>

      {testAccounts.length > 0 && (
        <div className="test-accounts">
          <p className="muted">테스트 계정으로 바로 로그인</p>
          {testAccounts.map((account) => (
            <button
              key={account.loginId}
              type="button"
              onClick={() => testLogin.mutate(account.loginId, { onSuccess: goBack })}
              disabled={testLogin.isPending}
            >
              {account.nickname} ({account.loginId})
            </button>
          ))}
          <ErrorMessage error={testLogin.error} />
        </div>
      )}

      <p className="muted">
        계정이 없나요? <Link to={`/signup?redirect=${encodeURIComponent(redirect)}`}>회원가입</Link>
      </p>
    </section>
  )
}
