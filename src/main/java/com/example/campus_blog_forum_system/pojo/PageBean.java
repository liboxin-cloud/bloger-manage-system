package com.example.campus_blog_forum_system.pojo;

import lombok.Data;
import java.util.List;

@Data
public class PageBean<T>
{
    private long total;        // 总记录数
    private long pageNum;      // 当前页码
    private long pageSize;     // 每页大小
    private long pages;        // 总页数
    private List<T> items;     // 当前页数据
    private boolean hasNextPage;    // 是否有下一页
    private boolean hasPreviousPage; // 是否有上一页

    // 静态方法，用于从PageInfo转换为PageBean
    public static <T> PageBean<T> fromPageInfo(com.github.pagehelper.PageInfo<T> pageInfo) {
        PageBean<T> pageBean = new PageBean<>();
        pageBean.total = pageInfo.getTotal();
        pageBean.pageNum = pageInfo.getPageNum();
        pageBean.pageSize = pageInfo.getPageSize();
        pageBean.pages = pageInfo.getPages();
        pageBean.items = pageInfo.getList();
        pageBean.hasNextPage = pageInfo.isHasNextPage();
        pageBean.hasPreviousPage = pageInfo.isHasPreviousPage();
        return pageBean;
    }

    // 提供一个空的PageBean实例方法
    public static <T> PageBean<T> emptyPageBean() {
        PageBean<T> pageBean = new PageBean<>();
        pageBean.total = 0;
        pageBean.pageNum = 1;
        pageBean.pageSize = 10;
        pageBean.pages = 0;
        pageBean.items = List.of();
        pageBean.hasNextPage = false;
        pageBean.hasPreviousPage = false;
        return pageBean;
    }
}