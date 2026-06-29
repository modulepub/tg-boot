package pub.module.dating.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.dating.api.constants.DatingWxMaConfigConstants;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.DatingWxSubscribeSceneEnum;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.biz.service.DatingWxSubscribeNotifyService;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.entity.DtMatch;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeMessageService;
import pub.module.wx.api.service.ApiWxMaSubscribeTemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DatingWxSubscribeNotifyServiceImpl implements DatingWxSubscribeNotifyService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
    private static final DateTimeFormatter DATE_DOT_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Resource
    private ApiWxMaSubscribeMessageService apiWxMaSubscribeMessageService;
    @Resource
    private ApiWxMaSubscribeTemplateService apiWxMaSubscribeTemplateService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    public void sendFriendRequestReceived(DtContactApply apply) {
        if (apply == null || StrUtil.isBlank(apply.getCusUserCode())) {
            return;
        }
        DatingWxSubscribeSceneEnum scene = DatingWxSubscribeSceneEnum.FRIEND_REQUEST_RECEIVED;
        String openId = resolveOpenId(apply.getCusUserCode());
        if (openId == null) {
            return;
        }
        Map<String, String> data = new LinkedHashMap<>();
        data.put("date1", formatDateTime(LocalDateTime.now()));
        data.put("name3", truncateName(StrUtil.blankToDefault(apply.getAppCusName(), "用户")));
        String greeting = StrUtil.trim(apply.getContactApplyGreeting());
        data.put("thing2", truncateThing(StrUtil.isNotBlank(greeting) ? greeting : "您有一条好友申请"));
        send(scene, openId, "friend_req:" + apply.getContactApplyCode(), data, scene.getJumpPage());
    }

    @Override
    public void sendFriendAddSuccess(DtContactApply apply) {
        if (apply == null || StrUtil.isBlank(apply.getUserCode())) {
            return;
        }
        DatingWxSubscribeSceneEnum scene = DatingWxSubscribeSceneEnum.FRIEND_ADD_SUCCESS;
        String openId = resolveOpenId(apply.getUserCode());
        if (openId == null) {
            return;
        }
        Map<String, String> data = new LinkedHashMap<>();
        data.put("thing2", truncateThing("对方已同意您的好友申请，点击查看"));
        data.put("date1", formatDateTime(LocalDateTime.now()));
        send(scene, openId, "friend_ok:" + apply.getContactApplyCode(), data, scene.getJumpPage());
    }

    @Override
    public void sendMatchRequest(DtMatch match, String matchmakerUserCode) {
        if (match == null || StrUtil.isBlank(matchmakerUserCode)) {
            return;
        }
        DatingWxSubscribeSceneEnum scene = DatingWxSubscribeSceneEnum.MATCH_REQUEST;
        String openId = resolveOpenId(matchmakerUserCode);
        if (openId == null) {
            return;
        }
        String pursuingName = truncateName(StrUtil.blankToDefault(match.getMtPursuingCusName(), "用户"));
        Map<String, String> data = new LinkedHashMap<>();
        data.put("name1", pursuingName);
        data.put("thing3", truncateThing("有群友请求您的牵线"));
        data.put("thing4", pursuingName);
        data.put("time5", formatDateDot(LocalDateTime.now()));
        String page = scene.getJumpPage();
        if (StrUtil.isNotBlank(match.getMtMkCode())) {
            page = page + "?mkCode=" + match.getMtMkCode();
        }
        send(scene, openId, "match_req:" + match.getMtCode(), data, page);
    }

    @Override
    public void sendFreeRecommendNotify(String userCode, List<DtCustomerDTO> recommendedCustomers) {
        if (StrUtil.isBlank(userCode) || recommendedCustomers == null || recommendedCustomers.isEmpty()) {
            return;
        }
        UserDTO user = apiSysUserService.getUserByUserCode(userCode.trim());
        if (user != null && StatusCodeEnum.YES.equals(user.getUserTestStatusCode())) {
            return;
        }
        DatingWxSubscribeSceneEnum scene = DatingWxSubscribeSceneEnum.FREE_RECOMMEND;
        String openId = resolveOpenId(userCode);
        if (openId == null) {
            return;
        }
        Map<String, String> data = new LinkedHashMap<>();
        data.put("thing1", buildFreeRecommendReason(recommendedCustomers));
        data.put("thing5", truncateThing("如果喜欢，欢迎来撩哦"));
        String idempotentKey = "free_rec:" + userCode.trim() + ":"
                + recommendedCustomers.stream()
                .map(c -> StrUtil.trim(c.getCusCode()))
                .filter(StrUtil::isNotBlank)
                .sorted()
                .collect(Collectors.joining(","));
        send(scene, openId, idempotentKey, data, scene.getJumpPage());
    }

    /** 模板 thing1：今天为您推荐 N 个（可选附带首位嘉宾城市） */
    private static String buildFreeRecommendReason(List<DtCustomerDTO> recommendedCustomers) {
        int count = recommendedCustomers.size();
        String prefix = "今天为您推荐" + count + "个";
        if (prefix.length() >= 20) {
            return truncateThing(prefix);
        }
        DtCustomerDTO first = recommendedCustomers.get(0);
        String city = first == null ? null : StrUtil.trimToNull(first.getCusCityResidenceName());
        if (StrUtil.isBlank(city)) {
            return truncateThing(prefix);
        }
        String suffix = "(" + city;
        int remaining = 20 - prefix.length();
        if (remaining <= 0) {
            return truncateThing(prefix);
        }
        if (suffix.length() > remaining) {
            suffix = suffix.substring(0, remaining);
        }
        return truncateThing(prefix + suffix);
    }

    private void send(DatingWxSubscribeSceneEnum scene, String openId, String idempotentKey,
                      Map<String, String> data, String page) {
        String templateId = apiWxMaSubscribeTemplateService.resolveTemplateId(scene.getTemplateCode());
        if (StrUtil.isBlank(templateId)) {
            log.warn("订阅消息跳过：未配置模板 templateCode={}", scene.getTemplateCode());
            return;
        }
        ApiWxMaSubscribeMessageService.SendRequest request = new ApiWxMaSubscribeMessageService.SendRequest();
        request.setWxMiniConfigCode(DatingWxMaConfigConstants.WX_MINI_CONFIG_CODE);
        request.setToOpenId(openId);
        request.setTemplateId(templateId);
        request.setPage(page);
        request.setData(data);
        request.setIdempotentKey(idempotentKey);
        ApiWxMaSubscribeMessageService.SendResult result = apiWxMaSubscribeMessageService.send(request);
        if (result != null && !result.isSuccess() && !result.isSkipped()) {
            log.info("订阅消息未送达 scene={} idempotentKey={} errCode={} errMsg={}",
                    scene.getTemplateCode(), idempotentKey, result.getWxErrCode(), result.getWxErrMsg());
        }
    }

    private String resolveOpenId(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return null;
        }
        UserDTO user = apiSysUserService.getUserByUserCode(userCode.trim());
        if (user == null || StrUtil.isBlank(user.getUserWxOpenId())) {
            log.debug("订阅消息跳过：用户无 openId userCode={}", userCode);
            return null;
        }
        return user.getUserWxOpenId().trim();
    }

    private static String formatDateTime(LocalDateTime time) {
        return DATE_TIME_FMT.format(time);
    }

    private static String formatDateDot(LocalDateTime time) {
        return DATE_DOT_FMT.format(time);
    }

    /** thing 类字段最多 20 字 */
    private static String truncateThing(String value) {
        return truncate(value, 20);
    }

    /** name 类字段最多 10 字 */
    private static String truncateName(String value) {
        return truncate(value, 10);
    }

    private static String truncate(String value, int maxLen) {
        if (StrUtil.isBlank(value)) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() <= maxLen ? trimmed : trimmed.substring(0, maxLen);
    }
}
