package pub.module.wx.biz.vo;

import lombok.Data;

/**
 * 微信登录请求类
 * 封装微信登录所需的请求参数
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
public class LoginRequest {

    String appId;
    //用户登录凭证
    String code;

    //原始数据字符串
    String signature;

    //校验用户信息字符串
    String rawData;

    //加密用户数据
    String encryptedData;

    //加密算法的初始向量
    String iv;

}