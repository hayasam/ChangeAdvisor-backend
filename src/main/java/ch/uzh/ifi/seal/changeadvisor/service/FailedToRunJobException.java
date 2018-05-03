package ch.uzh.ifi.seal.changeadvisor.service;

public class FailedToRunJobException extends Exception {

    FailedToRunJobException(String message, Throwable cause) {
        super(message, cause);
    }

}
