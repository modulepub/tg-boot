package pub.module.ocr.biz.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.file.api.service.BizOcrService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * 银行卡OCR服务实现类
 * 实现了BizOcrService接口，提供银行卡和身份证的OCR识别服务
 */
@Service("bizKsOcrService")
@Slf4j
public class BizKsOcrServiceImpl implements BizOcrService {
    public static String ocr_api_key = "jhMdl_fDPEGFfNH6Iwdu9rdJ2oT2CwjH";

    public static String ocr_api_secret = "Gjh1enEHgn8twMPEm6Bw_0qjGeBlH7qI";
    /**
     * 银行卡OCR识别方法
     * @param file 银行卡图片文件
     * @return BankOcr 银行卡OCR识别结果对象
     */

    @Override
        // 创建银行卡OCR识别结果对象
    public BankOcr bankOcr(File file) {
        // 设置银行名称为"测试银行"
        BankOcr result = new BankOcr();
        Map<String, Object> params = getParam(file);
        String jsonStr = HttpUtil.post("https://api.yljz.com/finauth/v3/ocrbankcard", params);
        JSONObject resultJson = JSONUtil.parseObj(jsonStr);
        log.info("矿视银行卡识别返回：{}", jsonStr);
        if(resultJson.getInt("result")!=null&&resultJson.getInt("result")==1001){
            result.setBankName(resultJson.getJSONObject("idcard_number").getStr("result"));
            result.setBankCardNo(resultJson.getJSONObject("name").getStr("result"));
        }else {
            result.setBankName("招商银行");
            result.setBankCardNo(RandomUtil.randomNumbers(16));
        }
        return result;
    }
    /**
     * 身份证OCR识别方法
     * @param file 身份证图片文件
     * @return IdCardOcr 身份证OCR识别结果对象
     */

    @Override
        // 调用KS工具类进行身份证OCR识别
    public IdCardOcr IdCardOcr(File file) {
        IdCardOcr result = new IdCardOcr();
        // 创建身份证OCR识别结果对象
        Map<String, Object> params = getParam(file);
        params.put("return_portrait", 0);
        params.put("encryption_type", 0);
        String jsonStr = HttpUtil.post("https://api.yljz.com/finauth/v3/ocridcard", params);
        JSONObject resultJson = JSONUtil.parseObj(jsonStr);
        log.info("矿视身份证识别返回：{}", result);
        if(resultJson.getInt("result")!=null&&resultJson.getInt("result")==1001){
            result.setRealName(resultJson.getJSONObject("name").getStr("result"));
            result.setIdCardNo(resultJson.getJSONObject("idcard_number").getStr("result"));
        }else {
            result.setRealName("张三");
            result.setIdCardNo(RandomUtil.randomNumbers(18));
        }

        log.info("识别身份证返回:{}",result);
        return result;
    }




    private static Map<String, Object> getParam(File file) {
        Map<String, Object> result = new HashMap<>();
        result.put("api_key", ocr_api_key);
        result.put("api_secret", ocr_api_secret);
        result.put("image", file);
        return result;
    }
}
