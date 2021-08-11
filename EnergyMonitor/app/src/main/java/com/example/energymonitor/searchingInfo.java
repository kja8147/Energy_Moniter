package com.example.energymonitor;

/**
 * Created by 김진아 on 2021-08-06.
 */

public class searchingInfo {
    private String str_year, str_month, str_metro, str_city,num_metro,num_city;
    private static searchingInfo instance=null;

    public static synchronized searchingInfo getInstance(){
        if(null==instance) instance=new searchingInfo();
        return instance;
    }

    public String getYear(){
        return str_year;
    }
    public void setYear(String str_year){
        this.str_year=str_year;
    }

    public String getMonth(){
        return str_month;
    }
    public void setMonth(String str_month){
        this.str_month=str_month;
    }

    public String getMetro(){
        return str_metro;
    }
    public void setMetro(String str_metro){
        this.str_metro=str_metro;
    }

    public String getCity(){
        return str_city;
    }
    public void setCity(String str_city){
        this.str_city=str_city;
    }

    public String getCityNum(){
        return num_city;
    }
    public void setCityNum(String num_city){
        this.num_city=num_city;
    }

    public String getMetroNum(){
        return num_metro;
    }
    public void setMetroNum(String num_metro){
        this.num_metro=num_metro;
    }


}
