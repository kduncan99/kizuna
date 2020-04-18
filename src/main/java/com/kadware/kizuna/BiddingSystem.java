/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.List;

public abstract class BiddingSystem {

    public static enum Name {
        AmericanStandard
    }

    /**
     * Recommends what the player holding the given hand should be at this point,
     * as evidenced by the current content of the given bidding board.
     * Do not call if the hand is passed out
     */
    public Bid recommendBid(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    ) {
        //  Where is the auction at?
        if (biddingBoard._openingBid == null) {
            //  No bids yet - look for an opening bid
            return recommendOpeningBid(hand, biddingBoard, commentary);
        }

        //  Is this an opportunity for a response to, or an overcall of an opening bid?
        //  It's okay if it's doubled or even redoubled - we'll think about that in the
        //  response or overcall method...
        if (biddingBoard._openingBid == biddingBoard._highestBid) {
            if (biddingBoard._openingBid._position == hand._position.getPartner()) {
                return recommendOpeningResponse(hand, biddingBoard, commentary);
            } else {
                return recommendOvercall(hand, biddingBoard, commentary);
            }
        }

        //  How about an overcall of an opening bid?
        return new Bid.Pass(hand._position);
    }

    public abstract Bid recommendOpeningBid(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    );

    public abstract Bid recommendOpeningResponse(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    );

    public abstract Bid recommendOvercall(
        final Hand hand,
        final Board biddingBoard,
        final List<String> commentary
    );
}
