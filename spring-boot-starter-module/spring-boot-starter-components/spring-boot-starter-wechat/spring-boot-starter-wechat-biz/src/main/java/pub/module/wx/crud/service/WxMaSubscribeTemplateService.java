package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxMaSubscribeTemplate;

import java.util.List;

/**
 * 微信小程序订阅消息模板 CRUD 服务。
 */
public interface WxMaSubscribeTemplateService extends IService<WxMaSubscribeTemplate> {

    WxMaSubscribeTemplate getByCode(String wxMaSubscribeTemplateCode);

    WxMaSubscribeTemplate getByTemplateId(String wxMaSubscribeTemplateId);

    List<WxMaSubscribeTemplate> listAllEnabled();
}
