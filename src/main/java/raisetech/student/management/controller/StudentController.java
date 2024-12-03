package raisetech.student.management.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.RunException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.controller.ExcepionHandler.TestException;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;
import org.springframework.ui.Model;

/**
 * 受講生情報の検索や登録、更新、削除をREST APIとして実行されるControllerです。
 */
@Validated
@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
   }

  /**
   * 受講生詳細の一覧検索です。
   * 全件検索を行うので、条件指定は行いません。
   * @return　受講生一覧（全件）
   */
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  /*
    * 受講生詳細の検索です。
    * IDに紐づく任意の受講生の情報を取得します。
    *
    * @param nameId 受講生ID
    * @return 受講生詳細
   */
  @GetMapping("/student/{nameId}")
  public StudentDetail getStudent(
      @PathVariable @NotBlank @Pattern(regexp = "^\\d+$") String nameId) {
    return service.searchStudent(nameId);
  }

  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudentCourseList(Arrays.asList(new StudentCourse()));
    model.addAttribute("studentDetail", studentDetail);
    return "registerStudent";
  }

  /*
    * 受講生詳細の登録です。
    * 受講生情報と受講生コース情報を登録します。
    *
    * @param studentDetail 受講生情報
    * @return　実行結果
   */
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responsStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responsStudentDetail);
    }
/*
  * 受講生詳細の更新です。
  * キャンセルフラグの更新もここで行います。（論理削除）
  *
  * @param studentDetail 受講生詳細
  * @return 実行結果
 */
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  @GetMapping("/testException")
  public void testException() throws TestException {
    throw new TestException("テスト用の例外です。");
  }
}
