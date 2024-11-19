package restaurant.exceptions;

public class MenuServiceException extends Exception {
    public MenuServiceException(String message) {
        super(message);
    }

    public MenuServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
