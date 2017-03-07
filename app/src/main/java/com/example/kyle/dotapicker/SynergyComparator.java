package com.example.kyle.dotapicker;

import java.util.Comparator;

/**
 * Created by Kyle on 2/26/2017.
 */

public class SynergyComparator implements Comparator<Hero> {
    @Override
    public int compare(Hero o1, Hero o2) {
        Double tot1 = new Double(o1.getSynergy());
        Double tot2 = new Double(o2.getSynergy());
        int retval = tot2.compareTo(tot1);
        return retval;
    }
}