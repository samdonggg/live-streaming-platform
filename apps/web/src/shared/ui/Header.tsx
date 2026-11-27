import { Link, useNavigate } from 'react-router'
import { useLogout, useMe } from '../../features/auth/hooks'

export function Header() {
  const { data: user } = useMe()
  const logout = useLogout()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout.mutate(undefined, { onSuccess: () => navigate('/') })
  }

  return (
    <header className="header">
      <Link to="/" className="logo">
        라이브 방송
      </Link>
      <nav className="nav">
        {user ? (
          <>
            <Link to="/broadcasts/new">방송하기</Link>
            <span className="muted">{user.nickname}님</span>
            <button type="button" onClick={handleLogout} disabled={logout.isPending}>
              로그아웃
            </button>
          </>
        ) : (
          <>
            <Link to="/login">로그인</Link>
            <Link to="/signup">회원가입</Link>
          </>
        )}
      </nav>
    </header>
  )
}
