package code.util;

import code.modules.conversation.IConversationCommandFacade.RequestGenerateDto;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;

public interface ControllerUtil {
  @SuppressWarnings("unchecked") // is safe
  static <T> T getOrSetSessionAttr(
    HttpSession session,
    String attrName,
    T value,
    T defaultValue
  ) {
    if (Objects.isNull(value)) {
      Object attribute = session.getAttribute(attrName);
      try {
        return Objects.isNull(attribute) ? defaultValue : (T) attribute;
      } catch (ClassCastException e) {
        throw new IllegalArgumentException("Type mismatch for attribute: %s, value: %s"
          .formatted(attrName, attribute));
      }
    } else {
      session.setAttribute(attrName, value);
      return value;
    }
  }

  default RequestGenerateDto getEmptyRequest() {
    return new RequestGenerateDto(null);
  }
}