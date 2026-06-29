<template>
	<div class="login-container">
		<!-- 科技风背景 -->
		<div class="tech-bg">
			<canvas ref="particleCanvas" class="particle-canvas"></canvas>
			<div class="grid-overlay"></div>
			<div class="glow-orb glow-orb-1"></div>
			<div class="glow-orb glow-orb-2"></div>
			<div class="glow-orb glow-orb-3"></div>
			<div class="scan-line"></div>
		</div>

		<!-- 左侧品牌区 -->
		<div class="login-brand">
			<div class="brand-content">
				<div class="brand-logo">
					<div class="logo-icon">
						<div class="logo-glow"></div>
						<svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
							<path d="M24 4L6 14v20l18 10 18-10V14L24 4z" stroke="currentColor" stroke-width="2" fill="none" />
							<path d="M24 4v20m0 0l18-10m-18 10L6 14m18 30V24" stroke="currentColor" stroke-width="2" />
							<circle cx="24" cy="24" r="6" fill="currentColor" opacity="0.5" />
						</svg>
					</div>
					<h1 class="brand-title">{{ $t('app.title') }}</h1>
				</div>
				<p class="brand-slogan">成功源于坚持不懈的努力和开放合作的态度！</p>
				<div class="brand-desc">
					{{ $t('app.description') }}
				</div>
				<div class="brand-features">
					<div class="feature-item">
						<div class="feature-icon">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
							</svg>
						</div>
						<span>拥有梦想</span>
					</div>
					<div class="feature-item">
						<div class="feature-icon">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<circle cx="12" cy="12" r="10" />
								<polyline points="12 6 12 12 16 14" />
							</svg>
						</div>
						<span>明确目标</span>
					</div>
					<div class="feature-item">
						<div class="feature-icon">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
								<line x1="3" y1="9" x2="21" y2="9" />
								<line x1="9" y1="21" x2="9" y2="9" />
							</svg>
						</div>
						<span>乐在其中</span>
					</div>
					<div class="feature-item">
						<div class="feature-icon">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<rect x="4" y="4" width="16" height="16" rx="2" />
								<rect x="9" y="9" width="6" height="6" />
								<line x1="9" y1="1" x2="9" y2="4" />
								<line x1="15" y1="1" x2="15" y2="4" />
								<line x1="9" y1="20" x2="9" y2="23" />
								<line x1="15" y1="20" x2="15" y2="23" />
								<line x1="20" y1="9" x2="23" y2="9" />
								<line x1="20" y1="14" x2="23" y2="14" />
								<line x1="1" y1="9" x2="4" y2="9" />
								<line x1="1" y1="14" x2="4" y2="14" />
							</svg>
						</div>
						<span>信念达成</span>
					</div>
				</div>
			</div>
		</div>

		<!-- 右侧登录区 -->
		<div class="login-panel">
			<div class="login-card">
				<div class="card-corner card-corner-tl"></div>
				<div class="card-corner card-corner-tr"></div>
				<div class="card-corner card-corner-bl"></div>
				<div class="card-corner card-corner-br"></div>

				<div class="card-header">
					<div class="card-badge">
						<span class="badge-dot"></span>
						SECURE ACCESS
					</div>
					<h2 class="card-title">欢迎使用</h2>
					<p class="card-subtitle">请登录您的账户继续使用</p>
				</div>

				<div class="login-form-wrapper">
					<account />
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import Account from './account.vue'
import { useRoute } from 'vue-router'
import cache from '@/utils/cache'

const particleCanvas = ref<HTMLCanvasElement | null>(null)
let animationId = 0

interface Particle {
	x: number
	y: number
	vx: number
	vy: number
	radius: number
}

const route = useRoute()
watch(
	() => route,
	() => {
		const redirect = route?.query?.redirect as string
		if (redirect && redirect !== '/') {
			cache.setRedirect(redirect)
		} else {
			cache.setRedirect('')
		}
	},
	{ immediate: true }
)

