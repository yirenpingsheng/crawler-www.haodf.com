package com.hao.model;

/**
 * 医院
 *
 * @author Hao
 */
public class Hospital {

    // 医院名称
    private String name;
    // 医院详情Url
    private String url;
    // 医院等级
    private String level;
    // 医院类型
    private String type;
    // 医院地址
    private String address;
    // 医院电话
    private String phone;
    // 路线
    private String route;
    // 经度
    private String longitude;
    // 纬度
    private String latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Hospital() {
        super();
    }

    public Hospital(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }

    public Hospital(String name, String url, String level, String type, String address, String phone, String route,
                    String longitude, String latitude) {
        super();
        this.name = name;
        this.url = url;
        this.level = level;
        this.type = type;
        this.address = address;
        this.phone = phone;
        this.route = route;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Hospital [name=" + name + ", url=" + url + ", level=" + level + ", type=" + type + ", address="
                + address + ", phone=" + phone + ", route=" + route + ", longitude=" + longitude + ", latitude="
                + latitude + "]";
    }

}