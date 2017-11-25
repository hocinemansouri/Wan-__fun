package com.biskra.ansej.v3.kick;

/**
 * Created by ACER User on 13/06/2017.
 */

public class ListItem {

    private String head;
    private String desc;
    private String familyID;
    private String suppName;
    private int idDossier;
    private String bgp;

    public ListItem(String head, String desc, String familyID, String suppName, int idDossier, String bgp) {
        this.head = head;
        this.desc = desc;
        this.familyID = familyID;
        this.suppName = suppName;
        this.idDossier = idDossier;
        this.bgp = bgp;
    }

    public String getBgp() {
        return bgp;
    }

    public String getFirst() {
        return head;
    }

    public String getSecond() {
        return desc;
    }

    public String getThird() {
        return familyID;
    }

    public String getSuppName() {
        return suppName;
    }

    public int idDossier() {
        return idDossier;
    }

}
