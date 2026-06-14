package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.Student;
import com.exam.mapper.StudentMapper;
import com.exam.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public PageResult<Student> list(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Student> records = studentMapper.list(keyword, offset, size);
        long total = studentMapper.count(keyword);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Student getById(Integer studentId) {
        Student student = studentMapper.findById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在: " + studentId);
        }
        return student;
    }

    @Override
    public Student create(Student student) {
        student.setRole("2"); // 固定角色为学生
        studentMapper.insert(student);
        return student;
    }

    @Override
    public Student update(Student student) {
        getById(student.getStudentId());
        studentMapper.update(student);
        if (student.getPwd() != null && !student.getPwd().isEmpty()) {
            studentMapper.updatePassword(student.getStudentId(), student.getPwd());
        }
        return student;
    }

    @Override
    public void delete(Integer studentId) {
        getById(studentId);
        studentMapper.delete(studentId);
    }
}
