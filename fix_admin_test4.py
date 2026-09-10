import os
path = 'src/test/java/com/edtech/platform/admin/AdminCourseModerationIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

import2 = "import com.edtech.platform.user.entity.UserStatus;\n"
if "UserStatus" not in c:
    c = c.replace("import com.edtech.platform.user.entity.User;", import2 + "import com.edtech.platform.user.entity.User;")

c = c.replace('adminUser.setRole(Role.ADMIN);', 'adminUser.setRole(Role.ADMIN);\n        adminUser.setStatus(UserStatus.ACTIVE);')
c = c.replace('instructor.setRole(Role.INSTRUCTOR);', 'instructor.setRole(Role.INSTRUCTOR);\n        instructor.setStatus(UserStatus.ACTIVE);')
c = c.replace('student.setRole(Role.STUDENT);', 'student.setRole(Role.STUDENT);\n        student.setStatus(UserStatus.ACTIVE);')

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
