package miguelangellopez.ad.practica1_psp_ad;

import java.util.Objects;

public class Contactos {

    private String name, number;
    private int id;

    public Contactos( String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Contactos{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contactos contactos = (Contactos) o;
        return Objects.equals(name, contactos.name) &&
                Objects.equals(number, contactos.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }
}
