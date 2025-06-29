import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.practicum.models.UserRegistration;

import static org.hamcrest.Matchers.*;

public class UserCreationTest extends BaseTest {
    @Test
    @DisplayName("Создать пользователя")
    public void newUserCreate() {
        UserRegistration user = createRandomUser();

        Response response = authClient.register(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Создать существующего пользователя")
    public void newUserExisting() {
        UserRegistration user = new UserRegistration("Pavel@gmail.com", "qwerty", "Pavel");


        authClient.register(user);


        Response response = authClient.register(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя без одного поля")
    public void newUserWithoutPole() {
        UserRegistration user = new UserRegistration("Pavel@gmail.com", "qwerty", null);

        Response response = authClient.register(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}