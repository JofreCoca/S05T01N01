package cat.itacademy.s05.t01.n01.S05T01N01.exceptions;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
