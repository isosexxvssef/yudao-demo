import request from '@/config/axios'

/** 门店信息 */
export interface Store {
  id?: number // 主键
  storeCode?: string // 门店编码，租户内唯一
  storeName?: string // 门店名称
  platform?: string // 平台
  city?: string // 城市
  manager?: string // 负责人
  phone?: string // 联系电话
  status?: string // 营业状态（营业/停业），字典：store_status
  createTime?: string // 创建时间（后端返回）
}

// 门店 API
export const StoreApi = {
  // 查询门店分页
  getStorePage: async (params: any) => {
    return await request.get({ url: `/store/store/page`, params })
  },

  // 查询门店详情
  getStore: async (id: number) => {
    return await request.get({ url: `/store/store/get?id=` + id })
  },

  // 新增门店
  createStore: async (data: Store) => {
    return await request.post({ url: `/store/store/create`, data })
  },

  // 修改门店
  updateStore: async (data: Store) => {
    return await request.put({ url: `/store/store/update`, data })
  },

  // 删除门店
  deleteStore: async (id: number) => {
    return await request.delete({ url: `/store/store/delete?id=` + id })
  },

  /** 批量删除门店 */
  deleteStoreList: async (ids: number[]) => {
    return await request.delete({ url: `/store/store/delete-list?ids=${ids.join(',')}` })
  },

  // 导出门店 Excel
  exportStore: async (params) => {
    return await request.download({ url: `/store/store/export-excel`, params })
  },

  // 下载门店导入模板
  importTemplate: async () => {
    return await request.download({ url: `/store/store/get-import-template` })
  }
}

/** 门店清洗日志 */
export interface StoreCleanLog {
  id?: number
  startTime?: string
  endTime?: string
  scanCount?: number
  modifyCount?: number
  status?: number // 0=成功 1=失败
  errorMsg?: string
  triggerType?: number // 1=定时任务 2=手动触发
  createTime?: string
}

// 门店清洗 API
export const StoreCleanApi = {
  // 手动触发门店数据清洗
  triggerClean: async (triggerType: number = 2) => {
    return await request.post({ url: `/store/clean/trigger?triggerType=${triggerType}` })
  },

  // 查询清洗日志分页
  getCleanLogPage: async (params: any) => {
    return await request.get({ url: `/store/clean/log/page`, params })
  }
}
