package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

/*
 *受講詳細を受講生や受講生コース情報、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生情報と受講生コース情報を結合します。 受講生コース上hじょうは受講生に対して複数存在するので、ループを回して受講生詳細情報を組み立てる。
   *
   * @param studentList          受講生一覧
   * @param studentCourseList    受講生コース情報一覧
   * @param applicationStatusMap 受講状況一覧
   * @return 受講生詳細情報一覧
   */
  public List<StudentDetail> convertStudentDetails(
      List<Student> studentList,
      List<StudentCourse> studentCourseList,
      Map<String, String> applicationStatusMap) {

    List<StudentDetail> studentDetails = new ArrayList<>();

    // applicationStatusMap の中身を表示
    System.out.println("Application Status Map: " + applicationStatusMap);

    studentList.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<StudentCourse> convertStudentCourseList = studentCourseList.stream()
          .filter(studentCourse -> student.getNameId().equals(studentCourse.getNameId()))
          .collect(Collectors.toList());
      studentDetail.setStudentCourseList(convertStudentCourseList);

      Map<String, String> convertApplicationStatusMap = new HashMap<>();
      for (StudentCourse course : convertStudentCourseList) {
        // 各 Course ID を表示
        System.out.println("Course ID: " + course.getCourseId());

        // applicationStatusMap から値を取得して表示
        String status = applicationStatusMap.get(course.getCourseId());
        System.out.println("Status for Course ID " + course.getCourseId() + ": " + status);

        convertApplicationStatusMap.put(course.getCourseId(), status);
      }

      studentDetail.setApplicationStatusMap(convertApplicationStatusMap);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
