/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.Arrays;
import java.util.List;

public class AmericanStandardSystem extends BiddingSystem {

    public final boolean _fiveCardMajors;
    public final boolean _invertedMinorRaise;   // see pg 54
    public final boolean _staymanConvention;

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

    public AmericanStandardSystem(
        final boolean fiveCardMajors,
        final boolean invertedMinorRaise,
        final boolean staymanConvention
    ) {
        _fiveCardMajors = fiveCardMajors;
        _invertedMinorRaise = invertedMinorRaise;
        _staymanConvention = staymanConvention;
    }

    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Used for recommending an opening bid.
     * Called ONLY if the bidding board has three or fewer passes, and no real bids.
     */
    @Override
    public Bid recommendOpeningBid(
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

        Bid bid = selectOpeningBid(hand, sp, commentary);
        commentary.add(String.format("Recommending %s", bid.toString()));
        return bid;
    }

    /**
     * We have decided to open - now we need to decide what to bid
     * Any 5-card or longer suit is biddable
     * A 4-card suit headed by at least QJ is biddable
     *      Don't open a weak 4-card major (or at all, if 4-card-major is not enabled)
     *      If the only long suit is a weak 4-card major and you have 14 HCP, bid one club.
     *      If we have stronger 3-card diamonds than 3-card clubs - bid one diamond,
     *          But if both 3-card minors are equally strong, bid clubs.
     * If we have only one long suit (5 cards or more), open that one, and rebid if necessary
     * General rules for more than one long suit:
     *      Unequal in length, bid the longer one
     *      Equal in length, bid the higher-ranking one
     */
    private Bid selectOpeningBid(
        final Hand hand,
        final ScratchPad sp,
        final List<String> commentary
    ) {
        if (sp._leadDirect != null) {
            commentary.add(String.format("Recommending weak lead directing bid %s", sp._leadDirect.toString()));
            return sp._leadDirect;
        }

        int[] dist = hand._distribution._numericalDistribution;
        if (hand._distribution.isBalanced()) {
            Bid bid = selectOpeningNoTrump(hand, sp, commentary);
            if (bid != null) {
                return bid;
            }
        }

        if ((dist[0] >= 7)
            || ((dist[0] == 6) && (dist[1] < 5))
            || ((dist[0] == 5) && (dist[1] < 4))) {
            //  Only one biddable suit.  Bid it.
            commentary.add("One biddable suit");
            return new Bid.SuitBid(hand._position, 1, hand._distribution.get(0)._suit);
        }

        if ( ((dist[0] == 6) || (dist[0] == 5)) && (dist[0] == dist[1]) ) {
            //  6-6 or 5-5: Bid the higher-ranking suit first.
            //  We'll bid the other one (and maybe rebid it) later (unless partner raises this one)
            commentary.add("6-6 or 5-5, recommending higher-ranking suit");
            SuitSet set0 = hand._distribution.get(0);
            SuitSet set1 = hand._distribution.get(1);
            Suit bidSuit = (set0._suit._bidOrder > set1._suit._bidOrder) ? set0._suit : set1._suit;
            return new Bid.SuitBid(hand._position, 1, bidSuit);
        }

        if ((dist[0] == 6) && (dist[1] == 5)) {
            //  6-5: Bid the 6-card suit first, unless we've got a minimum hand;
            //  In that case, bid the higher-ranking suit to keep bidding lower.
            if (sp._hcPoints >= 13) {
                commentary.add("6-5 strong hand - recommending 6-card suit");
                return new Bid.SuitBid(hand._position, 1, hand._distribution.get(0)._suit);
            } else {
                commentary.add("6-5 weaker hand - recommending higher-ranking suit");
                SuitSet set0 = hand._distribution.get(0);
                SuitSet set1 = hand._distribution.get(1);
                Suit bidSuit = (set0._suit._bidOrder > set1._suit._bidOrder) ? set0._suit : set1._suit;
                return new Bid.SuitBid(hand._position, 1, bidSuit);
            }
        }

        if ((dist[0] == 5) && (dist[1] == 4)) {
            //  5-4: With a strong hand, bid the long suit and maybe come back with the shorter suit.
            //  With a weak hand, start with the shorter suit (but observe 4-card-major setting)
            //  unless there is room between the suits - i.e., clubs and hearts or spades.
            if (sp._hcPoints >= 13) {
                commentary.add("5-4 strong hand - recommending long suit");
                return new Bid.SuitBid(hand._position, 1, hand._distribution.get(0)._suit);
            } else {
                SuitSet set0 = hand._distribution.get(0);
                SuitSet set1 = hand._distribution.get(1);
                Suit higherSuit = (set0._suit._bidOrder > set1._suit._bidOrder) ? set0._suit : set1._suit;
                if (_fiveCardMajors && ((higherSuit == Suit.SPADES) || (higherSuit == Suit.HEARTS))) {
                    commentary.add("5-4 weaker hand, higher-ranking suit is 4-card major, recommending 5-card suit");
                    return new Bid.SuitBid(hand._position, 1, set0._suit);
                } else {
                    commentary.add("5-4 weaker hand, recommending higher-ranking suit");
                    return new Bid.SuitBid(hand._position, 1, higherSuit);
                }
            }
        }

        if (Arrays.equals(dist, Distribution.ND_4_4_4_1)) {
            //  Some trouble here. Generally we want to find a suit fit, but we may end up in NT.
            //  Bid the middle suit generally, but bid the lower suit if the next suit up is the singleton.
            //  In practice, this means we bid clubs if the singleton is diamonds,
            //  We bid hearts if the singleton is clubs,
            //  And we bid the stronger of clubs or diamonds otherwise.
            SuitSet singleton = hand._distribution.get(3);
            commentary.add(String.format("4-4-4-1 distribution, singleton is %s", singleton._suit.toString()));
            if (singleton._suit == Suit.CLUBS) {
                return new Bid.SuitBid(hand._position, 1, Suit.DIAMONDS);
            } else if (singleton._suit == Suit.DIAMONDS) {
                return new Bid.SuitBid(hand._position, 1, Suit.CLUBS);
            } else {
                SuitSet clubs = hand._distribution.getSuitSet(Suit.CLUBS);
                SuitSet diamonds = hand._distribution.getSuitSet(Suit.DIAMONDS);
                Suit strongest = selectStrongestSuit(clubs, diamonds);
                return new Bid.SuitBid(hand._position, 1, strongest);
            }
        }

        if ((dist[0] == 4) && !_fiveCardMajors) {
            //  We are prevented from opening hearts or spades.
            //  Open the stronger of clubs or diamonds.
            commentary.add("Balanced hand not suitable for NT, five-card majors required");
            SuitSet clubs = hand._distribution.getSuitSet(Suit.CLUBS);
            SuitSet diamonds = hand._distribution.getSuitSet(Suit.DIAMONDS);
            Suit strongest = selectStrongestSuit(clubs, diamonds);
            return new Bid.SuitBid(hand._position, 1, strongest);
        }

        if (Arrays.equals(dist, Distribution.ND_4_4_3_2)) {
            //  Some trouble here - if the two suits touch, bid the higher ranking one first.
            //  Otherwise, bid the lower-ranking suit - the idea is to preserve bidding space.
            //  We've already handled the five-card-major issue.
            commentary.add("With 4-4-3-2 distribution, bid appropriately to keep bidding low");
            SuitSet set0 = hand._distribution.get(0);
            SuitSet set1 = hand._distribution.get(1);
            int distance = set0._suit._bidOrder - set1._suit._bidOrder;
            if ((distance == 1) || (distance == -1)) {
                commentary.add("  Suits touch, bid highest ranking suit");
                Suit suit = set0._suit._bidOrder < set1._suit._bidOrder ? set1._suit : set0._suit;
                return new Bid.SuitBid(hand._position, 1, suit);
            } else {
                commentary.add("  Suits do not touch, bid highest ranking suit");
                Suit suit = set0._suit._bidOrder < set1._suit._bidOrder ? set0._suit : set1._suit;
                return new Bid.SuitBid(hand._position, 1, suit);
            }
        }

        if (Arrays.equals(dist, Distribution.ND_4_3_3_3) || Arrays.equals(dist, Distribution.ND_4_4_3_2)) {
            //  Bid the four card suit.  We already filtered out the five-card-major issue.
            commentary.add("With 4-3-3-3 distribution and no NT option, we bid the longest suit");
            return new Bid.SuitBid(hand._position, 1, hand._distribution.get(0)._suit);
        }

        throw new RuntimeException("Fell through all the possibilities");
    }

