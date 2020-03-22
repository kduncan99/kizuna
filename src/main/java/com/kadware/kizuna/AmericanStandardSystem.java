/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.List;

public class AmericanStandardSystem implements BiddingSystem {

    public final boolean _fourCardMajors = true;

    private static class ScratchPad {
        private int _hcPoints;
        private float _quickTricks;
        private boolean _reBiddableSuit;
        private boolean _lengthInMajors;
        private boolean _lengthInSpades;
        private int _passes;
        private boolean _thirdHand;
        private boolean _fourthHand;
        private Bid _leadDirect = null;
    }

    /**
     * Recommends what the player holding the given hand should be at this point,
     * as evidenced by the current content of the given bidding board.
     * Do not call if the hand is passed out
     */
    @Override
    public Bid recommendBid(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    ) {
        if (biddingBoard._highestBid == null) {
            return openingBid(hand, biddingBoard, commentary);
        }

        return new Bid.Pass(hand._position);
    }

    /**
     * Used for recommending an opening bid.
     * Called ONLY if the bidding board has three or fewer passes, and no real bids.
     */
    private Bid openingBid(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    ) {
        commentary.add(String.format("Checking opening bid for player %s", hand._position.toString()));

        ScratchPad sp = new ScratchPad();
        sp._hcPoints = hand.countHighCardPoints();
        sp._quickTricks = hand.countQuickTricks();
        sp._reBiddableSuit = hand.hasLongSuit();
        sp._lengthInMajors = hand.hasLengthInMajors();
        sp._lengthInSpades = hand._distribution.getSuitSet(Suit.SPADES).size() >= 5;
        sp._passes = biddingBoard.getConsecutivePasses();
        sp._thirdHand = sp._passes == 2;
        sp._fourthHand = sp._passes == 3;
        commentary.add(String.format("HCP=%d QTricks=%f reBiddableSuit=%s lengthInMajors=%s position=%d 3rd=%s 4th=%s",
                                     sp._hcPoints,
                                     sp._quickTricks,
                                     sp._reBiddableSuit,
                                     sp._lengthInMajors,
                                     sp._passes + 1,
                                     sp._thirdHand,
                                     sp._fourthHand));

        if (!shouldOpen(hand, sp, commentary)) {
            commentary.add("Recommending pass");
            return new Bid.Pass(hand._position);
        }

        if (sp._leadDirect != null) {
            commentary.add(String.format("Recommending weak lead directing bid %s", sp._leadDirect.toString()));
            return sp._leadDirect;
        }

        //  We have decided to bid.  What should we bid?
        //  Any 5-card or longer suit is biddable
        //  A 4-card suit headed by at least QJ is biddable
        //  Don't open a weak 4-card major (or at all, if 4-card-major is not enabled)
        //  If your only long suit is a weak 4-card major and you have 14 HCP, bid one club.
        //  Supposing you have stronger 3-card diamonds than 3-card clubs - bid one diamond.
        //  But if both 3-card minors are equally strong, bid clubs.

        //  If you have only one long suit (5 cards or more), open that one, and rebid if necessary
        //  General rules for more than one long suit:
        //  Unequal in length, bid the longer one
        //  Equal in length, bid the higher-ranking one

        //  2 6-card suits: Bid higher ranking suit first, then bid the other (and possibly rebid it)
        //      unless partner raises opening bid.
        //  6-card and 5-card: Normally bid 6-card suit first, then bid and re-bid the other.
        //      With a minimum opening hand, bid the higher ranking first, to keep bidding low
        //  2 5-card suits: Bid higher ranking suit first, then bid, and maybe rebid, the other
        //      If you get too high, just mention the first suit.
        //  5-card and 4-card: Show both suits if you have a strong hand or if the bidding is convenient.
        //      Otherwise, show only 5-card suit, or start with the shorter suit to avoid trouble.
        //  2 4-card suits: Some trouble here
        //      If two suits touch, bid higher-ranking one first, otherwise bid the lower one
        //  3 4-card suits: Bid the middle or lower
        //      Definitely bid the lower if the next suit up is the singleton

        return new Bid.Pass(hand._position);
    }

    /**
     *  Decide whether to open...
     *  Open any hand with 14 or more HCP
     *  Open any hand with 12-13 HCP, 2 quick tricks, and comfortable rebid (5-card suit)
     *  Open any hand with 10-11 HCP, 2 quick tricks, comfortable rebid, and length in majors
     *  Open 3rd position with 10-11 HCP without quick tricks/length in majors
     *      if that bid can also indicate a good lead in the event your partner leads
     *  4th hand - open any hand you would open in 1st or 2nd position
     *      pass any borderline hand lacking strength in majors
     *      maybe open a hand with strength in spades
     */
    private boolean shouldOpen(
        final Hand hand,
        final ScratchPad sp,
        final List<String> commentary
    ) {
        if (sp._hcPoints >= 14) {
            commentary.add("Sufficient HCP, needs nothing else to proceed");
            return true;
        }

        if ((sp._hcPoints >= 12) && (sp._quickTricks >= 2.0) && sp._reBiddableSuit) {
            commentary.add("Good HCP, quick tricks, and reBiddable suit, proceeding");
            return true;
        }

        if (sp._hcPoints >= 10) {
            if (sp._thirdHand) {
                commentary.add("Weak hand, 3rd position...");
                if ((sp._quickTricks >= 2.0) && sp._reBiddableSuit && sp._lengthInMajors) {
                    commentary.add("has quick tricks, reBiddable suit, length in majors.");
                    return true;
                }

                commentary.add("insufficient quick tricks, no reBiddable suit, or no length in majors");
                //  Do we have a lead direct? AKQ or AQJ four-long is good
                for (SuitSet suitSet : hand._distribution) {
                    if (suitSet.size() >= 4) {
                        if (suitSet.contains(new Card(suitSet._suit, Rank.ACE))
                            && suitSet.contains(new Card(suitSet._suit, Rank.QUEEN))
                            && (suitSet.contains(new Card(suitSet._suit, Rank.KING))
                            || suitSet.contains(new Card(suitSet._suit, Rank.JACK)))) {
                            commentary.add("has good direction for partner's opening lead.");
                            sp._leadDirect = new Bid.SuitBid(hand._position, 1, suitSet._suit);
                            return true;
                        }
                    }
                }

                commentary.add("has no good suit for suggesting an opening lead.");
                return false;
            } else if (sp._fourthHand) {
                commentary.add("Weak hand, 4th position...");
                if ((sp._quickTricks >= 2.0) && sp._reBiddableSuit && sp._lengthInMajors && sp._lengthInSpades) {
                    commentary.add("has quick tricks, reBiddable suit, length in majors esp. spades.");
                    return true;
                }

                commentary.add("insufficient quick tricks, no reBiddable suit, or no length in majors esp. spades.");
                return false;
            } else {
                commentary.add("Weak hand, 1st or 2nd position");
                if ((sp._quickTricks >= 2.0) && sp._reBiddableSuit && sp._lengthInMajors) {
                    commentary.add("has quick tricks, rebiddable suit, length in majors.");
                    return true;
                }

                commentary.add("insufficient quick tricks, no rebiddable suit, or no length in majors.");
                return false;
            }
        }

        commentary.add("Nothing worth bidding.");
        return false;
    }
}
