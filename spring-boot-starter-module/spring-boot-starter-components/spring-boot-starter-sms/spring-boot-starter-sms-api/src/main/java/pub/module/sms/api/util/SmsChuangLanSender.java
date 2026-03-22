package pub.module.sms.api.util;


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

/**
 * 创蓝短信发送器实现
 * 优化说明：使用配置类管理账号和API地址，移除硬编码
 */
@Slf4j
@Component("smsChuangLanSender")
public class SmsChuangLanSender extends BaseSender {

    /** 接口响应成功码 */
    private static final String SUCCESS_CODE = "0";

    @Override
    protected String getCaptchaUsername() {
        return smsProperties.getChannel().getChuanglan().getCaptchaUsername();
    }

    @Override
    protected String getCaptchaPassword() {
        return smsProperties.getChannel().getChuanglan().getCaptchaPassword();
    }

    @Override
    protected String getNoticeUsername() {
        return smsProperties.getChannel().getChuanglan().getNoticeUsername();
    }

    @Override
    protected String getNoticePassword() {
        return smsProperties.getChannel().getChuanglan().getNoticePassword();
    }

    @Override
    protected String getMarketingUsername() {
        return smsProperties.getChannel().getChuanglan().getMarketingUsername();
    }

    @Override
    protected String getMarketingPassword() {
        return smsProperties.getChannel().getChuanglan().getMarketingPassword();
    }

    /**
     * 发送POST请求到创蓝短信接口
     */
    @SneakyThrows
    @Override
    protected JSONObject sendPost(String phone, String content, String username, String password) {
        JSONObject postData = buildRequestData(phone, content, username, password);

        HttpURLConnection connection = null;
        try {
            connection = createConnection();
            sendRequest(connection, postData);
            String response = readResponse(connection);

            log.info("创蓝短信接口返回数据：{}", response);
            return parseAndValidateResponse(response);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void sendSms(String phone, String content) {
        sendPost(phone, content, getNoticeUsername(), getNoticePassword());
    }

    /**
     * 构建请求数据
     */
    private JSONObject buildRequestData(String phone, String content, String username, String password) {
        JSONObject postData = new JSONObject();
        postData.put("account", username);
        postData.put("password", password);
        postData.put("msg", content);
        postData.put("phone", phone);
        postData.put("report", false);
        postData.put("sendtime", null);
        postData.put("extend", null);
        postData.put("uid", null);
        return postData;
    }

    /**
     * 创建HTTP连接
     */
    private HttpURLConnection createConnection() throws IOException {
        String apiUrl = smsProperties.getChannel().getChuanglan().getApiUrl();
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(smsProperties.getChannel().getChuanglan().getConnectTimeout());
        connection.setReadTimeout(smsProperties.getChannel().getChuanglan().getReadTimeout());
        return connection;
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
     * 解析并验证响应
     */
    private JSONObject parseAndValidateResponse(String response) {
        JSONObject result = JSONObject.parseObject(response);
        String code = result.getString("code");

        if (!SUCCESS_CODE.equals(code)) {
            String errorMsg = result.getString("errorMsg");
            log.error("创蓝短信发送失败，错误码：{}，错误信息：{}", code, errorMsg);
            throw new RuntimeException(errorMsg != null ? errorMsg : "短信发送失败");
        }

        return result;
    }
}
