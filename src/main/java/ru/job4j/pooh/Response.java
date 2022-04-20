package ru.job4j.pooh;

public class Response {

    private final String text;
    private final String status;

    public Response(String text, String status) {
        this.text = text;
        this.status = status;
    }

    public String text() {
        return text;
    }

    public String status() {
        return status;
    }
}