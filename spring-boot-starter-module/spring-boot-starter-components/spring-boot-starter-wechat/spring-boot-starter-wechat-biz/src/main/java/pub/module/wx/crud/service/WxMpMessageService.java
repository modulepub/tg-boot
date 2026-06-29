package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxMpMessage;

import java.util.List;

public interface WxMpMessageService extends IService<WxMpMessage> {

    List<WxMpMessage> listByFan(String wxMpConfigCode, String openId);
}
