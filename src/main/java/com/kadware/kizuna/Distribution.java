/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Describes the entirety of a player's hand.
 * SuitSet objects are list in decreasing order of set size.
 */
public class Distribution extends LinkedList<SuitSet> {

    public static final int[] ND_4_3_3_3 = { 4, 3, 3, 3 };
    public static final int[] ND_4_4_3_2 = { 4, 4, 3, 2 };
    public static final int[] ND_4_4_4_1 = { 4, 4, 4, 1 };
    public static final int[] ND_5_3_3_2 = { 5, 3, 3, 2 };

    public final int[] _numericalDistribution;

    public Distribution(
        final Set<Card> cards
    ) {
        //  Create a temporary container of directly-addressable-by-suit SuitSets.
        Map<Suit, SuitSet> tempMap = new TreeMap<>();
        for (Suit suit : Suit.values()) {
            tempMap.put(suit, new SuitSet(suit));
        }

        //  Load the SuitSets from the given set of all cards in the hand
        for (Card card : cards) {
            tempMap.get(card._suit).add(card);
        }

        //  Load our list with the SuitSets, ordered by the length of each set - longest first
        _numericalDistribution = new int[4];
        int nx = 0;
        for (int len = 13; len >= 0; len--) {
            for (SuitSet suitSet : tempMap.values()) {
                if (suitSet.size() == len) {
                    add(suitSet);
                    _numericalDistribution[nx++] = len;
                }
            }
        }
    }

    public String getDistributionString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_numericalDistribution[0]);
        sb.append('-');
        sb.append(_numericalDistribution[1]);
        sb.append('-');
        sb.append(_numericalDistribution[2]);
        sb.append('-');
        sb.append(_numericalDistribution[3]);
        return sb.toString();
    }

    public SuitSet getSuitSet(
        final Suit suit
    ) {
        for (SuitSet suitSet : this) {
            if (suitSet._suit == suit) {
                return suitSet;
            }
        }
        throw new RuntimeException("Impossible failure");
    }

    /**
     * Indicates whether the hand is balanced - i.e., 4-4-3-2, 4-3-3-3, 5-3-3-2
     */
    public boolean isBalanced() {
        return Arrays.equals(_numericalDistribution, ND_4_3_3_3)
            || Arrays.equals(_numericalDistribution, ND_4_4_3_2)
            || Arrays.equals(_numericalDistribution, ND_5_3_3_2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (SuitSet suitSet : this) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(suitSet.toString());
        }
        return sb.toString();
    }
}
