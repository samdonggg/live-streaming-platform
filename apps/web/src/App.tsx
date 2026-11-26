import { useEffect, useState } from 'react'

type HealthState = 'loading' | 'UP' | 'DOWN'

function App() {
  const [health, setHealth] = useState<HealthState>('loading')

  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((body: { status?: string }) => setHealth(body.status === 'UP' ? 'UP' : 'DOWN'))
      .catch(() => setHealth('DOWN'))
  }, [])

  return (
    <main>
      <h1>라이브 방송 플랫폼</h1>
      <p>백엔드 상태: {health}</p>
    </main>
  )
}

export default App
