package pub.module.im.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAddFriendDTO;
import pub.module.im.api.service.dto.ImAccountDTO;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
public class ApiImTencentServiceImpl implements ApiImService {

    private static final String DEFAULT_SDK_APP_ID = "1600081016";
    private static final String DEFAULT_IDENTIFIER = "administrator";
    private static final String DEFAULT_SDK_SECRET_KEY = "520b95dc88c8bd5aeac091cc50dfc4a6c55dc19fd172a9944c4251e2ec208838";
    private static final long DEFAULT_USER_SIG_EXPIRE = 180L * 24 * 60 * 60;

    private volatile TencentImConfig tencentImConfig;

    @Override
    public void saveOrUpdateAccount(ImAccountDTO accountDTO) {
        Assert.notNull(accountDTO, "accountDTO is null");
        String userCode = accountDTO.getUserCode();
        String nickName = accountDTO.getNickName();
        String avatar = accountDTO.getAvatar();
        Assert.notBlank(userCode, "userCode is null");
        TencentImConfig config = getTencentImConfig();

        if (isImported(userCode, config)) {
            updateProfile(userCode, nickName, avatar, config);
            return;
        }
        importAccount(userCode, nickName, avatar, config);
    }

    @Override
    public void addFriend(ImAddFriendDTO addFriendDTO) {
        Assert.notNull(addFriendDTO, "addFriendDTO is null");
        Assert.notBlank(addFriendDTO.getFromUserCode(), "fromUserCode is null");
        Assert.notBlank(addFriendDTO.getToUserCode(), "toUserCode is null");

        TencentImConfig config = getTencentImConfig();

        String url = buildUrl("sns/friend_add", config);
        JSONObject body = new JSONObject();
        body.set("From_Account", addFriendDTO.getFromUserCode());
        body.set("AddType", "Add_Type_Both");
        body.set("ForceAddFlags", 1);

        JSONObject addItem = new JSONObject();
        addItem.set("To_Account", addFriendDTO.getToUserCode());
        if (StrUtil.isNotBlank(addFriendDTO.getRemark())) {
            addItem.set("Remark", addFriendDTO.getRemark());
        }
        if (StrUtil.isNotBlank(addFriendDTO.getAddWording())) {
            addItem.set("AddWording", addFriendDTO.getAddWording());
        }

        String addSource = addFriendDTO.getAddSource();
        if (StrUtil.isBlank(addSource)) {
            addSource = "AddSource_Type_Android";
        }
        addItem.set("AddSource", addSource);

        JSONArray addFriendItem = new JSONArray();
        addFriendItem.add(addItem);
        body.set("AddFriendItem", addFriendItem);
        postJson(url, body, "腾讯云IM添加好友");
    }

    @Override
    public String generateUserSig(String userCode) {
        Assert.notBlank(userCode, "userCode is null");
        TencentImConfig config = getTencentImConfig();
        return genUserSig(userCode, config.sdkAppId, DEFAULT_SDK_SECRET_KEY, DEFAULT_USER_SIG_EXPIRE);
    }

    @Override
    public void sendC2CTextMessage(String fromUserCode, String toUserCode, String text) {
        Assert.notBlank(fromUserCode, "fromUserCode is null");
        Assert.notBlank(toUserCode, "toUserCode is null");
        Assert.notBlank(text, "text is null");
        TencentImConfig config = getTencentImConfig();
        String url = buildUrl("openim/sendmsg", config);
        JSONObject body = new JSONObject();
        body.set("SyncOtherMachine", 2);
        body.set("From_Account", fromUserCode);
        body.set("To_Account", toUserCode);
        body.set("MsgRandom", (int) (System.currentTimeMillis() & 0x7fffffff));

        JSONObject msgBody = new JSONObject();
        msgBody.set("MsgType", "TIMTextElem");
        JSONObject msgContent = new JSONObject();
        msgContent.set("Text", text);
        msgBody.set("MsgContent", msgContent);

        JSONArray msgBodyList = new JSONArray();
        msgBodyList.add(msgBody);
        body.set("MsgBody", msgBodyList);
        postJson(url, body, "腾讯云IM发送C2C文本消息");
    }

