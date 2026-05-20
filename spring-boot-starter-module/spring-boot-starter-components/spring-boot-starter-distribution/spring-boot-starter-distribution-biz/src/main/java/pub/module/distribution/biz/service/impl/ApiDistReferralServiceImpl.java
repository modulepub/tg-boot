package pub.module.distribution.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.distribution.api.constants.DistRefBindSourceCodeEnum;
import pub.module.distribution.api.constants.DistRefBindStatusCodeEnum;
import pub.module.distribution.api.service.ApiDistReferralService;
import pub.module.distribution.curd.entity.DistRefBind;
import pub.module.distribution.curd.mapper.DistRefBindMapper;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ApiDistReferralServiceImpl implements ApiDistReferralService {

    @Resource
    private DistRefBindMapper distRefBindMapper;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindInviteeOnRegister(UserDTO invitee, String inviterUserCode, String distBizLineCode) {
        if (invitee == null || StrUtil.isBlank(invitee.getUserCode()) || StrUtil.isBlank(inviterUserCode)) {
            return;
        }
        if (Objects.equals(invitee.getUserCode(), inviterUserCode)) {
            return;
        }
        UserDTO inviter = apiSysUserService.getUserByUserCode(inviterUserCode);
        if (inviter == null) {
            return;
        }
        long exists = distRefBindMapper.selectCount(new QueryWrapper<DistRefBind>().lambda()
                .eq(DistRefBind::getDistBizLineCode, distBizLineCode)
                .eq(DistRefBind::getDistInviteeUserCode, invitee.getUserCode()));
        if (exists > 0) {
            return;
        }
        DistRefBind bind = new DistRefBind();
        bind.setId(IdUtil.getSnowflakeNextIdStr());
        bind.setDistRefBindCode(IdUtil.getSnowflakeNextIdStr());
        bind.setDistBizLineCode(distBizLineCode);
        bind.setDistInviteeUserCode(invitee.getUserCode());
        bind.setDistInviterUserCode(inviterUserCode);
        bind.setDistRefBindSourceCode(DistRefBindSourceCodeEnum.REGISTER.getCode());
        bind.setDistRefBindStatusCode(DistRefBindStatusCodeEnum.VALID.getCode());
        bind.setDistBindTime(LocalDateTime.now());
        distRefBindMapper.insert(bind);
    }
}
