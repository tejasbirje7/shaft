package org.shaft.administration.obligatory.tokens.exceptions;

public class ExpiredToken extends Exception {
    private static final long serialVersionUID = 1L;

    public ExpiredToken(String exception) {
        super(exception);
    }
}

