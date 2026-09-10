import os
path = 'src/test/java/com/edtech/platform/admin/AdminCourseModerationIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

c = c.replace("jwtService.generateToken(adminUser)", "jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(adminUser), null, UserDetailsImpl.build(adminUser).getAuthorities()))")
c = c.replace("jwtService.generateToken(instructor)", "jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(instructor), null, UserDetailsImpl.build(instructor).getAuthorities()))")
c = c.replace("jwtService.generateToken(student)", "jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(student), null, UserDetailsImpl.build(student).getAuthorities()))")

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
