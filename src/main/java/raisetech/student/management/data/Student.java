package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {
  @NotBlank
  @Pattern(regexp = "^\\d+$")
  private String nameId;

  @NotBlank
  private String name;

  @NotBlank
  private String furigana;

  private String nickname;

  @NotBlank
  @Email
  private String mailAddress;

  @NotBlank
  private String address;

  @NotBlank
  private int age;

  @NotBlank
  private String gender;

  private String remark;
  private boolean isDeleted;
}
