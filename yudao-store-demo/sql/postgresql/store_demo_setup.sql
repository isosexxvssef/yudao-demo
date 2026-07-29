-- =================================================================
-- 门店管理功能 SQL 脚本（PostgreSQL）
-- 数据库：yudao_store
--
-- 包含内容：
--   1. 门店表 zy_store（含 tenant_id，多租户隔离）
--   2. 门店清洗日志表 zy_store_clean_log（系统级，跨租户）
--   3. 字典 store_status（营业/停业）
--   4. 菜单权限（门店管理 + 清洗日志）
--   5. 两个测试租户（租户A / 租户B）
--
-- 注意：本脚本可重复执行（已做幂等处理）。
-- =================================================================

-- =================================================================
-- 1. 门店表 zy_store
-- =================================================================
CREATE TABLE IF NOT EXISTS zy_store (
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    store_code  VARCHAR(64)  NOT NULL,
    store_name  VARCHAR(128) NOT NULL,
    platform    VARCHAR(64),
    city        VARCHAR(64),
    manager     VARCHAR(64),
    phone       VARCHAR(64),
    status      VARCHAR(16),
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    creator     VARCHAR(64)  DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE  zy_store IS '门店表';
COMMENT ON COLUMN zy_store.id IS '主键';
COMMENT ON COLUMN zy_store.store_code IS '门店编码，租户内唯一';
COMMENT ON COLUMN zy_store.store_name IS '门店名称';
COMMENT ON COLUMN zy_store.platform IS '平台';
COMMENT ON COLUMN zy_store.city IS '城市';
COMMENT ON COLUMN zy_store.manager IS '负责人';
COMMENT ON COLUMN zy_store.phone IS '联系电话';
COMMENT ON COLUMN zy_store.status IS '营业状态（字典 store_status）';
COMMENT ON COLUMN zy_store.tenant_id IS '多租户编号';
COMMENT ON COLUMN zy_store.deleted IS '是否删除';

-- 租户内门店编码唯一索引（仅未删除数据）
CREATE UNIQUE INDEX IF NOT EXISTS uk_zy_store_tenant_code
    ON zy_store (tenant_id, store_code)
    WHERE deleted = 0;

-- =================================================================
-- 2. 门店清洗日志表 zy_store_clean_log（系统级，不参与多租户）
-- =================================================================
CREATE TABLE IF NOT EXISTS zy_store_clean_log (
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

-- =================================================================
-- 3. 字典 store_status（营业/停业）
-- =================================================================
-- 字典类型
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT '门店营业状态', 'store_status', 0, '门店营业状态（营业/停业）', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE type = 'store_status' AND deleted = 0);

-- 字典数据：营业 = 1
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 1, '营业', '1', 'store_status', 0, 'success', '', '营业中', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'store_status' AND value = '1' AND deleted = 0);

-- 字典数据：停业 = 0
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 2, '停业', '0', 'store_status', 0, 'danger', '', '已停业', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'store_status' AND value = '0' AND deleted = 0);

-- =================================================================
-- 4. 菜单权限
-- =================================================================
-- 4.1 门店管理（一级目录）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6800, '门店管理', '', 1, 30, 0, '/store', 'ep:shop', NULL, NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6800);

-- 4.2 门店列表（菜单）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6801, '门店列表', 'store:store:query', 2, 1, 6800, 'store', 'ep:shop', 'store/store/index', 'Store', 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6801);

-- 4.3 门店创建（按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6802, '门店创建', 'store:store:create', 3, 2, 6801, '', '', '', NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6802);

-- 4.4 门店更新（按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6803, '门店更新', 'store:store:update', 3, 3, 6801, '', '', '', NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6803);

-- 4.5 门店删除（按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6804, '门店删除', 'store:store:delete', 3, 4, 6801, '', '', '', NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6804);

-- 4.6 门店导入（按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6805, '门店导入', 'store:store:import', 3, 5, 6801, '', '', '', NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6805);

-- 4.7 门店导出（按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6806, '门店导出', 'store:store:export', 3, 6, 6801, '', '', '', NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6806);

