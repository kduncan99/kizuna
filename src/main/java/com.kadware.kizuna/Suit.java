/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public enum Suit {
    CLUBS(0, true, false),
    DIAMONDS(1, true, false),
    HEARTS(2, true, false),
    SPADES(3, true, false);

    public final int _ordinal;
    public final boolean _isMinor;
    public final boolean _isMajor;

    Suit(
        int ordinal,
        boolean isMinor,
        boolean isMajor
    ) {
        _ordinal = ordinal;
        _isMinor = isMinor;
        _isMajor = isMajor;
    }
}
