import { MGT_SYSTEM_USER_CODE } from '@/api/im'

type MessageHandler = (payload: string) => void

function buildWsBaseUrl() {
	const base = String(import.meta.env.VITE_API_URL || '').trim().replace(/\/$/, '')
	return base.replace(/^https:/i, 'wss:').replace(/^http:/i, 'ws:')
}

export function buildImWsUrl(token: string, imUserCode = MGT_SYSTEM_USER_CODE) {
	const wsBase = buildWsBaseUrl()
	const encoded = encodeURIComponent(token)
	const actAs = encodeURIComponent(imUserCode)
	return `${wsBase}/ws/im-native?token=${encoded}&imUserCode=${actAs}`
}

function encodeStompFrame(command: string, headers: Record<string, string>, body = '') {
	let frame = `${command}\n`
	for (const [key, value] of Object.entries(headers)) {
		frame += `${key}:${value}\n`
	}
	frame += `\n${body}\0`
	return frame
}

function parseStompFrames(raw: string): Array<{ command: string; headers: Record<string, string>; body: string }> {
	const frames: Array<{ command: string; headers: Record<string, string>; body: string }> = []
	const chunks = raw.split('\0').filter(Boolean)
	for (const chunk of chunks) {
		const normalized = chunk.replace(/^\n+/, '')
		const splitIndex = normalized.indexOf('\n\n')
		const headPart = splitIndex >= 0 ? normalized.slice(0, splitIndex) : normalized
		const body = splitIndex >= 0 ? normalized.slice(splitIndex + 2) : ''
		const lines = headPart.split('\n').filter(Boolean)
		if (!lines.length) continue
		const command = lines[0].trim()
		const headers: Record<string, string> = {}
		for (let i = 1; i < lines.length; i++) {
			const line = lines[i]
			const colon = line.indexOf(':')
			if (colon <= 0) continue
			headers[line.slice(0, colon)] = line.slice(colon + 1)
		}
		frames.push({ command, headers, body })
	}
	return frames
}

export class ImStompClient {
	private socket: WebSocket | null = null
	private connected = false
	private connectPromise: Promise<void> | null = null
	private handlers = new Map<string, MessageHandler>()
	private subId = 0
	private heartbeatTimer: ReturnType<typeof setInterval> | null = null
	private reconnectTimer: ReturnType<typeof setTimeout> | null = null
	private shouldReconnect = false
	private token = ''
	private onDisconnectCb: (() => void) | null = null

	onDisconnect(cb: () => void) {
		this.onDisconnectCb = cb
	}

	async connect(token: string): Promise<void> {
		this.token = token
		this.shouldReconnect = true
		if (this.connectPromise) return this.connectPromise
		this.connectPromise = this.doConnect().finally(() => {
			this.connectPromise = null
		})
		return this.connectPromise
	}

	private async doConnect(): Promise<void> {
		await this.closeSocket()
		const url = buildImWsUrl(this.token)
		return new Promise((resolve, reject) => {
			let settled = false
			const finish = (ok: boolean, err?: Error) => {
				if (settled) return
				settled = true
				clearTimeout(timer)
				if (ok) resolve()
				else reject(err || new Error('WebSocket 连接失败'))
			}

			const timer = setTimeout(() => finish(false, new Error('WebSocket 连接超时')), 15000)
			const socket = new WebSocket(url)
			this.socket = socket

			socket.onopen = () => {
				const frame = encodeStompFrame('CONNECT', {
					'accept-version': '1.1,1.0',
					'heart-beat': '10000,10000'
				})
				socket.send(frame)
			}

			socket.onmessage = (event) => {
				const data = String(event.data || '')
				const frames = parseStompFrames(data)
				for (const frame of frames) {
					if (frame.command === 'CONNECTED') {
						this.connected = true
						this.startHeartbeat()
						for (const [destination, handler] of this.handlers.entries()) {
							this.sendSubscribe(destination, handler)
						}
						finish(true)
					} else if (frame.command === 'MESSAGE') {
						const dest = frame.headers.destination || ''
						const handler = this.handlers.get(dest)
						if (handler) handler(frame.body)
					} else if (frame.command === 'ERROR') {
						console.error('[IM WS] STOMP ERROR', frame.body)
						if (!this.connected) finish(false, new Error(frame.body || 'STOMP 连接错误'))
					}
				}
			}

			socket.onerror = () => {
				this.connected = false
				if (!settled) finish(false, new Error('WebSocket 错误'))
				this.scheduleReconnect()
			}

			socket.onclose = () => {
				this.connected = false
				this.stopHeartbeat()
				this.onDisconnectCb?.()
				if (!settled) finish(false, new Error('WebSocket 已关闭'))
				this.scheduleReconnect()
			}
		})
	}

	private scheduleReconnect() {
		if (!this.shouldReconnect || !this.token) return
		if (this.reconnectTimer) return
		this.reconnectTimer = setTimeout(() => {
			this.reconnectTimer = null
			void this.connect(this.token).catch((e) => {
				console.warn('[IM WS] 重连失败', e)
			})
		}, 3000)
	}

	private startHeartbeat() {
		this.stopHeartbeat()
		this.heartbeatTimer = setInterval(() => {
			if (!this.connected) return
			this.send('/app/im/ping', {})
		}, 25000)
	}

	private stopHeartbeat() {
		if (this.heartbeatTimer) {
			clearInterval(this.heartbeatTimer)
			this.heartbeatTimer = null
		}
	}

	private async closeSocket() {
		this.connected = false
		this.stopHeartbeat()
		if (this.socket) {
			try {
				this.socket.close()
			} catch {
				/**/
			}
			this.socket = null
		}
	}

	async disconnect() {
		this.shouldReconnect = false
		if (this.reconnectTimer) {
			clearTimeout(this.reconnectTimer)
			this.reconnectTimer = null
		}
		await this.closeSocket()
		this.handlers.clear()
	}

	subscribe(destination: string, handler: MessageHandler) {
		this.handlers.set(destination, handler)
		if (this.socket && this.connected) {
			this.sendSubscribe(destination, handler)
		}
		return destination
	}

	private sendSubscribe(destination: string, handler: MessageHandler) {
		if (!this.socket) return
		const id = `sub-${++this.subId}`
		this.handlers.set(destination, handler)
		const frame = encodeStompFrame('SUBSCRIBE', { id, destination })
		this.socket.send(frame)
	}

	send(destination: string, body: Record<string, unknown>) {
		if (!this.socket || !this.connected) throw new Error('WebSocket 未连接')
		const frame = encodeStompFrame(
			'SEND',
			{ destination, 'content-type': 'application/json' },
			JSON.stringify(body)
		)
		this.socket.send(frame)
	}

	isConnected() {
		return this.connected
	}
}
