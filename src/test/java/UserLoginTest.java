import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.practicum.models.UserRegistration;
import ru.practicum.models.UserLogin;

import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseTest {

    @Test
    @DisplayName("Логин пользователя")
    public void loginUser() {
        UserRegistration user = createRandomUser();

        // Регистрация
        authClient.register(user);

        // Логин
        UserLogin credentials = new UserLogin(user.getEmail(), user.getPassword());
        Response response = authClient.login(credentials);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    public void loginNoUser() {
        UserLogin credentials = new UserLogin("noway@gmail.com", "qwerty");

        Response response = authClient.login(credentials);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}