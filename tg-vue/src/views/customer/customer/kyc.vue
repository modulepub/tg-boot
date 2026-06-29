<template>
	<el-dialog v-model="dialogVisible" title="KYC视图" width="85%" top="4vh" destroy-on-close>
		<el-tabs @tab-click="handleTabClick">
			<el-tab-pane label="客户资料">
				<div v-if="customerInfo" class="kyc-profile">
					<div class="kyc-profile__head">
						<div class="kyc-profile__avatar">
							<el-image
								v-if="avatarPreviewList.length"
								:src="avatarPreviewList[0]"
								:preview-src-list="avatarPreviewList"
								fit="cover"
								class="kyc-profile__avatar-img"
								preview-teleported
							/>
							<el-avatar v-else :size="64">{{ displayName.slice(0, 1) }}</el-avatar>
						</div>
						<div class="kyc-profile__main">
							<div class="kyc-profile__name">{{ displayName }}</div>
							<div class="kyc-profile__meta">
								<span>{{ customerInfo.cusCode || '—' }}</span>
								<el-divider direction="vertical" />
								<span>{{ customerInfo.cusPhone || '—' }}</span>
								<el-divider direction="vertical" />
								<span>用户号 {{ customerInfo.cusUserCode || '未绑定' }}</span>
							</div>
						</div>
						<div class="kyc-profile__photos">
							<div class="kyc-photo-group">
								<div class="kyc-photo-group__label">生活照</div>
								<div v-if="lifePhotoList.length" class="kyc-images">
									<el-image
										v-for="(url, idx) in lifePhotoList"
										:key="idx"
										:src="url"
										:preview-src-list="lifePhotoList"
										:initial-index="idx"
										fit="cover"
										class="kyc-images__item"
										preview-teleported
									/>
								</div>
								<span v-else class="kyc-field__value kyc-field__value--empty">—</span>
							</div>
							<div class="kyc-photo-group">
								<div class="kyc-photo-group__label">少年感照片</div>
								<div v-if="teenagePhotoList.length" class="kyc-images">
									<el-image
										v-for="(url, idx) in teenagePhotoList"
										:key="idx"
										:src="url"
										:preview-src-list="teenagePhotoList"
										:initial-index="idx"
										fit="cover"
										class="kyc-images__item"
										preview-teleported
									/>
								</div>
								<span v-else class="kyc-field__value kyc-field__value--empty">—</span>
							</div>
						</div>
					</div>

					<div v-for="section in customerFieldSections" :key="section.title" class="kyc-section">
						<div class="kyc-section__title">{{ section.title }}</div>
						<div class="kyc-grid">
							<div
								v-for="field in section.fields"
								:key="field.key"
								class="kyc-field"
								:class="{ 'kyc-field--full': (field.span || 1) >= 3 }"
							>
								<div class="kyc-field__label">{{ field.label }}</div>
								<div class="kyc-field__value">
									<template v-if="field.type === 'dict'">
										<span :class="{ 'kyc-field__value--empty': !getDictText(field.dictCode || field.key, customerInfo[field.key]) }">
											{{ getDictText(field.dictCode || field.key, customerInfo[field.key]) || '—' }}
										</span>
									</template>
									<template v-else>
										<span
											class="kyc-text"
											:class="{ 'kyc-field__value--empty': formatField(customerInfo[field.key]) === '—' }"
										>{{ formatField(customerInfo[field.key]) }}</span>
									</template>
								</div>
							</div>
						</div>
					</div>
				</div>
				<el-alert v-else type="info" title="加载中..." :closable="false" />
			</el-tab-pane>
			<el-tab-pane label="联络记录">
				<el-card>
					<el-table v-loading="contactRecordLoading" :data="contactRecordList" border>
						<el-table-column
							prop="contactRecordCode"
							width="100"
							label="记录编号"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column width="100" prop="cusCode" label="客户编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
						<el-table-column width="100" prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
						<el-table-column
							width="100"
							prop="cusPhone"
							label="客户手机号"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							width="100"
							prop="userCode"
							label="员工账号"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							prop="userRealName"
							label="员工姓名"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							width="120"
							prop="contactRecordTalkDuration"
							label="通话时长（s)"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<tg-file-column prop="contactRecordFile" label="录音文件" header-align="center" align="center"></tg-file-column>
						<el-table-column
							width="100"
							prop="contactRecordVoiceText"
							label="通话文字"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							width="100"
							prop="contactRecordDescription"
							label="跟踪描述"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<tg-dict-column width="100" prop="cusIntentionStatusCode" label="是否意向" dict-code="cusIntentionStatusCode"></tg-dict-column>
						<tg-dict-column width="100" prop="cusIntentionLevelCode" label="意向等级" dict-code="cusIntentionLevelCode"></tg-dict-column>
						<el-table-column prop="contactRecordTime" label="通话时间" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					</el-table>
					<el-pagination
						v-if="contactRecordTotal > 0"
						:current-page="contactRecordPageNo"
						:page-size="contactRecordPageSize"
						:total="contactRecordTotal"
						layout="total, sizes, prev, pager, next, jumper"
						@size-change="handleContactRecordSizeChange"
						@current-change="handleContactRecordCurrentChange"
					>
					</el-pagination>
				</el-card>
			</el-tab-pane>
		</el-tabs>
	</el-dialog>
