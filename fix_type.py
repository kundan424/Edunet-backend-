import os
path = 'src/test/java/com/edtech/platform/dashboard/DashboardIntegrationTest.java'
with open(path, 'r') as f: c = f.read()

c = c.replace('import com.edtech.platform.notification.entity.Notification;', 'import com.edtech.platform.notification.entity.Notification;\\nimport com.edtech.platform.notification.enums.NotificationType;')
c = c.replace('n.setType("GENERAL");', 'n.setType(NotificationType.COURSE_ENROLLED);')

with open(path, 'w') as f: f.write(c)
