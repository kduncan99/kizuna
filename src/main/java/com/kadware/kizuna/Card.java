/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public class Card {

    public final Rank _rank;
    public final Suit _suit;

    public Card(
        Suit suit,
        Rank rank
    ) {
        _rank = rank;
        _suit = suit;
    }

    @Override
    public boolean equals(
        final Object obj
    ) {
        if (obj instanceof Card) {
            Card card = (Card) obj;
            return (card._suit.equals(this._suit) && card._rank.equals(this._rank));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (_rank.hashCode() << 16) & _suit.hashCode();
    }
}
