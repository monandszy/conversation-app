package code.modules.google_api.internal;

import java.io.Serial;

public class InternalApiException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public InternalApiException(String message, Throwable cause) {
    super(message, cause);
  }
}