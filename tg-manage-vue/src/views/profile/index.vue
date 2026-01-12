<template>
	<el-row :gutter="20">
		<el-col :span="8">
			<el-card class="profile-card">
				<template #header> 个人信息 </template>
				<div class="avatar">
					<ma-upload-image v-model:image-url="userStore.user.userAvatar" biz="avatar" :drag="false" border-radius="50%" @success="handleSuccess">
						<template #empty>
							<el-icon><Avatar /></el-icon>
							<span>请上传头像</span>
						</template>
					</ma-upload-image>
				</div>
				<ul>
					<li>
						<ma-icon icon="icon-user" /> 用户名 <span>{{ userStore.user.userName }}</span>
					</li>
					<li>
						<ma-icon icon="icon-idcard" /> 姓名 <span>{{ userStore.user.userRealName }}</span>
					</li>
					<li>
						<ma-icon icon="icon-phone" /> 手机号码 <span>{{ userStore.user.userPhone }}</span>
					</li>
				</ul>
			</el-card>
		</el-col>
		<el-col :span="16">
			<el-card>
				<template #header> 基本信息 </template>
				<el-tabs v-model="activeName">
					<el-tab-pane label="基本资料" name="info">
						<UserInfo />
					</el-tab-pane>
					<el-tab-pane label="修改密码" name="password">
						<Password />
					</el-tab-pane>
				</el-tabs>
			</el-card>
		</el-col>
	</el-row>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import Password from '@/views/profile/password.vue'
import UserInfo from '@/views/profile/user-info.vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const userStore = useUserStore()
const activeName = ref('info')

const handleSuccess = (data: any) => {
	const dataForm = {
		userAvatar: data.filePath
	}
	// 修改登录用户头像
	service.post('/cus/sysUser/editUserInfo', dataForm).then(() => {
		ElMessage.success('修改成功')
	})
}
</script>

<style scoped lang="scss">
.profile-card {
	.avatar {
		display: flex;
		justify-content: space-around;
		margin-bottom: 20px;
	}
	ul {
		list-style: none;
		padding: 0;
		li {
			padding: 12px 0;
			border-bottom: 1px solid #f0f0f0;
			&:last-child {
				border-bottom: none;
				padding-top: 12px;
			}
			span {
				float: right;
			}
		}
	}
}
</style>