const initParticles = () => {
	const canvas = particleCanvas.value
	if (!canvas) return

	const ctx = canvas.getContext('2d')
	if (!ctx) return

	const resize = () => {
		canvas.width = window.innerWidth
		canvas.height = window.innerHeight
	}

	resize()

	const count = Math.min(80, Math.floor((canvas.width * canvas.height) / 18000))
	const particles: Particle[] = Array.from({ length: count }, () => ({
		x: Math.random() * canvas.width,
		y: Math.random() * canvas.height,
		vx: (Math.random() - 0.5) * 0.4,
		vy: (Math.random() - 0.5) * 0.4,
		radius: Math.random() * 1.5 + 0.5
	}))

	const maxDist = 140

	const draw = () => {
		ctx.clearRect(0, 0, canvas.width, canvas.height)

		for (const p of particles) {
			p.x += p.vx
			p.y += p.vy
			if (p.x < 0 || p.x > canvas.width) p.vx *= -1
			if (p.y < 0 || p.y > canvas.height) p.vy *= -1

			ctx.beginPath()
			ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
			ctx.fillStyle = 'rgba(0, 212, 255, 0.6)'
			ctx.fill()
		}

		for (let i = 0; i < particles.length; i++) {
			for (let j = i + 1; j < particles.length; j++) {
				const dx = particles[i].x - particles[j].x
				const dy = particles[i].y - particles[j].y
				const dist = Math.sqrt(dx * dx + dy * dy)
				if (dist < maxDist) {
					const alpha = (1 - dist / maxDist) * 0.25
					ctx.beginPath()
					ctx.moveTo(particles[i].x, particles[i].y)
					ctx.lineTo(particles[j].x, particles[j].y)
					ctx.strokeStyle = `rgba(0, 180, 255, ${alpha})`
					ctx.lineWidth = 0.5
					ctx.stroke()
				}
			}
		}

		animationId = requestAnimationFrame(draw)
	}

	draw()
	window.addEventListener('resize', resize)

	return () => {
		cancelAnimationFrame(animationId)
		window.removeEventListener('resize', resize)
	}
}

let cleanup: (() => void) | undefined

onMounted(() => {
	cleanup = initParticles()
})

onUnmounted(() => {
	cleanup?.()
})
</script>

<style lang="scss" scoped>
$tech-cyan: #00d4ff;
$tech-blue: #0066ff;
$tech-dark: #0a0e1a;
$tech-card-bg: rgba(10, 20, 40, 0.65);

.login-container {
	display: flex;
	min-height: 100vh;
	background: $tech-dark;
	position: relative;
	overflow: hidden;
}

// 科技风背景
.tech-bg {
	position: absolute;
	inset: 0;
	pointer-events: none;

	.particle-canvas {
		position: absolute;
		inset: 0;
		width: 100%;
		height: 100%;
	}

	.grid-overlay {
		position: absolute;
		inset: 0;
		background-image:
			linear-gradient(rgba(0, 180, 255, 0.04) 1px, transparent 1px),
			linear-gradient(90deg, rgba(0, 180, 255, 0.04) 1px, transparent 1px);
		background-size: 60px 60px;
		mask-image: radial-gradient(ellipse at center, black 30%, transparent 80%);
	}

	.glow-orb {
		position: absolute;
		border-radius: 50%;
		filter: blur(80px);
		opacity: 0.35;
		animation: float 8s ease-in-out infinite;

		&.glow-orb-1 {
			width: 500px;
			height: 500px;
			background: $tech-blue;
			top: -150px;
			left: -100px;
		}

		&.glow-orb-2 {
			width: 400px;
			height: 400px;
			background: #7b2fff;
			bottom: -100px;
			left: 25%;
			animation-delay: -3s;
		}

		&.glow-orb-3 {
			width: 300px;
			height: 300px;
			background: $tech-cyan;
			top: 30%;
			right: 10%;
			animation-delay: -5s;
			opacity: 0.2;
		}
	}

	.scan-line {
		position: absolute;
		inset: 0;
		background: linear-gradient(
			180deg,
			transparent 0%,
			rgba(0, 212, 255, 0.03) 50%,
			transparent 100%
		);
		background-size: 100% 4px;
		animation: scan 6s linear infinite;
	}
}

@keyframes float {
	0%,
	100% {
		transform: translateY(0) scale(1);
	}
	50% {
		transform: translateY(-20px) scale(1.05);
	}
}

