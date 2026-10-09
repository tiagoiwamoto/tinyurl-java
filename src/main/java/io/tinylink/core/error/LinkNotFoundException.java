package io.tinylink.core.error;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(String code) {
        super("Link não encontrado: " + code);
    }

    public LinkNotFoundException(Long id) {
        super("Link não encontrado: id " + id);
    }
}
