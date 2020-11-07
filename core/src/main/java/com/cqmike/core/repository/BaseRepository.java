package com.cqmike.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

/**
 * @program: iot
 * @Description BaseRepository基类仓库   公共repository 不创建spring实例 @NoRepositoryBean
 * @Author 陈琪
 * @Date 2020-2-16 0016 22:38
 * @Version 1.0
 **/
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     *  根据id批量删除
     * @param ids
     * @return long
     */
    long deleteByIdIn(@NonNull Iterable<ID> ids);

}