</template>

<script setup lang="ts" name="CustomerKyc">
import { ref, computed } from 'vue'
import service from '@/utils/request'
import { useAppStore } from '@/store/modules/app'
import { getDictDataList } from '@/utils/tool'
import { resolveCustomerDisplayName, formatCustomerFieldValue } from './customerDisplay'

const appStore = useAppStore()

const dialogVisible = ref(false)
const customerId = ref<number | undefined>(undefined)
const customerInfo = ref<Record<string, any> | null>(null)

const displayName = computed(() => resolveCustomerDisplayName(customerInfo.value))

const imageList = (value: unknown): string[] => {
	if (!value) {
		return []
	}
	return String(value)
		.split(',')
		.map(s => s.trim())
		.filter(Boolean)
}

const avatarPreviewList = computed(() => imageList(customerInfo.value?.cusAvatar))

const lifePhotoList = computed(() => {
	if (!customerInfo.value) {
		return []
	}
	const avatarSet = new Set(avatarPreviewList.value)
	return imageList(customerInfo.value.cusLifePhoto).filter(url => !avatarSet.has(url))
})

const teenagePhotoList = computed(() => imageList(customerInfo.value?.cusTeenagePhoto))

type FieldType = 'text' | 'dict'
interface CustomerFieldDef {
	key: string
	label: string
	type?: FieldType
	dictCode?: string
	span?: number
}

interface CustomerFieldSection {
	title: string
	fields: CustomerFieldDef[]
}

