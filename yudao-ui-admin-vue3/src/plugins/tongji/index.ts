import router from '@/router'

// 用于 router push
window._hmt = window._hmt || []
// HM_ID
const HM_ID = import.meta.env.VITE_APP_BAIDU_CODE
;(function () {
  // 有值的时候，才开启
  if (!HM_ID) {
    return
  }
  // 开发环境不加载，避免本地无法访问外网时的报错
  if (import.meta.env.DEV) {
    return
  }
  const hm = document.createElement('script')
  hm.src = 'https://hm.baidu.com/hm.js?' + HM_ID
  hm.onerror = () => {
    // 静默忽略加载失败（网络不通等情况）
  }
  const s = document.getElementsByTagName('script')[0]
  s.parentNode?.insertBefore(hm, s)
})()

router.afterEach(function (to) {
  if (!HM_ID) {
    return
  }
  if (import.meta.env.DEV) {
    return
  }
  _hmt.push(['_trackPageview', to.fullPath])
})
