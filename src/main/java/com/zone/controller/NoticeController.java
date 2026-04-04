package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.NoticeDTO;
import com.zone.domain.dto.NoticePageQueryDTO;
import com.zone.domain.vo.NoticeVO;
import com.zone.domain.vo.UserSelectVO;
import com.zone.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@Tag(name = "通知公告接口")
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // ================== B端：园区管理接口 ==================

    /**
     * B端-新增或修改公告(草稿)
     * @param dto
     * @return
     */
    @PostMapping("/save")
    @Operation(summary = "B端-新增或修改公告(草稿)")
    public Result<String> save(@RequestBody NoticeDTO dto) {
        return noticeService.saveOrUpdateNotice(dto) ? Result.success("保存成功") : Result.sysError("保存失败");
    }

    /**
     * B端-发布公告
     * @param id
     * @return
     */
    @PutMapping("/publish/{id}")
    @Operation(summary = "B端-发布公告", description = "将草稿或已撤回的公告正式发布")
    public Result<String> publish(@PathVariable Long id) {
        return noticeService.publish(id) ? Result.success("发布成功") : Result.sysError("操作失败");
    }

    /**
     * B端-撤回公告
     * @param id
     * @return
     */
    @PutMapping("/recall/{id}")
    @Operation(summary = "B端-撤回公告", description = "撤回后C端不可见，用户收件箱记录将被移除")
    public Result<String> recall(@PathVariable Long id) {
        return noticeService.recall(id) ? Result.success("已撤回") : Result.sysError("操作失败");
    }

    /**
     * B端-归档公告
     * @param id
     * @return
     */
    @PutMapping("/archive/{id}")
    @Operation(summary = "B端-归档公告")
    public Result<String> archive(@PathVariable Long id) {
        return noticeService.archive(id) ? Result.success("已归档") : Result.sysError("操作失败");
    }

    /**
     * B端-获取公告列表
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "B端-全量分页列表")
    public Result<PageResult<NoticeVO>> getPage(NoticePageQueryDTO dto) {
        return Result.success(noticeService.getAdminPage(dto));
    }

    /**
     * B端-获取所有正常状态(在职/未封禁)的用户ID
     */
    @GetMapping("/active-users")
    @Operation(summary = "B端-获取所有正常状态(在职/未封禁)的用户ID")
    public Result<List<UserSelectVO>> getActiveUsers() {
        return Result.success(noticeService.getActiveUserSelect());
    }

    // ================== C端：企业用户接口 ==================

    /**
     * C端-获取政策/动态/公告列表
     * @param dto
     * @return
     */
    @GetMapping("/list/public")
    @Operation(summary = "C端-获取政策/动态/公告列表", description = "仅查询 status=1 且 visibility=1 的数据")
    public Result<PageResult<NoticeVO>> getPublicList(NoticePageQueryDTO dto) {
        return Result.success(noticeService.getPublicPage(dto));
    }

    /**
     * C端-获取指定用户可见的已发布公告列表
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "通用-获取公告详情", description = "调用详情时，如果是C端用户则自动标记为已读")
    public Result<NoticeVO> getDetail(@PathVariable Long id) {
        return Result.success(noticeService.getDetail(id));
    }

    /**
     * C端-获取当前用户未读消息数
     * @return
     */
    @GetMapping("/unread-count")
    @Operation(summary = "C端-获取当前用户未读消息数")
    public Result<Integer> getUnreadCount() {
        return Result.success(noticeService.getUnreadCount());
    }
}