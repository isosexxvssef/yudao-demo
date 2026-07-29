-- =============================================
-- 修复菜单冲突：软删除旧的"门店管理"菜单
-- =============================================

-- 1. 软删除旧的冲突菜单（deleted = 0 的记录）
UPDATE system_menu SET deleted = 1 WHERE id IN (5986, 5992, 5998, 6004) AND deleted = 0;

-- 2. 验证修复结果
SELECT id, name, parent_id, path, component, deleted FROM system_menu WHERE id IN (5986, 5992, 5998, 6004, 6800, 6801, 6807);

-- 3. 检查是否有其他路径冲突的菜单
SELECT id, name, parent_id, path, component, deleted FROM system_menu WHERE path = 'store' OR path = '/store' OR path LIKE '%store%' ORDER BY id;