import psycopg2

conn = psycopg2.connect(host="127.0.0.1", port=5432, user="postgres", password="123456", dbname="yudao_store")
conn.autocommit = True
cur = conn.cursor()

cur.execute("""
SELECT id, name, parent_id, path, component, type, deleted 
FROM system_menu 
WHERE id IN (5986, 5992, 5998, 6004, 6800, 2179, 2166) 
ORDER BY id
""")
print("=== 旧菜单ID(5986,5992,5998,6004) vs 新菜单(6800,2179,2166) ===")
print("id | name | parent_id | path | component | type | deleted")
print("-" * 80)
for r in cur.fetchall():
    print(f"{r[0]} | {r[1]} | {r[2]} | {r[3]} | {r[4]} | {r[5]} | {r[6]}")
print(f"(共 {cur.rowcount} 行)")

print("\n=== 检查 parent_id=2166 的子菜单 ===")
cur.execute("""
SELECT id, name, parent_id, path, component, type 
FROM system_menu 
WHERE parent_id = 2166 AND deleted = 0
ORDER BY sort
""")
print("id | name | parent_id | path | component | type")
print("-" * 80)
for r in cur.fetchall():
    print(f"{r[0]} | {r[1]} | {r[2]} | {r[3]} | {r[4]} | {r[5]}")
print(f"(共 {cur.rowcount} 行)")

cur.close()
conn.close()