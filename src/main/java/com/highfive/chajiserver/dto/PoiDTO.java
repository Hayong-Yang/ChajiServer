package com.highfive.chajiserver.dto;

public class PoiDTO {
    private String name;
    private double lat;
    private double lon;
    private String address;
    private String tel;

    public PoiDTO() { }
    public PoiDTO(String name, double lat, double lon, String address, String tel) {
        this.name    = name;
        this.lat     = lat;
        this.lon     = lon;
        this.address = address;
        this.tel     = tel;
    }

    // Getter / Setter 반드시 있어야 Jackson 직렬화됨
    public String getName()    { return name; }
    public void setName(String name) { this.name = name; }

    public double getLat()     { return lat; }
    public void setLat(double lat)   { this.lat = lat; }

    public double getLon()     { return lon; }
    public void setLon(double lon)   { this.lon = lon; }

    public String getAddress(){ return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTel()     { return tel; }
    public void setTel(String tel)       { this.tel = tel; }
}
