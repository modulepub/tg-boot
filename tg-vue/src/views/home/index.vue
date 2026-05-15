<template>
	<div class="dashboard-container" :class="{ 'light-theme': !isDarkTheme }">
		<!-- Background animation container -->
		<div class="background-container">
			<canvas ref="backgroundCanvas" class="background-canvas"></canvas>
		</div>

		<!-- Dashboard content -->
		<div class="dashboard-content">
			<!-- Time and date -->
			<div class="time-date-section">
				<h1 class="time">{{ currentTime }}</h1>
				<p class="date">{{ currentDate }}</p>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useAppStore } from '@/store/modules/app'
import { useDark } from '@vueuse/core'

// Store
const appStore = useAppStore()
const isDark = useDark()

// Reactive data
const currentTime = ref('')
const currentDate = ref('')
const temperature = ref(24)
const weatherCondition = ref('晴朗')
const forecast = ref([
	{ day: '周一', high: 26, low: 22 },
	{ day: '周二', high: 23, low: 19 },
	{ day: '周三', high: 21, low: 18 },
	{ day: '周四', high: 22, low: 20 },
	{ day: '周五', high: 25, low: 21 }
])
const backgroundCanvas = ref<HTMLCanvasElement | null>(null)
let animationId: number | null = null

// Computed properties
const isDarkTheme = computed(() => {
	return isDark.value
})

// Format current time
const formatTime = () => {
	const now = new Date()
	const hours = now.getHours().toString().padStart(2, '0')
	const minutes = now.getMinutes().toString().padStart(2, '0')
	currentTime.value = `${hours}:${minutes}`
}

// Format current date
const formatDate = () => {
	const now = new Date()
	const year = now.getFullYear()
	const month = (now.getMonth() + 1).toString().padStart(2, '0')
	const day = now.getDate().toString().padStart(2, '0')
	const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
	const weekday = weekdays[now.getDay()]
	currentDate.value = `${year}年${month}月${day}日 ${weekday}`
}

// Starry night animation for dark theme
const initStarryNight = (ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement) => {
	// Generate stars
	const stars: { x: number; y: number; radius: number; opacity: number; speed: number }[] = []
	const shootingStars: { x: number; y: number; length: number; angle: number; speed: number; opacity: number }[] = []

	// Create stars
	for (let i = 0; i < 200; i++) {
		stars.push({
			x: Math.random() * canvas.width,
			y: Math.random() * canvas.height,
			radius: Math.random() * 1.5,
			opacity: Math.random(),
			speed: Math.random() * 0.05 + 0.01
		})
	}

	// Create shooting stars
	for (let i = 0; i < 5; i++) {
		shootingStars.push({
			x: Math.random() * canvas.width,
			y: Math.random() * canvas.height,
			length: Math.random() * 100 + 50,
			angle: (Math.random() * Math.PI) / 4 + Math.PI / 8, // 22.5 to 67.5 degrees
			speed: Math.random() * 10 + 5,
			opacity: Math.random() * 0.5 + 0.3
		})
	}

	// Animation loop
	const animate = () => {
		ctx.clearRect(0, 0, canvas.width, canvas.height)

		// Draw night sky background
		const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height)
		gradient.addColorStop(0, '#0a0a23')
		gradient.addColorStop(1, '#1a1a40')
		ctx.fillStyle = gradient
		ctx.fillRect(0, 0, canvas.width, canvas.height)

		// Draw stars
		stars.forEach(star => {
			ctx.beginPath()
			ctx.arc(star.x, star.y, star.radius, 0, Math.PI * 2)
			ctx.fillStyle = `rgba(255, 255, 255, ${star.opacity})`
			ctx.fill()

			// Twinkle effect
			star.opacity += star.speed
			if (star.opacity > 1) {
				star.speed *= -1
			} else if (star.opacity < 0.2) {
				star.speed *= -1
			}
		})

		// Draw shooting stars
		shootingStars.forEach(star => {
			ctx.beginPath()
			ctx.moveTo(star.x, star.y)
			ctx.lineTo(star.x + Math.cos(star.angle) * star.length, star.y + Math.sin(star.angle) * star.length)
			ctx.strokeStyle = `rgba(255, 255, 255, ${star.opacity})`
			ctx.lineWidth = 2
			ctx.stroke()

			// Move shooting star
			star.x += Math.cos(star.angle) * star.speed
			star.y += Math.sin(star.angle) * star.speed

			// Reset shooting star if it goes off screen
			if (star.x > canvas.width || star.y > canvas.height) {
				star.x = -100
				star.y = Math.random() * canvas.height
				star.length = Math.random() * 100 + 50
				star.speed = Math.random() * 10 + 5
				star.opacity = Math.random() * 0.5 + 0.3
			}
		})

		animationId = requestAnimationFrame(animate)
	}

	return animate
}

