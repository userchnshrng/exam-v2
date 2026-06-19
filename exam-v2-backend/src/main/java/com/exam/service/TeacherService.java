package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Teacher;

public interface TeacherService {

    PageResult<Teacher> list(String keyword, int page, int size);

    Teacher getById(Integer teacherId);

    /** 按工号查询（公开接口用，不存在返回 null 不抛异常） */
    Teacher findByTeacherId(Integer teacherId);

    Teacher create(Teacher teacher);

    Teacher update(Teacher teacher);

    void delete(Integer teacherId);
}
