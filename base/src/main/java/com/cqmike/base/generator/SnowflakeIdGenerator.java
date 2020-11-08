package com.cqmike.base.generator;

import com.cqmike.base.util.SpringUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @program: common
 * @description: 雪花id生成
 * @author: chen qi
 * @create: 2020-11-08 11:05
 **/
public class SnowflakeIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        SnowflakeIdWorker snowflakeIdWorker = SpringUtil.getBean(SnowflakeIdWorker.class);
        return snowflakeIdWorker.nextId();
    }

}