import re

file_path = "src/test/java/com/edtech/platform/user/controller/InstructorVerificationTest.java"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

test_method = """
    @Test
    void testApproveVerificationUsesUserIdInsteadOfProfileId() throws Exception {
        // Setup instructor
        User instructor = new User();
        instructor.setEmail("verify_test@test.com");
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

        // Setup Admin
        User admin = new User();
        admin.setEmail("admin_verify@test.com");
        admin.setPasswordHash("hash");
        admin.setName("Admin");
        admin.setRole(com.edtech.platform.user.entity.Role.ADMIN);
        userRepository.save(admin);
        
        com.edtech.platform.common.security.UserDetailsImpl adminDetails = com.edtech.platform.common.security.UserDetailsImpl.build(admin);

        mockMvc.perform(post("/api/v1/admin/instructors/" + instructor.getId() + "/verify")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(adminDetails)))
                .andExpect(status().isOk());

        com.edtech.platform.user.entity.InstructorProfile updated = instructorProfileRepository.findByUserId(instructor.getId()).get();
        org.junit.jupiter.api.Assertions.assertEquals(com.edtech.platform.user.entity.VerificationStatus.VERIFIED, updated.getVerificationStatus());
    }
}"""

content = content.replace("}\n}", "}\n" + test_method)
with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
