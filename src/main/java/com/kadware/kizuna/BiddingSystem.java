/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.List;

public interface BiddingSystem {

    public static enum Name {
        AmericanStandard
    }

    public abstract Bid recommendBid(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    );
}
