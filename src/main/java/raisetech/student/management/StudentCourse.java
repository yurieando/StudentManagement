package raisetech.student.management;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {
  private String courseId;
  private String nameId;
  private String course;
  private LocalDate startDate;
  private LocalDate deadline;
}
