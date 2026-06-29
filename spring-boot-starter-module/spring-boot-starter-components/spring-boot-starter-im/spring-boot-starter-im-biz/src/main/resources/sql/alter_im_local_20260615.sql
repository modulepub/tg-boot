-- =====================================================
-- IM模块重构：本地WebSocket+STOMP 数据库增量脚本
-- 日期: 2026-06-15
-- =====================================================

-- 1. 改造 im_user 表：移除腾讯云字段，增加标签字段
ALTER TABLE im_user
    DROP COLUMN IF EXISTS im_user_sdk_app_id,
    DROP COLUMN IF EXISTS im_user_sig_value,
    DROP COLUMN IF EXISTS im_user_sig_expire_time,
    ADD COLUMN im_user_tag VARCHAR(255) COMMENT '用户标签' AFTER im_user_real_name,
    ADD COLUMN im_user_unread_count INT DEFAULT 0 COMMENT '未读消息数（发给该用户且用户未读）' AFTER im_user_tag;

-- 2. 创建 im_friend 表：好友关系
CREATE TABLE IF NOT EXISTS im_friend (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    im_friend_code VARCHAR(36) UNIQUE COMMENT '业务编码',
    im_friend_user_code VARCHAR(64) NOT NULL COMMENT '用户编码',
    im_friend_friend_user_code VARCHAR(64) NOT NULL COMMENT '好友用户编码',
    im_friend_remark VARCHAR(128) COMMENT '好友备注',
    im_friend_status_code VARCHAR(32) DEFAULT '1' COMMENT '0=删除 1=正常',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    org_code VARCHAR(64) COMMENT '所属组织',
    version VARCHAR(32) COMMENT '版本',
    seq_no BIGINT COMMENT '序号',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标识',
    UNIQUE KEY uk_user_friend (im_friend_user_code, im_friend_friend_user_code)
) COMMENT 'IM-好友关系';

-- 3. 创建 im_message 表：消息记录
CREATE TABLE IF NOT EXISTS im_message (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    im_message_code VARCHAR(36) UNIQUE COMMENT '业务编码',
    im_message_conversation_code VARCHAR(64) NOT NULL COMMENT '会话编码',
    im_message_from_user_code VARCHAR(64) NOT NULL COMMENT '发送方',
    im_message_to_user_code VARCHAR(64) NOT NULL COMMENT '接收方',
    im_message_type_code VARCHAR(32) NOT NULL COMMENT 'text=文本 rich=图文',
    im_message_content TEXT COMMENT '文本内容',
    im_message_title VARCHAR(255) COMMENT '图文标题',
    im_message_image_url VARCHAR(1000) COMMENT '图文图片',
    im_message_link_url VARCHAR(2048) COMMENT '图文链接',
    im_message_read_status_code VARCHAR(32) DEFAULT '0' COMMENT '0=未读 1=已读',
    im_message_send_status_code VARCHAR(32) DEFAULT '1' COMMENT '0=失败 1=成功',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    org_code VARCHAR(64) COMMENT '所属组织',
    version VARCHAR(32) COMMENT '版本',
    seq_no BIGINT COMMENT '序号',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_conversation (im_message_conversation_code, create_time DESC),
    INDEX idx_to_user (im_message_to_user_code, im_message_read_status_code)
) COMMENT 'IM-消息记录';

-- 4. 创建 im_conversation 表：会话
CREATE TABLE IF NOT EXISTS im_conversation (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    im_conversation_code VARCHAR(64) UNIQUE COMMENT '业务编码',
    im_conversation_user_a_code VARCHAR(64) NOT NULL COMMENT '用户A',
    im_conversation_user_b_code VARCHAR(64) NOT NULL COMMENT '用户B',
    im_conversation_last_message_code VARCHAR(36) COMMENT '最后一条消息编码',
    im_conversation_unread_count_a INT DEFAULT 0 COMMENT 'A的未读数',
    im_conversation_unread_count_b INT DEFAULT 0 COMMENT 'B的未读数',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    org_code VARCHAR(64) COMMENT '所属组织',
    version VARCHAR(32) COMMENT '版本',
    seq_no BIGINT COMMENT '序号',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标识',
    UNIQUE KEY uk_users (im_conversation_user_a_code, im_conversation_user_b_code)
) COMMENT 'IM-会话';

-- 5. 创建 im_user_session 表：用户多端会话
CREATE TABLE IF NOT EXISTS im_user_session (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    im_user_session_code VARCHAR(36) UNIQUE COMMENT '业务编码',
    im_user_session_user_code VARCHAR(64) NOT NULL COMMENT '用户编码',
    im_user_session_client_id VARCHAR(64) NOT NULL COMMENT '客户端ID',
    im_user_session_device_type VARCHAR(32) COMMENT '设备类型',
    im_user_session_status_code VARCHAR(32) DEFAULT '1' COMMENT '0=离线 1=在线',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    org_code VARCHAR(64) COMMENT '所属组织',
    version VARCHAR(32) COMMENT '版本',
    seq_no BIGINT COMMENT '序号',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标识'
) COMMENT 'IM-用户多端会话';
