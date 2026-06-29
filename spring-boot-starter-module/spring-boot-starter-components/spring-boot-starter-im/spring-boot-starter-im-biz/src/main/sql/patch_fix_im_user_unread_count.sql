-- 修正 im_user_unread_count 统计口径：发给该用户且用户未读
UPDATE im_user u
SET im_user_unread_count = (
    SELECT COUNT(*)
    FROM im_message m
    WHERE m.im_message_to_user_code = u.im_user_user_code
      AND m.im_message_read_status_code = '0'
      AND m.deleted = 0
);
