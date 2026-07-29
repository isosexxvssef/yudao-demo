$psqlPath = "D:\PostgreSQL\18\bin\psql.exe"
$host = "127.0.0.1"
$port = "5432"
$username = "postgres"
$password = "123456"
$database = "yudao_store"
$sqlFile = "E:\code\project\yudao-demo\menu_conflict.sql"

$env:PGPASSWORD = $password

Write-Host "正在执行 SQL 查询..." -ForegroundColor Cyan
Write-Host "SQL 文件: $sqlFile" -ForegroundColor Gray
Write-Host ""

& $psqlPath -h $host -p $port -U $username -d $database -f $sqlFile

$env:PGPASSWORD = $null

Write-Host ""
Write-Host "查询执行完毕。" -ForegroundColor Cyan