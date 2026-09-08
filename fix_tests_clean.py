import re

def append_test(file_path, test_method, inject_field=None):
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    if inject_field:
        content = content.replace("private MockMvc mockMvc;", "private MockMvc mockMvc;\n" + inject_field)

    content = content.replace("}\n}", "}\n" + test_method)
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

test1 = """
    @Test
    void getCourseDetail_ShouldNotThrowMultipleBagFetchException() throws Exception {
        com.edtech.platform.user.entity.User instructor = new com.edtech.platform.user.entity.User();
        instructor.setEmail("detail_instructor@test.com");
        instructor.setPasswordHash("hash");
        instructor.setName("Detail Inst");
        instructor.setRole(com.edtech.platform.user.entity.Role.INSTRUCTOR);
        userRepository.save(instructor);

        com.edtech.platform.course.entity.Course c = new com.edtech.platform.course.entity.Course();
        c.setTitle("Detailed Course");
        c.setDescription("Desc");
        c.setCategory("Programming");
        c.setDifficulty(com.edtech.platform.course.enums.CourseDifficulty.BEGINNER);
        c.setPrice(new java.math.BigDecimal("10.00"));
        c.setInstructorId(instructor.getId());
        c.setPublishStatus(com.edtech.platform.course.enums.PublishStatus.PUBLISHED);

        com.edtech.platform.course.entity.Section s = new com.edtech.platform.course.entity.Section();
        s.setTitle("Section 1");
        s.setDisplayOrder(1);
        s.setCourse(c);
        c.getSections().add(s);

        com.edtech.platform.course.entity.Lesson l = new com.edtech.platform.course.entity.Lesson();
        l.setTitle("Lesson 1");
        l.setLessonType(com.edtech.platform.course.enums.LessonType.TEXT);
        l.setDisplayOrder(1);
        l.setSection(s);
        s.getLessons().add(l);

        courseRepository.save(c);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/courses/" + c.getId()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.title", org.hamcrest.Matchers.is("Detailed Course")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sections", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sections[0].lessons", org.hamcrest.Matchers.hasSize(1)));
    }
}"""
append_test("src/test/java/com/edtech/platform/course/CourseDiscoveryTest.java", test1)


test2 = """
    @Test
    void testApproveVerificationUsesUserIdInsteadOfProfileId() throws Exception {
        com.edtech.platform.user.entity.User instructor = new com.edtech.platform.user.entity.User();
        instructor.setEmail("verify_test2@test.com");
        instructor.setPasswordHash("hash");
        instructor.setName("Test Inst");
        instructor.setRole(com.edtech.platform.user.entity.Role.INSTRUCTOR);
        userRepository.save(instructor);

        com.edtech.platform.user.entity.InstructorProfile p = new com.edtech.platform.user.entity.InstructorProfile();
        p.setUser(instructor);
        p.setBio("Bio");
        p.setExpertise("Exp");
        p.setVerificationStatus(com.edtech.platform.user.entity.VerificationStatus.PENDING);
        instructorProfileRepository.save(p);

        com.edtech.platform.user.entity.User admin = new com.edtech.platform.user.entity.User();
        admin.setEmail("admin_verify2@test.com");
        admin.setPasswordHash("hash");
        admin.setName("Admin");
        admin.setRole(com.edtech.platform.user.entity.Role.ADMIN);
        userRepository.save(admin);
        
        com.edtech.platform.common.security.UserDetailsImpl adminDetails = com.edtech.platform.common.security.UserDetailsImpl.build(admin);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/admin/instructors/" + instructor.getId() + "/verify")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(adminDetails)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        com.edtech.platform.user.entity.InstructorProfile updated = instructorProfileRepository.findByUserId(instructor.getId()).get();
        org.junit.jupiter.api.Assertions.assertEquals(com.edtech.platform.user.entity.VerificationStatus.VERIFIED, updated.getVerificationStatus());
    }
}"""
field2 = """
    @org.springframework.beans.factory.annotation.Autowired
    private com.edtech.platform.user.repository.InstructorProfileRepository instructorProfileRepository;
"""
append_test("src/test/java/com/edtech/platform/user/controller/InstructorVerificationTest.java", test2, field2)
