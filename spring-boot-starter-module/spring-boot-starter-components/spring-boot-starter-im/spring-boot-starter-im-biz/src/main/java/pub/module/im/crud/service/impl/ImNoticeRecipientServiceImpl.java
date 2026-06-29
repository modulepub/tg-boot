package pub.module.im.crud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.im.crud.entity.ImNoticeRecipient;
import pub.module.im.crud.mapper.ImNoticeRecipientMapper;
import pub.module.im.crud.service.ImNoticeRecipientService;

@Service
public class ImNoticeRecipientServiceImpl extends ServiceImpl<ImNoticeRecipientMapper, ImNoticeRecipient>
        implements ImNoticeRecipientService {
}
