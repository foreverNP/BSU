package restaurant.exceptions;

public class OrderServiceException extends Exception {
    public OrderServiceException(String message) {
        super(message);
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
