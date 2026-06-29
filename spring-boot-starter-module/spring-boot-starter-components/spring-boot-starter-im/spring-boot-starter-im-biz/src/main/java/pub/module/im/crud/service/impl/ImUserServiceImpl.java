package pub.module.im.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.util.WebQueryUtil;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.crud.entity.ImUser;
import pub.module.im.crud.mapper.ImUserMapper;
import pub.module.im.crud.service.ImMessageService;
import pub.module.im.crud.service.ImUserService;

@Slf4j
@Service
public class ImUserServiceImpl extends ServiceImpl<ImUserMapper, ImUser> implements ImUserService {

    private final ImMessageService imMessageService;

    public ImUserServiceImpl(ImMessageService imMessageService) {
        this.imMessageService = imMessageService;
    }

    @Override
    public ImUser getByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImUser>()
                .eq("im_user_user_code", userCode.trim())
                .eq("deleted", 0), false);
    }

    @Override
    public ImUser getValidSigByUserCode(String userCode) {
        // 本地IM模式不再需要UserSig验证
        return getByUserCode(userCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateAccount(ImAccountDTO accountDTO, String sdkAppId) {
        Assert.notNull(accountDTO, "accountDTO is null");
        Assert.notBlank(accountDTO.getUserCode(), "userCode is null");

        String userCode = accountDTO.getUserCode().trim();
        ImUser existing = getBaseMapper().selectOne(new QueryWrapper<ImUser>()
                .eq("im_user_user_code", userCode), false);
        if (existing == null) {
            ImUser row = new ImUser();
            row.setImUserCode(IdUtil.getSnowflakeNextIdStr());
            row.setImUserUserCode(userCode);
            applyAccountProfile(row, accountDTO);
            getBaseMapper().insert(row);
            return;
        }
        applyAccountProfile(existing, accountDTO);
        if (existing.getDeleted() != null && existing.getDeleted() != 0) {
            existing.setDeleted(0);
        }
        getBaseMapper().updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshUserSig(String userCode, String sdkAppId, String userSig, long expireSeconds) {
        // 本地IM实现不再需要UserSig
        log.debug("本地IM模式，跳过UserSig刷新, userCode={}", userCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUserSigByUserCode(String userCode) {
        // 本地IM实现不再需要UserSig
        log.debug("本地IM模式，跳过UserSig清理, userCode={}", userCode);
    }

    private void applyAccountProfile(ImUser row, ImAccountDTO accountDTO) {
        if (StrUtil.isNotBlank(accountDTO.getNickName())) {
            row.setImUserNickName(accountDTO.getNickName().trim());
        }
        if (StrUtil.isNotBlank(accountDTO.getAvatar())) {
            row.setImUserAvatar(accountDTO.getAvatar().trim());
        }
        if (StrUtil.isNotBlank(accountDTO.getRealName())) {
            row.setImUserRealName(accountDTO.getRealName().trim());
        }
    }

    @Override
    public IPage<ImUser> pageForMgt(String keyword, ImUser query, long pageNo, long pageSize) {
        QueryWrapper<ImUser> wrapper = query == null ? new QueryWrapper<>() : WebQueryUtil.buildQuery(query);
        wrapper.eq("deleted", 0);
        String kw = StrUtil.trim(keyword);
        if (StrUtil.isNotBlank(kw)) {
            wrapper.and(w -> w.like("im_user_nick_name", kw)
                    .or().like("im_user_real_name", kw)
                    .or().like("im_user_user_code", kw));
        }
        wrapper.orderByDesc("update_time");
        return page(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public void incrementUnreadCount(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        UpdateWrapper<ImUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("im_user_user_code", userCode.trim())
                .eq("deleted", 0);
        wrapper.setSql("im_user_unread_count = IFNULL(im_user_unread_count, 0) + 1");
        update(null, wrapper);
    }

    @Override
    public void syncUnreadCount(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        String code = userCode.trim();
        ImUser user = getByUserCode(code);
        if (user == null) {
            return;
        }
        int count = imMessageService.countUnreadByToUser(code);
        UpdateWrapper<ImUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("im_user_user_code", code)
                .eq("deleted", 0);
        wrapper.set("im_user_unread_count", count);
        update(null, wrapper);
    }
}
