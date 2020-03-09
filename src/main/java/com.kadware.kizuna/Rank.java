/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

/**
 * Describes a rank, from Ace down to two.
 * Fairly trivial, but we do use this for a couple of ... useful purposes.
 */
public enum Rank {
    ACE(0, 4),
    KING(1, 3),
    QUEEN(2, 2),
    JACK(3, 1),
    TEN(4, 0),
    NINE(5, 0),
    EIGHT(6, 0),
    SEVEN(7, 0),
    SIX(8, 0),
    FIVE(9, 0),
    FOUR(10, 0),
    THREE(11, 0),
    TWO(12, 0);

    public final int _highCardPoints;
    public final int _level;

    Rank(
        int level,
        int highCardPoints
    ) {
        _highCardPoints = highCardPoints;
        _level = level;
    }

}
