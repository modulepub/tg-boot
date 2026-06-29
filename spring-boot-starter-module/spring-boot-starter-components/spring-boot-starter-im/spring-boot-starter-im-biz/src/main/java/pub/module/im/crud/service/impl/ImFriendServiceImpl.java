package pub.module.im.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.im.crud.entity.ImFriend;
import pub.module.im.crud.mapper.ImFriendMapper;
import pub.module.im.crud.service.ImFriendService;

import java.util.List;

@Service
public class ImFriendServiceImpl extends ServiceImpl<ImFriendMapper, ImFriend> implements ImFriendService {

    @Override
    public ImFriend getByUserAndFriend(String userCode, String friendUserCode) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(friendUserCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImFriend>()
                .eq("im_friend_user_code", userCode.trim())
                .eq("im_friend_friend_user_code", friendUserCode.trim())
                .eq("deleted", 0), false);
    }

    @Override
    public List<ImFriend> listByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return List.of();
        }
        return list(new QueryWrapper<ImFriend>()
                .eq("im_friend_user_code", userCode.trim())
                .eq("im_friend_status_code", "1")
                .eq("deleted", 0)
                .orderByDesc("create_time"));
    }

    @Override
    public void removeFriendBidirectional(String userCodeA, String userCodeB) {
        if (StrUtil.isBlank(userCodeA) || StrUtil.isBlank(userCodeB)) {
            return;
        }
        String a = userCodeA.trim();
        String b = userCodeB.trim();
        // 删除 A->B
        ImFriend friendAB = getByUserAndFriend(a, b);
        if (friendAB != null) {
            friendAB.setImFriendStatusCode("0");
            updateById(friendAB);
        }
        // 删除 B->A
        ImFriend friendBA = getByUserAndFriend(b, a);
        if (friendBA != null) {
            friendBA.setImFriendStatusCode("0");
            updateById(friendBA);
        }
    }
}
