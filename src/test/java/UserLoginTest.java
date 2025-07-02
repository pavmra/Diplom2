import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.models.UserRegistration;
import ru.practicum.models.UserLogin;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseTest {

    private UserRegistration user;
    private String token;

    @Before
    public void setUp() {
        user = createRandomUser();
        authClient.register(user);
        token = authClient.getAuthToken(user);
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Проверяем что можно залогиниться")
    public void loginUserTest() {
        UserLogin credentials = new UserLogin(user.getEmail(), user.getPassword());
        Response response = authClient.login(credentials);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверяем что нельзя залогиниться с несуществующим пользователем")
    public void loginNoUserTest() {
        UserLogin credentials = new UserLogin("noway@gmail.com", "qwerty");
        Response response = authClient.login(credentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверяем что нельзя залогиниться не заполнив поле Email")
    public void loginNoEmailTest() {
        UserLogin credentials = new UserLogin(null, "qwerty");
        Response response = authClient.login(credentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверяем что нельзя залогиниться не заполнив поле Password")
    public void loginNoPassTest() {
        UserLogin credentials = new UserLogin("noway@gmail.com", null);
        Response response = authClient.login(credentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            authClient.deleteUser(token);
        }
    }
}