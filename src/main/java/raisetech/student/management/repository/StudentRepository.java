package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.management.Student;
import raisetech.student.management.StudentCourse;

@Mapper//Mybatisが自動で処理をしてくれるようになる
public interface StudentRepository {
  @Select("SELECT * FROM students")
  List<Student> search();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchCourse();
}