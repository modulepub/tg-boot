<template>
	<el-card>
		<el-row :gutter="20" justify="space-between">
			<el-col :span="15">
				<div style="display: flex; flex-direction: row">
					<el-avatar :size="60" :src="userStore.user.userAvatar" />
					<div style="display: flex; flex-direction: column; margin-left: 20px; line-height: 28px">
						<h1>早安，{{ userStore.user.userRealName }}，祝你开心每一天!</h1>
						<div>今日晴，25℃ - 28℃！</div>
					</div>
				</div>
			</el-col>
			<el-col :span="3">
				<el-statistic title="用户量" :value="outputValue" />
			</el-col>
			<el-col :span="3">
				<el-statistic title="项目数" :value="66" />
			</el-col>
			<el-col :span="3">
				<el-statistic :value="8">
					<template #title>
						<div style="display: inline-flex; align-items: center">待办</div>
					</template>
					<template #suffix>/100</template>
				</el-statistic>
			</el-col>
		</el-row>
	</el-card>
	<br />

	<el-space fill wrap :fill-ratio="28" style="width: 100%">
		<el-card style="width: 100px">
			<template #header>
				<div class="card-header">
					<span>关注公众号，加入微信交流群</span>
				</div>
			</template>
			<div style="height: 220px; display: flex; justify-content: center">
				<el-image src="https://TG.net/app/img/qrcode.jpg" />
			</div>
		</el-card>
		<el-card style="width: 600px">
			<template #header>
				<div class="card-header">
					<span>销售统计</span>
				</div>
			</template>
			<div style="height: 220px; display: flex; justify-content: center">
				<div ref="main" style="width: 100%; height: 220px"></div>
			</div>
		</el-card>
	</el-space>

	<br />
	<br />
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'
import { useTransition } from '@vueuse/core'
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'

const userStore = useUserStore()

const source = ref(0)
const outputValue = useTransition(source, {
	duration: 1500
})
source.value = 888888

const active = ref('first')

onMounted(() => {
	init()
})

const main = ref()

const init = () => {
	let option
	const myChart = echarts.init(main.value)
	const series = [
		{
			data: [120, 200, 150, 80, 70, 110, 130, 150, 80, 70, 110, 130],
			type: 'bar',
			stack: 'a',
			name: 'a'
		},
		{
			data: [10, 46, 64, '-', 0, '-', 0, 150, 80, 70, 110, 130],
			type: 'bar',
			stack: 'a',
			name: 'b'
		},
		{
			data: [30, '-', 0, 20, 10, '-', 0, 150, 80, 70, 110, 130],
			type: 'bar',
			stack: 'a',
			name: 'c'
		},
		{
			data: [30, '-', 0, 20, 10, '-', 0, 150, 80, 70, 110, 130],
			type: 'bar',
			stack: 'b',
			name: 'd'
		},
		{
			data: [10, 20, 150, 0, '-', 50, 10, 150, 80, 70, 110, 130],
			type: 'bar',
			stack: 'b',
			name: 'e'
		}
	]
	const stackInfo = {} as any
	for (let i = 0; i < series[0].data.length; ++i) {
		for (let j = 0; j < series.length; ++j) {
			const stackName = series[j].stack
			if (!stackName) {
				continue
			}
			if (!stackInfo[stackName]) {
				stackInfo[stackName] = {
					stackStart: [],
					stackEnd: []
				}
			}
			const info = stackInfo[stackName]
			const data = series[j].data[i]
			if (data && data !== '-') {
				if (info.stackStart[i] == null) {
					info.stackStart[i] = j
				}
				info.stackEnd[i] = j
			}
		}
	}
	for (let i = 0; i < series.length; ++i) {
		const data = series[i].data as any
		const info = stackInfo[series[i].stack]
		for (let j = 0; j < series[i].data.length; ++j) {
			const isEnd = info.stackEnd[j] === i
			const topBorder = isEnd ? 20 : 0
			const bottomBorder = 0
			data[j] = {
				value: data[j],
				itemStyle: {
					borderRadius: [topBorder, topBorder, bottomBorder, bottomBorder]
				}
			}
		}
	}
	option = {
		xAxis: {
			type: 'category',
			data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
		},
		yAxis: {
			type: 'value'
		},
		series: series
	}

	option && myChart.setOption(option)
	myChart.resize()
}
</script>
