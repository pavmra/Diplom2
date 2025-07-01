package steps;

import io.restassured.response.Response;
import ru.practicum.models.Order;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String ORDERS = "/api/orders";
    private static final String INGREDIENTS = "/api/ingredients";

    @Step("Заказ с авторизацией")
    public Response createOrder(Order order, String authToken) {
        return given()
                .contentType("application/json")
                .header("Authorization", authToken)
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Заказ без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Получение первого ингредиента")
    public String getFirstIngredientId() {
        return given()
                .get(INGREDIENTS)
                .then()
                .extract()
                .path("data[0]._id");
    }
}