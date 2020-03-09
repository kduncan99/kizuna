/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is the bidding board - contains the list of bids in order, and related information
 */
public class Board {

    public static class SuitMention {
        public Position _openedBy = null;   //  honestly opened by this person
        public boolean _supported = false;  //  honest open supported by opener's partner
        public boolean _rebid = false;      //  rebid by opener
    }

    public final List<Bid> _bids = new LinkedList<>();
    public final Map<Suit, SuitMention> _suitMentions = new HashMap<>();
}
