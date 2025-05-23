package raisetech.student.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.student.management.controller.ExcepionHandler.ResourceNotFoundException;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;
import raisetech.student.management.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  @MockBean
  private StudentRepository repository;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


  @Test
  void 受講生詳細一覧検索_正常系_検索および全件取得が実行できること() throws Exception {
    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細検索_正常系_受講生詳細の検索が実行できて空で返ってくること() throws Exception {
    String studentId = "テストID";
    mockMvc.perform(get("/student/{studentId}", studentId))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudent(studentId);
  }

  @Test
  void 受講生詳細検索_異常系_存在しないIDを指定した場合エラーが表示されること() throws Exception {
    String invalidStudentId = "0";
    doThrow(new ResourceNotFoundException("入力されたIDは存在しません: " + invalidStudentId))
        .when(service).searchStudent(invalidStudentId);

    mockMvc.perform(get("/student/{studentId}", invalidStudentId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(
            "入力されたIDは存在しません: " + invalidStudentId));

    verify(service, times(1)).searchStudent(invalidStudentId);
  }

  @Test
  void 受講生登録_正常系_入力値に誤りがなかった場合正常に登録が実行できること() throws Exception {
    String requestBody = """
        {
            "student": {
                "name": "テスト名",
                "furigana": "テストメイ",
                "nickname": "てすと",
                "mailAddress": "test@email.com",
                "address": "東京",
                "age": 20,
                "gender": "男性",
                "remark": ""
            },
            "studentCourseList": [
                {
                    "courseName": "Java"
                }
            ]
        }
        """;

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    verify(service, times(1)).registerStudent(any());
  }

  @Test
  void 受講生登録_異常系_名前の入力漏れがあった場合にエラーが表示されること() throws Exception {
    String requestBody = """
        {
            "student": {
                "name": "",
                "furigana": "テストメイ",
                "nickname": "てすと",
                "mailAddress": "test@email.com",
                "address": "東京",
                "age": 20,
                "gender": "男性",
                "remark": ""
            },
            "studentCourseList": [
                {
                    "courseName": "Java"
                }
            ]
        }
        """;

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("student.name: 入力必須です。"));

    verify(service, times(0)).registerStudent(any());
  }

  @Test
  void 受講生登録_異常系_furiganaにカタカナ以外が入力された場合にエラーが表示されること()
      throws Exception {
    String requestBody = """
        {
            "student": {
                "name": "テスト名",
                "furigana": "test",
                "nickname": "てすと",
                "mailAddress": "test@email.com",
                "address": "東京",
                "age": 20,
                "gender": "男性",
                "remark": ""
            },
            "studentCourseList": [
                {
                    "courseName": "Java"
                }
            ]
        }
        """;

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("student.furigana: フリガナはカタカナで入力してください。"));

    verify(service, times(0)).registerStudent(any());
  }


  @Test
  void 受講生登録_異常系_メールアドレスの入力が不正な場合にエラーが表示されること()
      throws Exception {
    String requestBody = """
        {
            "student": {
                "name": "テスト名",
                "furigana": "テストメイ",
                "nickname": "てすと",
                "mailAddress": "testmail",
                "address": "東京",
                "age": 20,
                "gender": "男性",
                "remark": ""
            },
            "studentCourseList": [
                {
                    "courseName": "Java"
                }
            ]
        }
        """;

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("student.mailAddress: メールアドレスの形式が不正です。"));

    verify(service, times(0)).registerStudent(any());
  }

  @Test
  void 受講生コース情報登録_正常系_入力値が全て正しい場合に正常に登録が実行できること()
      throws Exception {
    String requestBody = """
        {
            "courseName": "Java"
        }
        """;

    String studentId = "999";

    mockMvc.perform(post("/registerStudentCourse/{studentId}", studentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    verify(service, times(1)).registerStudentCourse(eq(studentId), any());
  }

  @Test
  void 受講生コース情報登録_重複申し込みの場合にエラーが返されること() throws Exception {
    String studentId = "999";
    String requestBody = """
        {
            "courseName": "Java"
        }
        """;

    when(repository.isStudentRegisteredForCourse(eq(studentId), eq("Java"))).thenReturn(true);

    doThrow(new IllegalStateException("このコースには既に申し込んでいます: Java"))
        .when(service).registerStudentCourse(eq(studentId), any());

    mockMvc.perform(post("/registerStudentCourse/{studentId}", studentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("このコースには既に申し込んでいます: Java"));  // エラーメッセージの確認

    verify(service, times(1)).registerStudentCourse(eq(studentId), any());
  }

  @Test
  void 受講生詳細更新_正常系_入力値が全て正しい場合に正常に更新が実行できること() throws Exception {
    String requestBody = """
        {
            "student": {
                "name": "テスト名",
                "furigana": "テストメイ",
                "nickname": "てすと",
                "mailAddress": "test@email.com",
                "address": "東京",
                "age": 20,
                "gender": "男性",
                "remark": ""
            },
            "studentCourseList": [
                {
                    "courseId" : "テストコースID",
                    "studentId" : "テストID",
                    "courseName": "Java",
                    "startDate" : "2024-01-01T11:11:11.123456",
                    "deadline" : "2024-01-01T11:11:11.123456"
                }
            ]
            ,
            "applicationStatusList": [
                        {
                            "courseId": "テストコースID",
                            "studentId": "テストID",
                            "applicationStatus": "受講中"
                        }
                    ]
        }
        """;

    mockMvc.perform(put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生詳細削除_正常系_IDが正しい場合に正常に削除が実行できること() throws Exception {
    String studentId = "テストID";
    mockMvc.perform(delete("/deleteStudent/{studentId}", studentId))
        .andExpect(status().isOk());

    verify(service, times(1)).deleteStudent(studentId);
  }

  @Test
  void 受講生詳細削除_異常系_IDが存在しない場合にエラーが表示されること() throws Exception {
    String invalidStudentId = "0";
    doThrow(new ResourceNotFoundException("入力されたIDは存在しません: " + invalidStudentId))
        .when(service).deleteStudent(invalidStudentId);

    mockMvc.perform(delete("/deleteStudent/{studentId}", invalidStudentId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(
            "入力されたIDは存在しません: " + invalidStudentId));

    verify(service, times(1)).deleteStudent(invalidStudentId);

  }

  @Test
  void 受講生コース情報削除_正常系_IDが正しい場合に正常に削除が実行できること() throws Exception {
    String courseId = "テストコースID";
    mockMvc.perform(delete("/deleteStudentCourse/{courseId}", courseId))
        .andExpect(status().isOk());

    verify(service, times(1)).deleteStudentCourse(courseId);
  }

  @Test
  void 受講生コース情報削除_異常系_IDが存在しない場合にエラーが表示されること() throws Exception {
    String invalidCourseId = "0";
    doThrow(new ResourceNotFoundException("入力されたコースIDは存在しません: " + invalidCourseId))
        .when(service).deleteStudentCourse(invalidCourseId);

    mockMvc.perform(delete("/deleteStudentCourse/{courseId}", invalidCourseId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(
            "入力されたコースIDは存在しません: " + invalidCourseId));

    verify(service, times(1)).deleteStudentCourse(invalidCourseId);
  }
}
