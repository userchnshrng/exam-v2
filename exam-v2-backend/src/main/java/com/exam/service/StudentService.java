package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Student;

public interface StudentService {

    PageResult<Student> list(String keyword, int page, int size);

    Student getById(Integer studentId);

    Student create(Student student);

    Student update(Student student);

    void delete(Integer studentId);
}
