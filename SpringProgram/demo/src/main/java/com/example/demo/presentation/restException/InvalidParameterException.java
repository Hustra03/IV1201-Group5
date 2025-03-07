package com.example.demo.presentation.restException;

/**
 * This class is for the error where a parameter is invalid
 */
public class InvalidParameterException extends RuntimeException{
    /**
     * This constructor creates the error, and defines it's error message, which is based on the cause parameter
     * @param cause A description of how/why the parameter is incorrect, with potential solutions
     */
    public InvalidParameterException(String cause) {
        super("Invalid parameter : " + cause);
      }
}
