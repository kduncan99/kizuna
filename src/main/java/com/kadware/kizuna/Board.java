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
        if ((_highestBid instanceof Bid.RealBid) && (bid instanceof Bid.RealBid)) {
            if (((Bid.RealBid) bid).compareTo(_highestBid) <= 0) {
                return false;
            }
        }

        if ((bid instanceof Bid.Double) && !isDoubleAllowed(bid._position)) {
            return false;
        }

        if ((bid instanceof Bid.Redouble) && !isRedoubleAllowed(bid._position)) {
            return false;
        }

        if (bid instanceof Bid.RealBid) {
            _highestBid = bid;
        }

        return true;
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

    public boolean isDoubleAllowed(
        final Position position
    ) {
        return (_highestBid != null)
            && ((_highestBid._position != position) || (_highestBid._position != position.getPartner()));
    }

    public boolean isDoubled() {
        Iterator<Bid> iter = _bids.descendingIterator();
        while (iter.hasNext()) {
            Bid bid = iter.next();
            if ((bid instanceof Bid.Double) || (bid instanceof Bid.Redouble)) {
                return true;
            } else if (!(bid instanceof Bid.Pass)) {
                break;
            }
        }

        return false;
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

    public boolean isRedoubleAllowed(
        final Position position
    ) {
        return isDoubled()
            && ((position == _highestBid._position) || (position.getPartner() == _highestBid._position));
    }

    public boolean isRedoubled() {
        Iterator<Bid> iter = _bids.descendingIterator();
        while (iter.hasNext()) {
            Bid bid = iter.next();
            if (bid instanceof Bid.Redouble) {
                return true;
            } else if (!(bid instanceof Bid.Pass)) {
                break;
            }
        }

        return false;
    }
}
