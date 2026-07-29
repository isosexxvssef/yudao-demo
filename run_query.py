import psycopg2, sys

conn = psycopg2.connect(host="127.0.0.1", port=5432, user="postgres", password="123456", dbname="yudao_store")
conn.autocommit = True
cur = conn.cursor()

queries = [
    ("=== 查询1: 旧门店管理菜单的子菜单 ===",
     "SELECT id, name, parent_id, path, component, type FROM system_menu WHERE parent_id IN (5986, 5992, 5998, 6004) AND deleted = 0 ORDER BY parent_id, sort;"),
    ("=== 查询2: 租户角色分配的菜单 ===",
     "SELECT rm.role_id, rm.menu_id, m.name, m.parent_id, m.path FROM system_role_menu rm JOIN system_menu m ON rm.menu_id = m.id WHERE rm.role_id IN (156, 157) AND rm.deleted = 0 ORDER BY rm.role_id, m.parent_id, m.sort;"),
    ("=== 查询3: 搜索码头管理相关菜单 ===",
     "SELECT id, name, parent_id, path FROM system_menu WHERE name LIKE '%码头%' OR name LIKE '%管理%' ORDER BY id LIMIT 50;"),
]

for title, sql in queries:
    print(f"\n{title}")
    print("-" * 60)
    cur.execute(sql)
    cols = [desc[0] for desc in cur.description]
    print(" | ".join(cols))
    print("-" * 60)
    for row in cur.fetchall():
        print(" | ".join(str(v) for v in row))
    print(f"(共 {cur.rowcount} 行)")

cur.close()
conn.close()
print("\n查询执行完毕。")