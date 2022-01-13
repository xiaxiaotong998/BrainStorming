package com.haoran.Brainstorming.mapper;

import com.haoran.Brainstorming.model.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


public interface NotificationMapper extends BaseMapper<Notification> {

    List<Map<String, Object>> selectByUserId(@Param("userId") Integer userId, @Param("read") Boolean read, @Param
            ("limit") Integer limit);

    @Select("select count(1) from notification where target_user_id = #{userId} and `read` = false")
    long countNotRead(@Param("userId") Integer userId);

    @Update("update notification set `read` = true where target_user_id = #{targetUserId}")
    void updateNotificationStatus(@Param("targetUserId") Integer targetUserId);
}
