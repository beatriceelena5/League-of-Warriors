package src.java.org.example.entities.exceptions;

public class InvalidEmailException extends Exception {
    public InvalidEmailException() {
        super("Invalid email format. Please enter a valid email address.");
    }

    public InvalidEmailException(String message) {
        super(message);
    }
}
