-- 修正 role_menu 记录的 tenant_id，使其与角色所属租户一致
-- Role 156 属于租户 123，Role 157 属于租户 124
UPDATE system_role_menu SET tenant_id = 123 WHERE role_id = 156 AND menu_id BETWEEN 6800 AND 6808;
UPDATE system_role_menu SET tenant_id = 124 WHERE role_id = 157 AND menu_id BETWEEN 6800 AND 6808;
