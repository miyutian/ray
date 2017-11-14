package com.tci.pojo;

import com.vividsolutions.jts.geom.MultiPolygon;

public class County {
    
    private String country;
    
    private String state;
    
    private String stateCode;
    
    private String county;
    
    private String countyCode;
    
    private MultiPolygon boundary;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public MultiPolygon getBoundary() {
        return boundary;
    }

    public void setBoundary(MultiPolygon boundary) {
        this.boundary = boundary;
    }
    
}
