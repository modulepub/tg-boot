-- ============================================================================
-- 清理：删除「推荐表 / 喜欢(偏好)表 / 联系人表 / 红娘关注表」中
--       所引用客户编码（cus_code）已不在 dt_customer 表的脏数据。
--
-- 说明：
--   1. 客户主键以 dt_customer.cus_code 为准；
--   2. 采用 NOT EXISTS（对 NULL 安全，且可走索引），
--      cus_code 为 NULL 的行不做删除；
--   3. 如需仅以「未逻辑删除」的客户为基准，可在子查询中加上 c.deleted = '0'；
--   4. 为物理删除（硬删除），执行前建议先用文末的 SELECT 预览数量。
-- ============================================================================

-- ---------- 1. 推荐表 dt_recommended（cus_code = 被推荐的客户） ----------
DELETE r FROM dt_recommended r
WHERE r.cus_code IS NOT NULL
  AND NOT EXISTS (
        SELECT 1 FROM dt_customer c WHERE c.cus_code = r.cus_code
  );

-- ---------- 2. 喜欢(偏好)表 dt_preference（本人 + 目标对象 任一不存在即删） ----------
DELETE p FROM dt_preference p
WHERE (
        p.preference_cus_code IS NOT NULL
        AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = p.preference_cus_code)
      )
   OR (
        p.preference_target_cus_code IS NOT NULL
        AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = p.preference_target_cus_code)
      );

-- ---------- 3. 联系人表 dt_contact（cus_code = 对方客户） ----------
DELETE ct FROM dt_contact ct
WHERE ct.cus_code IS NOT NULL
  AND NOT EXISTS (
        SELECT 1 FROM dt_customer c WHERE c.cus_code = ct.cus_code
  );

-- ---------- 4. 红娘关注表 dt_cus_matchmaker_rel（cus_code = 客户） ----------
DELETE rel FROM dt_cus_matchmaker_rel rel
WHERE rel.cus_code IS NOT NULL
  AND NOT EXISTS (
        SELECT 1 FROM dt_customer c WHERE c.cus_code = rel.cus_code
  );


-- ============================================================================
-- 执行前预览（可选）：查看各表将被删除的脏数据数量
-- ============================================================================
-- SELECT COUNT(*) AS dt_recommended_orphan
--   FROM dt_recommended r
--  WHERE r.cus_code IS NOT NULL
--    AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = r.cus_code);
--
-- SELECT COUNT(*) AS dt_preference_orphan
--   FROM dt_preference p
--  WHERE (p.preference_cus_code IS NOT NULL
--           AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = p.preference_cus_code))
--     OR (p.preference_target_cus_code IS NOT NULL
--           AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = p.preference_target_cus_code));
--
-- SELECT COUNT(*) AS dt_contact_orphan
--   FROM dt_contact ct
--  WHERE ct.cus_code IS NOT NULL
--    AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = ct.cus_code);
--
-- SELECT COUNT(*) AS dt_cus_matchmaker_rel_orphan
--   FROM dt_cus_matchmaker_rel rel
--  WHERE rel.cus_code IS NOT NULL
--    AND NOT EXISTS (SELECT 1 FROM dt_customer c WHERE c.cus_code = rel.cus_code);
