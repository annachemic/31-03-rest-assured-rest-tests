import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class UploadImageTests extends BaseTest{
    static final String INPUT_IMG_FILE_PATH = "src/test/resources/avatarGru.jpeg";
    static byte[] fileContent;
    static Properties properties;
    static String token;
    static String username;
    private String uploadedImageId;

    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new Properties();
        File inputFile = new File(INPUT_IMG_FILE_PATH);

        try {
            fileContent = readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties.load(new FileInputStream("src/test/resources/application.properties"));
        token = properties.getProperty("token");
        username = properties.getProperty("username");
        RestAssured.baseURI = properties.getProperty("base.url");
    }

    @Test
    void uploadImageTest() {
        given()
                .multiPart("image", new File(INPUT_IMG_FILE_PATH))
                .header("Authorization", token)
        .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void uploadImageFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(fileContent);
         uploadedImageId = given()
                .multiPart("image", fileContentBase64)
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @AfterEach
    void tearDown() {
        given()
                .headers(headers)
        .when()
                .delete("/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
