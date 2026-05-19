package com.community.backend.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeRecordMapper {
    
    @Insert("INSERT INTO like_record(user_id, post_id) VALUES(#{userId}, #{postId})")
    void insert(@Param("userId") Long userId, @Param("postId") Long postId);
    
    @Select("SELECT id FROM like_record WHERE user_id = #{userId} AND post_id = #{postId}")
    Long exists(@Param("userId") Long userId, @Param("postId") Long postId);
}