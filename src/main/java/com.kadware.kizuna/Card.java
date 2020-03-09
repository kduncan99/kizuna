/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public class Card {

    public final Rank _rank;
    public final Suit _suit;

    public Card(
        Rank rank,
        Suit suit
    ) {
        _rank = rank;
        _suit = suit;
    }
}
