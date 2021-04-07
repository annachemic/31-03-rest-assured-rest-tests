import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetAccountTests extends BaseTest {

    @Test
    void getExistentAccountTest() {
        given()
                .headers(headers)
                .log()
                .all(true)
                .when()
                .get("account/{username}", username)
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .body("data.url", equalTo(username));
    }

    @Test
    void getExistentAccountWithPrettyPeekTest() {
        Response response = given()
                .header("Authorization", "Bearer 81ed217eee6d991be324edc8754a07e4ce686bb9")
                .expect()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("data.reputation_name", equalTo("Neutral"))
                .when()
                .get("https://api.imgur.com/3/account/{username}", "testprogmath")
                .prettyPeek();
    }
}
