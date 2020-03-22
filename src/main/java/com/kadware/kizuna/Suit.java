/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public enum Suit {
    CLUBS(0, "C", true, false),
    DIAMONDS(1, "D", true, false),
    HEARTS(2, "H", true, false),
    SPADES(3, "S", true, false);

    public final int _ordinal;
    public final String _symbol;
    public final boolean _isMinor;
    public final boolean _isMajor;

    Suit(
        int ordinal,
        String symbol,
        boolean isMinor,
        boolean isMajor
    ) {
        _ordinal = ordinal;
        _symbol = symbol;
        _isMinor = isMinor;
        _isMajor = isMajor;
    }

    public boolean isLessThan(
        final Suit comp
    ) {
        return this._ordinal < comp._ordinal;
    }
}
