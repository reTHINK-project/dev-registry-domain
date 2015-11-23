package domainregistry;

public class Errors {

    private String message;

    public Errors(String message, String... args) {
        this.message = String.format(message, args);
    }

    public Errors(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}
