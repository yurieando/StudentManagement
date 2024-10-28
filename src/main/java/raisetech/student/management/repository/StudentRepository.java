package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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

  /**
   * 受講生情報を新規登録します
   * @param student 受講生情報
   */
@Insert("INSERT INTO students(name, furigana, nickname, mail_address, address, age, gender, remark, is_deleted) "
    + "VALUES(#{name}, #{furigana}, #{nickname}, #{mailAddress}, #{address}, #{age}, #{gender}, #{remark}, false)")
@Options(useGeneratedKeys = true, keyProperty = "nameId")
void registerStudent(Student student);

@Insert("INSERT INTO students_courses(name_id, course, start_date, deadline)"
    + "VALUES(#{nameId}, #{course}, #{startDate}, #{deadline})")
@Options(useGeneratedKeys = true, keyProperty = "courseId")
void registerStudentsCourses(StudentsCourses studentsCourses);
}
