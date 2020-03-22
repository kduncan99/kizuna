/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Kizuna {

    public static final int POINTS_FOR_GAME_MAJORS = 26;
    public static final int POINTS_FOR_GAME_NOTRUMP = 26;
    public static final int POINTS_FOR_GAME_MINORS = 29;
    public static final int POINTS_FOR_SMALL_SLAM = 33;
    public static final int POINTS_FOR_GRAND_SLAM = 37;

    public static void main(
        final String[] args
    ) {
        BiddingSystem system = new AmericanStandardSystem(false);
        Board board = new Board();
        Deck deck = Deck.getInstance();

        deck.shuffle();
        Map<Position, Hand> hands = deck.deal();
        for (Position pos : Position.getValuesByOrdinal()) {
            Hand hand = hands.get(pos);
            System.out.println(hand.toString());
            List<String> commentary = new LinkedList<>();
            system.recommendBid(hand, board, commentary);
            for (String c : commentary) {
                System.out.println(":: " + c);
            }
        }
    }
}
