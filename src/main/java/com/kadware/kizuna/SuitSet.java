/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.TreeSet;

/**
 * A container of all the cards in a player's hand, of a particular suit.
 */
public class SuitSet extends TreeSet<Card> {

    public final Suit _suit;

    public SuitSet(
        final Suit suit
    ) {
        super();
        _suit = suit;
    }

    public SuitSet(
        final Suit suit,
        final TreeSet<Card> cards
    ) {
        super(cards);
        _suit = suit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_suit._symbol);
        sb.append(":");
        for (Card card : this) {
            if (sb.length() > 2) {
                sb.append("-");
            }
            sb.append(card._rank._symbol);
        }
        return sb.toString();
    }
}