@keyframes scan {
	0% {
		background-position: 0 -100%;
	}
	100% {
		background-position: 0 200%;
	}
}

// 左侧品牌区
.login-brand {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	padding: 60px;
	color: #fff;
	position: relative;
	z-index: 1;

	.brand-content {
		max-width: 500px;
	}

	.brand-logo {
		display: flex;
		align-items: center;
		gap: 16px;
		margin-bottom: 16px;

		.logo-icon {
			width: 60px;
			height: 60px;
			background: rgba(0, 180, 255, 0.1);
			border-radius: 16px;
			display: flex;
			align-items: center;
			justify-content: center;
			backdrop-filter: blur(10px);
			border: 1px solid rgba(0, 212, 255, 0.3);
			position: relative;
			box-shadow: 0 0 20px rgba(0, 212, 255, 0.2);

			.logo-glow {
				position: absolute;
				inset: -2px;
				border-radius: 18px;
				background: linear-gradient(135deg, $tech-cyan, $tech-blue);
				opacity: 0.3;
				filter: blur(8px);
				z-index: -1;
			}

			svg {
				width: 34px;
				height: 34px;
				color: $tech-cyan;
				filter: drop-shadow(0 0 6px rgba(0, 212, 255, 0.6));
			}
		}
	}

	.brand-title {
		font-size: 36px;
		font-weight: 700;
		letter-spacing: 2px;
		margin: 0;
		background: linear-gradient(135deg, #fff 0%, $tech-cyan 100%);
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
		background-clip: text;
		font-family:
			'SF Pro Display',
			-apple-system,
			BlinkMacSystemFont,
			sans-serif;
	}

	.brand-slogan {
		font-size: 16px;
		color: rgba(255, 255, 255, 0.75);
		margin-bottom: 24px;
		font-weight: 300;
		letter-spacing: 3px;
	}

	.brand-desc {
		font-size: 14px;
		line-height: 1.8;
		color: rgba(255, 255, 255, 0.6);
		margin-bottom: 40px;
		padding: 20px 24px;
		background: rgba(0, 180, 255, 0.05);
		border-radius: 12px;
		backdrop-filter: blur(10px);
		border: 1px solid rgba(0, 212, 255, 0.15);
		border-left: 3px solid $tech-cyan;
	}

	.brand-features {
		display: flex;
		gap: 28px;

		.feature-item {
			display: flex;
			align-items: center;
			gap: 8px;
			font-size: 13px;
			color: rgba(255, 255, 255, 0.7);

			.feature-icon {
				width: 36px;
				height: 36px;
				background: rgba(0, 180, 255, 0.08);
				border-radius: 10px;
				display: flex;
				align-items: center;
				justify-content: center;
				border: 1px solid rgba(0, 212, 255, 0.15);
				transition: all 0.3s;

				svg {
					width: 16px;
					height: 16px;
					color: $tech-cyan;
				}
			}

			&:hover .feature-icon {
				background: rgba(0, 180, 255, 0.15);
				box-shadow: 0 0 12px rgba(0, 212, 255, 0.2);
			}
		}
	}
}

// 右侧登录区
.login-panel {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40px;
	position: relative;
	z-index: 1;
}

.login-card {
	width: 440px;
	background: $tech-card-bg;
	border-radius: 20px;
	padding: 48px 40px;
	backdrop-filter: blur(24px);
	border: 1px solid rgba(0, 212, 255, 0.2);
	box-shadow:
		0 0 40px rgba(0, 100, 255, 0.15),
		0 25px 50px rgba(0, 0, 0, 0.4),
		inset 0 1px 0 rgba(255, 255, 255, 0.05);
	position: relative;
	overflow: hidden;

	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 1px;
		background: linear-gradient(90deg, transparent, $tech-cyan, transparent);
		opacity: 0.6;
	}

	.card-corner {
		position: absolute;
		width: 20px;
		height: 20px;
		border-color: $tech-cyan;
		border-style: solid;
		opacity: 0.6;

		&.card-corner-tl {
			top: 12px;
			left: 12px;
			border-width: 2px 0 0 2px;
		}

		&.card-corner-tr {
			top: 12px;
			right: 12px;
			border-width: 2px 2px 0 0;
		}

		&.card-corner-bl {
			bottom: 12px;
			left: 12px;
			border-width: 0 0 2px 2px;
		}

		&.card-corner-br {
			bottom: 12px;
			right: 12px;
			border-width: 0 2px 2px 0;
		}
	}

	.card-header {
		text-align: center;
		margin-bottom: 36px;

		.card-badge {
			display: inline-flex;
			align-items: center;
			gap: 6px;
			font-size: 11px;
			letter-spacing: 2px;
			color: $tech-cyan;
			margin-bottom: 16px;
			padding: 4px 12px;
			border: 1px solid rgba(0, 212, 255, 0.25);
			border-radius: 20px;
			background: rgba(0, 180, 255, 0.08);

			.badge-dot {
				width: 6px;
				height: 6px;
				border-radius: 50%;
				background: $tech-cyan;
				box-shadow: 0 0 6px $tech-cyan;
				animation: pulse 2s ease-in-out infinite;
			}
		}

		.card-title {
			font-size: 28px;
			font-weight: 600;
			color: #fff;
			margin: 0 0 8px 0;
			font-family:
				'SF Pro Display',
				-apple-system,
				BlinkMacSystemFont,
				sans-serif;
		}

		.card-subtitle {
			font-size: 14px;
			color: rgba(255, 255, 255, 0.45);
			margin: 0;
		}
	}

	.login-form-wrapper {
		min-height: 240px;
	}

	:deep(.el-form-item) {
		margin-bottom: 20px;
	}

	:deep(.el-input) {
		height: 50px;

		.el-input__wrapper {
			padding: 0 16px;
			border-radius: 8px;
			background: rgba(0, 20, 50, 0.5);
			box-shadow: none;
			border: 1px solid rgba(0, 180, 255, 0.15);
			transition: all 0.3s;

			&:hover {
				border-color: rgba(0, 212, 255, 0.35);
				background: rgba(0, 30, 60, 0.6);
			}

			&.is-focus {
				background: rgba(0, 30, 70, 0.7);
				border-color: $tech-cyan;
				box-shadow: 0 0 12px rgba(0, 212, 255, 0.2);
			}
		}

		.el-input__inner {
			height: 50px;
			font-size: 15px;
			color: #e8f4ff;

			&::placeholder {
				color: rgba(255, 255, 255, 0.3);
			}
		}

		.el-input__prefix {
			color: rgba(0, 212, 255, 0.6);
		}

		.el-input__suffix {
			color: rgba(0, 212, 255, 0.5);
		}
	}

	:deep(.el-button--primary) {
		width: 100%;
		height: 50px;
		font-size: 16px;
		font-weight: 600;
		letter-spacing: 3px;
		border-radius: 8px;
		background: linear-gradient(135deg, $tech-blue 0%, #0099cc 100%);
		border: 1px solid rgba(0, 212, 255, 0.3);
		box-shadow: 0 4px 20px rgba(0, 100, 255, 0.35);
		transition: all 0.3s;

		&:hover {
			background: linear-gradient(135deg, #0077ee 0%, $tech-cyan 100%);
			box-shadow: 0 6px 28px rgba(0, 180, 255, 0.45);
			transform: translateY(-1px);
		}

		&:active {
			transform: translateY(0);
		}
	}
}

@keyframes pulse {
	0%,
	100% {
		opacity: 1;
	}
	50% {
		opacity: 0.4;
	}
}

// 响应式
@media only screen and (max-width: 1200px) {
	.login-brand {
		padding: 40px;

		.brand-title {
			font-size: 28px;
		}
	}
}

@media only screen and (max-width: 992px) {
	.login-brand {
		display: none;
	}

	.login-container {
		justify-content: center;
	}

	.login-panel {
		width: 100%;
		max-width: 500px;
	}
}

@media only screen and (max-width: 576px) {
	.login-container {
		padding: 20px;
	}

	.login-panel {
		padding: 0;
	}

	.login-card {
		width: 100%;
		padding: 36px 24px;
		border-radius: 16px;

		.card-header .card-title {
			font-size: 24px;
		}
	}
}
</style>
