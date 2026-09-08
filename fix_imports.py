import re

def fix(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Add imports and autowired fields
    fields = """
    @Autowired
    private com.edtech.platform.user.repository.UserRepository userRepository;
    
    @Autowired
    private com.edtech.platform.course.repository.CourseRepository courseRepository;
    
    @Autowired
    private com.edtech.platform.user.repository.InstructorProfileRepository instructorProfileRepository;
"""
    content = content.replace("private MockMvc mockMvc;", "private MockMvc mockMvc;\n" + fields)
    content = content.replace("User instructor = new User();", "com.edtech.platform.user.entity.User instructor = new com.edtech.platform.user.entity.User();")
    content = content.replace("User admin = new User();", "com.edtech.platform.user.entity.User admin = new com.edtech.platform.user.entity.User();")

    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

fix("src/test/java/com/edtech/platform/course/CourseDiscoveryTest.java")
fix("src/test/java/com/edtech/platform/user/controller/InstructorVerificationTest.java")
