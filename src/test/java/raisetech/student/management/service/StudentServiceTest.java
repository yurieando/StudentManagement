package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.ExcepionHandler.ResourceNotFoundException;
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
        applicationStatusList)).thenReturn(expectedStudentDetails);

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
    String studentId = "テストID";
    String courseId = "テストコースID";
    Student student = new Student(studentId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    StudentCourse studentCourse = new StudentCourse(courseId, studentId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    ApplicationStatus testApplicationStatus = new ApplicationStatus(courseId, studentId, "受講中");
    List<ApplicationStatus> applicationStatusList = List.of(testApplicationStatus);

    when(repository.findByStudentId(studentId)).thenReturn(Optional.of(student));
    when(repository.searchStudent(studentId)).thenReturn(student);
    when(repository.searchStudentCourseList(student.getStudentId())).thenReturn(studentCourseList);
    when(repository.searchApplicationStatusList(studentCourse.getCourseId())).thenReturn(
        applicationStatusList);

    StudentDetail expected = new StudentDetail(student, studentCourseList, applicationStatusList);

    StudentDetail actual = sut.searchStudent(studentId);

    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, times(1)).searchStudentCourseList(student.getStudentId());
    verify(repository, times(1)).searchApplicationStatusList(studentCourse.getCourseId());

    assertEquals(student.getStudentId(), actual.getStudent().getStudentId());
    assertEquals((studentCourse.getCourseId()), actual.getStudentCourseList().get(0).getCourseId());
    assertEquals(expected, actual);
  }

  @Test
  void 受講生詳細の検索_存在しないIDを指定した場合に例外がスローされること() {
    String invalidStudentId = "0";
    when(repository.searchStudent(invalidStudentId)).thenReturn(null);

    assertThrows(ResourceNotFoundException.class, () -> {
      sut.searchStudent(invalidStudentId);
    });
  }

  @Test
  void 受講生詳細の新規登録_登録処理のリポジトリを適切な回数呼び出せていること() {
    String studentId = "テストID";
    Student student = new Student(studentId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    StudentCourse studentCourse1 = new StudentCourse("1", studentId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    StudentCourse studentCourse2 = new StudentCourse("2", studentId, "Python", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(studentCourse1, studentCourse2);

    ApplicationStatus applicationStatus1 = new ApplicationStatus("1", studentId, "仮申込");
    ApplicationStatus applicationStatus2 = new ApplicationStatus("2", studentId, "仮申込");
    List<ApplicationStatus> applicationStatusList = List.of(applicationStatus1, applicationStatus2);

    doNothing().when(repository).registerStudent(any(Student.class));
    doNothing().when(repository).registerStudentCourse(any(StudentCourse.class));
    doNothing().when(repository).registerApplicationStatus(anyString(), anyString());

    StudentDetail studentDetail = new StudentDetail(student, studentCourseList,
        applicationStatusList);

    sut.registerStudent(studentDetail);

    verify(repository, times(1)).registerStudent(any(Student.class));
    verify(repository, times(studentCourseList.size())).registerStudentCourse(
        any(StudentCourse.class));
    verify(repository, times(applicationStatusList.size())).registerApplicationStatus(anyString(),
        anyString());
  }

  @Test
  void 受講生コース情報の新規登録_登録処理のリポジトリを適切な回数呼び出せていること() {
    String studentId = "テストID";
    String courseId = "テストコースID";
    StudentCourse studentCourse = new StudentCourse(courseId, studentId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    ApplicationStatus applicationStatus = new ApplicationStatus(courseId, studentId, "仮申込");

    Student student = new Student(studentId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    when(repository.findByStudentId(studentId)).thenReturn(Optional.of(student));

    doNothing().when(repository).registerStudentCourse(any(StudentCourse.class));
    doNothing().when(repository).registerApplicationStatus(anyString(), anyString());

    sut.registerStudentCourse(studentId, studentCourse);

    verify(repository, times(1)).registerStudentCourse(any(StudentCourse.class));
    verify(repository, times(1)).registerApplicationStatus(anyString(), anyString());
  }

  @Test
  void 受講生詳細の更新_更新処理のリポジトリを適切な回数呼び出せていること() {
    String studentId = "テストID";
    String courseId = "テストコースID";
    Student student = new Student(studentId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    when(repository.findByStudentId(studentId)).thenReturn(Optional.of(student));

    StudentCourse studentCourse = new StudentCourse(courseId, studentId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    ApplicationStatus applicationStatus = new ApplicationStatus(courseId, studentId, "受講中");
    List<ApplicationStatus> applicationStatusList = List.of(applicationStatus);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList,
        applicationStatusList);

    sut.updateStudent(studentDetail);

    verify(repository, times(1)).updateStudent(any(Student.class));
    verify(repository, times(1)).updateStudentCourse(any(StudentCourse.class));
    verify(repository, times(1)).updateApplicationStatus(any(ApplicationStatus.class));
  }

  @Test
  void 受講生詳細の削除_削除処理のリポジトリを適切な回数呼び出せていること() {
    String studentId = "テストID";
    String courseId = "テストコースID";
    Student student = new Student(studentId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    when(repository.findByStudentId(studentId)).thenReturn(Optional.of(student));
    when(repository.searchStudentCourseList(studentId))
        .thenReturn(List.of(new StudentCourse(courseId, studentId, "Java", LocalDate.now(),
            LocalDate.now().plusYears(1))));

    doNothing().when(repository).deleteStudent(studentId);
    doNothing().when(repository).deleteStudentCourse(courseId);
    doNothing().when(repository).deleteApplicationStatus(courseId);

    sut.deleteStudent(studentId);

    verify(repository, times(1)).deleteStudent(studentId);
    verify(repository, times(1)).deleteStudentCourse(courseId);
    verify(repository, times(1)).deleteApplicationStatus(courseId);
  }


  @Test
  void 受講生詳細の削除_存在しないIDを指定した場合に例外がスローされること() {
    String invalidStudentId = "0";

    when(repository.findByStudentId(invalidStudentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      sut.deleteStudent(invalidStudentId);
    });

    verify(repository, times(0)).deleteStudent(invalidStudentId);
  }

  @Test
  void 受講生コース情報の削除_削除処理のリポジトリを適切な回数呼び出せていること() {
    String courseId = "テストコースID";
    StudentCourse studentCourse = new StudentCourse(courseId, "テストID", "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));

    when(repository.findByCourseId(courseId)).thenReturn(Optional.of(studentCourse));

    doNothing().when(repository).deleteStudentCourse(courseId);
    doNothing().when(repository).deleteApplicationStatus(courseId);

    sut.deleteStudentCourse(courseId);

    verify(repository, times(1)).deleteStudentCourse(courseId);
    verify(repository, times(1)).deleteApplicationStatus(courseId);
  }

  @Test
  void 受講生コース情報の削除_存在しないIDを指定した場合に例外がスローされること() {
    String invalidCourseId = "0";

    when(repository.findByCourseId(invalidCourseId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      sut.deleteStudentCourse(invalidCourseId);
    });

    verify(repository, times(0)).deleteStudentCourse(invalidCourseId);
  }
}
