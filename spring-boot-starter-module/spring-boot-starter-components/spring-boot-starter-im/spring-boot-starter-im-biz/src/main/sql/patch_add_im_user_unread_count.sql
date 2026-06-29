-- IM 用户表增加未读消息统计冗余（管理端消息记录角标）
ALTER TABLE im_user
    ADD COLUMN im_user_unread_count INT DEFAULT 0 COMMENT '未读消息数（发给该用户且用户未读）' AFTER im_user_tag;

-- 回填历史未读数
UPDATE im_user u
SET im_user_unread_count = (
    SELECT COUNT(*)
    FROM im_message m
    WHERE m.im_message_to_user_code = u.im_user_user_code
      AND m.im_message_read_status_code = '0'
      AND m.deleted = 0
);
