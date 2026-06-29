-- 为已有会员套餐商品初始化权益配置（standardMember=20, premiumMember=40, diamondMember=60）
INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313827189555301', 1, '0', '206096313827189555301', 'standardMember', 'addFriendNum', 20, '每日添加好友次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'standardMember' AND `td_gd_bnf_key` = 'addFriendNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313827189555302', 2, '0', '206096313827189555302', 'standardMember', 'recNum', 20, '每日推荐次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'standardMember' AND `td_gd_bnf_key` = 'recNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313827189555303', 3, '0', '206096313827189555303', 'standardMember', 'matchNum', 20, '每日牵线次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'standardMember' AND `td_gd_bnf_key` = 'matchNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313827189555401', 1, '0', '206096313827189555401', 'premiumMember', 'addFriendNum', 40, '每日添加好友次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'premiumMember' AND `td_gd_bnf_key` = 'addFriendNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313827189555402', 2, '0', '206096313827189555402', 'premiumMember', 'recNum', 40, '每日推荐次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'premiumMember' AND `td_gd_bnf_key` = 'recNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313827189555403', 3, '0', '206096313827189555403', 'premiumMember', 'matchNum', 40, '每日牵线次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'premiumMember' AND `td_gd_bnf_key` = 'matchNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313832222720101', 1, '0', '206096313832222720101', 'diamondMember', 'addFriendNum', 60, '每日添加好友次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'diamondMember' AND `td_gd_bnf_key` = 'addFriendNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313832222720102', 2, '0', '206096313832222720102', 'diamondMember', 'recNum', 60, '每日推荐次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'diamondMember' AND `td_gd_bnf_key` = 'recNum');

INSERT INTO `td_goods_benefit` (`id`, `seq_no`, `deleted`, `td_gd_bnf_code`, `td_gd_code`, `td_gd_bnf_key`, `td_gd_bnf_value`, `td_gd_bnf_desc`)
SELECT '206096313832222720103', 3, '0', '206096313832222720103', 'diamondMember', 'matchNum', 60, '每日牵线次数'
WHERE NOT EXISTS (SELECT 1 FROM `td_goods_benefit` WHERE `td_gd_code` = 'diamondMember' AND `td_gd_bnf_key` = 'matchNum');
