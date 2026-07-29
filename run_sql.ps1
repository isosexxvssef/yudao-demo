# =============================================
# PowerShell 包装脚本：执行 SQL 文件
# =============================================

$psqlPath = "D:\PostgreSQL\18\bin\psql.exe"
$host = "127.0.0.1"
$port = "5432"
$user = "postgres"
$password = "123456"
$database = "yudao_store"
$sqlFile = "E:\code\project\yudao-demo\fix_menu_conflict.sql"

# 设置环境变量以避免密码提示
$env:PGPASSWORD = $password

Write-Host "============================================" -ForegroundColor Cyan
Write-Host " 菜单冲突修复脚本 - 开始执行" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 检查 psql 是否存在
if (-not (Test-Path $psqlPath)) {
    Write-Host "[错误] psql 不存在: $psqlPath" -ForegroundColor Red
    exit 1
}

# 检查 SQL 文件是否存在
if (-not (Test-Path $sqlFile)) {
    Write-Host "[错误] SQL 文件不存在: $sqlFile" -ForegroundColor Red
    exit 1
}

# 执行 SQL 文件
Write-Host "[执行] 正在执行 SQL 文件: $sqlFile" -ForegroundColor Yellow
Write-Host ""

& $psqlPath -h $host -p $port -U $user -d $database -f $sqlFile

$exitCode = $LASTEXITCODE

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
if ($exitCode -eq 0) {
    Write-Host " 执行完成，退出码: $exitCode (成功)" -ForegroundColor Green
} else {
    Write-Host " 执行完成，退出码: $exitCode (有错误)" -ForegroundColor Red
}
Write-Host "============================================" -ForegroundColor Cyan

# 清理环境变量
Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue