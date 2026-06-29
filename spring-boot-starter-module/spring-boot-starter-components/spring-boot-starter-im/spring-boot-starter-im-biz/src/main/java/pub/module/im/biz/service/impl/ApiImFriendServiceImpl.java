package pub.module.im.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.im.api.constants.ImFriendStatusCodeEnum;
import pub.module.im.api.service.ApiImFriendService;
import pub.module.im.api.service.dto.ImAddFriendDTO;
import pub.module.im.crud.entity.ImFriend;
import pub.module.im.crud.service.ImFriendService;

import java.util.List;

@Slf4j
@Service
public class ApiImFriendServiceImpl implements ApiImFriendService {

    @Resource
    private ImFriendService imFriendService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFriend(ImAddFriendDTO addFriendDTO) {
        Assert.notNull(addFriendDTO, "addFriendDTO is null");
        Assert.notBlank(addFriendDTO.getFromUserCode(), "fromUserCode is null");
        Assert.notBlank(addFriendDTO.getToUserCode(), "toUserCode is null");
        String from = addFriendDTO.getFromUserCode().trim();
        String to = addFriendDTO.getToUserCode().trim();
        Assert.isTrue(!from.equals(to), "不能添加自己为好友");

        ensureFriendRelation(from, to);
        ensureFriendRelation(to, from);
    }

    @Override
    public boolean isFriend(String userCodeA, String userCodeB) {
        if (StrUtil.isBlank(userCodeA) || StrUtil.isBlank(userCodeB)) {
            return false;
        }
        ImFriend friend = imFriendService.getByUserAndFriend(userCodeA.trim(), userCodeB.trim());
        return friend != null && ImFriendStatusCodeEnum.NORMAL.getCode().equals(friend.getImFriendStatusCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFriendBidirectional(String userCodeA, String userCodeB) {
        Assert.notBlank(userCodeA, "userCodeA is null");
        Assert.notBlank(userCodeB, "userCodeB is null");
        imFriendService.removeFriendBidirectional(userCodeA.trim(), userCodeB.trim());
    }

    @Override
    public List<?> listFriends(String userCode) {
        Assert.notBlank(userCode, "userCode is null");
        return imFriendService.listByUserCode(userCode.trim());
    }

    private void ensureFriendRelation(String userCode, String friendUserCode) {
        ImFriend existing = imFriendService.getByUserAndFriend(userCode, friendUserCode);
        if (existing == null) {
            ImFriend friend = new ImFriend();
            friend.setImFriendCode(IdUtil.getSnowflakeNextIdStr());
            friend.setImFriendUserCode(userCode);
            friend.setImFriendFriendUserCode(friendUserCode);
            friend.setImFriendStatusCode(ImFriendStatusCodeEnum.NORMAL.getCode());
            imFriendService.save(friend);
        } else if (ImFriendStatusCodeEnum.DELETED.getCode().equals(existing.getImFriendStatusCode())) {
            existing.setImFriendStatusCode(ImFriendStatusCodeEnum.NORMAL.getCode());
            imFriendService.updateById(existing);
        }
    }
}
