/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

/**
 * Describes a bidding suit.
 * Because this relates more toward the game and the peculiarities of bidding,
 * it includes NoTrump, which is not strictly a suite, but an important part of bidding.
 */
public enum Suit {
    CLUBS(0, true, false),
    DIAMONDS(1, true, false),
    HEARTS(2, true, false),
    SPADES(3, true, false),
    NOTRUMP(4, false, false);

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
