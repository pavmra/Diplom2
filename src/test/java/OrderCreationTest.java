import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.models.Order;
import ru.practicum.models.UserRegistration;
import static org.apache.http.HttpStatus.*;
import java.util.Collections;
import static org.hamcrest.Matchers.*;


public class OrderCreationTest extends BaseTest {

    private UserRegistration user;
    private String token;

    @Before
    public void setUp() {
        user = createRandomUser();
        authClient.register(user);
        token = authClient.getAuthToken(user);
    }

    @Test
    @DisplayName("Создать заказ с авторизацией")
    @Description ("Проверяем что можно создать заказ с авторизацией")
    public void createOrderWithRegistrationTest() {
        String ingredientId = orderClient.getFirstIngredientId();
        Order order = new Order(Collections.singletonList(ingredientId));
        Response response = orderClient.createOrder(order, token);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.ingredients", hasSize(1));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description ("Проверяем что можно создать заказ без авторизации")
    public void createOrderWithoutRegistrationTest() {
        String ingredientId = orderClient.getFirstIngredientId();
        Order order = new Order(Collections.singletonList(ingredientId));
        Response response = orderClient.createOrderWithoutAuth(order);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description ("Проверяем что нельзя создать заказ без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        Response response = orderClient.createOrder(order, token);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description ("Проверяем что можно создать заказ с неверным хешем ингредиентов")
    public void createOrderWithBadHashTest() {
        Order order = new Order(Collections.singletonList("invalid_hash"));
        Response response = orderClient.createOrder(order, token);
        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR );
    }

    @After
    public void tearDown() {
        if (token != null) {
            authClient.deleteUser(token);
        }
    }
}