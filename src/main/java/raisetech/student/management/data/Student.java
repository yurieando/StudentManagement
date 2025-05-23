package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
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

  private String studentId;

  @NotBlank(message = "入力必須です。")
  private String name;

  @NotBlank(message = "入力必須です。")
  @Pattern(regexp = "^[ァ-ンヴー]+$", message = "フリガナはカタカナで入力してください。")
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

  public Student(String studentId, String name, String furigana, String nickname,
      String mailAddress,
      String address, int age, String gender, String remark, boolean isDeleted) {
    this.studentId = studentId;
    this.name = name;
    this.furigana = furigana;
    this.nickname = nickname;
    this.mailAddress = mailAddress;
    this.address = address;
    this.age = age;
    this.gender = gender;
    this.remark = remark;
    this.isDeleted = isDeleted;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    Student student = (Student) other;
    return age == student.age &&
        Objects.equals(studentId, student.studentId) &&
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
    return Objects.hash(studentId, name, furigana, nickname, mailAddress, address, age, gender,
        remark);
  }


}
