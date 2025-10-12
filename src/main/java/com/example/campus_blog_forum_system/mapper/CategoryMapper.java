package com.example.campus_blog_forum_system.mapper;
import com.example.campus_blog_forum_system.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper
{
    @Insert("INSERT INTO category(category_name, category_alias, create_user, create_time, update_time) " +
            "VALUES(#{categoryName}, #{categoryAlias}, #{createUser}, #{createTime}, #{updateTime})")
    void add(Category category);
    //查询所有
    @Select("SELECT " +
            "id, " +
            "category_name AS categoryName, " +
            "category_alias AS categoryAlias, " +
            "create_user AS createUser, " +
            "create_time AS createTime, " +
            "update_time AS updateTime " +
            "FROM category WHERE create_user = #{userId}")
    List<Category> list(Integer userId);

    @Select("SELECT " +
            "id, " +
            "category_name AS categoryName, " +
            "category_alias AS categoryAlias, " +
            "create_user AS createUser, " +
            "create_time AS createTime, " +
            "update_time AS updateTime " +
            "FROM category WHERE id = #{id}")
    Category findUserById(Integer id);

    @Delete("DELETE FROM category WHERE id=#{id}")
    void delete(Integer id);

    @Update("update category set category_name=#{categoryName},category_alias=#{categoryAlias},update_time=#{updateTime} where id=#{id}")
    void update(Category category);

    @Select("SELECT * FROM category")
    List<Category> selectAll();

    @Insert("INSERT INTO category(category_name, category_alias, create_user, create_time, update_time) " +
            "VALUES(#{categoryName}, #{categoryAlias}, #{createUser}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteById(Integer id);

}
