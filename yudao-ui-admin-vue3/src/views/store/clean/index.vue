<template>
  <ContentWrap>
    <!-- 操作栏 -->
    <el-form class="-mb-15px" :inline="true" label-width="68px">
      <el-form-item>
        <el-button
          type="primary"
          plain
          :loading="cleanLoading"
          @click="handleClean"
          v-hasPermi="['store:clean:trigger']"
        >
          <Icon icon="ep:refresh" class="mr-5px" /> 手动触发清洗
        </el-button>
      </el-form-item>
      <el-form-item label="执行状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable class="!w-200px">
          <el-option label="成功" :value="0" />
          <el-option label="失败" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="触发方式" prop="triggerType">
        <el-select v-model="queryParams.triggerType" placeholder="请选择" clearable class="!w-200px">
          <el-option label="定时任务" :value="1" />
          <el-option label="手动触发" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="开始时间" align="center" prop="startTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="结束时间" align="center" prop="endTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="扫描数量" align="center" prop="scanCount" width="100" />
      <el-table-column label="修改数量" align="center" prop="modifyCount" width="100" />
      <el-table-column label="执行状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="触发方式" align="center" prop="triggerType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.triggerType === 1" type="info">定时任务</el-tag>
          <el-tag v-else-if="scope.row.triggerType === 2" type="warning">手动触发</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误信息" align="center" prop="errorMsg" min-width="200" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { StoreCleanApi, StoreCleanLog } from '@/api/store/store'

/** 门店清洗日志列表 */
defineOptions({ name: 'StoreCleanLog' })

const message = useMessage()

const loading = ref(true)
const list = ref<StoreCleanLog[]>([])
const total = ref(0)
const cleanLoading = ref(false)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: undefined,
  triggerType: undefined,
  createTime: []
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await StoreCleanApi.getCleanLogPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置 */
const resetQuery = () => {
  queryParams.status = undefined
  queryParams.triggerType = undefined
  queryParams.createTime = []
  handleQuery()
}

/** 手动触发清洗 */
const handleClean = async () => {
  try {
    await message.confirm('确认要手动触发门店数据清洗吗？')
    cleanLoading.value = true
    const data = await StoreCleanApi.triggerClean(2)
    if (data.status === 0) {
      message.success(`清洗成功：扫描 ${data.scanCount} 条，修改 ${data.modifyCount} 条`)
    } else {
      message.error(`清洗失败：${data.errorMsg || '未知错误'}`)
    }
    await getList()
  } finally {
    cleanLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
