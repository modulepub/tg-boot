package pub.module.sms.api.util;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * 玄武短信发送器实现
 * 优化说明：使用配置类管理账号和API地址，移除硬编码
 */
@Slf4j
@Component("smsXuanWuSender")
public class SmsXuanWuSender extends BaseSender {

    /** 消息类型 */
    private static final String MSG_TYPE = "sms";

    @Override
    protected String getCaptchaUsername() {
        return smsProperties.getChannel().getXuanwu().getCaptchaUsername();
    }

    @Override
    protected String getCaptchaPassword() {
        return smsProperties.getChannel().getXuanwu().getCaptchaPassword();
    }

    @Override
    protected String getNoticeUsername() {
        return smsProperties.getChannel().getXuanwu().getNoticeUsername();
    }

    @Override
    protected String getNoticePassword() {
        return smsProperties.getChannel().getXuanwu().getNoticePassword();
    }

    @Override
    protected String getMarketingUsername() {
        return smsProperties.getChannel().getXuanwu().getMarketingUsername();
    }

    @Override
    protected String getMarketingPassword() {
        return smsProperties.getChannel().getXuanwu().getMarketingPassword();
    }

    /**
     * 发送POST请求到玄武短信接口
     */
    @SneakyThrows
    @Override
    protected JSONObject sendPost(String phone, String content, String username, String password) {
        JSONObject postData = buildRequestData(phone, content);

        HttpURLConnection connection = null;
        try {
            connection = createConnection(username, password);
            sendRequest(connection, postData);
            String response = readResponse(connection);

            log.info("玄武短信接口返回数据：{}", response);
            return parseResponse(response);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 构建请求数据
     */
    private JSONObject buildRequestData(String phone, String content) {
        JSONObject item = new JSONObject();
        item.put("to", phone);
        item.put("content", content);

        List<JSONObject> itemList = Collections.singletonList(item);

        JSONObject postData = new JSONObject();
        postData.put("items", itemList);
        postData.put("msgType", MSG_TYPE);

        return postData;
    }

    /**
     * 创建HTTP连接并设置认证信息
     */
    private HttpURLConnection createConnection(String username, String password) throws IOException {
        String apiUrl = smsProperties.getChannel().getXuanwu().getApiUrl();
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", generateAuthHeader(username, password));
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(smsProperties.getChannel().getXuanwu().getConnectTimeout());
        connection.setReadTimeout(smsProperties.getChannel().getXuanwu().getReadTimeout());

        return connection;
    }

    /**
     * 生成认证Header
     */
    private String generateAuthHeader(String username, String password) {
        String md5Password = MD5.create().digestHex(password);
        String authString = String.format("%s:%s", username, md5Password);
        return Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 发送请求数据
     */
    private void sendRequest(HttpURLConnection connection, JSONObject postData) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] requestBytes = postData.toJSONString().getBytes(StandardCharsets.UTF_8);
            os.write(requestBytes);
            os.flush();
        }
    }

    /**
     * 读取响应数据
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    /**
     * 解析响应
     */
    private JSONObject parseResponse(String response) {
        try {
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            log.error("解析玄武短信接口响应失败，响应内容：{}", response, e);
            throw new RuntimeException("短信接口响应解析失败");
        }
    }
}
