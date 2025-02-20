package voteapp.authservice.exception;

public class DifferentPasswordsException extends RuntimeException{
    public DifferentPasswordsException(String message) {super(message);}
}
