package com.kadware.kizuna;

public class Bid {

    public final Position _position;

    private Bid(
        final Position position
    ) {
        _position = position;
    }

    public static class SuitBid extends Bid {
        public final int _level;
        public final Suit _suit;

        SuitBid(
            final Position position,
            final int level,
            final Suit suit
        ) {
            super(position);
            _level = level;
            _suit = suit;
        }

        @Override
        public String toString() {
            return String.format("%s:%d%s", _position.toString(), _level, _suit.toString());
        }
    }

    public static class NoTrumpBid extends Bid {
        public final int _level;

        public NoTrumpBid(
            final Position position,
            final int level
        ) {
            super(position);
            _level = level;
        }

        @Override
        public String toString() {
            return String.format("%s:%dNT", _position.toString(), _level);
        }
    }

    public static class Double extends Bid {

        public Double(
            final Position position
        ) {
            super(position);
        }

        @Override
        public String toString() {
            return String.format("%s:DOUBLE", _position.toString());
        }
    }

    public static class Redouble extends Bid {

        public Redouble(
            final Position position
        ) {
            super(position);
        }

        @Override
        public String toString() {
            return String.format("%s:REDOUBLE", _position.toString());
        }
    }

    public static class Pass extends Bid {

        public Pass(
            final Position position
        ) {
            super(position);
        }

        @Override
        public String toString() {
            return String.format("%s:PASS", _position.toString());
        }
    }
}
