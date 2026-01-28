package com.nova.nova_server.domain.ai.exception;

public class AiException extends RuntimeException {

    public AiException(String message) {
        super(message);
    }

    public static class InvalidBatchInputException extends AiException {
        public InvalidBatchInputException(String message) {
            super(message);
        }
    }

    public static class InvalidBatchOutputException extends AiException {
        public InvalidBatchOutputException(String message) {
            super(message);
        }
    }

    public static class InvalidBatchIdException extends AiException {
        public InvalidBatchIdException(String message) {
            super(message);
        }
    }

    public static class PendingBatchException extends AiException {
        public PendingBatchException(String message) {
            super(message);
        }
    }

}
