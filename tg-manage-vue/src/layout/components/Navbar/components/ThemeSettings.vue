<template>
	<div>
		<tg-icon icon="icon-ellipsis-v" @click="themeSettingsHandle"></tg-icon>
	</div>
</template>

<script setup lang="ts">
import emits from '@/utils/emits'
const themeSettingsHandle = () => {
	emits.emit('openThemeSettings')
}
</script>
