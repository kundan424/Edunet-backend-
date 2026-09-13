import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.net.URI;

public class ScratchTest {
    public static void main(String[] args) {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create("", "");
            S3Configuration serviceConfiguration = S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build();
            S3Client s3Client = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .endpointOverride(URI.create(""))
                    .region(Region.of("auto"))
                    .serviceConfiguration(serviceConfiguration)
                    .build();
            System.out.println("Success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
