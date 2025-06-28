import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserCreationTest extends BaseTest {
    private static final String REGISTRATION = "/api/auth/register";

    @Test
    @DisplayName("Создать пользователя")
    public void newUserCreate() {
        String random = RandomStringUtils.randomAlphanumeric(5);
        String email = "Testuser" + random + "@gmail.com";
        String password = "qwerty";
        String name = "Pavel";

        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(REGISTRATION)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Создать существующего пользователя")
    public void newUserExisting() {
        String email = "Pavel@gmail.com";
        String password = "qwerty";
        String name = "Pavel";

        // Сначала создаем пользователя
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(REGISTRATION);

        // Пытаемся создать того же пользователя снова
        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(REGISTRATION)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя без одного поля")
    public void newUserWithoutPole() {
        // Тест без поля email
        String email = "Pavel@gmail.com";
        String password = "qwerty";
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}",
                email, password);

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(REGISTRATION)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}