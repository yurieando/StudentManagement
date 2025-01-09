package raisetech.student.management.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

/*
  * 受講生情報を扱うサービス
  * 受講生の検索や登録、更新処理を行ないます。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }
/*
  * 受講生詳細の一覧を検索します。
  * 全件検索を行うので、条件指定は行ないません。
  * @return 受講生詳細一覧（全件）
 */
  public List<StudentDetail> searchStudentList() {
    List <Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    return converter.convertStudentDetails(studentList, studentCourseList);
  }

  /*
    * 受講生詳細の検索です。
    * IDに紐づく任意の受講生の情報を取得した後、その受講生に紐づく受講生コース情報を取得して設定します。
    *
    * @param nameId 受講生ID
    * @return 受講生詳細情報
   */
  public StudentDetail searchStudent(String nameId) {
    Student student = repository.searchStudent(nameId);
    List<StudentCourse> studentCourseList = repository.searchStudentCourse(student.getNameId());
    return new StudentDetail(student, studentCourseList);
  }

  /*
    * 受講生詳細を登録します。
    * 受講生情報と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づけるための値や日付情報を設定します。
    *
    * @param studentDetail 受講生詳細
    * @return 登録情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    repository.registerStudent(student);
    studentDetail.getStudentCourseList().forEach(studentCourse -> {
      initStudentCourse(studentCourse, student);
      repository.registerStudentCourse(studentCourse);
    });
    return studentDetail;
  }
/*
  * 受講生コース情報を登録する際の初期情報を設定します。
  *
  * @param studentCourse 受講生コース情報
  * @param student 受講生
 */
  private static void initStudentCourse(StudentCourse studentCourse, Student student) {
    LocalDate now = LocalDate.now();

    studentCourse.setNameId(student.getNameId());
    studentCourse.setStartDate(now);
    studentCourse.setDeadline(now.plusYears(1));
  }
/*
  * 受講生詳細を更新します。
  * 受講生と受講生コース情報を個別に更新します。
  *
  * @param studentDetail 受講生詳細
 */
@Transactional
public StudentDetail updateStudent(StudentDetail studentDetail) {
  String studentId = studentDetail.getStudent().getNameId();
  repository.updateStudent(studentDetail.getStudent());
  studentDetail.getStudentCourseList().forEach(studentCourse -> repository.updateStudentCourse(studentCourse));
  return studentDetail;
  }
}
