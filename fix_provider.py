import os
path = 'src/test/java/com/edtech/platform/dashboard/DashboardIntegrationTest.java'
with open(path, 'r') as f: c = f.read()

c = c.replace('p1.setAmount(BigDecimal.valueOf(100.00));', 'p1.setAmount(BigDecimal.valueOf(100.00));\\n        p1.setProvider("STRIPE");')
c = c.replace('p2.setAmount(BigDecimal.valueOf(100.00));', 'p2.setAmount(BigDecimal.valueOf(100.00));\\n        p2.setProvider("STRIPE");')
c = c.replace('n.setTitle("Hello");', 'n.setTitle("Hello");\\n        n.setType("GENERAL");')

with open(path, 'w') as f: f.write(c)
