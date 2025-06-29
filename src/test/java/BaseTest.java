import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.BeforeClass;
import ru.practicum.models.UserRegistration;
import ru.practicum.steps.AuthClient;
import ru.practicum.steps.OrderClient;

public class BaseTest {
    protected static AuthClient authClient;
    protected static OrderClient orderClient;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );

        authClient = new AuthClient();
        orderClient = new OrderClient();
    }

    protected UserRegistration createRandomUser() {
        String random = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(5);
        return new UserRegistration(
                "Testuser" + random + "@gmail.com",
                "qwerty",
                "Pavel"
        );
    }
}