    @Override
    public void sendC2CRichMessage(String fromUserCode, String toUserCode, String title, String imageUrl, String linkUrl) {
        Assert.notBlank(fromUserCode, "fromUserCode is null");
        Assert.notBlank(toUserCode, "toUserCode is null");
        Assert.notBlank(title, "title is null");
        Assert.notBlank(imageUrl, "imageUrl is null");
        Assert.notBlank(linkUrl, "linkUrl is null");
        TencentImConfig config = getTencentImConfig();
        String url = buildUrl("openim/sendmsg", config);

        JSONObject customPayload = new JSONObject();
        customPayload.set("type", "rich_link");
        customPayload.set("title", title);
        customPayload.set("imageUrl", imageUrl);
        customPayload.set("linkUrl", linkUrl);

        JSONObject msgBody = new JSONObject();
        msgBody.set("MsgType", "TIMCustomElem");
        JSONObject msgContent = new JSONObject();
        msgContent.set("Data", customPayload.toString());
        msgContent.set("Desc", "[图文消息]");
        msgBody.set("MsgContent", msgContent);

        JSONArray msgBodyList = new JSONArray();
        msgBodyList.add(msgBody);

        JSONObject body = new JSONObject();
        body.set("SyncOtherMachine", 2);
        body.set("From_Account", fromUserCode);
        body.set("To_Account", toUserCode);
        body.set("MsgRandom", (int) (System.currentTimeMillis() & 0x7fffffff));
        body.set("MsgBody", msgBodyList);
        postJson(url, body, "腾讯云IM发送C2C图文消息");
    }

    private boolean isImported(String userCode, TencentImConfig config) {
        String url = buildUrl("im_open_login_svc/account_check", config);
        JSONObject body = new JSONObject();
        JSONArray checkItems = new JSONArray();
        JSONObject checkItem = new JSONObject();
        checkItem.set("UserID", userCode);
        checkItems.add(checkItem);
        body.set("CheckItem", checkItems);
        JSONObject result = postJson(url, body, "查询腾讯云IM账号");
        JSONArray resultItems = result.getJSONArray("ResultItem");
        if (resultItems == null || resultItems.isEmpty()) {
            return false;
        }
        JSONObject first = resultItems.getJSONObject(0);
        return "Imported".equalsIgnoreCase(first.getStr("AccountStatus"));
    }

    private void importAccount(String userCode, String nickName, String avatar, TencentImConfig config) {
        String url = buildUrl("im_open_login_svc/account_import", config);
        JSONObject body = new JSONObject();
        body.set("Identifier", userCode);
        if (StrUtil.isNotBlank(nickName)) {
            body.set("Nick", nickName);
        }
        if (StrUtil.isNotBlank(avatar)) {
            body.set("FaceUrl", avatar);
        }
        postJson(url, body, "创建腾讯云IM账号");
    }

    private void updateProfile(String userCode, String nickName, String avatar, TencentImConfig config) {
        String url = buildUrl("profile/portrait_set", config);
        JSONObject body = new JSONObject();
        body.set("From_Account", userCode);
        List<JSONObject> profileItem = new ArrayList<>();
        if (StrUtil.isNotBlank(nickName)) {
            JSONObject item = new JSONObject();
            item.set("Tag", "Tag_Profile_IM_Nick");
            item.set("Value", nickName);
            profileItem.add(item);
        }
        if (StrUtil.isNotBlank(avatar)) {
            JSONObject item = new JSONObject();
            item.set("Tag", "Tag_Profile_IM_Image");
            item.set("Value", avatar);
            profileItem.add(item);
        }
        if (profileItem.isEmpty()) {
            log.info("腾讯云IM账号已存在，未传入昵称与头像，跳过资料更新: {}", userCode);
            return;
        }
        body.set("ProfileItem", profileItem);
        postJson(url, body, "更新腾讯云IM账号资料");
    }

