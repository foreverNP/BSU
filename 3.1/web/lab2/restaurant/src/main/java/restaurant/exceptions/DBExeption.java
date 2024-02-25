package restaurant.exceptions;

public class DBExeption extends Exception {
    public DBExeption(String message) {
        super(message);
    }

    public DBExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
