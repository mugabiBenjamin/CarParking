package domain.exceptions;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(normalizeMessage(message));
    }

    public DomainException(String message, Throwable cause) {
        super(normalizeMessage(message), cause);
    }

    private static String normalizeMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "A domain error occurred";
        }

        return message.trim();
    }
}