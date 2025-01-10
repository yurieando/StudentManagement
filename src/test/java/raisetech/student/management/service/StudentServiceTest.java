package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
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
    List<StudentDetail> expectedStudentDetails = new ArrayList<>();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(
        expectedStudentDetails);

    //実行
    List<StudentDetail> actual = sut.searchStudentList();

    //検証
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
    assertEquals(expectedStudentDetails, actual);
    //後処理（変更を加えた場合ここで元に戻す）
  }

  @Test
  void 受講生詳細の検索_個別の受講生詳細を呼び出せていること() {
    String nameId = "テストID";
    Student student = new Student(nameId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse testCourse = new StudentCourse("1", nameId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    studentCourseList.add(testCourse);

    when(repository.searchStudent(nameId)).thenReturn(student);
    when(repository.searchStudentCourse(student.getNameId())).thenReturn(studentCourseList);

    StudentDetail expected = new StudentDetail(student, studentCourseList);

    StudentDetail actual = sut.searchStudent(nameId);

    verify(repository, times(1)).searchStudent(nameId);
    verify(repository, times(1)).searchStudentCourse(student.getNameId());

    assertEquals(expected, actual);
  }

  @Test
  void 受講生詳細の新規登録_登録処理のリポジトリを適切な回数呼び出せていること() {
    Student student = new Student("1", "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);
    StudentCourse studentCourse = new StudentCourse("1", "1", "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    StudentDetail result = sut.registerStudent(studentDetail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(studentCourseList.size())).registerStudentCourse(studentCourse);
  }

  @Test
  void 受講生詳細の更新_更新処理のリポジトリを適切な回数呼び出せていること() {
    String nameId = "テストID";
    Student student = new Student(nameId, "氏名", "シメイ", "ニックネーム", "test@mail.com",
        "住所", 20, "male", "", false);

    StudentCourse studentCourse = new StudentCourse("1", nameId, "Java", LocalDate.now(),
        LocalDate.now().plusYears(1));
    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    sut.updateStudent(studentDetail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(studentCourseList.size())).updateStudentCourse(studentCourse);
  }
}
