import os
path = 'src/test/java/com/edtech/platform/dashboard/DashboardIntegrationTest.java'
with open(path, 'r') as f: c = f.read()

c = c.replace('andExpect(jsonPath("$.data.totalRevenue", is(0.0)));', 'andExpect(jsonPath("$.data.totalRevenue", anyOf(is(0.0), is(0))));')
c = c.replace('import static org.hamcrest.Matchers.*;', 'import static org.hamcrest.Matchers.*;\nimport static org.hamcrest.core.AnyOf.anyOf;')

with open(path, 'w') as f: f.write(c)
