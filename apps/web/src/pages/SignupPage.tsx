import { type FormEvent, useState } from 'react'
import { Link, Navigate, useNavigate, useSearchParams } from 'react-router'
import { useMe, useSignup } from '../features/auth/hooks'
import { safeRedirect } from '../features/auth/redirect'
import { ErrorMessage } from '../shared/ui/ErrorMessage'

export function SignupPage() {
  const [searchParams] = useSearchParams()
  const redirect = safeRedirect(searchParams.get('redirect'))
  const navigate = useNavigate()

  const { data: user } = useMe()
  const signup = useSignup()

  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [nickname, setNickname] = useState('')

  if (user && !signup.isSuccess) return <Navigate to={redirect} replace />

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    signup.mutate(
      { loginId, password, nickname },
      { onSuccess: () => navigate(redirect, { replace: true }) },
    )
  }

  return (
    <section className="card">
      <h1>회원가입</h1>
      <form onSubmit={handleSubmit} className="form">
        <label>
          아이디
          <input
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
            pattern="[a-z0-9]{4,20}"
            title="영문 소문자와 숫자 4~20자"
            autoComplete="username"
            required
          />
          <small className="muted">영문 소문자와 숫자 4~20자</small>
        </label>
        <label>
          비밀번호
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            minLength={8}
            maxLength={64}
            autoComplete="new-password"
            required
          />
          <small className="muted">8~64자</small>
        </label>
        <label>
          닉네임
          <input
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            minLength={2}
            maxLength={20}
            required
          />
          <small className="muted">2~20자</small>
        </label>
        <ErrorMessage error={signup.error} />
        <button type="submit" className="primary" disabled={signup.isPending}>
          가입하기
        </button>
      </form>
      <p className="muted">
        이미 계정이 있나요?{' '}
        <Link to={`/login?redirect=${encodeURIComponent(redirect)}`}>로그인</Link>
      </p>
    </section>
  )
}
