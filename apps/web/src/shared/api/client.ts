/** 서버가 공통 에러 형식({ error: { code, message } })으로 응답한 실패 */
export class ApiError extends Error {
  readonly status: number
  readonly code: string

  constructor(status: number, code: string, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
  }
}

type Method = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

interface RequestOptions {
  method?: Method
  body?: unknown
}

const CSRF_COOKIE = 'XSRF-TOKEN'
const CSRF_HEADER = 'X-XSRF-TOKEN'

function readCookie(name: string): string | undefined {
  return document.cookie
    .split('; ')
    .find((row) => row.startsWith(`${name}=`))
    ?.slice(name.length + 1)
}

/**
 * CSRF 토큰 쿠키가 없으면 서버에 가벼운 GET 요청을 보내 발급받는다.
 * 로그인·로그아웃 직후에는 서버가 토큰을 새로 발급하므로 쿠키가 비어 있을 수 있다.
 */
async function ensureCsrfToken(): Promise<string | undefined> {
  const token = readCookie(CSRF_COOKIE)
  if (token) return decodeURIComponent(token)
  await fetch('/api/health', { credentials: 'same-origin' })
  const issued = readCookie(CSRF_COOKIE)
  return issued ? decodeURIComponent(issued) : undefined
}

/** 백엔드 API를 호출한다. 실패하면 {@link ApiError}를 던진다. */
export async function api<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const method = options.method ?? 'GET'
  const headers: Record<string, string> = { Accept: 'application/json' }

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }
  if (method !== 'GET') {
    const token = await ensureCsrfToken()
    if (token) headers[CSRF_HEADER] = token
  }

  const response = await fetch(path, {
    method,
    headers,
    credentials: 'same-origin',
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  })

  if (response.status === 204) {
    return undefined as T
  }

  const data: unknown = await response.json().catch(() => null)
  if (!response.ok) {
    const error = (data as { error?: { code?: string; message?: string } } | null)?.error
    throw new ApiError(
      response.status,
      error?.code ?? 'UNKNOWN',
      error?.message ?? '요청을 처리하지 못했습니다.',
    )
  }
  return data as T
}
