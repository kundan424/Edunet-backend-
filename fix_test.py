import os
path = 'src/test/java/com/edtech/platform/dashboard/DashboardIntegrationTest.java'
with open(path, 'r') as f: c = f.read()

c = c.replace('import com.edtech.platform.course.entity.PublishStatus;', 'import com.edtech.platform.course.enums.PublishStatus;')
c = c.replace('import com.edtech.platform.enrollment.entity.EnrollmentStatus;', 'import com.edtech.platform.enrollment.enums.EnrollmentStatus;')

with open(path, 'w') as f: f.write(c)
