/**
 * 로그인 후 이동할 경로를 고른다.
 * 외부 사이트로 보내는 공격(open redirect)을 막기 위해 같은 사이트 안의 경로만 허용한다.
 */
export function safeRedirect(value: string | null): string {
  if (!value || !value.startsWith('/') || value.startsWith('//')) return '/'
  return value
}
