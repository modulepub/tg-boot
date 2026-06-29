package pub.module.im.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.im.crud.entity.ImMessage;
import pub.module.im.crud.mapper.ImMessageMapper;
import pub.module.im.crud.service.ImMessageService;

import java.util.List;

@Service
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements ImMessageService {

    @Override
    public ImMessage getByCode(String imMessageCode) {
        if (StrUtil.isBlank(imMessageCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImMessage>()
                .eq("im_message_code", imMessageCode.trim())
                .eq("deleted", 0), false);
    }

    @Override
    public IPage<ImMessage> pageByConversation(String conversationCode, long pageNo, long pageSize) {
        return pageByConversation(conversationCode, pageNo, pageSize, false);
    }

    @Override
    public IPage<ImMessage> pageByConversation(String conversationCode, long pageNo, long pageSize, boolean asc) {
        if (StrUtil.isBlank(conversationCode)) {
            return new Page<>();
        }
        QueryWrapper<ImMessage> wrapper = new QueryWrapper<ImMessage>()
                .eq("im_message_conversation_code", conversationCode.trim())
                .eq("deleted", 0);
        if (asc) {
            wrapper.orderByAsc("create_time");
        } else {
            wrapper.orderByDesc("create_time");
        }
        return page(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public IPage<ImMessage> pageByParticipant(String userCode, long pageNo, long pageSize, boolean asc) {
        if (StrUtil.isBlank(userCode)) {
            return new Page<>();
        }
        String code = userCode.trim();
        QueryWrapper<ImMessage> wrapper = new QueryWrapper<ImMessage>()
                .and(w -> w.eq("im_message_from_user_code", code).or().eq("im_message_to_user_code", code))
                .eq("deleted", 0);
        if (asc) {
            wrapper.orderByAsc("create_time");
        } else {
            wrapper.orderByDesc("create_time");
        }
        return page(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public List<ImMessage> listUnreadByToUser(String toUserCode) {
        if (StrUtil.isBlank(toUserCode)) {
            return List.of();
        }
        return list(new QueryWrapper<ImMessage>()
                .eq("im_message_to_user_code", toUserCode.trim())
                .eq("im_message_read_status_code", "0")
                .eq("deleted", 0)
                .orderByAsc("create_time"));
    }

    @Override
    public List<ImMessage> listUnreadByFromAndTo(String fromUserCode, String toUserCode) {
        if (StrUtil.isBlank(fromUserCode) || StrUtil.isBlank(toUserCode)) {
            return List.of();
        }
        return list(new QueryWrapper<ImMessage>()
                .eq("im_message_from_user_code", fromUserCode.trim())
                .eq("im_message_to_user_code", toUserCode.trim())
                .eq("im_message_read_status_code", "0")
                .eq("deleted", 0)
                .orderByAsc("create_time"));
    }

    @Override
    public int countUnreadByToUser(String toUserCode) {
        if (StrUtil.isBlank(toUserCode)) {
            return 0;
        }
        return Math.toIntExact(count(new QueryWrapper<ImMessage>()
                .eq("im_message_to_user_code", toUserCode.trim())
                .eq("im_message_read_status_code", "0")
                .eq("deleted", 0)));
    }

    @Override
    public void logicDeleteByConversationCode(String conversationCode) {
        if (StrUtil.isBlank(conversationCode)) {
            return;
        }
        UpdateWrapper<ImMessage> wrapper = new UpdateWrapper<>();
        wrapper.eq("im_message_conversation_code", conversationCode.trim())
                .eq("deleted", 0);
        wrapper.set("deleted", 1);
        update(null, wrapper);
    }

    @Override
    public void markRead(List<String> messageCodes, String toUserCode) {
        if (messageCodes == null || messageCodes.isEmpty() || StrUtil.isBlank(toUserCode)) {
            return;
        }
        UpdateWrapper<ImMessage> wrapper = new UpdateWrapper<>();
        wrapper.in("im_message_code", messageCodes)
                .eq("im_message_to_user_code", toUserCode.trim())
                .eq("im_message_read_status_code", "0")
                .eq("deleted", 0);
        wrapper.set("im_message_read_status_code", "1");
        update(null, wrapper);
    }
}