    private JSONObject postJson(String url, JSONObject body, String bizName) {
        log.info("腾讯云IM请求开始, bizName={}, url={}, body={}", bizName, url, body);
        String resp = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .execute()
                .body();
        log.info("腾讯云IM请求结束, bizName={}, url={}, resp={}", bizName, url, resp);
        Assert.notBlank(resp, bizName + "失败: 响应为空");
        JSONObject result = JSONUtil.parseObj(resp);
        Integer errorCode = result.getInt("ErrorCode");
        if (errorCode != null && errorCode != 0) {
            String errorInfo = result.getStr("ErrorInfo");
            throw new IllegalArgumentException(bizName + "失败: " + errorInfo);
        }
        return result;
    }

    private String buildUrl(String servicePath, TencentImConfig config) {
        return "https://console.tim.qq.com/v4/" + servicePath
                + "?sdkappid=" + config.sdkAppId
                + "&identifier=" + config.identifier
                + "&usersig=" + config.userSig
                + "&random=" + config.random
                + "&contenttype=json";
    }

    private TencentImConfig getTencentImConfig() {
        TencentImConfig cachedConfig = tencentImConfig;
        if (cachedConfig != null) {
            return cachedConfig;
        }
        synchronized (this) {
            cachedConfig = tencentImConfig;
            if (cachedConfig != null) {
                return cachedConfig;
            }
            cachedConfig = loadTencentImConfig();
            tencentImConfig = cachedConfig;
            return cachedConfig;
        }
    }

    private TencentImConfig loadTencentImConfig() {
        String userSig = buildUserSig(
                DEFAULT_SDK_APP_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_SDK_SECRET_KEY,
                DEFAULT_USER_SIG_EXPIRE
        );
        String random = String.valueOf(System.currentTimeMillis());
        return new TencentImConfig(DEFAULT_SDK_APP_ID, DEFAULT_IDENTIFIER, userSig, random);
    }

    private String buildUserSig(String sdkAppId, String identifier, String sdkSecretKey, long userSigExpire) {
        Assert.notBlank(sdkAppId, "腾讯云IM配置缺失: sdkAppId");
        Assert.notBlank(identifier, "腾讯云IM配置缺失: identifier");
        Assert.notBlank(sdkSecretKey, "腾讯云IM配置缺失: sdkSecretKey");
        return genUserSig(identifier, sdkAppId, sdkSecretKey, userSigExpire);
    }

    private String genUserSig(String identifier, String sdkAppId, String secretKey, long expire) {
        try {
            long currTime = System.currentTimeMillis() / 1000;
            JSONObject sigDoc = new JSONObject();
            sigDoc.set("TLS.ver", "2.0");
            sigDoc.set("TLS.identifier", identifier);
            sigDoc.set("TLS.sdkappid", sdkAppId);
            sigDoc.set("TLS.expire", expire);
            sigDoc.set("TLS.time", currTime);

            String contentToBeSigned =
                    "TLS.identifier:" + identifier + "\n"
                            + "TLS.sdkappid:" + sdkAppId + "\n"
                            + "TLS.time:" + currTime + "\n"
                            + "TLS.expire:" + expire + "\n";
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            String sig = Base64.getEncoder().encodeToString(mac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8)));
            sigDoc.set("TLS.sig", sig);

            byte[] compressed;
            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                 DeflaterOutputStream deflater = new DeflaterOutputStream(output)) {
                deflater.write(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
                deflater.finish();
                compressed = output.toByteArray();
            }
            String userSig = Base64.getEncoder().encodeToString(compressed);
            return userSig.replace("+", "*").replace("/", "-").replace("=", "_");
        } catch (Exception e) {
            throw new IllegalArgumentException("生成腾讯云IM UserSig失败", e);
        }
    }

    private record TencentImConfig(String sdkAppId, String identifier, String userSig, String random) {
    }
}
