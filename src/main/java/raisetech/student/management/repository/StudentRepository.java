package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourses;
import raisetech.student.management.data.StudentsCourses;

/**
 * 受講生情報を扱うリポジトリ
 *
 * 全件検索や単一条件での検索、コース情報の検索が行えるクラスです
 */
@Mapper//Mybatisが自動で処理をしてくれるようになる
public interface StudentRepository {
  @Select("SELECT * FROM students")
  List<Student> search();

  /**
   * 全件検索します
   * @return　全件検索した受講生情報の一覧
   */
  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchCourse();
}
