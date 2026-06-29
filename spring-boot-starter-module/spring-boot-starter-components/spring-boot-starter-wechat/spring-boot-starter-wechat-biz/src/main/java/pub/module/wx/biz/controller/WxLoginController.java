package pub.module.wx.biz.controller;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.common.model.vo.Result;
import pub.module.wx.biz.utils.WxUtil;
import pub.module.wx.biz.vo.LoginRequest;
import pub.module.wx.biz.vo.WxMaUserInfoEx;

import jakarta.annotation.Resource;

/**
 * 公开-微信登录控制器
 * 提供微信小程序登录相关的API接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/pub")
@Tag(name ="公开-用户登录")
@Slf4j
public class WxLoginController {
    @Resource
    ApiSysUserService apiSysUserService;



    @Data
    public static class WxMaLoginRequest {

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
        @Schema(description = "推荐码")
        private String referenceCode;

    }

    @PostMapping("/wxMaLogin")
    public Result<WxMaUserInfoEx> loginByMa(@RequestBody WxMaLoginRequest request) {
        LoginRequest loginRequest = new LoginRequest();
        BeanUtil.copyProperties(request, loginRequest);
        WxMaUserInfoEx wxMaUserInfo = WxUtil.getWxMaUserInfo(loginRequest);
        return Result.ok(wxMaUserInfo);
    }
}