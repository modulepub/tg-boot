package pub.module.wx.biz.vo;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信小程序用户信息扩展类
 * 扩展微信小程序用户信息，添加openId字段
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxMaUserInfoEx extends WxMaUserInfo {
    private String openId;
    private String nickName;
    private String gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
}
