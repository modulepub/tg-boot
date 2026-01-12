package pub.module.im.biz.ry.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import io.rong.CenterEnum;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BizRyService {

    Map<String, RongCloud> rongCloudMap = new HashMap<>();

    /**
     * 可以配置缓存
     */
    public RongCloud getRongCloud() {
        String sysConfigCode = "ry";
        RongCloud rongCloud = rongCloudMap.get(sysConfigCode);
        ;
        if (rongCloud == null) {
            JSONObject configJSON = new JSONObject();
            String appKey = configJSON.getStr("appKey");
            String appSecret = configJSON.getStr("appSecret");
            Assert.notEmpty(appKey, "config error,appKey is null");
            Assert.notEmpty(appKey, "config error,appSecret is null");
            rongCloud = RongCloud.getInstance(appKey, appSecret, CenterEnum.BJ);
            log.info("获取RongCloud对象，{}", rongCloud);
            rongCloudMap.put(sysConfigCode, rongCloud);
        }

        return rongCloud;
    }

    /**
     * 获取融云TOKEN，判断TOKEN是否超时，及时刷新
     *
     * @param userCode 用户编码
     * @return 返回客户信息
     */
    public TokenResult getRyToken(String userCode, String sysUseNickName, String headImgUrl) {
        Assert.notEmpty(userCode, "userCode is null");
        Assert.notEmpty(sysUseNickName, "sysUseNickName is null");
        User user = this.getRongCloud().user;
        UserModel userModel = new UserModel()
                .setId(String.valueOf(userCode))
                .setName(sysUseNickName)
                .setPortrait(headImgUrl);
        TokenResult tokenResult = null;
        try {
            tokenResult = user.register(userModel);
            Assert.isTrue(tokenResult.getCode() == 200, tokenResult.getErrorMessage());
            log.info("用户{}获取到融云TOKEN：{}", userCode, tokenResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return tokenResult;
    }


}
