package ru.practicum.models;

public class OrderResponse extends ApiResponse {
    private Order order;

    // Геттеры и сеттеры
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}