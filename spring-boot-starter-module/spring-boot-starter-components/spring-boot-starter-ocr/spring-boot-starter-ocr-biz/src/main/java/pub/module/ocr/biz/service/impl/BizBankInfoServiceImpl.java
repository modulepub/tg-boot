package pub.module.ocr.biz.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.ocr.api.service.BizBankInfoService;
import pub.module.file.api.service.BizOcrService;

import java.io.File;


@Service
@Slf4j
public class BizBankInfoServiceImpl implements BizBankInfoService {
    @Override
    public BankInfo getBankInfoByBankCardNo(String bankCardNo) {
        BankInfo result  = new BankInfo();
        result.setBankCode(RandomUtil.randomString(6));
        result.setBankName("招商银行");
        result.setBankLogo("https://ts1.tc.mm.bing.net/th/id/R-C.1d5c28d8cee331d396b0c8298e2aa38a?rik=RY52gcQiXCZ5oQ&riu=http%3a%2f%2fimg3.redocn.com%2f20120514%2fRedocn_2012051407295659.jpg&ehk=Sf7PF4S7nUBO3E5ycqwTYVZEXp091DDop9QbAe6J5%2bU%3d&risl=&pid=ImgRaw&r=0");
        return result;
    }
}
