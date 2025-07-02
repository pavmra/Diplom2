import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.models.UserRegistration;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserCreationTest extends BaseTest {

    private UserRegistration user;
    private String token;

    @Before
    public void setUp() {
        user = createRandomUser();
    }

    @Test
    @DisplayName("Создать пользователя")
    @Description("Проверяем что можно создать пользователя")
    public void newUserCreateTest() {
        Response response = authClient.register(user);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Создать существующего пользователя")
    @Description("Проверяем что нельзя создать существующего пользователя")
    public void newUserExistingTest() {
        UserRegistration user = new UserRegistration("Pavel@gmail.com", "qwerty", "Pavel");
        authClient.register(user);
        Response response = authClient.register(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя без одного поля")
    @Description("Проверяем что нельзя создать пользователя не заполнив поле Name")
    public void newUserWithoutPoleNameTest() {
        UserRegistration user = new UserRegistration("Pavel@gmail.com", "qwerty", null);
        Response response = authClient.register(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создать пользователя без одного поля")
    @Description("Проверяем что нельзя создать пользователя не заполнив поле Email")
    public void newUserWithoutPoleMailTest() {
        UserRegistration user = new UserRegistration(null, "qwerty", "Pavel");
        Response response = authClient.register(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создать пользователя без одного поля")
    @Description("Проверяем что нельзя создать пользователя не заполнив поле Password")
    public void newUserWithoutPolePassTest() {
        UserRegistration user = new UserRegistration("Pavel@gmail.com", null, "Pavel");
        Response response = authClient.register(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            authClient.deleteUser(token);
        }
    }
}