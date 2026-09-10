import os
path = 'src/test/java/com/edtech/platform/admin/AdminCourseModerationIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

# We replace the setUp to register users via auth API, or generate token manually
# Since Admin requires Role.ADMIN, and register might not allow Role.ADMIN easily, wait! RegisterRequest allows role?
# Let's see if we can register an ADMIN.
