import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FixTest {
    public static void main(String[] args) throws IOException {
        String path = "e:/projects/E-learning/src/test/java/com/edtech/platform/enrollment/EnrollmentTest.java";
        String content = new String(Files.readAllBytes(Paths.get(path)));
        content = content.replace("import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;", "import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;\nimport static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;");
        content = content.replace(".header(HttpHeaders.AUTHORIZATION, \"Bearer \" + token)", ".with(user(studentDetails))");
        Files.write(Paths.get(path), content.getBytes());
    }
}
