/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Hand {

    public final Distribution _distribution;
    public final Position _position;

    public Hand(
        final Position position,
        final Set<Card> cards
    ) {
        _position = position;
        _distribution = new Distribution(cards);
    }

    /**
     * Counts high-card points
     */
    public int countHighCardPoints() {
        int result = 0;
        for (SuitSet suitSet : _distribution) {
            for (Card card : suitSet) {
                result += card._rank._highCardPoints;
            }
        }
        return result;
    }

    /**
     * Evaluates point value of the hand
     * @param board Bidding board - if null, we presume the bidding has not yet started
     */
    public int countPoints(
        final Board board
    ) {
        int result = countHighCardPoints();

        //  Figure out if there was any suit opened *and* supported, and who did the supporting.
        //  How we use this assumes that only one suit will be opened and supported, but it probably works
        //  even if somehow there were more than one opened and supported... but it probably doesn't
        //  work right if we opened one suit which partner supported, then partner opened some other suit
        //  and we supported - that situation is rare enough we just make sure not to get confused by it.
        boolean partnerSupported = false;
        boolean weSupported = false;
        Suit suitWeSupported = null;
        for (Suit suit : Suit.values()) {
            Board.SuitMention mention = null;
            if (board != null) {
                mention = board._suitMentions.get(suit);
                if (mention != null) {
                    if ((mention._openedBy == _position) && mention._supported) {
                        partnerSupported = true;
                        break;
                    } else if ((mention._openedBy == getPartner()) && mention._supported) {
                        weSupported = true;
                        suitWeSupported = suit;
                        break;
                    }
                }
            }
        }

        //  Distribution points
        //  TODO rethink this if/when necessary
//        for (Map.Entry<Suit, Set<Card>> entry : _cardsBySuit.entrySet()) {
//            Suit suit = entry.getKey();
//            Set<Card> subHand = entry.getValue();
//            if (subHand.size() == 0) {
//                //  void, usually worth 3 points
//                result += 3;
//                if (weSupported) {
//                    ++result;
//                    if (_cardsBySuit.get(suitWeSupported).size() > 3) {
//                        ++result;
//                    }
//                }
//            } else if (subHand.size() == 1) {
//                //  singleton, *may* be worth an extra two points, but a singleton king is only worth 1 extra.
//                result += subHand.contains(new Card(suit, Rank.KING)) ? 1 : 2;
//                if (weSupported && _cardsBySuit.get(suitWeSupported).size() > 3) {
//                    ++result;
//                }
//            } else if (subHand.size() == 2) {
//                //  doubleton, *may* be worth an extra point, but Q-x and J-x won't be.
//                if (!subHand.contains(new Card(suit, Rank.QUEEN))
//                    && !subHand.contains(new Card(suit, Rank.JACK))) {
//                    ++result;
//                }
//            } else if (subHand.size() > 4 && partnerSupported) {
//                //  add points for length presuming partner has raised our 5-card suit
//                //  1 point for the 5th card, 2 points for each additional card
//                int cardsOverFive = subHand.size() - 5;
//                result = result + 1 + 2 * cardsOverFive;
//            }
//        }

        return result;
    }

    /**
     * Count number of quick tricks
     */
    public float countQuickTricks() {
        float result = 0;
        for (SuitSet suitSet : _distribution) {
            boolean ace = suitSet.contains(new Card(suitSet._suit, Rank.ACE));
            boolean king = suitSet.contains(new Card(suitSet._suit, Rank.KING));
            boolean queen = suitSet.contains(new Card(suitSet._suit, Rank.QUEEN));
            if (ace && king) {
                result += 2.0f;
            } else if (ace && queen) {
                result += 1.5f;
            } else if (ace || (king && queen)) {
                result += 1.0f;
            } else if (king && (suitSet.size() > 1)) {
                result += 0.5f;
            }
        }

        return result;
    }

    /**
     * Retrieves the Position of the partner of the holder of this hand.
     */
    public Position getPartner() {
        return _position.getPartner();
    }

    /**
     * Determines whether we have length in majors...
     * For bidding purposes this is defined by having at least 8 cards in majors,
     * and at least one 5-card or better major suit.
     */
    public boolean hasLengthInMajors() {
        int spades = _distribution.getSuitSet(Suit.SPADES).size();
        int hearts = _distribution.getSuitSet(Suit.HEARTS).size();
        return (spades + hearts) >= 8 && ((spades >= 5) || (hearts >= 5));
    }

    /**
     * For bidding purposes, determines whether we have at least one 5-card or longer suit
     */
    public boolean hasLongSuit() {
        return _distribution.getFirst().size() >= 5;
    }

    @Override
    public String toString() {
        return String.format("%s : %s DIST=%s HCP=%d qt=%f",
                             _position.toString(),
                             _distribution.toString(),
                             _distribution.getDistributionString(),
                             countHighCardPoints(),
                             countQuickTricks());
    }
}
