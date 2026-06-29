-- 批次已标记完成、但关联结算记录订单状态未冗余为已完成时，按批次完成时间回填
UPDATE `dist_user_bill_settle_record` r
INNER JOIN `dist_settle_batch` b ON r.`dist_settle_batch_code` = b.`dist_settle_batch_code`
SET r.`dist_settled_status_code` = '1',
    r.`dist_settle_batch_status_code` = '1',
    r.`dist_settled_at` = COALESCE(r.`dist_settled_at`, b.`dist_settled_at`),
    r.`dist_in_service_status_code` = '0',
    r.`update_time` = NOW()
WHERE b.`dist_settled_status_code` = '1'
  AND b.`deleted` = '0'
  AND r.`deleted` = '0'
  AND r.`dist_settled_status_code` = '0';