    /**
     * Sub-function used when we have a balanced hand, to determine whether to open NoTrump
     */
    private Bid selectOpeningNoTrump(
        final Hand hand,
        final ScratchPad sp,
        final List<String> commentary
    ) {
        commentary.add("Balanced Hand - considering NoTrump...");

        //  If we have a 'good' 5-card suit, bail out and bid the suit
        SuitSet highSet = hand._distribution.get(0);
        if ((highSet.size() == 5) && (highSet.countHonors() > 2)) {
            commentary.add("Strong hand has strong 5-card suit; bid suit");
            return null;
        }

        //  How are we doing on stoppers?
        int stoppers = 0;
        int probableStoppers = 0;
        for (SuitSet suitSet : hand._distribution) {
            if (suitSet.hasStopper()) {
                ++stoppers;
            } else if (suitSet.hasProbableStopper()) {
                ++probableStoppers;
            }
        }
        commentary.add(String.format("  Stoppers=%d  Probable Stoppers=%d", stoppers, probableStoppers));
        if (stoppers + probableStoppers < 4) {
            commentary.add("  Insufficient stoppers for NoTrump");
            return null;
        }

        if ((sp._hcPoints >= 16) && (sp._hcPoints <= 18)) {
            if (stoppers + probableStoppers == 4) {
                commentary.add("  16-18 points with sufficient stoppers, recommending 1NT");
                return new Bid.NoTrumpBid(hand._position, 1);
            } else {
                commentary.add("  16-18 points, but insufficient stoppers");
                return null;
            }
        }

        if ((sp._hcPoints >= 22) && (sp._hcPoints <= 24)) {
            if (stoppers == 4) {
                commentary.add("  22-24 points with sufficient stoppers, recommending 2NT");
                return new Bid.NoTrumpBid(hand._position, 2);
            } else {
                commentary.add("  22-24 points, but insufficient stoppers");
                return null;
            }
        }

        if ((sp._hcPoints >= 25) && (sp._hcPoints <= 27)) {
            if (stoppers == 4) {
                commentary.add("  25-27 points with sufficient stoppers, recommending 3NT");
                return new Bid.NoTrumpBid(hand._position, 3);
            } else {
                commentary.add("  25-27 points, but insufficient stoppers");
                return null;
            }
        }

        commentary.add("  Unsuitable point range");
        return null;
    }

