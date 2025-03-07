package raisetech.student.management.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CourseRegistrationRequest {

  @NotBlank(message = "入力必須です。")
  private String courseName;
}
