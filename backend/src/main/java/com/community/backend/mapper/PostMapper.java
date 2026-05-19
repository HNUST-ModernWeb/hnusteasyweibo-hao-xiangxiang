package com.community.backend.mapper;

import com.community.backend.entity.Post;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PostMapper {
    
    @Select("SELECT p.*, u.username, u.nickname FROM post p LEFT JOIN user u ON p.user_id = u.id ORDER BY p.created_at DESC")
    List<Post> findAll();
    
    @Insert("INSERT INTO post(content, images, user_id) VALUES(#{content}, #{images}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);
    
    @Update("UPDATE post SET likes = likes + 1 WHERE id = #{id}")
    int addLike(@Param("id") Long id);
    
    @Select("SELECT likes FROM post WHERE id = #{id}")
    Integer getLikes(@Param("id") Long id);
}