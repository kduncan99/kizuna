/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public class Card implements Comparable<Card> {

    public final Rank _rank;
    public final Suit _suit;
    public Position _position;  //  hand the is holding, or held, this card
    public boolean _played;     //  true if the card has been played, false if it is still in the player's hand

    public Card(
        Suit suit,
        Rank rank
    ) {
        _rank = rank;
        _suit = suit;
        _position = null;
        _played = false;
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

    /**
     * For sorting by suit, then rank.
     * For suit, sort order should be Spades Hearts Diamonds Clubs
     * For rank, sort order should be A K Q J 10 9 8 7 6 5 4 3 2
     */
    public boolean isLessThan(
        final Card card
    ) {
        if (_suit.equals(card._suit)) {
            return _rank._sortOrder < card._rank._sortOrder;
        } else {
            return _suit._sortOrder < card._suit._sortOrder;
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s", _rank._symbol, _suit._symbol);
    }

    @Override
    public int compareTo(
        final Card card
    ) {
        if (card.equals(this)) return 0;
        if (isLessThan(card)) return -1;
        return 1;
    }
}
