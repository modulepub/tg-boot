-- 以 sys_user.user_org_code 为当前登录/切换部门，修复历史脏数据

-- 1. 为「运营部」补全机构编码
UPDATE sys_organization
SET org_code = 'A02A03'
WHERE id = '2069097397408288769'
  AND (org_code IS NULL OR org_code = '');

-- 2. 修复用户-机构-角色关联中的空编码
UPDATE sys_user_organization
SET org_code = 'A02A03'
WHERE (org_code IS NULL OR org_code = '')
  AND deleted = '0';

-- 3. 同步当前登录部门 user_org_code（权限/菜单按此字段加载）
UPDATE sys_user u
INNER JOIN sys_user_organization uo ON uo.user_code = u.user_code AND uo.deleted = '0' AND uo.org_code = 'A02A03'
SET u.user_org_code = 'A02A03'
WHERE u.user_org_code IS NULL OR u.user_org_code = '';
