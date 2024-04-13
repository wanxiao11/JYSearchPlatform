package com.springbootinit.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口<新接入的数据必须实现>
 *
 * @param <T>
 */
public interface DataSource<T> {
    /**
     *搜索
     *
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> dosearch(String searchText, long pageNum, long pageSize);
}