// Daytime animation for light theme
const initDaytimeScene = (ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement) => {
	// Generate elements for daytime scene
	const clouds: { x: number; y: number; width: number; height: number; speed: number }[] = []
	const trees: { x: number; y: number; height: number; width: number }[] = []

	// Create clouds
	for (let i = 0; i < 10; i++) {
		clouds.push({
			x: Math.random() * canvas.width,
			y: Math.random() * canvas.height * 0.4,
			width: Math.random() * 100 + 50,
			height: Math.random() * 40 + 20,
			speed: Math.random() * 0.5 + 0.2
		})
	}

	// Create trees
	for (let i = 0; i < 20; i++) {
		trees.push({
			x: Math.random() * canvas.width,
			y: canvas.height - Math.random() * 100 - 50,
			height: Math.random() * 100 + 50,
			width: Math.random() * 30 + 20
		})
	}

	// Animation loop
	const animate = () => {
		ctx.clearRect(0, 0, canvas.width, canvas.height)

		// Draw sky background
		const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height)
		gradient.addColorStop(0, '#87CEEB')
		gradient.addColorStop(1, '#B0E0E6')
		ctx.fillStyle = gradient
		ctx.fillRect(0, 0, canvas.width, canvas.height)

		// Draw sun
		ctx.beginPath()
		ctx.arc(canvas.width - 150, 150, 80, 0, Math.PI * 2)
		ctx.fillStyle = '#FFD700'
		ctx.fill()
		ctx.shadowColor = '#FFD700'
		ctx.shadowBlur = 30

		// Reset shadow
		ctx.shadowBlur = 0

		// Draw clouds
		clouds.forEach(cloud => {
			ctx.beginPath()
			ctx.arc(cloud.x, cloud.y, cloud.height / 2, 0, Math.PI * 2)
			ctx.arc(cloud.x + cloud.width / 3, cloud.y, cloud.height / 2, 0, Math.PI * 2)
			ctx.arc(cloud.x + (cloud.width * 2) / 3, cloud.y, cloud.height / 2, 0, Math.PI * 2)
			ctx.arc(cloud.x + cloud.width / 2, cloud.y - cloud.height / 4, cloud.height / 2, 0, Math.PI * 2)
			ctx.fillStyle = '#FFFFFF'
			ctx.fill()

			// Move cloud
			cloud.x += cloud.speed
			if (cloud.x > canvas.width + cloud.width) {
				cloud.x = -cloud.width
				cloud.y = Math.random() * canvas.height * 0.4
			}
		})

		// Draw grass
		ctx.fillStyle = '#228B22'
		ctx.fillRect(0, canvas.height - 50, canvas.width, 50)

		// Draw trees
		trees.forEach(tree => {
			// Tree trunk
			ctx.fillStyle = '#8B4513'
			ctx.fillRect(tree.x - 5, tree.y - tree.height, 10, tree.height * 0.3)

			// Tree leaves
			ctx.beginPath()
			ctx.arc(tree.x, tree.y - tree.height * 0.7, tree.width, 0, Math.PI * 2)
			ctx.arc(tree.x - tree.width * 0.6, tree.y - tree.height * 0.5, tree.width * 0.8, 0, Math.PI * 2)
			ctx.arc(tree.x + tree.width * 0.6, tree.y - tree.height * 0.5, tree.width * 0.8, 0, Math.PI * 2)
			ctx.arc(tree.x - tree.width * 0.3, tree.y - tree.height * 0.3, tree.width * 0.7, 0, Math.PI * 2)
			ctx.arc(tree.x + tree.width * 0.3, tree.y - tree.height * 0.3, tree.width * 0.7, 0, Math.PI * 2)
			ctx.fillStyle = '#2E8B57'
			ctx.fill()
		})

		animationId = requestAnimationFrame(animate)
	}

	return animate
}

// Initialize background animation
const initBackgroundAnimation = () => {
	if (!backgroundCanvas.value) {
		return
	}

	const canvas = backgroundCanvas.value
	const ctx = canvas.getContext('2d')
	if (!ctx) {
		return
	}

	// Set canvas size
	const resizeCanvas = () => {
		canvas.width = window.innerWidth
		canvas.height = window.innerHeight
	}

	resizeCanvas()
	window.addEventListener('resize', resizeCanvas)

	// Cancel existing animation if any
	if (animationId) {
		cancelAnimationFrame(animationId)
		animationId = null
	}

	// Start appropriate animation based on theme
	let animationFunction
	if (isDarkTheme.value) {
		animationFunction = initStarryNight(ctx, canvas)
	} else {
		animationFunction = initDaytimeScene(ctx, canvas)
	}

	animationFunction()
}

