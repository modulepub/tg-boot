package pub.module.affines.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.api.service.ApiAfChildProfileService;
import pub.module.affines.api.service.ApiAfParentFollowService;
import pub.module.affines.api.service.dto.AfChildProfileDTO;
import pub.module.affines.api.service.dto.AfParentFollowDTO;
import pub.module.affines.crud.entity.AfChildProfile;
import pub.module.affines.crud.entity.AfParentFollow;
import pub.module.affines.crud.service.AfChildProfileService;
import pub.module.affines.crud.service.AfParentFollowService;
import pub.module.common.enums.StatusCodeEnum;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiAfParentFollowServiceImpl implements ApiAfParentFollowService {

    @Resource
    private AfParentFollowService afParentFollowService;
    @Resource
    private AfChildProfileService afChildProfileService;
    @Resource
    private ApiAfChildProfileService apiAfChildProfileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void follow(String followerUserCode, String targetChildProfileCode) {
        Assert.notBlank(followerUserCode, "关注者用户编码不能为空");
        Assert.notBlank(targetChildProfileCode, "资料卡编码不能为空");
        AfChildProfile profile = afChildProfileService.getByCode(targetChildProfileCode.trim());
        Assert.notNull(profile, "资料卡不存在");
        AfParentFollow existing = afParentFollowService.getByFollowerAndTarget(
                followerUserCode.trim(), targetChildProfileCode.trim());
        if (existing != null) {
            existing.setAfFollowStatusCode(StatusCodeEnum.YES);
            afParentFollowService.updateById(existing);
            return;
        }
        AfParentFollow follow = new AfParentFollow();
        follow.setAfFollowerUserCode(followerUserCode.trim());
        follow.setAfTargetChildProfileCode(targetChildProfileCode.trim());
        follow.setAfFollowStatusCode(StatusCodeEnum.YES);
        afParentFollowService.save(follow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollow(String followerUserCode, String targetChildProfileCode) {
        Assert.notBlank(followerUserCode, "关注者用户编码不能为空");
        Assert.notBlank(targetChildProfileCode, "资料卡编码不能为空");
        AfParentFollow existing = afParentFollowService.getByFollowerAndTarget(
                followerUserCode.trim(), targetChildProfileCode.trim());
        if (existing == null) {
            return;
        }
        existing.setAfFollowStatusCode(StatusCodeEnum.NO);
        afParentFollowService.updateById(existing);
    }

    @Override
    public boolean isFollowing(String followerUserCode, String targetChildProfileCode) {
        if (StrUtil.isBlank(followerUserCode) || StrUtil.isBlank(targetChildProfileCode)) {
            return false;
        }
        AfParentFollow existing = afParentFollowService.getByFollowerAndTarget(
                followerUserCode.trim(), targetChildProfileCode.trim());
        return existing != null && StatusCodeEnum.YES.equals(existing.getAfFollowStatusCode());
    }

    @Override
    public List<AfParentFollowDTO> listMyFollows(String followerUserCode) {
        Assert.notBlank(followerUserCode, "关注者用户编码不能为空");
        List<AfParentFollow> follows = afParentFollowService.lambdaQuery()
                .eq(AfParentFollow::getAfFollowerUserCode, followerUserCode.trim())
                .eq(AfParentFollow::getAfFollowStatusCode, StatusCodeEnum.YES)
                .orderByDesc(AfParentFollow::getUpdateTime)
                .list();
        return follows.stream().map(this::toDto).collect(Collectors.toList());
    }

    private AfParentFollowDTO toDto(AfParentFollow follow) {
        AfParentFollowDTO dto = BeanUtil.copyProperties(follow, AfParentFollowDTO.class);
        if (StrUtil.isNotBlank(follow.getAfTargetChildProfileCode())) {
            AfChildProfileDTO profile = apiAfChildProfileService.getDetailByCode(follow.getAfTargetChildProfileCode());
            dto.setTargetProfile(profile);
        }
        return dto;
    }
}
