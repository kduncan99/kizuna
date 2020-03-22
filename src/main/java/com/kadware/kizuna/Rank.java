/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

/**
 * Describes a rank, from Ace down to two.
 * Fairly trivial, but we do use this for a couple of ... useful purposes.
 */
public enum Rank {
    ACE( 4, "A", 0),
    KING( 3, "K", 1),
    QUEEN( 2, "Q", 2),
    JACK( 1, "J", 3),
    TEN( 0, "10", 4),
    NINE( 0, "9", 5),
    EIGHT( 0, "8", 6),
    SEVEN( 0, "7", 7),
    SIX( 0, "6", 8),
    FIVE( 0, "5", 9),
    FOUR( 0, "4", 10),
    THREE( 0, "3", 11),
    TWO( 0, "2", 12);

    public final int _highCardPoints;
    public final String _symbol;
    public final int _sortOrder;

    Rank(
        int highCardPoints,
        String symbol,
        int sortOrder
    ) {
        _highCardPoints = highCardPoints;
        _symbol = symbol;
        _sortOrder = sortOrder;
    }
}
