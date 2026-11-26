import { ApiError } from '../api/client'

export function ErrorMessage({ error }: { error: unknown }) {
  if (!error) return null
  const message = error instanceof ApiError ? error.message : '요청을 처리하지 못했습니다.'
  return (
    <p className="error" role="alert">
      {message}
    </p>
  )
}
