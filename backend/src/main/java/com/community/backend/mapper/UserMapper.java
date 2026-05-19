package com.community.backend.mapper;

import com.community.backend.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    @Insert("INSERT INTO user(username, password, nickname) VALUES(#{username}, #{password}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}