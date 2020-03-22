/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public enum Suit {
    CLUBS("C", true, false, 0, 3),
    DIAMONDS("D", true, false, 1, 2),
    HEARTS("H", true, false, 2, 1 ),
    SPADES("S", true, false, 3, 0);

    public final String _symbol;
    public final boolean _isMinor;
    public final boolean _isMajor;
    public final int _bidOrder;
    public final int _sortOrder;

    Suit(
        String symbol,
        boolean isMinor,
        boolean isMajor,
        int bidOrder,
        int sortOrder
    ) {
        _symbol = symbol;
        _isMinor = isMinor;
        _isMajor = isMajor;
        _bidOrder = bidOrder;
        _sortOrder = sortOrder;
    }
}
