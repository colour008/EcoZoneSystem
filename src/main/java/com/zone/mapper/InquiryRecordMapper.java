package com.zone.mapper;

import com.zone.domain.entity.InquiryRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface InquiryRecordMapper {
    // 添加一条记录
    @Insert("INSERT INTO biz_inquiry_record (inquiry_id, handler_id, action_type, content, create_time) " +
            "VALUES (#{inquiryId}, #{handlerId}, #{actionType}, #{content}, NOW())")
    int insert(InquiryRecord record);

    // 根据意向ID查询记录
    @Select("SELECT r.*, u.real_name as handlerName FROM biz_inquiry_record r " +
            "LEFT JOIN sys_user u ON r.handler_id = u.id " +
            "WHERE r.inquiry_id = #{inquiryId} ORDER BY r.create_time DESC")
    List<InquiryRecord> selectByInquiryId(Long inquiryId);
}