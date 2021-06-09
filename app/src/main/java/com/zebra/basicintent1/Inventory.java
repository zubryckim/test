package com.zebra.basicintent1;

public class Inventory {

        private String nrInv;
        private String location;
        private String date;
        private String imei;
        private String dateOfLiquidation;
        private String oldSticker;


        public Inventory(String nr_inwentarzowy, String lokalizacja, String data, String imei, String data_likwidacji, String stara_nalepka ){
            this.nrInv = nr_inwentarzowy;
            this.location = lokalizacja;
            this.date = data;
            this.imei = imei;
            this.dateOfLiquidation = data_likwidacji;
            this.oldSticker = stara_nalepka;
        }
        public String getAll() {
            return location+date+imei+oldSticker;

        }
        public String getNrInw() { return nrInv; }
        public String getDateOfLiquidation() {
            return dateOfLiquidation;
        }

    }
