import os
path = 'src/test/java/com/edtech/platform/dashboard/DashboardIntegrationTest.java'
with open(path, 'r') as f: c = f.read()
c = c.replace('\\n', '\n').replace('\\\\', '\\')
with open(path, 'w') as f: f.write(c)
