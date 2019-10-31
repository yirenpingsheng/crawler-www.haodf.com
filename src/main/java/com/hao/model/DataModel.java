package com.hao.model;

import java.util.List;

/**
 * 省数据模型
 *
 * @author Hao
 */
public class DataModel {

    // 城市
    private String city;
    // 城市下的医院列表
    private List<Hospital> hospitals;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public DataModel() {
        super();
    }

    public DataModel(String city) {
        super();
        this.city = city;
    }

    public DataModel(String city, List<Hospital> hospitals) {
        super();
        this.city = city;
        this.hospitals = hospitals;
    }

    @Override
    public String toString() {
        return "DataModel [city=" + city + ", hospitals=" + hospitals + "]";
    }

}