package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講生コース情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentCourse {

  private String courseId;
  private String nameId;
  private String courseName;
  private LocalDate startDate;
  private LocalDate deadline;


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StudentCourse that = (StudentCourse) other;
    return Objects.equals(courseName, that.courseName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseName);
  }
}