// Watch for theme changes
watch(isDark, () => {
	initBackgroundAnimation()
})

// Lifecycle hooks
onMounted(() => {
	formatTime()
	formatDate()
	setInterval(formatTime, 60000) // Update time every minute
	initBackgroundAnimation()
})

onUnmounted(() => {
	if (animationId) {
		cancelAnimationFrame(animationId)
	}
})
</script>

<style scoped>
.dashboard-container {
	position: relative;
	width: 100%;
	height: 100vh;
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
	transition: all 0.5s ease;
}

/* Dark theme styles */
.dashboard-container:not(.light-theme) {
	background-color: #0a0a23;
}

/* Light theme styles */
.dashboard-container.light-theme {
	background-color: #b0e0e6;
}

.background-container {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 0;
}

.background-canvas {
	width: 100%;
	height: 100%;
}

.dashboard-content {
	position: relative;
	z-index: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	color: white;
	text-align: center;
	padding: 20px;
	transition: all 0.5s ease;
}

/* Light theme text color */
.light-theme .dashboard-content {
	color: #333;
}

.time-date-section {
	margin-bottom: 40px;
}

.time {
	font-size: 4rem;
	font-weight: 300;
	margin: 0;
	text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
	transition: all 0.5s ease;
}

.light-theme .time {
	text-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.date {
	font-size: 1.2rem;
	margin: 10px 0 0 0;
	opacity: 0.8;
}

.weather-section {
	margin-bottom: 60px;
}

.temperature-container {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.temperature {
	font-size: 8rem;
	font-weight: 300;
	margin: 0;
	text-shadow: 0 0 30px rgba(255, 255, 255, 0.4);
	transition: all 0.5s ease;
}

.light-theme .temperature {
	text-shadow: 0 0 30px rgba(0, 0, 0, 0.1);
}

.degree {
	font-size: 4rem;
	vertical-align: top;
	margin-top: 10px;
	display: inline-block;
}

.weather-icon {
	margin: 20px 0;
}

.sun {
	position: relative;
	width: 100px;
	height: 100px;
}

.sun-inner {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 60px;
	height: 60px;
	background-color: #ffd700;
	border-radius: 50%;
	box-shadow: 0 0 20px rgba(255, 215, 0, 0.8);
}

.sun-rays {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
}

.ray {
	position: absolute;
	background-color: #ffd700;
	width: 2px;
	height: 15px;
	left: 50%;
	top: 0;
	transform-origin: bottom center;
}

.ray:nth-child(1) {
	transform: translateX(-50%) rotate(0deg);
}

.ray:nth-child(2) {
	transform: translateX(-50%) rotate(45deg);
}

.ray:nth-child(3) {
	transform: translateX(-50%) rotate(90deg);
}

.ray:nth-child(4) {
	transform: translateX(-50%) rotate(135deg);
}

.ray:nth-child(5) {
	transform: translateX(-50%) rotate(180deg);
}

.ray:nth-child(6) {
	transform: translateX(-50%) rotate(225deg);
}

.ray:nth-child(7) {
	transform: translateX(-50%) rotate(270deg);
}

.ray:nth-child(8) {
	transform: translateX(-50%) rotate(315deg);
}

.weather-condition {
	font-size: 1.5rem;
	margin: 10px 0 0 0;
	opacity: 0.9;
}

.forecast-section {
	display: flex;
	gap: 20px;
}

.forecast-day {
	background-color: rgba(255, 255, 255, 0.1);
	border-radius: 10px;
	padding: 15px 20px;
	min-width: 80px;
	backdrop-filter: blur(10px);
	transition:
		transform 0.3s ease,
		box-shadow 0.3s ease;
}

.light-theme .forecast-day {
	background-color: rgba(255, 255, 255, 0.7);
	color: #333;
}

.forecast-day:hover {
	transform: translateY(-5px);
	box-shadow: 0 10px 20px rgba(255, 255, 255, 0.1);
}

.light-theme .forecast-day:hover {
	box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.day-name {
	margin: 0 0 10px 0;
	font-size: 1rem;
	opacity: 0.8;
}

.day-high {
	margin: 0 0 5px 0;
	font-size: 1.2rem;
	font-weight: 500;
}

.day-low {
	margin: 0;
	font-size: 1rem;
	opacity: 0.7;
}

/* Responsive design */
@media (max-width: 768px) {
	.time {
		font-size: 3rem;
	}

	.temperature {
		font-size: 6rem;
	}

	.degree {
		font-size: 3rem;
	}

	.forecast-section {
		flex-wrap: wrap;
		justify-content: center;
	}
}
</style>
