import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class OrderCreationTest extends BaseTest {
    private static final String ZAKAZ = "/api/orders";
    private static final String REGISTRATION = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";

    private String getAuthToken(String email, String password) {
        String loginBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        Response response = given()
                .header("Content-type", "application/json")
                .body(loginBody)
                .post(LOGIN);

        return response.path("accessToken");
    }

    @Test
    @DisplayName("Создать заказа с авторизацией")
    public void createZakazWithRegistration() {
        // Создаем пользователя
        String random = RandomStringUtils.randomAlphanumeric(5);
        String email = "Pavel" + random + "@gmail.com";
        String password = "qwerty";
        String name = "Pavel";

        String registerBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(registerBody)
                .post(REGISTRATION);

        // Получаем токен
        String token = getAuthToken(email, password);

        // Получаем список ингредиентов (предполагаем, что есть хотя бы один)
        Response ingredientsResponse = given()
                .get("/api/ingredients");

        String ingredientId = ingredientsResponse.path("data[0]._id");

        // Создаем заказ
        String orderBody = String.format("{\"ingredients\": [\"%s\"]}", ingredientId);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(orderBody)
                .when()
                .post(ZAKAZ)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients", hasSize(1));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createZakazWithoutRegistration() {
        // Получаем список ингредиентов
        Response ingredientsResponse = given()
                .get("/api/ingredients");

        String ingredientId = ingredientsResponse.path("data[0]._id");

        String orderBody = String.format("{\"ingredients\": [\"%s\"]}", ingredientId);

        given()
                .header("Content-type", "application/json")
                .body(orderBody)
                .when()
                .post(ZAKAZ)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createZakazWithoutIngredients() {
        // Создаем пользователя
        String random = RandomStringUtils.randomAlphanumeric(5);
        String email = "Pavel" + random + "@gmail.com";
        String password = "qwerty";
        String name = "Pavel";

        String registerBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(registerBody)
                .post(REGISTRATION);

        // Получаем токен
        String token = getAuthToken(email, password);

        // Пытаемся создать заказ без ингредиентов
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body("{\"ingredients\": []}")
                .when()
                .post(ZAKAZ)
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createZakazWithBadHash() {
        // Создаем пользователя
        String random = RandomStringUtils.randomAlphanumeric(5);
        String email = "Pavel" + random + "@gmail.com";
        String password = "qwerty";
        String name = "Pavel";

        String registerBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}",
                email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(registerBody)
                .post(REGISTRATION);

        // Получаем токен
        String token = getAuthToken(email, password);

        // Пытаемся создать заказ с неверным хешем
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body("{\"ingredients\": [\"invalid_hash\"]}")
                .when()
                .post(ZAKAZ)
                .then()
                .statusCode(500);
    }
}