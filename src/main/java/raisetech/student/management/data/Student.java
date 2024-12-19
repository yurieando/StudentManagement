package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {

  @NotBlank
  @Pattern(regexp = "^\\d+$", message = "数値のみ入力してください。")
  private String nameId;

  @NotBlank(message = "入力必須です。")
  private String name;

  @NotBlank(message = "入力必須です。")
  @Pattern(regexp = "^[ァ-ンヴー]+$", message = "ふりがなはカタカナで入力してください")

  private String furigana;

  private String nickname;

  @NotBlank(message = "入力必須です。")
  @Email(message = "メールアドレスの形式が不正です。")
  private String mailAddress;

  @NotBlank
  private String address;

  @NotNull
  private int age;

  @NotBlank
  private String gender;

  private String remark;
  private boolean isDeleted;

  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;

    if (other == null || getClass() != other.getClass())
      return false;

    Student student = (Student) other;
    return age == student.age &&
        Objects.equals(nameId, student.nameId) &&
        Objects.equals(name, student.name) &&
        Objects.equals(furigana, student.furigana) &&
        Objects.equals(nickname, student.nickname) &&
        Objects.equals(mailAddress, student.mailAddress) &&
        Objects.equals(address, student.address) &&
        Objects.equals(gender, student.gender) &&
        Objects.equals(remark, student.remark);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nameId, name, furigana, nickname, mailAddress, address, age, gender, remark);
  }
}
