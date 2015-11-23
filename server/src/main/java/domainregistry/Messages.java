package domainregistry;

public class Messages {

    private String message;

    public Messages(String message, String... args) {
        this.message = String.format(message, args);
    }

    public Messages(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}
