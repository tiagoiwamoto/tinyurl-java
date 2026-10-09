package io.tinylink.core.error;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String username) {
        super("Usuário já existe: " + username);
    }
}
