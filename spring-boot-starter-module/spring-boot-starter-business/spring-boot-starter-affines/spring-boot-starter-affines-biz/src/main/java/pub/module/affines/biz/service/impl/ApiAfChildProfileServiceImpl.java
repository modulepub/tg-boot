package pub.module.affines.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.api.service.ApiAfChildProfileService;
import pub.module.affines.api.service.dto.AfChildIntentionDTO;
import pub.module.affines.api.service.dto.AfChildProfileDTO;
import pub.module.affines.crud.entity.AfChildIntention;
import pub.module.affines.crud.entity.AfChildProfile;
import pub.module.affines.crud.service.AfChildIntentionService;
import pub.module.affines.crud.service.AfChildProfileService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiAfChildProfileServiceImpl implements ApiAfChildProfileService {

    @Resource
    private AfChildProfileService afChildProfileService;
    @Resource
    private AfChildIntentionService afChildIntentionService;

    @Override
    public List<AfChildProfileDTO> listByParentUserCode(String parentUserCode) {
        Assert.notBlank(parentUserCode, "家长用户编码不能为空");
        List<AfChildProfile> profiles = afChildProfileService.lambdaQuery()
                .eq(AfChildProfile::getAfParentUserCode, parentUserCode.trim())
                .orderByDesc(AfChildProfile::getCreateTime)
                .list();
        return profiles.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public AfChildProfileDTO getDetailByCode(String afChildProfileCode) {
        AfChildProfile profile = afChildProfileService.getByCode(afChildProfileCode);
        if (profile == null) {
            return null;
        }
        return toDto(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AfChildProfileDTO saveProfile(String parentUserCode, AfChildProfileDTO dto) {
        Assert.notBlank(parentUserCode, "家长用户编码不能为空");
        Assert.notNull(dto, "资料卡不能为空");
        AfChildProfile profile = BeanUtil.copyProperties(dto, AfChildProfile.class);
        profile.setAfParentUserCode(parentUserCode.trim());
        profile.setAfChildProfileCode(null);
        afChildProfileService.save(profile);
        saveOrUpdateIntention(parentUserCode, profile.getAfChildProfileCode(), dto.getIntention());
        return getDetailByCode(profile.getAfChildProfileCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AfChildProfileDTO updateProfile(String parentUserCode, AfChildProfileDTO dto) {
        Assert.notBlank(parentUserCode, "家长用户编码不能为空");
        Assert.notNull(dto, "资料卡不能为空");
        Assert.notBlank(dto.getAfChildProfileCode(), "资料卡编码不能为空");
        AfChildProfile existing = afChildProfileService.getByCode(dto.getAfChildProfileCode());
        Assert.notNull(existing, "资料卡不存在");
        Assert.isTrue(parentUserCode.trim().equals(existing.getAfParentUserCode()), "无权编辑该资料卡");
        AfChildProfile profile = BeanUtil.copyProperties(dto, AfChildProfile.class);
        profile.setId(existing.getId());
        profile.setAfParentUserCode(existing.getAfParentUserCode());
        afChildProfileService.updateById(profile);
        saveOrUpdateIntention(parentUserCode, existing.getAfChildProfileCode(), dto.getIntention());
        return getDetailByCode(existing.getAfChildProfileCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProfile(String parentUserCode, String afChildProfileCode) {
        Assert.notBlank(parentUserCode, "家长用户编码不能为空");
        Assert.notBlank(afChildProfileCode, "资料卡编码不能为空");
        AfChildProfile existing = afChildProfileService.getByCode(afChildProfileCode.trim());
        Assert.notNull(existing, "资料卡不存在");
        Assert.isTrue(parentUserCode.trim().equals(existing.getAfParentUserCode()), "无权删除该资料卡");
        AfChildIntention intention = afChildIntentionService.getByChildProfileCode(afChildProfileCode.trim());
        if (intention != null) {
            afChildIntentionService.removeById(intention.getId());
        }
        afChildProfileService.removeById(existing.getId());
    }

    private void saveOrUpdateIntention(String parentUserCode, String afChildProfileCode, AfChildIntentionDTO intentionDto) {
        if (intentionDto == null) {
            return;
        }
        AfChildIntention existing = afChildIntentionService.getByChildProfileCode(afChildProfileCode);
        AfChildIntention intention = BeanUtil.copyProperties(intentionDto, AfChildIntention.class);
        intention.setAfChildProfileCode(afChildProfileCode);
        intention.setAfParentUserCode(parentUserCode.trim());
        if (existing == null) {
            intention.setAfChildIntentionCode(null);
            afChildIntentionService.save(intention);
        } else {
            intention.setId(existing.getId());
            intention.setAfChildIntentionCode(existing.getAfChildIntentionCode());
            afChildIntentionService.updateById(intention);
        }
    }

    private AfChildProfileDTO toDto(AfChildProfile profile) {
        AfChildProfileDTO dto = BeanUtil.copyProperties(profile, AfChildProfileDTO.class);
        AfChildIntention intention = afChildIntentionService.getByChildProfileCode(profile.getAfChildProfileCode());
        if (intention != null) {
            dto.setIntention(BeanUtil.copyProperties(intention, AfChildIntentionDTO.class));
        }
        return dto;
    }
}
