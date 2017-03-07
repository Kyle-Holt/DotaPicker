package com.example.kyle.dotapicker;

import java.util.Comparator;

/**
 * Created by Kyle on 2/20/2017.
 */

public class DoubleComparator implements Comparator<Hero> {
@Override
public int compare(Hero o1, Hero o2) {
        Double tot1 = new Double(o1.getAdvantage());
        Double tot2 = new Double(o2.getAdvantage());
        int retval = tot2.compareTo(tot1);
        return retval;
        }
}
