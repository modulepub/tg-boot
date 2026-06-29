<template>
	<div class="log-intro-page">
		<el-card shadow="never">
			<template #header>
				<div class="card-title">系统日志说明（V3.5.2+）</div>
			</template>

			<div class="section">
				<div class="section-title">业务说明</div>
				<p>
					自 V3.5.2 起，后台系统日志能力由“列表式记录”升级为“描述式日志追踪”。日志不再依赖业务表查询，
					支持按实际场景灵活检索。
				</p>
				<p>可通过日志文件直接查询，也可接入 ELK 或市面上各类日志分析工具进行多维度检索、聚合与排查。</p>
			</div>

			<el-divider />

			<div class="section">
				<div class="section-title">核心优点</div>
				<p>日志写入与检索链路从业务数据库解耦，避免日志查询与业务读写互相影响。</p>
				<p>不占用业务数据库性能，降低核心交易表与业务接口的负载压力，提升系统整体稳定性。</p>
			</div>

			<el-divider />

			<div class="section">
				<div class="section-title">技术说明</div>
				<p>
					系统采用标准化日志风格输出，结合 MDC（Mapped Diagnostic Context）记录结构化上下文信息。
				</p>
				<p>日志不再落入业务表，而是通过 logback.xml 配置输出通道，可按需选择日志文件、ELK 或其他日志分析工具作为存储与检索目标。</p>
				<p>日志工具类已迁入 common 模块，统一接入方式，降低业务模块维护成本。</p>
			</div>

			<el-divider />

			<div class="section">
				<div class="section-title">使用示例</div>
				<el-alert type="success" :closable="false" show-icon>
					<template #default>
						<div class="code-block">
							EasyLog.record("账号密码登录", "登录成功", "用户：" + loginVO.getUsername(), userDTO.getUserCode());
						</div>
					</template>
				</el-alert>
				<p class="tip">该工具会自动记录请求追踪链路 ID、客户端 IP 及其他 MDC 上下文信息，便于在日志文件或任意日志分析平台中快速检索与关联追踪。</p>
			</div>
		</el-card>
	</div>
</template>

<script setup lang="ts" name="${ModuleName}logIndex"></script>

<style scoped lang="scss">
.log-intro-page {
	.section + .section {
		margin-top: 8px;
	}

	.card-title {
		font-size: 18px;
		font-weight: 600;
		color: var(--el-text-color-primary);
	}

	.section-title {
		margin-bottom: 10px;
		font-size: 16px;
		font-weight: 600;
		color: var(--el-text-color-primary);
	}

	p {
		margin: 8px 0;
		line-height: 1.8;
		color: var(--el-text-color-regular);
	}

	.code-block {
		padding: 12px;
		overflow-x: auto;
		font-family: 'Consolas', 'Courier New', monospace;
		font-size: 13px;
		line-height: 1.6;
		background: var(--el-fill-color-light);
		border-radius: 6px;
	}

	.tip {
		margin-top: 12px;
		color: var(--el-text-color-secondary);
	}
}
</style>
