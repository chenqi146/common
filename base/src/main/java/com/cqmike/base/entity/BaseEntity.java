package com.cqmike.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: iot
 * @Description 基类
 * @Author 陈琪
 * @Date 2020-2-16 0016 16:30
 * @Version 1.0
 **/
@MappedSuperclass
public abstract class BaseEntity extends AbstractAggregateRoot implements Serializable {

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "snowflake", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "snowflake", strategy = "com.cqmike.base.generator.SnowflakeIdGenerator")
    protected Long id;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    @Column(
            name = "create_at",
            columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'"
    )
    @CreationTimestamp
    protected LocalDateTime createAt;

    @Column(
            name = "create_by",
            columnDefinition = "varchar(32) DEFAULT '' COMMENT '创建用户'"
    )
    protected String createBy;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    @Column(
            name = "update_at",
            columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '最后修改时间'"
    )
    @UpdateTimestamp
    protected LocalDateTime updateAt;
    @Column(
            name = "update_by",
            columnDefinition = "varchar(32) DEFAULT '' COMMENT '最后修改用户'",
            length = 32
    )
    protected String updateBy;

    @PrePersist
    protected void prePersist() {
        if (createAt == null) {
            createAt = LocalDateTime.now();
        }

        if (updateAt == null) {
            updateAt = LocalDateTime.now();
        }
    }

    public BaseEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createTime) {
        this.createAt = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createUser) {
        this.createBy = createUser;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateTime) {
        this.updateAt = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateUser) {
        this.updateBy = updateUser;
    }
}
