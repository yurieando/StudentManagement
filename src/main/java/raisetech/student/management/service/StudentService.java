package raisetech.student.management.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    //３０代以上の人のみを抽出する
    return repository.search().stream()
        .filter(student -> student.getAge() >= 30)
        .collect(Collectors.toList());
  }
  public List<StudentCourse> searchCourseList() {
    //Javaコースのみを抽出する
    return repository.searchCourse().stream()
        .filter(course -> "Java".equals(course.getCourse()))
        .collect(Collectors.toList());
  }
}