-- 给租户套餐(id=111, 普通套餐)添加门店菜单权限(6800-6808)
-- menu_ids 字段是 JSON 数组，需要追加门店菜单 ID

-- 使用 jsonb 方式追加门店菜单 ID
UPDATE system_tenant_package
SET menu_ids = (
    SELECT jsonb_agg(elem ORDER BY elem::int)
    FROM (
        SELECT jsonb_array_elements_text(menu_ids::jsonb) AS elem
        FROM system_tenant_package WHERE id = 111
        UNION
        SELECT '6800' UNION SELECT '6801' UNION SELECT '6802' UNION SELECT '6803'
        UNION SELECT '6804' UNION SELECT '6805' UNION SELECT '6806'
        UNION SELECT '6807' UNION SELECT '6808'
    ) t
)
WHERE id = 111;
