package raisetech.student.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.controller.ExcepionHandler.TestException;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

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
  @Operation(summary = "受講生一覧の検索", description = "受講生一覧を全件検索します。")
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  @Operation(summary = "IDによる受講生検索", description = "受講生をIDから個別検索します。")
  @GetMapping("/student/{nameId}")
  public StudentDetail getStudent(
      @PathVariable @NotBlank @Pattern(regexp = "^\\d+$", message = "IDは数値のみ入力してください。") String nameId) {
    return service.searchStudent(nameId);
  }

  @Operation(summary = "受講生詳細の新規登録", description = "受講生詳細を新しく登録します。")
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responsStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responsStudentDetail);
    }

  @Operation(summary = "受講生詳細の更新", description = "受講生詳細の更新です。キャンセルフラグの更新もここで行います。")
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail updateStudentDetail = service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  @Operation(summary = "テスト用例外", description = "例外を発生させます。")
  @GetMapping("/testException")
  public void testException() throws TestException {
    throw new TestException("テスト用の例外です。");
  }
}
