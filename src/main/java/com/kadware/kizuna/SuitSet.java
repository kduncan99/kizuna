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

    public int countHighCardPoints() {
        int points = 0;
        for (Card card : this) {
            points += card._rank._highCardPoints;
        }
        return points;
    }

    public int countHonors() {
        int honors = 0;
        for (Card card : this) {
            if (card._rank._highCardPoints > 0) {
                ++honors;
            }
        }
        return honors;
    }

    public boolean hasProbableStopper() {
        boolean king = contains(new Card(_suit, Rank.KING));
        if (king && (size() >= 2)) {
            return true;
        }

        boolean queen = contains(new Card(_suit, Rank.QUEEN));
        boolean jack = contains(new Card(_suit, Rank.JACK));
        boolean ten = contains(new Card(_suit, Rank.TEN));
        return queen && (jack || ten) && (size() >= 3);
    }

    public boolean hasStopper() {
        if (contains(new Card(_suit, Rank.ACE))) {
            return true;
        }

        boolean king = contains(new Card(_suit, Rank.KING));
        boolean queen = contains(new Card(_suit, Rank.QUEEN));
        if (king && queen) {
            return true;
        }

        boolean jack = contains(new Card(_suit, Rank.JACK));
        boolean ten = contains(new Card(_suit, Rank.TEN));
        if (queen && jack && ten) {
            return true;
        }

        boolean nine = contains(new Card(_suit, Rank.NINE));
        boolean eight = contains(new Card(_suit, Rank.EIGHT));
        return jack && ten && nine && eight;
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
