import { computed, ref } from "vue";
import { forInObject, isNull, isObject, parse, storage } from "../utils";
import { router } from "../router";
import { request } from "../service";
import type { UserInfo } from "@/types";

export type Token = {
	accessToken: string; // 访问token
	accessTokenExpire: number; // token过期时间（秒）
	refreshToken: string; // 刷新token
	refreshTokenExpire: number; // 刷新token过期时间（秒）
};

export class User {
	/**
	 * 用户信息，响应式对象
	 */
	info = ref<UserInfo | null>(null);

	/**
	 * 当前token，字符串或null
	 */
	accessToken: string | null = null;

	constructor() {
		// 获取本地用户信息
		const userInfo = storage.get("userInfo");

		// 获取本地token
		const token = storage.get("accessToken") as string | null;

		// 如果token为空字符串则置为null
		this.accessToken = token == "" ? null : token;

		// 初始化用户信息
		if (userInfo != null && isObject(userInfo)) {
			this.set(userInfo);
		}
	}

	/**
	 * 获取用户信息（从服务端拉取最新信息并更新本地）
	 * @returns Promise<void>
	 */
	async get() {
		if (this.accessToken != null) {
			await request({
				url: "/cus/sysUser/getInfo"
			})
				.then((res) => {
					if (res != null) {
						this.set(res);
					}
				})
				.catch(() => {
					// this.logout();
				});
		}
	}

	/**
	 * 设置用户信息并存储到本地
	 * @param data 用户信息对象
	 */
	set(data: any) {
		if (isNull(data)) {
			return;
		}

		// 设置
		this.info.value = parse<UserInfo>(data)!;

		// 持久化到本地存储
		storage.set("userInfo", data, 0);
	}

	/**
	 * 更新用户信息（本地与服务端同步）
	 * @param data 新的用户信息
	 */
	async update(data: any) {
		if (isNull(data) || isNull(this.info.value)) {
			return;
		}

		// 本地同步更新
		forInObject(data, (value, key) => {
			this.info.value![key] = value;
		});

		// 同步到服务端
		await request({
			url: "/app/user/info/updatePerson",
			method: "POST",
			data
		});
	}

	/**
	 * 移除用户信息
	 */
	remove() {
		this.info.value = null;
		storage.remove("userInfo");
	}

	/**
	 * 判断用户信息是否为空
	 * @returns boolean
	 */
	isNull() {
		return this.info.value == null;
	}

	/**
	 * 清除本地所有用户信息和token
	 */
	clear() {
		storage.remove("userInfo");
		storage.remove("accessToken");
		storage.remove("refreshToken");
		this.accessToken = null;
		this.remove();
	}

	/**
	 * 退出登录，清除所有信息并跳转到登录页
	 */
	logout() {
		this.clear();
		router.login();
	}

	/**
	 * 设置token并存储到本地
	 * @param data Token对象
	 */
	setToken(data: Token) {
		let accessToken: string =  data.accessToken;
		this.accessToken = accessToken;

		// 访问token，提前5秒过期，防止边界问题
		storage.set("accessToken", accessToken, data.accessTokenExpire - 5);
		// 刷新token，提前5秒过期
		storage.set("refreshToken", accessToken, data.refreshTokenExpire - 5);
	}

	/**
	 * 刷新token（调用服务端接口，自动更新本地token）
	 * @returns Promise<string> 新的token
	 */
	refreshToken(): Promise<string> {
		return new Promise((resolve, reject) => {
			request({
				url: "/cus/sysUser/refreshToken",
				method: "POST",
				data: {
					refreshToken: storage.get("refreshToken")
				}
			})
				.then((res) => {
					if (res != null) {
						const data = parse<Token>(res);

						if (data != null) {
							this.setToken(data);
							resolve(data.accessToken);
						}
					}
				})
				.catch((err) => {
					reject(err);
				});
		});
	}
}

/**
 * 单例用户对象，项目全局唯一
 */
export const user = new User();

/**
 * 用户信息，响应式对象
 */
export const userInfo = computed(() => user.info.value);
