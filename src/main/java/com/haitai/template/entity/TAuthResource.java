package com.haitai.template.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author bin.wang
 * @since 2020-07-23
 */
public class TAuthResource extends Model<TAuthResource> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 前端资源url
     */
    private String resourceUrl;

    /**
     * 左侧资源菜单名称
     */
    private String resourceName;

    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 图标
     */
    private String iconType;

    /**
     * 排序  数值越大越靠后
     */
    private Integer sort;

    /**
     * 二级排序
     */
    private Integer secondSort;

    /**
     * 状态 0正常，1禁用，2删除
     */
    private Integer checkStatus;

    /**
     * 资源描述信息
     */
    private String resourceDescription;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gmtCreate;

    /**
     * 最后编辑时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gmtModified;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSecondSort() {
        return secondSort;
    }

    public void setSecondSort(Integer secondSort) {
        this.secondSort = secondSort;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getResourceDescription() {
        return resourceDescription;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "TAuthResource{" +
        ", id=" + id +
        ", resourceUrl=" + resourceUrl +
        ", resourceName=" + resourceName +
        ", parentId=" + parentId +
        ", iconType=" + iconType +
        ", sort=" + sort +
        ", secondSort=" + secondSort +
        ", checkStatus=" + checkStatus +
        ", resourceDescription=" + resourceDescription +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        "}";
    }
}
