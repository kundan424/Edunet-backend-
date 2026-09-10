import os
path = 'src/test/java/com/edtech/platform/admin/AdminCourseModerationIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

c = c.replace('jwtService.generateToken(', 'jwtUtils.generateJwtToken(')

# Wait, we need to know the exact method in JwtUtils.
# Usually it's generateJwtToken(Authentication authentication) or generateJwtToken(UserDetailsImpl userPrincipal)
