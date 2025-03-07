package raisetech.student.management.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.ExcepionHandler.ResourceNotFoundException;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;


/**
 * 受講生情報を扱うサービス 受講生の検索や登録、更新処理を行ないます。
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

  /**
   * 受講生詳細の一覧を検索します。 全件検索を行うので、条件指定は行ないません。
   *
   * @return 受講生詳細一覧（全件）
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.allSearchStudentList();
    List<StudentCourse> studentCourseList = repository.allSearchStudentCourseList();
    List<ApplicationStatus> applicationStatusList = repository.allSearchApplicationStatusList();

    return converter.convertStudentDetails(studentList, studentCourseList, applicationStatusList);
  }

  /**
   * 受講生詳細の検索です。 IDに紐づく任意の受講生の情報を取得した後、その受講生に紐づく受講生コース情報を取得して設定します。
   *
   * @param nameId 受講生ID
   * @return 受講生詳細情報
   */
  public StudentDetail searchStudent(String nameId) {
    Student student = repository.searchStudent(nameId);

    Optional<Student> existingStudent = repository.findByNameId(nameId);
    if (existingStudent.isEmpty()) {
      throw new ResourceNotFoundException("入力されたIDは存在しません: " + nameId);
    } else {
      List<StudentCourse> studentCourseList = repository.searchStudentCourseList(
          student.getNameId());
      List<String> courseIdList = studentCourseList.stream()
          .map(StudentCourse::getCourseId)
          .collect(Collectors.toList());

      List<ApplicationStatus> applicationStatusList = courseIdList.stream()
          .flatMap(courseId -> repository.searchApplicationStatusList(courseId).stream())
          .collect(Collectors.toList());

      return new StudentDetail(student, studentCourseList, applicationStatusList);
    }
  }


  /**
   * 受講生詳細を登録します。 受講生情報と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づけるための値や日付情報を設定します。
   * 受講生コース情報を登録すると共に、受講状況を仮申込で登録します。
   *
   * @param studentDetail 受講生詳細
   * @return 登録情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    List<ApplicationStatus> applicationStatusList = new ArrayList<>();

    repository.registerStudent(student);

    studentDetail.getStudentCourseList().forEach(studentCourse -> {
      initStudentCourse(studentCourse, student);
      repository.registerStudentCourse(studentCourse);

      String courseId = studentCourse.getCourseId();
      repository.registerApplicationStatus(courseId, student.getNameId());
      applicationStatusList.add(new ApplicationStatus(courseId, student.getNameId(), "仮申込"));
    });

    studentDetail.setApplicationStatusList(applicationStatusList);

    return studentDetail;
  }

  /**
   * 受講生コース情報を登録します。 受講生コース情報には受講生情報を紐づけるための値や日付情報を設定します。 受講生コース情報を登録すると共に、受講状況を仮申込で登録します。
   *
   * @param studentCourse 受講生コース情報
   */
  @Transactional
  public StudentCourse registerStudentCourse(String nameId, StudentCourse studentCourse) {
    List<ApplicationStatus> applicationStatusList = new ArrayList<>();

    Optional<Student> studentOptional = repository.findByNameId(nameId);
    if (studentOptional.isEmpty()) {
      throw new ResourceNotFoundException("入力されたIDは存在しません: " + nameId);
    }

    Student student = studentOptional.get();

    boolean isAlreadyRegistered = repository.isStudentRegisteredForCourse(nameId,
        studentCourse.getCourseName());
    if (isAlreadyRegistered) {
      throw new IllegalStateException(
          "このコースには既に申し込んでいます: " + studentCourse.getCourseName());
    }

    initStudentCourse(studentCourse, student);
    repository.registerStudentCourse(studentCourse);

    String courseId = studentCourse.getCourseId();
    repository.registerApplicationStatus(courseId, student.getNameId());
    applicationStatusList.add(new ApplicationStatus(courseId, student.getNameId(), "仮申込"));

    return studentCourse;
  }


  /**
   * 受講生コース情報を登録する際の初期情報を設定します。
   *
   * @param studentCourse 受講生コース情報
   * @param student       受講生
   */
  private static void initStudentCourse(StudentCourse studentCourse, Student student) {
    LocalDate now = LocalDate.now();

    studentCourse.setNameId(student.getNameId());
    studentCourse.setStartDate(now);
    studentCourse.setDeadline(now.plusYears(1));
  }

  /**
   * 受講生詳細を更新します。 受講生と受講生コース情報を個別に更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  public StudentDetail updateStudent(StudentDetail studentDetail) {
    String nameId = studentDetail.getStudent().getNameId();

    Optional<Student> existingStudent = repository.findByNameId(nameId);
    if (existingStudent.isEmpty()) {
      throw new ResourceNotFoundException("入力されたIDは存在しません: " + nameId);
    } else {
      repository.updateStudent(studentDetail.getStudent());

      studentDetail.getStudentCourseList().forEach(studentCourse -> {
        repository.updateStudentCourse(studentCourse);
      });
      studentDetail.getApplicationStatusList().forEach(applicationStatus -> {
        repository.updateApplicationStatus(applicationStatus);
      });

      return studentDetail;
    }
  }

  /**
   * 受講生詳細を削除します。同時に受講生コース情報と受講状況も削除します。
   *
   * @param nameId 受講生ID
   */
  public void deleteStudent(String nameId) {
    Optional<Student> existingStudent = repository.findByNameId(nameId);
    if (existingStudent.isEmpty()) {
      throw new ResourceNotFoundException("入力されたIDは存在しません: " + nameId);
    } else {
      repository.deleteStudent(nameId);
      List<StudentCourse> studentCourseList = repository.searchStudentCourseList(nameId);
      studentCourseList.forEach(studentCourse -> {
        String courseId = studentCourse.getCourseId();
        repository.deleteStudentCourse(courseId);
        repository.deleteApplicationStatus(courseId);
      });
    }
  }

  /**
   * 受講生コース情報を削除します。同時に受講状況も削除します。
   *
   * @param courseId コースID
   */
  public void deleteStudentCourse(String courseId) {
    Optional<StudentCourse> studentCourseOptional = repository.findByCourseId(courseId);
    if (studentCourseOptional.isEmpty()) {
      throw new ResourceNotFoundException("入力されたコースIDは存在しません: " + courseId);
    }
    repository.deleteStudentCourse(courseId);
    repository.deleteApplicationStatus(courseId);
  }
}
