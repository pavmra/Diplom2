import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.apache.commons.lang3.RandomStringUtils;

public class UserLoginTest extends BaseTest {
    private static final String LOGIN = "/api/auth/login";
    private static final String REGISTRATION = "/api/auth/register";

    @Test
    @DisplayName("Логин пользователя")
    public void loginUser() {
        // Сначала создаем пользователя
        String random = RandomStringUtils.randomAlphanumeric(5);
        String email = "Pavel" + random + "@gmail.com";
        String password = "qwerty";
        String name = "Pavel";

        String registerBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(registerBody)
                .when()
                .post(REGISTRATION);

        // Пытаемся войти
        String loginBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        given()
                .header("Content-type", "application/json")
                .body(loginBody)
                .when()
                .post(LOGIN)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    public void loginNoUser() {
        String loginBody = "{\"email\": \"noway@gmail.com\", \"password\": \"qwerty\"}";

        given()
                .header("Content-type", "application/json")
                .body(loginBody)
                .when()
                .post(LOGIN)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}