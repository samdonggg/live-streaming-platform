import { useMe } from '../features/auth/hooks'

/** 로그인한 사용자만 들어올 수 있는 방송 만들기 화면. 방송 관리 기능에서 채운다. */
export function NewBroadcastPage() {
  const { data: user } = useMe()
  return (
    <section>
      <h1>방송하기</h1>
      <p className="muted">{user?.nickname}님, 방송 만들기는 방송 관리 기능에서 추가합니다.</p>
    </section>
  )
}
