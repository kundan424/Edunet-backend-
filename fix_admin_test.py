import os

path = 'src/test/java/com/edtech/platform/admin/AdminCourseModerationServiceTest.java'
with open(path, 'r') as f: c = f.read()

c = c.replace('verify(eventPublisher).publishEvent(any());', 'verify(eventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(Object.class));')

with open(path, 'w') as f: f.write(c)
