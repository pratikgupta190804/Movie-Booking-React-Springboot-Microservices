package com.moviebooking.userservice.exception;

public class KeycloakServiceException extends RuntimeException{
    public KeycloakServiceException(String message){
        super(message);
    }
}
