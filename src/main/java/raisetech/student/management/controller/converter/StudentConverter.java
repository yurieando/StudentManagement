package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

/*
 *受講詳細を受講生や受講生コース情報、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class StudentConverter {

  /*
    * 受講生情報と受講生コース情報を結合します。
    * 受講生コース上hじょうは受講生に対して複数存在するので、ループを回して受講生詳細情報を組み立てる。
    *
    * @param studentList 受講生一覧
    * @param studentsCourses 受講生コース情報一覧
    * @return 受講生詳細情報一覧
   */
  public List<StudentDetail> convertStudentDetails(List<Student> studentList, List<StudentCourse> studentCourseList) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    studentList.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<StudentCourse> convertStudentCourseList = studentCourseList.stream()
          .filter(studentCourse -> student.getNameId().equals(studentCourse.getNameId()))
          .collect(Collectors.toList());
      studentDetail.setStudentCourseList(convertStudentCourseList);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
