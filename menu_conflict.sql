-- 查询1: 查找旧"门店管理"菜单(ID 5986, 5992, 5998, 6004)的所有子菜单
\echo '=== 查询1: 旧门店管理菜单的子菜单 ==='
SELECT id, name, parent_id, path, component, type FROM system_menu WHERE parent_id IN (5986, 5992, 5998, 6004) AND deleted = 0 ORDER BY parent_id, sort;

-- 查询2: 分配给租户角色的菜单(156=租户A, 157=租户B)
\echo ''
\echo '=== 查询2: 租户角色分配的菜单 ==='
SELECT rm.role_id, rm.menu_id, m.name, m.parent_id, m.path 
FROM system_role_menu rm 
JOIN system_menu m ON rm.menu_id = m.id 
WHERE rm.role_id IN (156, 157) AND rm.deleted = 0 
ORDER BY rm.role_id, m.parent_id, m.sort;

-- 查询3: 查找与"码头管理"或"码头"相关的菜单
\echo ''
\echo '=== 查询3: 搜索码头管理相关菜单 ==='
SELECT id, name, parent_id, path FROM system_menu WHERE name LIKE '%码头%' OR name LIKE '%管理%' ORDER BY id LIMIT 50;