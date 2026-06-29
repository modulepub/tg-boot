package pub.module.wx.biz.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 微信「消息推送」URL 验签与（安全模式）AES 解密工具。
 *
 * <p>算法与微信公众号/小程序消息加解密一致：</p>
 * <ul>
 *     <li>验签：sha1(sort(token, timestamp, nonce[, encrypt]))，按字典序拼接后取 SHA1。</li>
 *     <li>解密：AES-256-CBC，key = Base64(aesKey + "=")，iv = key 前 16 字节；
 *     去 PKCS7 padding 后，[0,16) 随机串、[16,20) 网络字节序的内容长度、其后为明文内容与 appId。</li>
 * </ul>
 */
public final class WxMsgPushCryptoUtil {

    private WxMsgPushCryptoUtil() {
    }

    /**
     * 校验消息推送签名（明文模式 URL 验签 / POST 校验）。
     *
     * @param token     配置的 Token
     * @param signature 微信传入的 signature
     * @param params    参与签名的其余参数（timestamp、nonce，安全模式再加 encrypt 密文）
     */
    public static boolean checkSignature(String token, String signature, String... params) {
        if (StrUtil.isBlank(token) || StrUtil.isBlank(signature)) {
            return false;
        }
        String[] arr = ArrayUtil.append(new String[]{token}, params);
        Arrays.sort(arr);
        String computed = SecureUtil.sha1(String.join("", arr));
        return computed.equalsIgnoreCase(signature.trim());
    }

    /**
     * 解密安全模式下的 Encrypt 密文，返回明文（JSON 或 XML）。
     *
     * @param encodingAesKey 43 位 EncodingAESKey
     * @param encrypt        Base64 密文
     * @return 明文字符串
     */
    public static String decrypt(String encodingAesKey, String encrypt) {
        if (StrUtil.isBlank(encodingAesKey) || StrUtil.isBlank(encrypt)) {
            throw new IllegalArgumentException("EncodingAESKey 或密文为空，无法解密");
        }
        try {
            byte[] aesKey = Base64.decode(encodingAesKey.trim() + "=");
            byte[] iv = Arrays.copyOfRange(aesKey, 0, 16);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
            byte[] original = cipher.doFinal(Base64.decode(encrypt.trim()));
            byte[] bytes = removePkcs7Padding(original);
            // [0,16) 随机串；[16,20) 内容长度（网络字节序）；其后为内容与 appId
            int contentLen = bytesToInt(Arrays.copyOfRange(bytes, 16, 20));
            return new String(bytes, 20, contentLen, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("微信消息推送密文解密失败: " + ex.getMessage(), ex);
        }
    }

    private static byte[] removePkcs7Padding(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    private static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
    }
}
