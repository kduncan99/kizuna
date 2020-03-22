/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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

    private final LinkedList<Bid> _bids = new LinkedList<>();
    public Bid _highestBid = null;          //  highest real bid so-far (not pass, double, redouble)
    public final Map<Suit, SuitMention> _suitMentions = new HashMap<>();

    /**
     * Convenience wrapper
     */
    public void display() {
        System.out.println("Bids------");
        String[] result = getDisplay();
        for (String s : result) {
            System.out.println(String.format("  %s", s));
        }
    }

    public boolean establishBid(
        final Bid bid
    ) {
        if (bid instanceof Bid.NoTrumpBid) {
            Bid.NoTrumpBid ntBid = (Bid.NoTrumpBid) bid;
            if (_highestBid instanceof Bid.NoTrumpBid) {
                Bid.NoTrumpBid highest = (Bid.NoTrumpBid) _highestBid;
                if (ntBid._level <= highest._level) {
                    return false;
                }
            } else if (_highestBid instanceof Bid.SuitBid) {
                Bid.SuitBid highest = (Bid.SuitBid) _highestBid;
                if (ntBid._level < highest._level) {
                    return false;
                }
            }
        } else if (bid instanceof Bid.SuitBid) {
            Bid.SuitBid suitBid = (Bid.SuitBid) bid;
            if (_highestBid instanceof Bid.NoTrumpBid) {
                Bid.NoTrumpBid highest = (Bid.NoTrumpBid) _highestBid;
                if (suitBid._level <= highest._level) {
                    return false;
                }
            } else if (_highestBid instanceof Bid.SuitBid) {
                Bid.SuitBid highest = (Bid.SuitBid) _highestBid;
                if (suitBid._level < highest._level) {
                    return false;
                } else if (suitBid._level == highest._level) {
                    if (!highest._suit.isLessThan(suitBid._suit)) {
                        return false;
                    }
                }
            }
        } else if ((bid instanceof Bid.Double) && !doubleAllowed()) {
            return false;
        } else if ((bid instanceof Bid.Redouble) && !redoubleAllowed()) {
            return false;
        }

        return true;
    }

    /**
     * Is a DOUBLE bid allowed?  It is if there are 0 or 2 consecutive PASS bids after a NT or Suit bid
     */
    public boolean doubleAllowed() {
        int passes = 0;
        Iterator<Bid> iter = _bids.descendingIterator();
        while (iter.hasNext()) {
            Bid bid = iter.next();
            if (!(bid instanceof Bid.Pass)) {
                return (((passes == 0) || (passes == 2)) && ((bid instanceof Bid.SuitBid) || (bid instanceof Bid.NoTrumpBid)));
            }
            ++passes;
        }

        return false;
    }

    /**
     * Calculates number of consecutive passes after most recent real bid or double or redouble
     */
    public int getConsecutivePasses() {
        int passes = 0;
        Iterator<Bid> iter = _bids.descendingIterator();
        while (iter.hasNext()) {
            Bid bid = iter.next();
            if (!(bid instanceof Bid.Pass)) {
                break;
            }
            ++passes;
        }

        return passes;
    }

    /**
     * Generate an array of strings to display the bidding
     */
    public String[] getDisplay() {
        String[] result = new String[_bids.size()];
        int rx = 0;
        for (Bid bid : _bids) {
            result[rx++] = bid.toString();
        }
        return result;
    }

    /**
     * Indicates whether the hand has been passed-out...
     * That is, either all four positions passed consecutively,
     * or three positions passed consecutively after a non-pass bid.
     */
    public boolean isPassedOut() {
        if (_highestBid == null) {
            return getConsecutivePasses() == 4;
        } else {
            return getConsecutivePasses() == 3;
        }
    }

    /**
     * Is a REDOUBLE allowed? It is if there are zero or two consecutive PASS bids after a DOUBLE bid
     */
    public boolean redoubleAllowed() {
        int passes = 0;
        Iterator<Bid> iter = _bids.descendingIterator();
        while (iter.hasNext()) {
            Bid bid = iter.next();
            if (!(bid instanceof Bid.Pass)) {
                return (((passes == 0) || (passes == 2)) && (bid instanceof Bid.Double));
            }
            ++passes;
        }

        return false;
    }
}
