package com.cqmike.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: iot
 * @Description 基类
 * @Author 陈琪
 * @Date 2020-2-16 0016 16:30
 * @Version 1.0
 **/
@MappedSuperclass
public class BaseEntity implements Serializable {

    @ApiModelProperty(
            hidden = true
    )
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "jpa-uuid")
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid")
    private String id;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    @Column(
            name = "create_time",
            columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'"
    )
    @ApiModelProperty(
            hidden = true
    )
    @CreationTimestamp
    private Date createTime;

    @Column(
            name = "create_user_name",
            columnDefinition = "varchar(32) COMMENT '创建用户'"
    )
    @ApiModelProperty(
            hidden = true
    )
    private String createUserName;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    @Column(
            name = "update_time",
            columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '最后修改时间'"
    )
    @ApiModelProperty(
            hidden = true
    )
    @UpdateTimestamp
    private Date updateTime;

    @Column(
            name = "update_user_name",
            columnDefinition = "varchar(32) COMMENT '最后修改用户'"
    )
    @ApiModelProperty(
            hidden = true
    )
    private String updateUserName;

    @PrePersist
    protected void prePersist() {
        //todo 时间工具类
        Date now = new Date();
        if (createTime == null) {
            createTime = now;
        }

        if (updateTime == null) {
            updateTime = now;
        }
    }

    public BaseEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }
}
