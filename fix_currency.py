import os
path = 'src/test/java/com/edtech/platform/dashboard/DashboardIntegrationTest.java'
with open(path, 'r') as f: c = f.read()

c = c.replace('p1.setProvider("STRIPE");', 'p1.setProvider("STRIPE");\n        p1.setCurrency("usd");')
c = c.replace('p2.setProvider("STRIPE");', 'p2.setProvider("STRIPE");\n        p2.setCurrency("usd");')

with open(path, 'w') as f: f.write(c)
