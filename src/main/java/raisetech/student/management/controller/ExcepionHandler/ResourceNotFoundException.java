package raisetech.student.management.controller.ExcepionHandler;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ResourceNotFoundException extends RuntimeException {

  private String message;

}
