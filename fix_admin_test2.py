import os
path = 'src/test/java/com/edtech/platform/admin/AdminCourseModerationIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

import1 = "import com.edtech.platform.common.security.UserDetailsImpl;\nimport org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n"
if "UserDetailsImpl" not in c:
    c = c.replace("import com.edtech.platform.common.security.JwtUtils;", import1 + "import com.edtech.platform.common.security.JwtUtils;")

c = c.replace("jwtUtils.generateToken(adminUser)", "jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(adminUser), null, UserDetailsImpl.build(adminUser).getAuthorities()))")
c = c.replace("jwtUtils.generateToken(instructor)", "jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(instructor), null, UserDetailsImpl.build(instructor).getAuthorities()))")
c = c.replace("jwtUtils.generateToken(student)", "jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(student), null, UserDetailsImpl.build(student).getAuthorities()))")

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
