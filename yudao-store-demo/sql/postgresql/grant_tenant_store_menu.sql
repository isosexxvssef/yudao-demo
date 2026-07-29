-- 给租户A(123)的租户管理员角色(156)和租户B(124)的租户管理员角色(157)分配门店菜单权限
-- 菜单ID: 6800-6808
-- 使用显式 ID（当前 max=6908，从 6910 开始）

-- 角色 156（租户A 的租户管理员）
INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 6910 + m.idx, 156, m.menu_id, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
FROM (VALUES (0, 6800),(1, 6801),(2, 6802),(3, 6803),(4, 6804),(5, 6805),(6, 6806),(7, 6807),(8, 6808)) AS m(idx, menu_id)
WHERE NOT EXISTS (
    SELECT 1 FROM system_role_menu rm
    WHERE rm.role_id = 156 AND rm.menu_id = m.menu_id AND rm.deleted = 0
);

-- 角色 157（租户B 的租户管理员）
INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 6920 + m.idx, 157, m.menu_id, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
FROM (VALUES (0, 6800),(1, 6801),(2, 6802),(3, 6803),(4, 6804),(5, 6805),(6, 6806),(7, 6807),(8, 6808)) AS m(idx, menu_id)
WHERE NOT EXISTS (
    SELECT 1 FROM system_role_menu rm
    WHERE rm.role_id = 157 AND rm.menu_id = m.menu_id AND rm.deleted = 0
);
