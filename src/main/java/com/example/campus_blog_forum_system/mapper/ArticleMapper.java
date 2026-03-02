package com.example.campus_blog_forum_system.mapper;

import com.example.campus_blog_forum_system.pojo.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper
{
    @Insert("INSERT INTO article(" +
            "title, content, cover_img, state, category_id, " +
            "create_user, creator_type, create_time, update_time" +
            ") VALUES (" +
            "#{title}, #{content}, #{coverImg}, #{state}, #{categoryId}, " +
            "#{createUser}, #{creatorType}, #{createTime}, #{updateTime}" +
            ")")
    void add(Article article);

    //条件分页
    @Select("SELECT * FROM article WHERE create_user = #{userId}")
    List<Article> list(String userId, Integer categoryId, String state);

    @Select("""
    <script>
    SELECT id, title, content, cover_img, state, category_id, create_user, create_time, update_time 
    FROM article 
    WHERE 1=1
    <if test='categoryId != null'>
        AND category_id = #{categoryId}
    </if>
    <if test='state != null and state != ""'>
        AND state = #{state}
    </if>
    ORDER BY create_time DESC
    </script>
    """)
    List<Article> listAll(@Param("categoryId") Integer categoryId,
                          @Param("state") String state);

    @Delete("DELETE FROM article WHERE id = #{id}")
    void deleteById(Integer id);

    @Select("SELECT * FROM article WHERE category_id = #{categoryId}")
    List<Article> findCategoryId(Integer categoryId);


    @Select("SELECT * FROM article WHERE create_user = #{userId}")
    List<Article> findByCreateUser(Integer userId);

    @Update("""
    UPDATE article 
    SET title = #{title}, 
        content = #{content}, 
        cover_img = #{coverImg}, 
        state = #{state}, 
        category_id = #{categoryId},
        update_time = #{updateTime}
    WHERE id = #{id}
    """)
    void update(Article article);
    //List<Article> listAll(Integer categoryId, String state);

    // 根据用户ID和条件查询文章（用于我的文章列表）
    @Select("""
        <script>
        SELECT id, title, content, cover_img, state, category_id, create_user, create_time, update_time 
        FROM article 
        WHERE create_user = #{userId}
        <if test='categoryId != null'>
            AND category_id = #{categoryId}
        </if>
        <if test='state != null and state != ""'>
            AND state = #{state}
        </if>
        ORDER BY create_time DESC
        </script>
        """)
    List<Article> listByUserId(@Param("userId") Integer userId,
                               @Param("categoryId") Integer categoryId,
                               @Param("state") String state);


    @Select("SELECT * FROM article WHERE id = #{id}")
    Article findById(Integer id);

    // 查询热门文章（热度 > 5）
    @Select("SELECT * FROM article WHERE state = '已发布' AND popularity > 5 ORDER BY popularity DESC, create_time DESC")
    List<Article> findHotArticles();

    // 带分类筛选的热门文章
    @Select("""
    <script>
    SELECT * FROM article 
    WHERE state = '已发布' 
    AND popularity > 5
    <if test='categoryId != null'>
        AND category_id = #{categoryId}
    </if>
    ORDER BY popularity DESC, create_time DESC
    </script>
    """)
    List<Article> findHotArticlesByCategory(@Param("categoryId") Integer categoryId);

    // 分页查询热门文章
    @Select("""
    <script>
    SELECT * FROM article 
    WHERE state = '已发布' 
    AND popularity > 5
    <if test='categoryId != null'>
        AND category_id = #{categoryId}
    </if>
    ORDER BY popularity DESC, create_time DESC
    </script>
    """)
    List<Article> findHotArticlesWithPage(@Param("categoryId") Integer categoryId);

}
