package raisetech.student.management.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;
import raisetech.student.management.service.StudentService;

@ExtendWith(MockitoExtension.class)
public class StudentConverterTest {

  @InjectMocks
  private StudentConverter sut;

  @Test
  void 生徒情報とコース情報と申込状況を結合したリストが生成できていること() {
    List<Student> studentList = new ArrayList<>();
    Student testStudent = new Student("1", "テスト名", "テストフリガナ", "テストニックネーム"
        , "test@email.com", "東京", 20, "男性", "", false);
    studentList.add(testStudent);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testStudentCourse = new StudentCourse("1", "1", "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    studentCourseList.add(testStudentCourse);

    List<ApplicationStatus> applicationStatusList = new ArrayList<>();
    ApplicationStatus testApplicationStatus = new ApplicationStatus("1", "1", "受講中");
    applicationStatusList.add(testApplicationStatus);

    List<StudentDetail> expectedStudentDetails = new ArrayList<>();
    StudentDetail testDetail = new StudentDetail();
    testDetail.setStudent(testStudent);

    List<StudentCourse> testStudentCourseList = new ArrayList<>();
    testStudentCourseList.add(testStudentCourse);
    testDetail.setStudentCourseList(testStudentCourseList);

    List<ApplicationStatus> testApplicationStatusList = new ArrayList<>();
    testApplicationStatusList.add(testApplicationStatus);
    testDetail.setApplicationStatusList(testApplicationStatusList);

    expectedStudentDetails.add(testDetail);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        applicationStatusList);

    assertEquals(expectedStudentDetails, actual);
  }

  @Test
  void 生徒情報とstudentIdが紐づかないコース情報はリストから除外されること() {
    List<Student> studentList = new ArrayList<>();
    Student testStudent = new Student("1", "テスト名", "テストフリガナ", "テストニックネーム"
        , "test@email.com", "東京", 20, "男性", "", false);
    studentList.add(testStudent);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testStudentCourse = new StudentCourse("1", "99", "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    studentCourseList.add(testStudentCourse);

    List<ApplicationStatus> applicationStatusList = new ArrayList<>();
    ApplicationStatus testApplicationStatus = new ApplicationStatus("1", "1", "受講中");
    applicationStatusList.add(testApplicationStatus);

    List<StudentDetail> expectedStudentDetails = new ArrayList<>();
    StudentDetail testDetail = new StudentDetail();
    testDetail.setStudent(testStudent);

    List<StudentCourse> testStudentCourseList = new ArrayList<>();
    testDetail.setStudentCourseList(testStudentCourseList);

    List<ApplicationStatus> testApplicationStatusList = new ArrayList<>();
    testDetail.setApplicationStatusList(testApplicationStatusList);

    expectedStudentDetails.add(testDetail);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        applicationStatusList);

    assertEquals(expectedStudentDetails, actual,
        "studentIdが紐づかないコースが除外されていません。");
  }
}
