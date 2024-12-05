package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
}
