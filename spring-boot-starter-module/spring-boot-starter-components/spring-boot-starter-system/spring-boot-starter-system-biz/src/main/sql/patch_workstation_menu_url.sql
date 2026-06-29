-- 工作台菜单：配置说明页路由，点击「工作台」进入开发者指引页
UPDATE `sys_permission`
SET `per_url` = 'workstation/index',
    `update_time` = NOW()
WHERE `per_code` = 'workstation'
  AND `per_parent_code` = 'manage'
  AND (`per_url` IS NULL OR `per_url` = '');
