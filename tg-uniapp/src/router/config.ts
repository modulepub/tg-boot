import { getAllPages } from '@/utils'

const definePageNeedLoginPathList = getAllPages('needLogin').map(page => page.path)

export const NEED_LOGIN_PATH_LIST = [
  '/pages/settings/index',
  '/pages/me/recommend-list',
  '/pages/me/liked-me-list',
  '/pages/me/my-like-list',
  ...definePageNeedLoginPathList,
]

export function isNeedLoginPath(path: string) {
  const purePath = path.split('?')[0]
  return new Set(NEED_LOGIN_PATH_LIST).has(purePath)
}
