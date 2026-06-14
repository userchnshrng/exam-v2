package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.Teacher;
import com.exam.mapper.TeacherMapper;
import com.exam.service.TeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    public PageResult<Teacher> list(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Teacher> records = teacherMapper.list(keyword, offset, size);
        long total = teacherMapper.count(keyword);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Teacher getById(Integer teacherId) {
        Teacher teacher = teacherMapper.findById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在: " + teacherId);
        }
        return teacher;
    }

    @Override
    public Teacher create(Teacher teacher) {
        teacher.setRole("1"); // 固定角色为教师
        teacherMapper.insert(teacher);
        return teacher;
    }

    @Override
    public Teacher update(Teacher teacher) {
        getById(teacher.getTeacherId()); // 校验存在
        teacherMapper.update(teacher);
        // 如果传了密码，单独更新
        if (teacher.getPwd() != null && !teacher.getPwd().isEmpty()) {
            teacherMapper.updatePassword(teacher.getTeacherId(), teacher.getPwd());
        }
        return teacher;
    }

    @Override
    public void delete(Integer teacherId) {
        getById(teacherId);
        teacherMapper.delete(teacherId);
    }
}
