package raisetech.student.management.controller.ExcepionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter

public class ErrorResponse {

  private String message;
  private int value;
}
