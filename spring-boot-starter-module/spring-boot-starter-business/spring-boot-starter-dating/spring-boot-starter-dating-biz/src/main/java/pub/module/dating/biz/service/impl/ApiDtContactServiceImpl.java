package pub.module.dating.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtContactService;
import pub.module.dating.crud.entity.DtContact;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.service.DtContactApplyService;
import pub.module.dating.crud.service.DtContactService;
import pub.module.im.api.service.ApiImService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * Api 联系人 Service
 *
 * @author tg
 */
@Slf4j
@Service
public class ApiDtContactServiceImpl implements ApiDtContactService {

    @Resource
    DtContactService dtContactService;
    @Resource
    DtContactApplyService dtContactApplyService;
    @Resource
    ApiImService apiImService;

    @Override
    public boolean isMutualContact(String userCodeA, String userCodeB) {
        String normalizedA = StrUtil.trim(userCodeA);
        String normalizedB = StrUtil.trim(userCodeB);
        if (StrUtil.hasBlank(normalizedA, normalizedB) || StrUtil.equals(normalizedA, normalizedB)) {
            return false;
        }
        boolean aToB = dtContactService.count(new QueryWrapper<DtContact>().lambda()
                .eq(DtContact::getUserCode, normalizedA)
                .eq(DtContact::getCusUserCode, normalizedB)) > 0;
        boolean bToA = dtContactService.count(new QueryWrapper<DtContact>().lambda()
                .eq(DtContact::getUserCode, normalizedB)
                .eq(DtContact::getCusUserCode, normalizedA)) > 0;
        return aToB && bToA;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(RemoveDTO removeDTO) {
        Assert.notNull(removeDTO, "参数不能为空");
        DtContact dtContact = dtContactService.getByCode(removeDTO.getContactCode());
        Assert.notNull(dtContact, "联系人不存在");

        UserDTO userDTO = UserUtil.getCurrentSysUser();
        Assert.isTrue(StrUtil.equals(userDTO.getUserCode(), dtContact.getUserCode()), "无权操作该联系人");

        String ownerUserCode = StrUtil.trim(dtContact.getUserCode());
        String peerUserCode = StrUtil.trim(dtContact.getCusUserCode());
        if (StrUtil.isAllNotBlank(ownerUserCode, peerUserCode)) {
            apiImService.removeFriendBidirectional(ownerUserCode, peerUserCode);
            apiImService.clearC2cChatBidirectional(ownerUserCode, peerUserCode);
            try {
                apiImService.notifyContactRemoved(ownerUserCode, peerUserCode);
            }
            catch (Exception ex) {
                log.warn("联系人解除通知发送失败 owner={} peer={}: {}", ownerUserCode, peerUserCode, ex.getMessage());
            }
        }

        dtContactService.remove(new QueryWrapper<DtContact>().lambda()
                .eq(DtContact::getContactApplyCode, dtContact.getContactApplyCode()));
        dtContactApplyService.remove(new QueryWrapper<DtContactApply>().lambda()
                .eq(DtContactApply::getContactApplyCode, dtContact.getContactApplyCode()));
    }
}
