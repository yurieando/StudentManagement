package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "申込状況")
@Getter
@Setter
public class ApplicationStatus {

  private String courseId;
  private String nameId;
  private String applicationStatus;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ApplicationStatus that = (ApplicationStatus) other;
    return applicationStatus.equals(that.applicationStatus);
  }

  @Override
  public int hashCode() {
    return applicationStatus.hashCode();
  }
}
