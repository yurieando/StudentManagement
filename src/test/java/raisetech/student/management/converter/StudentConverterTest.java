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
    Student testStudent = new Student();
    testStudent.setNameId("1");
    testStudent.setName("テスト名");
    testStudent.setFurigana("テストフリガナ");
    testStudent.setNickname("テストニックネーム");
    testStudent.setMailAddress("test@email.com");
    testStudent.setAddress("東京");
    testStudent.setAge(20);
    testStudent.setGender("男性");
    testStudent.setRemark("");
    testStudent.setDeleted(false);

    studentList.add(testStudent);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testStudentCourse = new StudentCourse();
    testStudentCourse.setCourseId("1");
    testStudentCourse.setNameId("1");
    testStudentCourse.setCourse("Java");
    testStudentCourse.setStartDate(LocalDate.now());
    testStudentCourse.setDeadline(LocalDate.now().plusYears(1));
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
    Student testStudent = new Student();
    testStudent.setNameId("1");
    testStudent.setName("テスト名");
    testStudent.setFurigana("テストフリガナ");
    testStudent.setNickname("テストニックネーム");
    testStudent.setMailAddress("test@email.com");
    testStudent.setAddress("東京");
    testStudent.setAge(20);
    testStudent.setGender("男性");
    testStudent.setRemark("");
    testStudent.setDeleted(false);

    studentList.add(testStudent);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testStudentCourse = new StudentCourse();
    testStudentCourse.setCourseId("1");
    testStudentCourse.setNameId("99");
    testStudentCourse.setCourse("Java");
    testStudentCourse.setStartDate(LocalDate.now());
    testStudentCourse.setDeadline(LocalDate.now().plusYears(1));
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
