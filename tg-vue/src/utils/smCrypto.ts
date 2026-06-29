import { sm2 } from 'sm-crypto'

const publicKey = '04d3341fcb5689cddc6068605bdf943e03a0678a3ab38fb8491922e60de46364ebd282c9bbc3a9d938c8914ed56c1db2b4de84cb34d7f57ed527e80f0fd7e577f2'

/**
 * sm2加密
 * @param data 待加密数据
 * @return 加密后的数据
 */
export const sm2Encrypt = (data: string): string => {
	const hexReg = /^[0-9a-fA-F]+$/
	if (!hexReg.test(publicKey)) {
		throw new Error('SM2公钥格式非法，仅支持十六进制字符串')
	}
	return '04' + sm2.doEncrypt(data, publicKey, 1)
}
