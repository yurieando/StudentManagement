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
  void 生徒情報とコース情報を結合したリストが生成できていること() {
    List<Student> studentList = new ArrayList<>();
    Student testStudent = new Student("1", "テスト名", "テストフリガナ", "テストニックネーム"
        , "test@email.com", "東京", 20, "男性", "", false);
    studentList.add(testStudent);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testStudentCourse = new StudentCourse("1", "1", "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    studentCourseList.add(testStudentCourse);

    List<StudentDetail> expectedStudentDetails = new ArrayList<>();
    StudentDetail testDetail = new StudentDetail();
    testDetail.setStudent(testStudent);
    List<StudentCourse> testStudentCourseList = new ArrayList<>();
    testStudentCourseList.add(testStudentCourse);
    testDetail.setStudentCourseList(testStudentCourseList);
    expectedStudentDetails.add(testDetail);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList);

    assertEquals(expectedStudentDetails, actual);
  }

  @Test
  void 生徒情報とnameIdが紐づかないコース情報はリストから除外されること() {
    List<Student> studentList = new ArrayList<>();
    Student testStudent = new Student("1", "テスト名", "テストフリガナ", "テストニックネーム"
        , "test@email.com", "東京", 20, "男性", "", false);
    studentList.add(testStudent);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testStudentCourse = new StudentCourse("1", "99", "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    studentCourseList.add(testStudentCourse);

    List<StudentDetail> expectedStudentDetails = new ArrayList<>();
    StudentDetail testDetail = new StudentDetail();
    testDetail.setStudent(testStudent);
    List<StudentCourse> testStudentCourseList = new ArrayList<>();
    testDetail.setStudentCourseList(testStudentCourseList);
    expectedStudentDetails.add(testDetail);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList);

    assertEquals(expectedStudentDetails, actual, "nameIdが紐づかないコースが除外されていません。");
  }
}
