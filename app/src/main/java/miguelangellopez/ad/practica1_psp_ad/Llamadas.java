package miguelangellopez.ad.practica1_psp_ad;

import java.util.Date;

public class Llamadas {

    private String number, name;
    private String dateTime;

    public Llamadas(String number,String dateTime) {
        this.number = number;
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String toCsv() {
        return dateTime + "; " + number;
    }


    public static Llamadas fromCsvString (String csv, String separator) {
        Llamadas call = null;
        String[] partes = csv.split(separator);
        if(partes.length == 3) {
            call = new Llamadas(partes[0].trim(), partes[1].trim());
        }
        return call;
    }


}
