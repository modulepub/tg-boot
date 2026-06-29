package pub.module.im.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.im.crud.entity.ImConversation;
import pub.module.im.crud.mapper.ImConversationMapper;
import pub.module.im.crud.service.ImConversationService;
import pub.module.im.crud.service.ImMessageService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImConversationServiceImpl extends ServiceImpl<ImConversationMapper, ImConversation> implements ImConversationService {

    private final ImMessageService imMessageService;

    public ImConversationServiceImpl(ImMessageService imMessageService) {
        this.imMessageService = imMessageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearByUserPair(String userCodeA, String userCodeB) {
        ImConversation conv = getByUserPair(userCodeA, userCodeB);
        if (conv == null) {
            return;
        }
        imMessageService.logicDeleteByConversationCode(conv.getImConversationCode());
        conv.setImConversationLastMessageCode(null);
        conv.setImConversationUnreadCountA(0);
        conv.setImConversationUnreadCountB(0);
        updateById(conv);
    }

    @Override
    public ImConversation getByUserPair(String userACode, String userBCode) {
        if (StrUtil.isBlank(userACode) || StrUtil.isBlank(userBCode)) {
            return null;
        }
        String a = userACode.trim();
        String b = userBCode.trim();
        // 统一排序：字典序小的为A
        if (a.compareTo(b) > 0) {
            String temp = a;
            a = b;
            b = temp;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImConversation>()
                .eq("im_conversation_user_a_code", a)
                .eq("im_conversation_user_b_code", b)
                .eq("deleted", 0), false);
    }

    @Override
    public List<ImConversation> listByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return List.of();
        }
        String u = userCode.trim();
        return list(new QueryWrapper<ImConversation>()
                .and(w -> w.eq("im_conversation_user_a_code", u).or().eq("im_conversation_user_b_code", u))
                .eq("deleted", 0)
                .orderByDesc("update_time"));
    }

    @Override
    public void updateLastMessage(String conversationCode, String lastMessageCode) {
        if (StrUtil.isBlank(conversationCode) || StrUtil.isBlank(lastMessageCode)) {
            return;
        }
        UpdateWrapper<ImConversation> wrapper = new UpdateWrapper<>();
        wrapper.eq("im_conversation_code", conversationCode.trim());
        wrapper.set("im_conversation_last_message_code", lastMessageCode.trim());
        wrapper.set("update_time", LocalDateTime.now());
        update(null, wrapper);
    }

    @Override
    public void incrementUnread(String conversationCode, String forUserCode) {
        if (StrUtil.isBlank(conversationCode) || StrUtil.isBlank(forUserCode)) {
            return;
        }
        ImConversation conv = getByCode(conversationCode);
        if (conv == null) {
            return;
        }
        String field = forUserCode.trim().equals(conv.getImConversationUserACode())
                ? "im_conversation_unread_count_a"
                : "im_conversation_unread_count_b";
        UpdateWrapper<ImConversation> wrapper = new UpdateWrapper<>();
        wrapper.eq("im_conversation_code", conversationCode.trim());
        wrapper.setSql(field + " = " + field + " + 1");
        update(null, wrapper);
    }

    @Override
    public void decrementUnread(String conversationCode, String forUserCode, int count) {
        if (StrUtil.isBlank(conversationCode) || StrUtil.isBlank(forUserCode) || count <= 0) {
            return;
        }
        ImConversation conv = getByCode(conversationCode);
        if (conv == null) {
            return;
        }
        String field = forUserCode.trim().equals(conv.getImConversationUserACode())
                ? "im_conversation_unread_count_a"
                : "im_conversation_unread_count_b";
        UpdateWrapper<ImConversation> wrapper = new UpdateWrapper<>();
        wrapper.eq("im_conversation_code", conversationCode.trim());
        wrapper.setSql(field + " = GREATEST(0, " + field + " - " + count + ")");
        update(null, wrapper);
    }

    private ImConversation getByCode(String conversationCode) {
        if (StrUtil.isBlank(conversationCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImConversation>()
                .eq("im_conversation_code", conversationCode.trim())
                .eq("deleted", 0), false);
    }
}
