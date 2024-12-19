package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース情報")
@Getter
@Setter
public class StudentCourse {
  private String courseId;
  private String nameId;
  private String course;
  private LocalDate startDate;
  private LocalDate deadline;

  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (other == null || getClass() != other.getClass())
      return false;
    StudentCourse that = (StudentCourse) other;
    return Objects.equals(course, that.course);
  }

  @Override
  public int hashCode() {
    return Objects.hash(course);
  }
}