-- 4.8 数据清洗日志（菜单）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6807, '数据清洗日志', 'store:clean:query', 2, 2, 6800, 'clean', 'ep:brush', 'store/clean/index', 'StoreCleanLog', 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6807);

-- 4.9 清洗触发（按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 6808, '清洗触发', 'store:clean:trigger', 3, 1, 6807, '', '', '', NULL, 0, true, true, true, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 6808);

-- =================================================================
-- 5. 序列创建（PostgreSQL 需要，供 @KeySequence 使用）
-- =================================================================
CREATE SEQUENCE IF NOT EXISTS zy_store_seq;
SELECT setval('zy_store_seq', COALESCE((SELECT MAX(id) FROM zy_store), 1));
CREATE SEQUENCE IF NOT EXISTS zy_store_clean_log_seq;
SELECT setval('zy_store_clean_log_seq', COALESCE((SELECT MAX(id) FROM zy_store_clean_log), 1));

-- =================================================================
-- 6. 测试租户（租户A / 租户B）
--    说明：租户需通过 UI 创建。此处仅做名称规范化。
--    当前测试租户 ID：租户A=123，租户B=124（请根据实际情况调整）。
-- =================================================================
UPDATE system_tenant SET name = '租户A', contact_name = '租户A管理员', status = 0
WHERE id = 123 AND deleted = 0;

UPDATE system_tenant SET name = '租户B', contact_name = '租户B管理员', status = 0
WHERE id = 124 AND deleted = 0;

-- =================================================================
-- 7. 给「超级管理员」角色（id=1）分配门店相关菜单权限
--    使得租户 1 的超级管理员可以访问门店管理与清洗日志。
-- =================================================================
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6800, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6800 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6801, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6801 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6802, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6802 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6803, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6803 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6804, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6804 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6805, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6805 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6806, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6806 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6807, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6807 AND deleted = 0);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, 6808, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id = 1 AND menu_id = 6808 AND deleted = 0);

-- =================================================================
-- 8. 给租户套餐（id=111, 普通套餐）追加门店菜单权限
--    menu_ids 字段是 JSON 数组，追加 6800-6808
-- =================================================================
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

-- =================================================================
-- 9. 给租户A(123)的租户管理员角色(156)和租户B(124)的租户管理员角色(157)
--    分配门店菜单权限（注意：role_menu 有租户隔离，tenant_id 必须匹配）
--    使用显式 ID（请根据 system_role_menu 的 max(id) 调整起始值）
-- =================================================================
-- 角色 156（租户A 的租户管理员，tenant_id=123）
INSERT INTO system_role_menu (id, role_id, menu_id, tenant_id, creator, create_time, updater, update_time, deleted)
SELECT 6910 + m.idx, 156, m.menu_id, 123, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
FROM (VALUES (0, 6800),(1, 6801),(2, 6802),(3, 6803),(4, 6804),(5, 6805),(6, 6806),(7, 6807),(8, 6808)) AS m(idx, menu_id)
WHERE NOT EXISTS (
    SELECT 1 FROM system_role_menu rm
    WHERE rm.role_id = 156 AND rm.menu_id = m.menu_id AND rm.deleted = 0
);

-- 角色 157（租户B 的租户管理员，tenant_id=124）
INSERT INTO system_role_menu (id, role_id, menu_id, tenant_id, creator, create_time, updater, update_time, deleted)
SELECT 6920 + m.idx, 157, m.menu_id, 124, 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 0
FROM (VALUES (0, 6800),(1, 6801),(2, 6802),(3, 6803),(4, 6804),(5, 6805),(6, 6806),(7, 6807),(8, 6808)) AS m(idx, menu_id)
WHERE NOT EXISTS (
    SELECT 1 FROM system_role_menu rm
    WHERE rm.role_id = 157 AND rm.menu_id = m.menu_id AND rm.deleted = 0
);

-- =================================================================
-- 脚本执行完毕
--
-- 后续步骤：
--   1. 重启后端服务（让 ignore-tables 配置生效）
--   2. 清空 Redis 缓存（FLUSHDB），确保权限缓存刷新
--   3. 登录管理后台，切换到租户A（123）或租户B（124）测试门店管理功能
-- =================================================================
