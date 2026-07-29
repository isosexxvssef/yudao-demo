$env:PGPASSWORD="123456"
$out = & "D:\PostgreSQL\18\bin\psql.exe" -h 127.0.0.1 -U postgres -d yudao_store -c "SELECT id, name, parent_id FROM system_menu WHERE id BETWEEN 6800 AND 6810 ORDER BY id;"
Write-Output $out
