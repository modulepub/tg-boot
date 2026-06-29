package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxMpFan;

import java.util.List;

public interface WxMpFanService extends IService<WxMpFan> {

    WxMpFan getByConfigAndOpenId(String wxMpConfigCode, String openId);

    List<WxMpFan> listByConfigCode(String wxMpConfigCode);
}
