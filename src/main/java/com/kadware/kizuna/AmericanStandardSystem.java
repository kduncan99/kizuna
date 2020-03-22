/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

public class AmericanStandardSystem implements BiddingSystem {

    public final boolean _fourCardMajors = true;
    private final Logger LOGGER = LogManager.getLogger("AmericanStandardSystem");

    /**
     * Recommends what the player holding the given hand should be at this point,
     * as evidenced by the current content of the given bidding board.
     * Do not call if the hand is passed out
     */
    @Override
    public Bid recommendBid(
        final Hand hand,
        final Board biddingBoard
    ) {
        if (biddingBoard._highestBid == null) {
            return openingBid(hand, biddingBoard);
        }

        return new Bid.Pass(hand._position);
    }

    /**
     * Used for recommending an opening bid.
     * Called ONLY if the bidding board has three or fewer passes, and no real bids.
     */
    private Bid openingBid(
        final Hand hand,
        final Board biddingBoard
    ) {
        LOGGER.info(String.format("Checking opening bid for player %s", hand._position.toString()));

        int hcPoints = hand.countHighCardPoints();
        float quickTricks = hand.countQuickTricks();
        boolean reBiddableSuit = hand.hasFiveCardSuit();
        boolean lengthInMajors = hand.hasLengthInMajors();
        boolean lengthInSpades = hand._cardsBySuit.get(Suit.SPADES).size() >= 5;
        int passes = biddingBoard.getConsecutivePasses();
        LOGGER.info(String.format("  HCP=%d QTricks=%f reBiddableSuit=%s lengthInMajors=%s position=%d",
                                  hcPoints,
                                  quickTricks,
                                  reBiddableSuit,
                                  lengthInMajors,
                                  passes + 1));

        //  Decide whether to open...
        //      Open any hand with 14 or more HCP
        //      Open any hand with 12-13 HCP, 2 quick tricks, and comfortable rebid (5-card suit)
        //      Open any hand with 10-11 HCP, 2 quick tricks, comfortable rebid, and length in majors
        //      Open 3rd position with 10-11 HCP without quick tricks/length in majors
        //          if that bid can also indicate a good lead in the event your partner leads
        //      4th hand - open any hand you would open in 1st or 2nd position
        //          pass any borderline hand lacking strength in majors
        //          maybe open a hand with strength in spades

        boolean shouldBid = false;
        boolean thirdHand = passes == 2;
        boolean fourthHand = passes == 3;
        Bid leadDirect = null;
        if (hcPoints >= 14) {
            LOGGER.info("  Sufficient HCP, needs nothing else to proceed");
            shouldBid = true;
        } else if ((hcPoints >= 12) && (quickTricks >= 2.0) && reBiddableSuit) {
            LOGGER.info("  Good HCP, quick tricks, and reBiddable suit, proceeding");
            shouldBid = true;
        } else if (hcPoints >= 10) {
            LOGGER.info("  Weak hand");
            if (thirdHand) {
                LOGGER.info("    3rd position");
                if ((quickTricks >= 2.0) && reBiddableSuit && lengthInMajors) {
                    LOGGER.info("    has quick tricks, reBiddable suit, length in majors.");
                    shouldBid = true;
                } else {
                    LOGGER.info("    insufficient quick tricks, no reBiddable suit, or no length in majors");
                    //  Do we have a lead direct? AKQ or AQJ four-long is good
                    for (Map.Entry<Suit, Set<Card>> entry : hand._cardsBySuit.entrySet()) {
                        Suit suit = entry.getKey();
                        Set<Card> set = entry.getValue();
                        if (set.size() >= 4) {
                            if (set.contains(new Card(suit, Rank.ACE))
                                && set.contains(new Card(suit, Rank.QUEEN))
                                && (set.contains(new Card(suit, Rank.KING)) || set.contains(new Card(suit, Rank.JACK)))) {
                                LOGGER.info("    has good direction for partner's opening lead.");
                                leadDirect = new Bid.SuitBid(hand._position, 1, suit);
                                shouldBid = true;
                                break;
                            }
                        }
                    }
                    if (leadDirect == null) {
                        LOGGER.info("    has no good suit for suggesting an opening lead.");
                    }
                }
            } else if (fourthHand) {
                LOGGER.info("    4th position");
                if ((quickTricks >= 2.0) && reBiddableSuit && lengthInMajors && lengthInSpades) {
                    LOGGER.info("    has quick tricks, reBiddable suit, length in majors esp. spades.");
                    shouldBid = true;
                } else {
                    LOGGER.info("    insufficient quick tricks, no reBiddable suit, or no length in majors esp. spades.");
                }
            } else {
                LOGGER.info("    1st or 2nd position");
                if ((quickTricks >= 2.0) && reBiddableSuit && lengthInMajors) {
                    LOGGER.info("    has quick tricks, rebiddable suit, length in majors.");
                    shouldBid = true;
                } else {
                    LOGGER.info("    insufficient quick tricks, no rebiddable suit, or no length in majors.");
                }
            }
        }

        if (!shouldBid) {
            LOGGER.info("  Recommending pass");
            return new Bid.Pass(hand._position);
        }

        if (leadDirect != null) {
            LOGGER.info("  Recommending weak lead directing bid %s", leadDirect.toString());
            return leadDirect;
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
}
