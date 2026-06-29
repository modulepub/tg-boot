-- mock 婚介公司信息修正（营业执照 / 法人 / 电话 / 地址）
-- 适用：mock seed 导入的公司，及历史成都 mock 数据

UPDATE `dt_matchmaking_company`
SET
    `mk_company_usci_code` = '91110000MA0000000X',
    `mk_company_legal_name` = '张三',
    `mk_company_tel` = '010-12345678',
    `mk_company_address_detail` = '中国某省某市某区示例路 1 号',
    `mk_company_address_lat_lon` = '116.397128,39.916527',
    `update_time` = NOW()
WHERE `deleted` = '0'
  AND (
        `mk_company_name` = '示例科技有限公司'
        OR `mk_company_tel` = '021-87654321'
        OR `mk_company_legal_name` = '李四'
        OR `mk_company_address_detail` LIKE '%示例路%示例婚恋%'
        OR `mk_company_usci_code` LIKE '91110000MA0%'
    );
