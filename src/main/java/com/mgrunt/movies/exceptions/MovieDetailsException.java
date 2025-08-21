package com.mgrunt.movies.exceptions;

public class MovieDetailsException extends RuntimeException {
    public MovieDetailsException(String message) {
        super(message);
    }
  public MovieDetailsException(String message, Throwable cause) {
    super(message, cause);
  }
}
