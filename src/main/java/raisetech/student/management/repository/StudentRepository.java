package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper//Mybatisが自動で処理をしてくれるようになる
public interface StudentRepository {

  /*
    * 受講生一覧を検索します。
    * @return 受講生一覧（全件）
   */
  @Select("SELECT * FROM students")
  List<Student> search();

  /*
    * 受講生情報の検索です。
    * IDに紐づく任意の受講生の情報を取得します。
    *
    * @param nameId 受講生ID
    * @return 受講生情報
   */
  @Select("SELECT * FROM students WHERE name_id = #{nameId}")
  Student searchStudent(String nameId);

  /*
    * 受講生コース情報の検索です。
    * @return 受講生コース情報(全件)
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentCourseList();

  /*
  *受講生IDに紐づく受講生コース情報を検索します。
  *
  * @param nameId 受講生ID
  * @return 受講生IDに紐づく受講生コース情報
   */
  @Select("SELECT * FROM students_courses WHERE name_id = #{nameId}")
  List<StudentCourse> searchStudentCourse(String nameId);

  /**
   * 受講生情報を新規登録します
   * @param student 受講生情報
   */
@Insert("INSERT INTO students(name, furigana, nickname, mail_address, address, age, gender, remark, is_deleted) "
    + "VALUES(#{name}, #{furigana}, #{nickname}, #{mailAddress}, #{address}, #{age}, #{gender}, #{remark}, false)")

@Options(useGeneratedKeys = true, keyProperty = "nameId")
void registerStudent(Student student);

/*
* 受講生コース情報を新規登録します
* IDに関しては自動採番を行います。
 */

@Insert("INSERT INTO students_courses(name_id, course, start_date, deadline)"
    + "VALUES(#{nameId}, #{course}, #{startDate}, #{deadline})")

@Options(useGeneratedKeys = true, keyProperty = "courseId")
void registerStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生情報を更新します
   * @param student 受講生
   */
  @Update("UPDATE students SET name = #{name}, furigana = #{furigana}, nickname = #{nickname}, mail_address = #{mailAddress},"
      + " address = #{address}, age = #{age}, gender = #{gender}, remark = #{remark}, is_deleted = #{isDeleted} WHERE name_id = #{nameId}")
  void updateStudent(Student student);


  /**
   * 受講生コース情報を更新します
   * @param studentCourse 受講生コース
   */
  @Update("UPDATE students_courses SET course = #{course} WHERE name_id = #{nameId}")
  void updateStudentCourse(StudentCourse studentCourse);
}


