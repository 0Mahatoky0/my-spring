package exeption.http;

public class BadRequestException extends Exception {
    private final int code = 400;

    public BadRequestException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }
}