    /**
     * Determines the stronger suit, first considering length, then considering strength.
     * If everything is equal, return the higher-ranking suit (e.g., spades over hearts)
     */
    private Suit selectStrongestSuit(
        final SuitSet suitSet1,
        final SuitSet suitSet2
    ) {
        if (suitSet1.size() > suitSet2.size()) {
            return suitSet1._suit;
        } else if (suitSet2.size() > suitSet1.size()) {
            return suitSet2._suit;
        }

        int honors1 = suitSet1.countHonors();
        int honors2 = suitSet2.countHonors();
        if (honors1 > honors2) {
            return suitSet1._suit;
        } else if (honors2 > honors1) {
            return suitSet2._suit;
        }

        int hcp1 = suitSet1.countHighCardPoints();
        int hcp2 = suitSet2.countHighCardPoints();
        if (hcp1 > hcp2) {
            return suitSet1._suit;
        } else if (hcp2 > hcp1) {
            return suitSet2._suit;
        }

        if (suitSet1._suit._bidOrder > suitSet2._suit._bidOrder) {
            return suitSet1._suit;
        } else {
            return suitSet2._suit;
        }
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

    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Used for recommending a response to a partner's opening bid.
     * The opening bid *might* be doubled.
     */
    @Override
    public Bid recommendOpeningResponse(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    ) {
        return null;    //TODO
    }

    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Used for recommending an overcall to an opponent's opening bid.
     * The opening bid might be doubled and even redoubled, but it is the most recent real bid.
     */
    @Override
    public Bid recommendOvercall(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    ) {
        return null;    //TODO
    }
}
