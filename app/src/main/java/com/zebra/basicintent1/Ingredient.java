package com.zebra.basicintent1;

public class Ingredient {
    private String nrInv;
    private String name;
    private String CostPosition;
    private String dateOfLiquidation;

    public Ingredient() {}

    public Ingredient(String nr_inwentarzowy, String nazwa_skladnika, String st_Koszt_idn, String data_likwidacji ){
        this.nrInv = nr_inwentarzowy;
        this.name = nazwa_skladnika;
        this.CostPosition = st_Koszt_idn;
        this.dateOfLiquidation = data_likwidacji;
    }

    public String getName(){
        return name;
    }
    public String getNrInw() {
        return nrInv;
    }
    public String getCostPosition(){
        return CostPosition;
    }
    public String getDateOfLiquidation() {
        return dateOfLiquidation;
    }


}
