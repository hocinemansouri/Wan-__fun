package com.biskra.ansej.v3.kick;

import java.util.ArrayList;

/**
 * Created by ACER User on 13/06/2017.
 */

public class Activities {

    private String des_act;
    private String des_act_ar;
    private String imgurl;
    private String color;
    private ArrayList<inclAct> inclActlist;
    private String color1, color2;
    private int sectorCode;
    private String sectorName;

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getDes_act() {
        return des_act;
    }

    public void setDes_act(String des_act) {
        this.des_act = des_act;
    }

    public String getDes_act_ar() {
        return des_act_ar;
    }

    public void setDes_act_ar(String des_act_ar) {
        this.des_act_ar = des_act_ar;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<inclAct> getInclActlist() {
        return inclActlist;
    }

    public void setInclActlist(ArrayList<inclAct> inclActlist) {
        this.inclActlist = inclActlist;
    }

    public int getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(int sectorCode) {
        this.sectorCode = sectorCode;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public static class inclAct {
        private String des_act;
        private String code_act;

        public String getDes_act() {
            return des_act;
        }

        public void setDes_act(String des_act) {
            this.des_act = des_act;
        }

        public String getCode_act() {
            return code_act;
        }

        public void setCode_act(String code_act) {
            this.code_act = code_act;
        }
    }
}
