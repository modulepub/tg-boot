<template>
	<el-card class="workstation-guide">
		<template #header>
			<div class="card-header">
				<span>工作台</span>
				<el-tag type="info" effect="plain">开发者说明</el-tag>
			</div>
		</template>

		<el-alert
			type="warning"
			show-icon
			:closable="false"
			title="工作台属于业务域能力，不应在本运维后台（tg-vue）内持续迭代；请单独新建业务项目承载面向运营/红娘/企业的日常工作台。"
			class="mb-16"
		/>

		<el-descriptions :column="1" border title="平台定位">
			<el-descriptions-item label="当前系统">
				卿卿运维后台（tg-boot / tg-vue）——面向技术人员与系统管理员的配置、监控与数据运维入口。
			</el-descriptions-item>
			<el-descriptions-item label="工作台定位">
				面向业务角色（如红娘、企业管理员、客服等）的日常作业界面，强调任务处理、流程协同与业务数据呈现。
			</el-descriptions-item>
		</el-descriptions>

		<el-divider />

		<h3 class="section-title">为何业务工作台要独立项目？</h3>
		<ul class="guide-list">
			<li>
				<strong>职责分离</strong>：运维后台聚焦系统配置、权限、字典、对接参数等「技术运维」能力；工作台聚焦「业务操作」体验，两者用户、发布节奏与权限模型不同，混在一起会增加耦合与回归成本。
			</li>
			<li>
				<strong>独立演进</strong>：业务工作台通常需要更频繁的 UI 迭代、移动端/H5 适配、角色化首页与埋点实验；独立仓库/项目可按业务线版本发布，不必与后台发版绑定。
			</li>
			<li>
				<strong>安全边界</strong>：运维后台权限高、接口面广；业务工作台应遵循最小权限，仅暴露业务 API。物理/工程隔离可降低误操作与越权风险。
			</li>
			<li>
				<strong>团队协作</strong>：后台由平台/后端同学维护，工作台由业务前端或垂直团队负责；拆分后代码归属清晰，Code Review 与 CI 策略可各自优化。
			</li>
		</ul>

		<h3 class="section-title">推荐做法</h3>
		<el-descriptions :column="1" border class="mb-16">
			<el-descriptions-item label="新建项目">
				在 monorepo 或独立仓库中创建业务工作台前端（本仓库已有示例：<code>matchmaker-web</code> 中的红娘/企业工作台页面，供 H5 场景参考）。
			</el-descriptions-item>
			<el-descriptions-item label="接口复用">
				通过 tg-boot 已提供的用户端 / 业务 API（如 <code>cus</code> 前缀接口）获取数据，勿在运维后台新增面向 C 端/业务端的页面逻辑。
			</el-descriptions-item>
			<el-descriptions-item label="菜单占位">
				本页仅作架构说明与入口占位；左侧「联络任务」「服务工作台」等子菜单为历史 CRM 能力，与独立业务工作台项目并行存在，后续可按需迁移或下线。
			</el-descriptions-item>
		</el-descriptions>

		<el-alert type="success" show-icon :closable="false">
			<template #title>
				若你正在开发新的业务工作台，请从独立前端项目接入后端 API，并在部署层配置独立域名或路径（如 H5 / 小程序 WebView），无需修改 tg-vue 运维后台。
			</template>
		</el-alert>
	</el-card>
</template>

<script setup lang="ts" name="WorkstationGuideIndex">
</script>

<style lang="scss" scoped>
.workstation-guide {
	width: 100%;
	box-sizing: border-box;
}

.card-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
}

.mb-16 {
	margin-bottom: 16px;
}

.section-title {
	margin: 8px 0 12px;
	font-size: 15px;
	font-weight: 600;
	color: var(--el-text-color-primary);
}

.guide-list {
	margin: 0 0 20px;
	padding-left: 20px;
	line-height: 1.75;
	color: var(--el-text-color-regular);

	li + li {
		margin-top: 10px;
	}

	strong {
		color: var(--el-text-color-primary);
	}
}

code {
	padding: 2px 6px;
	font-size: 12px;
	background: var(--el-fill-color-light);
	border-radius: 4px;
}
</style>
