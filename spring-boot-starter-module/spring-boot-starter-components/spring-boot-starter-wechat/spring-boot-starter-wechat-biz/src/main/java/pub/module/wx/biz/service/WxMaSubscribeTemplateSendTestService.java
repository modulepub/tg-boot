package pub.module.wx.biz.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.wx.api.dto.WxMaSubscribeTemplateDTO;
import pub.module.wx.api.dto.WxMaSubscribeTemplateSendTestDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeMessageService;
import pub.module.wx.api.service.ApiWxMaSubscribeTemplateService;

/**
 * 管理端订阅消息模板测试发送（编排模板配置 + 用户 + 通用发送，避免 Api/crud 层循环依赖）。
 */
@Service
public class WxMaSubscribeTemplateSendTestService {

    @Resource
    private ApiWxMaSubscribeTemplateService apiWxMaSubscribeTemplateService;
    @Resource
    private ApiWxMaSubscribeMessageService apiWxMaSubscribeMessageService;
    @Resource
    private ApiSysUserService apiSysUserService;

    public ApiWxMaSubscribeMessageService.SendResult sendTest(WxMaSubscribeTemplateSendTestDTO dto) {
        Assert.notNull(dto, "测试发送参数不能为空");
        Assert.notBlank(dto.getWxMaSubscribeTemplateCode(), "模板编码不能为空");
        Assert.notBlank(dto.getUserCode(), "请选择接收用户");
        WxMaSubscribeTemplateDTO template = apiWxMaSubscribeTemplateService.getByCode(dto.getWxMaSubscribeTemplateCode().trim());
        Assert.notNull(template, "订阅消息模板不存在");
        Assert.notBlank(template.getWxMaSubscribeTemplateId(), "模板未配置微信 templateId");
        UserDTO user = apiSysUserService.getUserByUserCode(dto.getUserCode().trim());
        Assert.notNull(user, "用户不存在");
        Assert.notBlank(user.getUserWxOpenId(), "该用户未绑定微信小程序 openId，请先在小程序登录");

        ApiWxMaSubscribeMessageService.SendRequest request = new ApiWxMaSubscribeMessageService.SendRequest();
        request.setWxMiniConfigCode(StrUtil.trimToNull(dto.getWxMiniConfigCode()));
        request.setToOpenId(user.getUserWxOpenId().trim());
        request.setTemplateId(template.getWxMaSubscribeTemplateId().trim());
        request.setPage(StrUtil.trimToNull(dto.getPage()));
        request.setData(dto.getData());
        return apiWxMaSubscribeMessageService.send(request);
    }
}
