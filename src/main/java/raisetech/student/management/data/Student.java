package raisetech.student.management.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
  private String nameId;
  private String name;
  private String furigana;
  private String nickname;
  private String mailAddress;
  private String address;
  private int age;
  private String gender;
  private String remark;
  private boolean isDeleted;
}
