import type { ICustomerInfoRes } from '@/api/types/login'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCurrCusInfo } from '@/api/login'

const customerInfoState: ICustomerInfoRes = {}

export const useCustomerStore = defineStore(
  'customer',
  () => {
    const customerInfo = ref<ICustomerInfoRes>({ ...customerInfoState })

    const setCustomerInfo = (val: ICustomerInfoRes | Record<string, any>) => {
      customerInfo.value = { ...(val || {}) }
    }

    const clearCustomerInfo = () => {
      customerInfo.value = { ...customerInfoState }
      uni.removeStorageSync('customer')
    }

    const fetchCustomerInfo = async () => {
      const res = await getCurrCusInfo()
      setCustomerInfo(res)
      return res
    }

    return {
      customerInfo,
      setCustomerInfo,
      clearCustomerInfo,
      fetchCustomerInfo,
    }
  },
  {
    persist: true,
  },
)
