package com.mgrunt.movies.exceptions;

public class MovieSearchException extends RuntimeException {
    public MovieSearchException(String message) {
        super(message);
    }
  public MovieSearchException(String message, Throwable cause) {
    super(message, cause);
  }
}
