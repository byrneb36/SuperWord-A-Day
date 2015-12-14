package com.dcu.superword_a_day;

import java.util.Random;

/**
 * Created by Brendan on 16-Nov-15.
 */
public class FisherYatesShuffler {

    // Fisher-Yates shuffle
    public static void shuffleArray(String [] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
