-- 从 dist_accrual 回填用户账单汇总（一次性迁移）

DELETE FROM `dist_user_bill_summary`;

INSERT INTO `dist_user_bill_summary` (
  `id`, `dist_user_bill_summary_code`, `dist_user_code`, `dist_user_nick_name`,
  `dist_inviter_user_code`, `dist_inviter_user_nick_name`, `dist_biz_line_code`,
  `dist_paid_total_amount`, `dist_in_service_total_amount`, `dist_credited_total_amount`,
  `dist_sub_paid_total_amount`, `dist_sub_in_service_total_amount`, `dist_sub_credited_total_amount`,
  `create_time`, `update_time`, `deleted`
)
SELECT
  REPLACE(UUID(), '-', ''),
  REPLACE(UUID(), '-', ''),
  agg.dist_user_code,
  COALESCE(u.`user_nick_name`, agg.dist_user_code),
  NULLIF(TRIM(u.`user_reference_user_code`), ''),
  COALESCE(inv.`user_nick_name`, NULLIF(TRIM(u.`user_reference_user_code`), '')),
  agg.dist_biz_line_code,
  COALESCE(agg.dist_paid_total_amount, 0),
  COALESCE(agg.dist_in_service_total_amount, 0),
  COALESCE(agg.dist_credited_total_amount, 0),
  COALESCE(agg.dist_sub_paid_total_amount, 0),
  COALESCE(agg.dist_sub_in_service_total_amount, 0),
  COALESCE(agg.dist_sub_credited_total_amount, 0),
  NOW(),
  NOW(),
  '0'
FROM (
  SELECT
    u.dist_user_code,
    u.dist_biz_line_code,
    payer.paid_total AS dist_paid_total_amount,
    bene.in_service AS dist_in_service_total_amount,
    bene.credited AS dist_credited_total_amount,
    sub_paid.sub_paid_total AS dist_sub_paid_total_amount,
    sub_bene.sub_in_service AS dist_sub_in_service_total_amount,
    sub_bene.sub_credited AS dist_sub_credited_total_amount
  FROM (
    SELECT DISTINCT dist_payer_user_code AS dist_user_code, dist_biz_line_code FROM dist_accrual
    UNION
    SELECT DISTINCT dist_beneficiary_user_code AS dist_user_code, dist_biz_line_code FROM dist_accrual
  ) u
  LEFT JOIN (
    SELECT dist_payer_user_code AS dist_user_code, dist_biz_line_code,
           SUM(dist_base_amount) AS paid_total
    FROM (
      SELECT dist_payer_user_code, dist_biz_line_code, dist_accrual_source_id, MAX(dist_base_amount) AS dist_base_amount
      FROM dist_accrual
      WHERE deleted = '0'
      GROUP BY dist_payer_user_code, dist_biz_line_code, dist_accrual_source_id
    ) t
    GROUP BY dist_payer_user_code, dist_biz_line_code
  ) payer ON payer.dist_user_code = u.dist_user_code AND payer.dist_biz_line_code = u.dist_biz_line_code
  LEFT JOIN (
    SELECT dist_beneficiary_user_code AS dist_user_code, dist_biz_line_code,
           SUM(CASE WHEN dist_accrual_status_code = 'pending' THEN dist_commission_amount ELSE 0 END) AS in_service,
           SUM(CASE WHEN dist_accrual_status_code = 'settled' THEN dist_commission_amount ELSE 0 END) AS credited
    FROM dist_accrual
    WHERE deleted = '0'
    GROUP BY dist_beneficiary_user_code, dist_biz_line_code
  ) bene ON bene.dist_user_code = u.dist_user_code AND bene.dist_biz_line_code = u.dist_biz_line_code
  LEFT JOIN (
    SELECT dist_beneficiary_user_code AS dist_user_code, dist_biz_line_code,
           SUM(dist_base_amount) AS sub_paid_total
    FROM (
      SELECT dist_beneficiary_user_code, dist_biz_line_code, dist_accrual_source_id, MAX(dist_base_amount) AS dist_base_amount
      FROM dist_accrual
      WHERE deleted = '0' AND dist_rule_level_code = '1'
      GROUP BY dist_beneficiary_user_code, dist_biz_line_code, dist_accrual_source_id
    ) t
    GROUP BY dist_beneficiary_user_code, dist_biz_line_code
  ) sub_paid ON sub_paid.dist_user_code = u.dist_user_code AND sub_paid.dist_biz_line_code = u.dist_biz_line_code
  LEFT JOIN (
    SELECT dist_beneficiary_user_code AS dist_user_code, dist_biz_line_code,
           SUM(CASE WHEN dist_accrual_status_code = 'pending' THEN dist_commission_amount ELSE 0 END) AS sub_in_service,
           SUM(CASE WHEN dist_accrual_status_code = 'settled' THEN dist_commission_amount ELSE 0 END) AS sub_credited
    FROM dist_accrual
    WHERE deleted = '0' AND dist_rule_level_code = '1'
    GROUP BY dist_beneficiary_user_code, dist_biz_line_code
  ) sub_bene ON sub_bene.dist_user_code = u.dist_user_code AND sub_bene.dist_biz_line_code = u.dist_biz_line_code
) agg
LEFT JOIN `sys_user` u ON u.`user_code` = agg.dist_user_code AND u.`deleted` = '0'
LEFT JOIN `sys_user` inv ON inv.`user_code` = u.`user_reference_user_code` AND inv.`deleted` = '0';
