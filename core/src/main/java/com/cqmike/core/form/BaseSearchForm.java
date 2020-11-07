package com.cqmike.core.form;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @program: iot
 * @ClassName: BaseSearchForm
 * @Description: 搜索基类
 * @Author: chen qi
 * @Date: 2020/2/20 21:17
 * @Version: 1.0
 **/
public class BaseSearchForm implements Serializable {

    @ApiModelProperty(value = "分页参数page", example = "1")
    private Integer page;

    @ApiModelProperty(value = "分页参数size", example = "10")
    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
