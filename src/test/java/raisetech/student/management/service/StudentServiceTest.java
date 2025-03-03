package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;
  private StudentService sut;

  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    sut = new StudentService(repository, converter);
    //sutはテスト対象の意
  }

  @Test
    //テストのメソッドは日本語でOK
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    //事前準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<ApplicationStatus> applicationStatusList = new ArrayList<>();
    List<StudentDetail> expectedStudentDetails = new ArrayList<>();

    when(repository.allSearchStudentList()).thenReturn(studentList);
    when(repository.allSearchStudentCourseList()).thenReturn(studentCourseList);
    when(repository.allSearchApplicationStatusList()).thenReturn(applicationStatusList);
    when(converter.convertStudentDetails(studentList, studentCourseList,
        applicationStatusList)).thenReturn(
        expectedStudentDetails);

    //実行
    List<StudentDetail> actual = sut.searchStudentList();

    //検証
    verify(repository, times(1)).allSearchStudentList();
    verify(repository, times(1)).allSearchStudentCourseList();
    verify(repository, times(1)).allSearchApplicationStatusList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList,
        applicationStatusList);
    assertEquals(expectedStudentDetails, actual);
    //後処理（変更を加えた場合ここで元に戻す）
  }

  @Test
  void 受講生詳細の検索_個別の受講生詳細を呼び出せていること() {
    String nameId = "テストID";
    String courseId = "1";
    Student student = new Student(nameId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    StudentCourse testCourse = new StudentCourse(courseId, nameId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(testCourse);

    ApplicationStatus testApplicationStatus = new ApplicationStatus(courseId, nameId, "受講中");
    List<ApplicationStatus> applicationStatusList = List.of(testApplicationStatus);

    when(repository.searchStudent(nameId)).thenReturn(student);
    when(repository.searchStudentCourseList(student.getNameId())).thenReturn(studentCourseList);
    when(repository.searchApplicationStatusList(anyString())).thenReturn(applicationStatusList);

    StudentDetail expected = new StudentDetail(student, studentCourseList, applicationStatusList);

    StudentDetail actual = sut.searchStudent(nameId);

    verify(repository, times(1)).searchStudent(nameId);
    verify(repository, times(1)).searchStudentCourseList(student.getNameId());
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(repository, times(1)).searchApplicationStatusList(captor.capture());

    assertEquals((testCourse.getCourseId()), captor.getValue());
    assertEquals(expected, actual);
  }

  @Test
  void 受講生詳細の新規登録_登録処理のリポジトリを適切な回数呼び出せていること() {
    String nameId = "テストID";
    String courseId = "1";
    Student student = new Student(nameId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);
    StudentCourse studentCourse = new StudentCourse(courseId, nameId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    ApplicationStatus applicationStatus = new ApplicationStatus(courseId, nameId, "仮申込");
    List<ApplicationStatus> applicationStatusList = List.of(applicationStatus);

    StudentDetail studentDetail = new StudentDetail(student, studentCourseList,
        applicationStatusList);

    StudentDetail result = sut.registerStudent(studentDetail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(studentCourseList.size())).registerStudentCourse(studentCourse);
    verify(repository, times(applicationStatusList.size())).registerApplicationStatus(anyString(),
        anyString());
  }

  @Test
  void 受講生詳細の更新_更新処理のリポジトリを適切な回数呼び出せていること() {
    String nameId = "テストID";
    String courseId = "1";
    Student student = new Student(nameId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    StudentCourse studentCourse = new StudentCourse(courseId, nameId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = new ArrayList<>();

    ApplicationStatus applicationStatus = new ApplicationStatus(courseId, nameId, "受講中");
    List<ApplicationStatus> applicationStatusList = List.of(applicationStatus);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList,
        applicationStatusList);

    sut.updateStudent(studentDetail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(studentCourseList.size())).updateStudentCourse(studentCourse);
    verify(repository, times(applicationStatusList.size())).updateApplicationStatus(
        applicationStatus);
  }
}
