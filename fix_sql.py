import os

repos = [
    'src/main/java/com/edtech/platform/dashboard/repository/StudentDashboardRepository.java',
    'src/main/java/com/edtech/platform/dashboard/repository/InstructorDashboardRepository.java',
    'src/main/java/com/edtech/platform/dashboard/repository/AdminDashboardRepository.java'
]

for path in repos:
    with open(path, 'r') as f:
        c = f.read()
    c = c.replace('is_passed = true', 'passed = true')
    c = c.replace('is_passed = ?', 'passed = ?')
    c = c.replace('is_completed = true', "status = 'COMPLETED'")
    c = c.replace('qa.is_completed = true', "qa.status = 'COMPLETED'")
    with open(path, 'w') as f:
        f.write(c)
