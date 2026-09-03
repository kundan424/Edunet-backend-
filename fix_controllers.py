import re

def fix_file(file_path):
    with open(file_path, "r") as f:
        content = f.read()
    
    # Fix .ok(something) -> .ok(ApiResponse.success(something))
    # Note: Because regex for balanced parens is hard, we can just replace specific lines
    
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if 'return ResponseEntity.ok(' in line and 'ApiResponse.success' not in line:
            # extract the content inside ok(...)
            # find first '(' after ok
            idx1 = line.find('ok(') + 3
            # find last ')'
            idx2 = line.rfind(')')
            inner = line[idx1:idx2]
            if inner == '':
                # .ok().body(...)
                pass
            else:
                lines[i] = line[:idx1] + 'ApiResponse.success(' + inner + ')' + line[idx2:]
        if 'return ResponseEntity.noContent().body(ApiResponse.success(null));' in line:
            lines[i] = '        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));'
            
    with open(file_path, "w") as f:
        f.write('\n'.join(lines))

import os
base_dir = r"E:\projects\E-learning\src\main\java\com\edtech\platform\course\controller"
for f in ["CourseController.java", "SectionController.java", "LessonController.java"]:
    fix_file(os.path.join(base_dir, f))
print("Fixed files")
