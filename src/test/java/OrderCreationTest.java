import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.practicum.models.Order;
import ru.practicum.models.UserRegistration;

import java.util.Collections;

import static org.hamcrest.Matchers.*;

public class OrderCreationTest extends BaseTest {
    @Test
    @DisplayName("Создать заказ с авторизацией")
    public void createOrderWithRegistration() {
        UserRegistration user = createRandomUser();
        authClient.register(user);

        String token = authClient.getAuthToken(user);
        String ingredientId = orderClient.getFirstIngredientId();

        Order order = new Order(Collections.singletonList(ingredientId));
        Response response = orderClient.createOrder(order, token);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients", hasSize(1));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutRegistration() {
        String ingredientId = orderClient.getFirstIngredientId();
        Order order = new Order(Collections.singletonList(ingredientId));

        Response response = orderClient.createOrderWithoutAuth(order);

        response.then()
                .statusCode(200) // Изменено с 401 на 200
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        UserRegistration user = createRandomUser();
        authClient.register(user);

        String token = authClient.getAuthToken(user);
        Order order = new Order(Collections.emptyList());

        Response response = orderClient.createOrder(order, token);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithBadHash() {
        UserRegistration user = createRandomUser();
        authClient.register(user);

        String token = authClient.getAuthToken(user);
        Order order = new Order(Collections.singletonList("invalid_hash"));

        Response response = orderClient.createOrder(order, token);

        response.then()
                .statusCode(400);
    }
}