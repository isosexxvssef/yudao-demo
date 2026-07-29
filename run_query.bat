@echo off
set PGPASSWORD=123456
D:\PostgreSQL\18\bin\psql.exe -h 127.0.0.1 -U postgres -d yudao_store -f E:\code\project\yudao-demo\menu_conflict.sql