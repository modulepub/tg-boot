package pub.module.sms.biz.service.impl;

import com.xuanwu.mos.MessageData;
import com.xuanwu.mos.PostMsg;
import com.xuanwu.mos.PostMsgBuilder;
import com.xuanwu.mos.common.entity.Account;
import com.xuanwu.mos.common.entity.GsmsResponse;
import com.xuanwu.mos.common.entity.MTPack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.sms.api.service.BizSmsService;

import java.util.ArrayList;

@Slf4j
@Service("mosSmsSdk")
public class SpiMosSmsServiceImpl implements BizSmsService {
    @Override
    public void sendSms(SendSmsDTO sendSmsDTO) {
        final PostMsg pm = PostMsgBuilder.getInstance()
                .setShortConnMode(false)
                .setSoTimeout(300000)
                .setMaxConnForMT(2)
                .setMaxConnForMO(2)
                .setEnableSsl(false)
                .build();
        Account account = new Account("xx@xx", "xx");
        try {
            log.info("发送短信{}", sendSmsDTO);
            MTPack pack = new MTPack();
            pack.setBatchName("短信测试");
            pack.setMsgType(MTPack.MsgType.SMS);
            pack.setDistinctFlag(true); // 是否进行号码去重
            ArrayList<MessageData> msgList = new ArrayList<>();
            pack.setSendType(MTPack.SendType.GROUP);
            msgList.add(new MessageData(sendSmsDTO.getMobile(), sendSmsDTO.getContent()));
            pack.setMsgs(msgList);
            //pack.setTemplateNo("8973febf65e144d492d070dc8c55b46c");
            GsmsResponse resp = pm.post(account, pack);
            System.out.println(resp);
        } catch (Exception e) {
            log.error("发送短信失败{}", sendSmsDTO, e);
        }
    }

}
