<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="门店编码" prop="storeCode">
        <el-input v-model="formData.storeCode" placeholder="请输入门店编码（租户内唯一）" />
      </el-form-item>
      <el-form-item label="门店名称" prop="storeName">
        <el-input v-model="formData.storeName" placeholder="请输入门店名称" />
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-input v-model="formData.platform" placeholder="请输入平台" />
      </el-form-item>
      <el-form-item label="城市" prop="city">
        <el-input v-model="formData.city" placeholder="请输入城市" />
      </el-form-item>
      <el-form-item label="负责人" prop="manager">
        <el-input v-model="formData.manager" placeholder="请输入负责人" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入联系电话" />
      </el-form-item>
      <el-form-item label="营业状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择营业状态">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.STORE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { StoreApi, Store } from '@/api/store/store'

/** 门店 表单 */
defineOptions({ name: 'StoreForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  storeCode: undefined,
  storeName: undefined,
  platform: undefined,
  city: undefined,
  manager: undefined,
  phone: undefined,
  status: undefined
})
const formRules = reactive({
  storeCode: [{ required: true, message: '门店编码不能为空', trigger: 'blur' }],
  storeName: [{ required: true, message: '门店名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '营业状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await StoreApi.getStore(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as Store
    if (formType.value === 'create') {
      await StoreApi.createStore(data)
      message.success(t('common.createSuccess'))
    } else {
      await StoreApi.updateStore(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    storeCode: undefined,
    storeName: undefined,
    platform: undefined,
    city: undefined,
    manager: undefined,
    phone: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>