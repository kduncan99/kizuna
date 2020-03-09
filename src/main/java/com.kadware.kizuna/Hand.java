/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.HashSet;
import java.util.Set;

public class Hand {
    public final Set<Card> _cards;
    public final Set<Card> _played;
    public final Set<Card> _remaining;

    public Hand(
        final Set<Card> cards
    ) {
        _cards = cards;
        _played = new HashSet<>();
        _remaining = new HashSet<>(cards);
    }

    public int countHighCardPoints() {
        int result = 0;
        for (Card card : _cards) {
            result += card._rank._highCardPoints;
        }
        return result;
    }

    public int countSuitLength(
        final Suit suit
    ) {
        int result = 0;
        for (Card card : _cards) {
            if (card._suit == suit) {
                ++result;
            }
        }
        return result;
    }
}
