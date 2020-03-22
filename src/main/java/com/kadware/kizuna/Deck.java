/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Deck {

    private final LinkedList<Card> _deck = new LinkedList<>();
    private final Random _random = new Random(System.currentTimeMillis());
    private static final Deck _instance = new Deck();

    public Deck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                _deck.add(new Card(suit, rank));
            }
        }
    }

    public Map<Position, Hand> deal() {
        int cx = 0;
        while (cx < 52) {
            for (Position pos : Position.values()) {
                Card card = _deck.get(cx++);
                card._position = pos;
                card._played = false;
            }
        }

        Map<Position, Set<Card>> temporaryDistribution = new HashMap<>();
        for (Position pos : Position.values()) {
            temporaryDistribution.put(pos, new HashSet<Card>());
        }

        for (Card card : _deck) {
            temporaryDistribution.get(card._position).add(card);
        }

        HashMap<Position, Hand> result = new HashMap<>();
        for (Map.Entry<Position, Set<Card>> entry : temporaryDistribution.entrySet()) {
            Position pos = entry.getKey();
            result.put(pos, new Hand(pos, entry.getValue()));
        }

        return result;
    }

    public static Deck getInstance() {
        return _instance;
    }

    public void shuffle() {
        for (int i = 0; i < 250; ++i) {
            int from = _random.nextInt(52);
            int to = _random.nextInt(52);
            Card card = _deck.remove(from);
            _deck.add(to, card);
        }
    }
}
