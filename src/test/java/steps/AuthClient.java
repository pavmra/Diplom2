package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.models.UserRegistration;
import ru.practicum.models.UserLogin;

import static io.restassured.RestAssured.given;

public class AuthClient {
    private static final String REGISTER = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";

    @Step("Регистрация")
    public Response register(UserRegistration user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(REGISTER);
    }

    @Step("Авторизация")
    public Response login(UserLogin credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post(LOGIN);
    }

    @Step("Получить токен авторизации")
    public String getAuthToken(UserRegistration user) {
        UserLogin credentials = new UserLogin(user.getEmail(), user.getPassword());
        return login(credentials)
                .then()
                .extract()
                .path("accessToken");
    }
}