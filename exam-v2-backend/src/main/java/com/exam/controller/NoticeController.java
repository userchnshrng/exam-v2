package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.entity.Notice;
import com.exam.service.NoticeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /** 分页列表 + 搜索 */
    @GetMapping
    public ApiResponse<PageResult<Notice>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(noticeService.list(keyword, page, size));
    }

    /** 查单条 */
    @GetMapping("/{id}")
    public ApiResponse<Notice> getById(@PathVariable("id") Integer id) {
        return ApiResponse.success(noticeService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public ApiResponse<Notice> create(@RequestBody Notice notice) {
        return ApiResponse.success(noticeService.create(notice));
    }

    /** 更新 */
    @PutMapping("/{id}")
    public ApiResponse<Notice> update(@PathVariable("id") Integer id, @RequestBody Notice notice) {
        notice.setNoticeId(id);
        return ApiResponse.success(noticeService.update(notice));
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Integer id) {
        noticeService.delete(id);
        return ApiResponse.success(null);
    }
}
