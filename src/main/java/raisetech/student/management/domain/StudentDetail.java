package raisetech.student.management.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

@Schema(description = "受講生詳細情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentDetail {

  @Valid
  private Student student;

  @Valid
  private List<StudentCourse> studentCourseList;

  @Valid
  private List<ApplicationStatus> applicationStatusList;


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StudentDetail that = (StudentDetail) other;
    return Objects.equals(student, that.student) &&
        Objects.equals(studentCourseList, that.studentCourseList)
        && Objects.equals(applicationStatusList, that.applicationStatusList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(student, studentCourseList, applicationStatusList);
  }
}
