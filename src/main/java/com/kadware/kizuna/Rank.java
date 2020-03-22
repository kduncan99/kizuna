/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

/**
 * Describes a rank, from Ace down to two.
 * Fairly trivial, but we do use this for a couple of ... useful purposes.
 */
public enum Rank {
    ACE(0, 4, "A"),
    KING(1, 3, "K"),
    QUEEN(2, 2, "Q"),
    JACK(3, 1, "J"),
    TEN(4, 0, "10"),
    NINE(5, 0, "9"),
    EIGHT(6, 0, "8"),
    SEVEN(7, 0, "7"),
    SIX(8, 0, "6"),
    FIVE(9, 0, "5"),
    FOUR(10, 0, "4"),
    THREE(11, 0, "3"),
    TWO(12, 0, "2");

    public final int _highCardPoints;
    public final int _level;
    public final String _symbol;

    Rank(
        int level,
        int highCardPoints,
        String symbol
    ) {
        _highCardPoints = highCardPoints;
        _level = level;
        _symbol = symbol;
    }
}
