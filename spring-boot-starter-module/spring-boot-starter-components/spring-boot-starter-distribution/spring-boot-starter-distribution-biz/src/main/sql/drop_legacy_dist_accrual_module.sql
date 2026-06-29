-- 下线旧分佣规则/计提/服务期及提现审核菜单（已由用户账单汇总+结算记录替代）

DELETE FROM `sys_role_permission` WHERE `permission_code` IN (
  'distributionDistRuleIndex',
  'distributionDistAccrualIndex',
  'distributionWalWithdrawIndex'
);

DELETE FROM `sys_permission` WHERE `code` IN (
  'distributionDistRuleIndex',
  'distributionDistAccrualIndex',
  'distributionWalWithdrawIndex'
);

DROP TABLE IF EXISTS `dist_service_period`;
DROP TABLE IF EXISTS `dist_accrual`;
DROP TABLE IF EXISTS `dist_rule`;