const customerFieldSections: CustomerFieldSection[] = [
	{
		title: '身份信息',
		fields: [
			{ key: 'cusCode', label: '客户编号' },
			{ key: 'cusUserCode', label: '用户号' },
			{ key: 'cusName', label: '客户姓名' },
			{ key: 'cusNickName', label: '昵称' },
			{ key: 'cusPhone', label: '手机号' },
			{ key: 'cusIdTypeCode', label: '证件类型', type: 'dict' },
			{ key: 'cusIdNo', label: '证件号' },
			{ key: 'cusSexCode', label: '性别', type: 'dict', dictCode: 'userSexCode' },
			{ key: 'cusKinshipCode', label: '身份关系' },
			{ key: 'cusBirthday', label: '生日' },
			{ key: 'cusAge', label: '年龄' },
			{ key: 'cusWxIdNo', label: '微信号' },
			{ key: 'cusWechatId', label: '微信ID' }
		]
	},
	{
		title: '认证状态',
		fields: [
			{ key: 'cusIdentityAuthenticatedStatusCode', label: '实名认证', type: 'dict' },
			{ key: 'cusIdentityAuthenticatedTime', label: '实名认证时间' },
			{ key: 'cusLsStatusCode', label: '点亮爱与诚', type: 'dict' },
			{ key: 'cusMaritalStatusCode', label: '婚姻状况', type: 'dict' },
			{ key: 'cusMaritalStatusAuthenticatedStatusCode', label: '婚姻认证', type: 'dict' },
			{ key: 'cusMaritalStatusAuthenticatedTime', label: '婚姻认证时间' },
			{ key: 'cusCarAssetCertStatusCode', label: '车产认证', type: 'dict' },
			{ key: 'cusCarAssetCertTime', label: '车产认证时间' },
			{ key: 'cusHouseAssetCertStatusCode', label: '房产认证', type: 'dict' },
			{ key: 'cusHouseAssetCertTime', label: '房产认证时间' },
			{ key: 'cusComleteProfileStatusCode', label: '资料完善', type: 'dict' },
			{ key: 'cusAuditProcessCode', label: '审核状态', type: 'dict', dictCode: 'cusAuditProcessCode' },
			{ key: 'cusHiddenStatusCode', label: '是否隐藏', type: 'dict' }
		]
	},
	{
		title: '生活与资产',
		fields: [
			{ key: 'cusHeight', label: '身高(cm)' },
			{ key: 'cusWeight', label: '体重(kg)' },
			{ key: 'cusCityResidenceName', label: '生活城市' },
			{ key: 'cusCityResidenceCode', label: '城市编码' },
			{ key: 'cusEducationName', label: '学历' },
			{ key: 'cusEducationCode', label: '学历编码' },
			{ key: 'cusOccupationalDescription', label: '职业描述' },
			{ key: 'cusAnnualIncomeAmount', label: '年收入' },
			{ key: 'cusHaveCarStatusCode', label: '是否有车', type: 'dict', dictCode: 'cusHaveCarStatusCode' },
			{ key: 'cusHaveHouseStatusCode', label: '是否有房', type: 'dict', dictCode: 'cusHaveHouseStatusCode' },
			{ key: 'cusRemarriageStatusCode', label: '是否二婚', type: 'dict' },
			{ key: 'cusDisabledStatusCode', label: '是否残疾', type: 'dict' }
		]
	},
	{
		title: '营销与跟进',
		fields: [
			{ key: 'cusSourceCode', label: '客户来源', type: 'dict' },
			{ key: 'cusTagCode', label: '用户标签', type: 'dict' },
			{ key: 'cusLevelCode', label: '客户等级', type: 'dict' },
			{ key: 'cusIntentionStatusCode', label: '是否意向', type: 'dict' },
			{ key: 'cusIntentionLevelCode', label: '意向等级', type: 'dict' },
			{ key: 'cusPoolStatusCode', label: '入库状态', type: 'dict' },
			{ key: 'cusAssignSalesStatusCode', label: '分配销售', type: 'dict' },
			{ key: 'cusAssignSalesTime', label: '分配销售时间' },
			{ key: 'cusAssignServersStatusCode', label: '分配服务', type: 'dict' },
			{ key: 'cusFollowUpStatusCode', label: '跟进状态', type: 'dict' },
			{ key: 'cusFollowUpReminderTypeCode', label: '跟进提醒', type: 'dict' },
			{ key: 'cusDealtStatusCode', label: '是否成交', type: 'dict' },
			{ key: 'cusDealtCompleteStatusCode', label: '是否完单', type: 'dict' },
			{ key: 'cusHandholdsNum', label: '牵手次数' },
			{ key: 'cusMemberTypeName', label: '会员类型' },
			{ key: 'cusMemberExpireTime', label: '会员到期' }
		]
	},
	{
		title: '描述与备注',
		fields: [
			{ key: 'cusDesc', label: '用户描述', span: 3 },
			{ key: 'cusDemand', label: '客户需求', span: 3 },
			{ key: 'cusMoment', label: '用户说说', span: 3 },
			{ key: 'cusRemark', label: '用户备注', span: 3 }
		]
	},
	{
		title: '系统信息',
		fields: [
			{ key: 'createTime', label: '创建时间' },
			{ key: 'updateTime', label: '更新时间' }
		]
	}
]

const getDictText = (dictCode: string, dictValue: unknown) => {
	if (dictValue === null || dictValue === undefined || dictValue === '') {
		return ''
	}
	const dictValues = String(dictValue).split(',')
	const dictList = getDictDataList(appStore.dictList, dictCode)
	const dictTexts = dictValues.map(value => {
		const dictItem = dictList.find((item: any) => item.dictItemValue === value)
		return dictItem ? dictItem.dictItemText : value
	})
	return dictTexts.join(', ')
}

const formatField = (value: unknown) => formatCustomerFieldValue(value)

