package miguelangellopez.ad.practica1_psp_ad.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import miguelangellopez.ad.practica1_psp_ad.Contactos;

public class ContactComparator implements Comparator<Contactos> {

    @Override
    public int compare(Contactos contactos, Contactos contactos1) {
        return contactos.getName().compareTo(contactos1.getName());
    }

}
