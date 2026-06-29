import service from '@/utils/request'

export const MGT_SYSTEM_USER_CODE = 'system'

export interface IImConversation {
	conversationCode: string
	peerUserCode: string
	peerNickName?: string
	peerAvatar?: string
	lastMessageContent?: string
	lastMessageTime?: string
	unreadCount?: number
}

export interface IImMessage {
	messageCode: string
	conversationCode: string
	fromUserCode: string
	toUserCode: string
	typeCode: string
	content?: string
	title?: string
	imageUrl?: string
	linkUrl?: string
	readStatusCode?: string
	sendStatusCode?: string
	createTime?: string
}

export interface IImMessageSendReq {
	toUserCode: string
	typeCode: 'text' | 'rich'
	content?: string
	title?: string
	imageUrl?: string
	linkUrl?: string
}

export interface IImMessageReadReq {
	messageCodes: string[]
	fromUserCode?: string
	conversationCode?: string
}

export interface IImWebSocketMessage {
	type: string
	fromUserCode?: string
	toUserCode?: string
	messageCode?: string
	content?: string
	title?: string
	imageUrl?: string
	linkUrl?: string
	conversationCode?: string
	timestamp?: number
	unreadCount?: number
	typeCode?: string
}

export function listMgtImConversations() {
	return service.get<IImConversation[]>('/mgt/im/message/conversation/list')
}

export function listMgtImMessages(conversationCode: string, pageNo = 1, pageSize = 50) {
	return service.get<IImMessage[]>('/mgt/im/message/list', {
		params: { conversationCode, pageNo, pageSize }
	})
}

export function sendMgtImMessage(data: IImMessageSendReq) {
	return service.post<IImMessage>('/mgt/im/message/send', data)
}

export function markMgtImMessagesRead(data: IImMessageReadReq) {
	return service.post<void>('/mgt/im/message/read', data)
}

export function getMgtImUnreadCount() {
	return service.get<number>('/mgt/im/message/unread/count')
}
