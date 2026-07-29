-- =================================================================
-- 修复脚本（PostgreSQL）
-- 1. 重建 zy_store_clean_log 表（匹配后端 DO 字段）
-- 2. 给超级管理员角色分配门店菜单权限（使用显式 id）
-- =================================================================

-- 1. 重建清洗日志表（数据为空，可安全重建）
DROP TABLE IF EXISTS zy_store_clean_log;

CREATE TABLE zy_store_clean_log (
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    start_time   TIMESTAMP,
    end_time     TIMESTAMP,
    scan_count   INTEGER      NOT NULL DEFAULT 0,
    modify_count INTEGER      NOT NULL DEFAULT 0,
    status       SMALLINT     NOT NULL DEFAULT 0,
    error_msg    VARCHAR(2000),
    trigger_type SMALLINT     NOT NULL DEFAULT 1,
    creator      VARCHAR(64)  DEFAULT '',
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater      VARCHAR(64)  DEFAULT '',
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE  zy_store_clean_log IS '门店数据清洗日志表';
COMMENT ON COLUMN zy_store_clean_log.start_time IS '开始时间';
COMMENT ON COLUMN zy_store_clean_log.end_time IS '结束时间';
COMMENT ON COLUMN zy_store_clean_log.scan_count IS '扫描数量';
COMMENT ON COLUMN zy_store_clean_log.modify_count IS '修改数量';
COMMENT ON COLUMN zy_store_clean_log.status IS '执行状态（0=成功 1=失败）';
COMMENT ON COLUMN zy_store_clean_log.error_msg IS '错误信息';
COMMENT ON COLUMN zy_store_clean_log.trigger_type IS '触发方式（1=定时任务 2=手动触发）';

-- 2. 给超级管理员角色（role_id=1）分配门店菜单权限
--    使用显式 id（从 6900 开始，避免冲突）
INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6900, 1, 6800, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6800 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6901, 1, 6801, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6801 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6902, 1, 6802, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6802 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6903, 1, 6803, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6803 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6904, 1, 6804, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6804 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6905, 1, 6805, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6805 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6906, 1, 6806, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6806 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6907, 1, 6807, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6807 AND deleted = 0);

INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 6908, 1, 6808, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6808 AND deleted = 0);

-- 3. 验证
SELECT '菜单数' AS item, COUNT(*) AS cnt FROM system_menu WHERE id BETWEEN 6800 AND 6808 AND deleted = 0
UNION ALL
SELECT '角色菜单关联数', COUNT(*) FROM system_role_menu WHERE id BETWEEN 6900 AND 6908 AND deleted = 0
UNION ALL
SELECT '清洗日志表', COUNT(*) FROM zy_store_clean_log;
