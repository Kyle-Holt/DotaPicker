package com.example.kyle.dotapicker;

import java.util.Comparator;

/**
 * Created by Kyle on 3/12/2017.
 */

public class BaseComparator implements Comparator<Hero> {

    @Override
    public int compare(Hero o1, Hero o2) {
        Double tot1 = new Double((o1.getUnbiasedAlliedWR() + o1.getUnbiasedEnemyWR())/2);
        Double tot2 = new Double((o2.getUnbiasedAlliedWR() + o2.getUnbiasedEnemyWR())/2);
        int retval = tot2.compareTo(tot1);

        if(retval != 0) {
            return retval;
        } else {
            Double adv1 = new Double((o1.getAdvantage() + o1.getSynergy())/2);
            Double adv2 = new Double((o2.getAdvantage() + o1.getSynergy())/2);
            return adv2.compareTo(adv1);
        }

    }
}
