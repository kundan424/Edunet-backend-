import os

files_to_fix = [
    'src/main/java/com/edtech/platform/course/service/CourseReviewService.java',
    'src/main/java/com/edtech/platform/notification/service/NotificationService.java'
]

for f in files_to_fix:
    with open(f, 'r', encoding='utf-8') as file:
        content = file.read()
    content = content.replace('ErrorCode.UNAUTHORIZED_ACCESS', 'ErrorCode.FORBIDDEN')
    content = content.replace('ErrorCode.VALIDATION_ERROR', 'ErrorCode.VALIDATION_FAILED')
    with open(f, 'w', encoding='utf-8') as file:
        file.write(content)
print('Fixed error codes')