const contactRecordList = ref<any[]>([])
const contactRecordLoading = ref(false)
const contactRecordPageNo = ref(1)
const contactRecordPageSize = ref(10)
const contactRecordTotal = ref(0)

const init = (id: number) => {
	customerId.value = id
	dialogVisible.value = true
	customerInfo.value = null
	loadCustomerInfo().then(() => {
		loadContactRecordList()
	})
}

const loadCustomerInfo = () => {
	return new Promise(resolve => {
		if (customerId.value) {
			service.get('/mgt/customer/customer/queryById?id=' + customerId.value).then((res: any) => {
				customerInfo.value = res.data
				resolve(res.data)
			})
		} else {
			resolve(null)
		}
	})
}

const loadContactRecordList = () => {
	if (customerInfo.value && customerInfo.value.cusCode) {
		contactRecordLoading.value = true
		service
			.get('/mgt/customer/customerContactRecord/list', {
				params: {
					pageNo: contactRecordPageNo.value,
					pageSize: contactRecordPageSize.value,
					cusCode: customerInfo.value.cusCode
				}
			})
			.then((res: any) => {
				contactRecordList.value = res.data.records || res.data.list || []
				contactRecordTotal.value = res.data.total
				contactRecordLoading.value = false
			})
	}
}

const handleContactRecordSizeChange = (size: number) => {
	contactRecordPageSize.value = size
	loadContactRecordList()
}

const handleContactRecordCurrentChange = (current: number) => {
	contactRecordPageNo.value = current
	loadContactRecordList()
}

const handleTabClick = (tab: any) => {
	const tabLabel = tab.props.label
	if (tabLabel === '客户资料') {
		loadCustomerInfo()
	} else if (tabLabel === '联络记录') {
		loadContactRecordList()
	}
}

defineExpose({
	init
})
</script>

<style scoped>
.kyc-profile__head {
	display: flex;
	align-items: flex-start;
	gap: 14px;
	margin-bottom: 12px;
	padding-bottom: 12px;
	border-bottom: 1px solid var(--el-border-color-lighter);
}
.kyc-profile__avatar-img {
	width: 64px;
	height: 64px;
	border-radius: 50%;
	cursor: pointer;
	flex-shrink: 0;
}
.kyc-profile__main {
	flex: 1;
	min-width: 0;
	padding-top: 4px;
}
.kyc-profile__name {
	font-size: 17px;
	font-weight: 600;
	color: var(--el-text-color-primary);
	line-height: 1.3;
}
.kyc-profile__meta {
	margin-top: 6px;
	color: var(--el-text-color-placeholder);
	font-size: 12px;
}
.kyc-profile__photos {
	display: flex;
	gap: 24px;
	flex-shrink: 0;
	align-items: flex-start;
}
.kyc-photo-group__label {
	font-size: 11px;
	color: var(--el-text-color-placeholder);
	margin-bottom: 6px;
	line-height: 1;
}
.kyc-section {
	margin-bottom: 10px;
}
.kyc-section__title {
	font-size: 13px;
	font-weight: 600;
	color: var(--el-text-color-regular);
	margin-bottom: 6px;
	padding-left: 8px;
	border-left: 3px solid var(--el-color-primary);
	line-height: 1.2;
}
.kyc-grid {
	display: grid;
	grid-template-columns: repeat(6, minmax(0, 1fr));
	gap: 8px 14px;
}
.kyc-field--full {
	grid-column: 1 / -1;
}
.kyc-field__label {
	font-size: 11px;
	color: var(--el-text-color-placeholder);
	line-height: 1.2;
	margin-bottom: 3px;
}
.kyc-field__value {
	font-size: 13px;
	font-weight: 500;
	color: var(--el-text-color-primary);
	line-height: 1.35;
	word-break: break-all;
}
.kyc-field__value--empty {
	color: var(--el-text-color-placeholder);
	font-weight: 400;
}
.kyc-text {
	white-space: pre-wrap;
	word-break: break-word;
}
.kyc-images {
	display: flex;
	flex-wrap: wrap;
	gap: 6px;
}
.kyc-images__item {
	width: 48px;
	height: 48px;
	border-radius: 4px;
	cursor: pointer;
}
</style>
