import type { IImMessage, IImMessageSendReq, IImWebSocketMessage } from '@/api/im'
import { sendMgtImMessage } from '@/api/im'

export function parseDateTimeToSeconds(value?: string): number {
	const raw = String(value || '').trim()
	if (!raw) return 0
	const normalized = raw.includes('T') ? raw : raw.replace(' ', 'T')
	const ms = Date.parse(normalized)
	if (Number.isNaN(ms)) return 0
	return Math.floor(ms / 1000)
}

export function formatLastMessagePreview(content?: string): string {
	const raw = String(content || '').trim()
	if (!raw) return ''
	if (raw.startsWith('[图文]')) return raw
	return formatConversationPreview({ typeCode: 'text', content: raw })
}

export function formatConversationPreview(msg: Pick<IImMessage, 'typeCode' | 'content' | 'title' | 'imageUrl'>): string {
	if (msg.typeCode === 'rich') {
		const custom = tryParseCustomJson(msg.content)
		if (custom?.type === 'chat_image') return '图片'
		return `[图文]${String(msg.title || '').trim() || '图文消息'}`
	}
	const custom = tryParseCustomJson(msg.content)
	if (!custom) return String(msg.content || '').trim()
	switch (custom.type) {
		case 'rich_link':
			return '图文消息'
		case 'chat_image':
			return '图片'
		case 'wx_exchange_request':
			return '[交换微信]'
		case 'wx_exchange_accept':
			return '我的微信号'
		default:
			return '新消息'
	}
}

export function tryParseCustomJson(content?: string): Record<string, unknown> | null {
	const raw = String(content || '').trim()
	if (!raw || !raw.startsWith('{')) return null
	try {
		const parsed = JSON.parse(raw) as Record<string, unknown>
		return parsed && typeof parsed === 'object' ? parsed : null
	} catch {
		return null
	}
}

export type IRawChatMessage = {
	id: string
	flow: 'in' | 'out'
	type: 'text' | 'rich' | 'image' | 'wx_exchange_request' | 'wx_exchange_accept' | 'custom'
	text: string
	richTitle?: string
	richText?: string
	richImageUrl?: string
	richLinkUrl?: string
	imageUrl?: string
	wxRequestId?: string
	wxRequestFromNick?: string
	wxExchangePeerWx?: string
	time: number
	messageCode: string
	conversationCode: string
	fromUserCode: string
	toUserCode: string
}

export function toRawChatMessage(msg: IImMessage | IImWebSocketMessage, selfUserCode: string): IRawChatMessage {
	const imMsg = msg as IImMessage
	const wsMsg = msg as IImWebSocketMessage
	const from = String(msg.fromUserCode || '').trim()
	const to = String(msg.toUserCode || '').trim()
	const flow: 'in' | 'out' = from === selfUserCode ? 'out' : 'in'
	const time =
		parseDateTimeToSeconds(imMsg.createTime) ||
		Math.floor(Number(wsMsg.timestamp || 0) / 1000) ||
		Math.floor(Date.now() / 1000)
	const base = {
		id: String(msg.messageCode || `${time}_${Math.random()}`),
		flow,
		time,
		messageCode: String(msg.messageCode || ''),
		conversationCode: String(msg.conversationCode || ''),
		fromUserCode: from,
		toUserCode: to,
		text: '',
		type: 'text' as const
	}

	if (imMsg.typeCode === 'rich' || (wsMsg.type === 'rich' && !imMsg.typeCode)) {
		const custom = tryParseCustomJson(msg.content)
		if (custom?.type === 'chat_image' && (custom.imageUrl || msg.imageUrl)) {
			return {
				...base,
				type: 'image',
				text: '[图片]',
				imageUrl: String(custom.imageUrl || msg.imageUrl || '').trim()
			}
		}
		return {
			...base,
			type: 'rich',
			text: '[图文消息]',
			richTitle: String(msg.title || '').trim(),
			richText: String(msg.content || '').trim(),
			richImageUrl: String(msg.imageUrl || '').trim(),
			richLinkUrl: String(msg.linkUrl || '').trim()
		}
	}

	const type = String(imMsg.typeCode || wsMsg.type || 'text').replace(/^self_/, '')
	if (type === 'text') {
		const custom = tryParseCustomJson(msg.content)
		if (custom) {
			if (custom.type === 'wx_exchange_request' && custom.requestId) {
				return {
					...base,
					type: 'wx_exchange_request',
					text: '[交换微信]',
					wxRequestId: String(custom.requestId),
					wxRequestFromNick: String(custom.fromNick || '')
				}
			}
			if (custom.type === 'wx_exchange_accept') {
				return {
					...base,
					type: 'wx_exchange_accept',
					text: '[交换微信]',
					wxExchangePeerWx: String(custom.accepterWxId || custom.wxId || '').trim(),
					wxRequestId: String(custom.requestId || '')
				}
			}
			if (custom.type === 'chat_image' && custom.imageUrl) {
				return {
					...base,
					type: 'image',
					text: '[图片]',
					imageUrl: String(custom.imageUrl || '').trim()
				}
			}
			if (custom.type === 'contact_removed') {
				return { ...base, type: 'custom', text: '[联系人已解除]' }
			}
		}
		return { ...base, type: 'text', text: String(msg.content || '') }
	}

	return { ...base, type: 'text', text: String(msg.content || '[消息]') }
}

export async function sendTextMessage(toUserCode: string, text: string) {
	return sendMgtImMessage({ toUserCode, typeCode: 'text', content: text })
}

export async function sendCustomJsonMessage(toUserCode: string, custom: Record<string, unknown>) {
	return sendMgtImMessage({ toUserCode, typeCode: 'text', content: JSON.stringify(custom) })
}

export function isContactRemovedWsMessage(msg: IImWebSocketMessage): boolean {
	return msg.type === 'contact_removed'
}

export function isWxExchangeAcceptRaw(msg: IRawChatMessage): boolean {
	return msg.type === 'wx_exchange_accept'
}
