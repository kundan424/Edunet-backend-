import os

path1 = 'src/main/java/com/edtech/platform/dashboard/repository/StudentDashboardRepository.java'
with open(path1, 'r') as f: c1 = f.read()
c1 = c1.replace('AVG(score)', 'AVG(percentage)')
with open(path1, 'w') as f: f.write(c1)

path2 = 'src/main/java/com/edtech/platform/dashboard/repository/InstructorDashboardRepository.java'
with open(path2, 'r') as f: c2 = f.read()
c2 = c2.replace('AVG(qa.score)', 'AVG(qa.percentage)')
with open(path2, 'w') as f: f.write(c2)
