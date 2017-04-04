package com.example.kyle.dotapicker;

import java.util.Comparator;

/**
 * Created by Kyle on 3/11/2017.
 */

public class SynergyAdvantageComparator implements Comparator<Hero> {
    @Override
    public int compare(Hero o1, Hero o2) {
        Double tot1 = new Double(o1.getSynergy() + o1.getAdvantage());
        Double tot2 = new Double(o2.getSynergy() + o2.getAdvantage());
        int retval = tot2.compareTo(tot1);
        return retval;
    }
}
