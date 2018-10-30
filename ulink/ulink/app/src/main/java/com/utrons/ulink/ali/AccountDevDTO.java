package com.utrons.ulink.ali;

import java.util.Date;

public class AccountDevDTO {
    /**
     * 用户的身份ID
     */
    private String identityId;
    /**
     * 设备的iotId
     */
    private String iotId;
    /**
     * 设备的productKey
     */
    private String productKey;
    /**
     * 设备的deviceName
     */
    private String deviceName;
    /**
     * 设备的产品名称
     */
    private String productName;
    /**
     * 设备的产品图片
     */
    private String productImage;
    /**
     * 设备的产品型号
     */
    private String productModel;
    /**
     * 品类图标
     */
    private String categoryImage;
    /**
     * 用户对设备的昵称，用户通过setDeviceNickName设置的昵称
     */
    private String nickName;
    /**
     * 设备入网类型
     */
    private String netType;
    /**
     * 设备的类型:"VIRTUAL", "WEB", "APP","DEVICE"
     */
    private String thingType;
    /**
     * 设备的节点类型:"DEVICE", "GATEWAY"
     */
    private String nodeType;
    /**
     * 设备的状态 0：未激活 ；1：在线；3：离线；8：禁用
     */
    private Byte status;
    /**
     * 0:分享者；1：拥有者
     */
    private Byte owned;
    /**
     * 用户的显示名(mobile或loginName或email)
     */
    private String identityAlias;
    /**
     * 修改时间(绑定时间)
     */
    private Date gmtModified;
    /**
     * 描述
     */
    private String description;

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getThingType() {
        return thingType;
    }

    public void setThingType(String thingType) {
        this.thingType = thingType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getOwned() {
        return owned;
    }

    public void setOwned(Byte owned) {
        this.owned = owned;
    }

    public String getIdentityAlias() {
        return identityAlias;
    }

    public void setIdentityAlias(String identityAlias) {
        this.identityAlias = identityAlias;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
