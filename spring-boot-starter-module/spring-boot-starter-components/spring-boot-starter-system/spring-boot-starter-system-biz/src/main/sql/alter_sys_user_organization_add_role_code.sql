-- 合并 sys_user_organization_role 到 sys_user_organization

ALTER TABLE sys_user_organization
    ADD COLUMN role_code varchar(255) NULL COMMENT '角色编码' AFTER org_code;

-- 迁移角色数据（以角色表为准，每条机构+角色一条记录）
INSERT INTO sys_user_organization (id, seq_no, org_code, role_code, update_by, update_time, create_by, create_time, version, deleted, user_org_code, user_code)
SELECT r.id,
       r.seq_no,
       r.org_code,
       r.role_code,
       r.update_by,
       r.update_time,
       r.create_by,
       r.create_time,
       r.version,
       r.deleted,
       r.user_org_role_code,
       r.user_code
FROM sys_user_organization_role r
WHERE r.deleted = '0'
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_organization uo
    WHERE uo.id = r.id
);

-- 删除无角色编码的旧关联（已由角色表迁移覆盖）
DELETE FROM sys_user_organization WHERE role_code IS NULL OR role_code = '';

DROP TABLE IF EXISTS sys_user_organization_role;
