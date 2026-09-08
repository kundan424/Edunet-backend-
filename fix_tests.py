import re

file_path = "src/test/java/com/edtech/platform/course/CourseDiscoveryTest.java"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

test_method = """
    @Test
    void getCourseDetail_ShouldNotThrowMultipleBagFetchException() throws Exception {
        User instructor = new User();
        instructor.setEmail("detail_instructor@test.com");
        instructor.setPasswordHash("hash");
        instructor.setName("Detail Inst");
        instructor.setRole(Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course c = new Course();
        c.setTitle("Detailed Course");
        c.setDescription("Desc");
        c.setCategory("Programming");
        c.setDifficulty(CourseDifficulty.BEGINNER);
        c.setPrice(new BigDecimal("10.00"));
        c.setInstructorId(instructor.getId());
        c.setPublishStatus(PublishStatus.PUBLISHED);

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

        mockMvc.perform(get("/api/v1/courses/" + c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Detailed Course")))
                .andExpect(jsonPath("$.data.sections", hasSize(1)))
                .andExpect(jsonPath("$.data.sections[0].lessons", hasSize(1)));
    }
}"""

content = content.replace("}\n}", "}\n" + test_method)
with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
