import os
import re

base_dir = r"E:\projects\E-learning\src\main\java\com\edtech\platform\course\controller"

for file_name in ["CourseController.java", "SectionController.java", "LessonController.java"]:
    file_path = os.path.join(base_dir, file_name)
    with open(file_path, "r") as f:
        content = f.read()
    
    # add import
    if "ApiResponse" not in content:
        content = content.replace("import org.springframework.web.bind.annotation.*;", "import org.springframework.web.bind.annotation.*;\nimport com.edtech.platform.common.response.ApiResponse;")
    
    # replace ResponseEntity<T> with ResponseEntity<ApiResponse<T>>
    content = re.sub(r"ResponseEntity<(List<[A-Za-z]+Response>)>", r"ResponseEntity<ApiResponse<\1>>", content)
    content = re.sub(r"ResponseEntity<([A-Za-z]+Response)>", r"ResponseEntity<ApiResponse<\1>>", content)
    content = content.replace("ResponseEntity<Void>", "ResponseEntity<ApiResponse<Void>>")
    
    # wrap body
    content = re.sub(r"\.body\((.+?)\);", r".body(ApiResponse.success(\1));", content)
    
    # replace .ok(...) with .ok(ApiResponse.success(...)) if not already
    content = re.sub(r"\.ok\((?!ApiResponse)([^)]+)\);", r".ok(ApiResponse.success(\1));", content)
    
    # replace .build() with .body(ApiResponse.success(null)) for Void responses
    content = re.sub(r"\.build\(\);", r".body(ApiResponse.success(null));", content)
    
    with open(file_path, "w") as f:
        f.write(content)

# update tests
test_file = r"E:\projects\E-learning\src\test\java\com\edtech\platform\course\CourseManagementTest.java"
with open(test_file, "r") as f:
    content = f.read()

content = content.replace('jsonPath("$.id")', 'jsonPath("$.data.id")')
content = content.replace('jsonPath("$.publishStatus")', 'jsonPath("$.data.publishStatus")')
content = content.replace('.get("id")', '.get("data").get("id")')

with open(test_file, "w") as f:
    f.write(content)

print("Updated controllers and tests to use ApiResponse")
