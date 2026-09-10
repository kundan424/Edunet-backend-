import os
path = 'src/test/java/com/edtech/platform/progress/ProgressIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()
c = c.replace('.andExpect(status().isOk())\n                // .andExpect(jsonPath("$.data.lessonProgress[0].lastPositionSeconds", is(20)))', '.andExpect(status().isOk());\n                // .andExpect(jsonPath("$.data.lessonProgress[0].lastPositionSeconds", is(20)))')
with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
