import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { IJumpOptions } from '@/utils/jump.types'

export interface IPendingJump {
  pathOrUrl: string
  title: string
  options?: IJumpOptions
}

export const useLoginModalStore = defineStore('loginModal', () => {
  const visible = ref(false)
  const pendingJump = ref<IPendingJump | null>(null)

  function open(pending: IPendingJump | null = null) {
    pendingJump.value = pending
    visible.value = true
  }

  function close() {
    visible.value = false
    pendingJump.value = null
  }

  return { visible, pendingJump, open, close }
})
