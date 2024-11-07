package code.modules.googleApi;

public class InternalApiException extends RuntimeException {
  public InternalApiException(String message, Throwable cause) {
    super(message, cause);
  }
}