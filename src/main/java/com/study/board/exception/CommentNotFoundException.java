package com.study.board.exception;

public class CommentNotFoundException extends RuntimeException{

    public CommentNotFoundException(String message) {
        super(message);
    }
}
