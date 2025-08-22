package com.mgrunt.movies.exceptions;

public class SearchResultsException extends RuntimeException {
    public SearchResultsException(String message) {
        super(message);
    }

    public SearchResultsException(String message, Throwable cause){
        super(message, cause);
    }
}
