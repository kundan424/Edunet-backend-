$ErrorActionPreference = 'Stop'
$regBody = '{"name":"Test User","email":"testd@example.com","password":"password123","role":"INSTRUCTOR"}'
Write-Host "Registering user..."
$reg = curl.exe -s -o NUL -w "%{http_code}" -X POST -H "Content-Type: application/json" -d $regBody http://localhost:8080/api/v1/auth/register
Write-Host "Register: $reg"

Write-Host "Logging in..."
$loginBody = '{"email":"testd@example.com","password":"password123"}'
$loginResp = curl.exe -s -X POST -H "Content-Type: application/json" -d $loginBody http://localhost:8080/api/v1/auth/login
Write-Host "Login: $loginResp"

$token = ($loginResp | ConvertFrom-Json).data.token

Write-Host "Me (No JWT)..."
$meNo = curl.exe -s -o NUL -w "%{http_code}" -X GET http://localhost:8080/api/v1/users/me
Write-Host "Me (No Auth): $meNo"

Write-Host "Me (With JWT)..."
$meAuth = curl.exe -s -o NUL -w "%{http_code}" -X GET -H "Authorization: Bearer $token" http://localhost:8080/api/v1/users/me
Write-Host "Me (Auth): $meAuth"
