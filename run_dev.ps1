$job = Start-Job {
    cd E:\projects\E-learning
    .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
}

Write-Host "Waiting for application to start..."
Start-Sleep -Seconds 20

Write-Host "Fetching logs from job..."
Receive-Job -Job $job

Write-Host "Stopping application..."
Stop-Job -Job $job
Remove-Job -Job $job -Force
