package com.kadware.kizuna;

public abstract class Bid {

    public final Position _position;

    private Bid(
        final Position position
    ) {
        _position = position;
    }

    public static abstract class RealBid extends Bid {

        public final int _level;

        private RealBid(
            final Position position,
            final int level
        ) {
            super(position);
            _level = level;
        }

        public abstract int compareTo(
            final Bid bid
        );
    }

    public static class SuitBid extends RealBid {

        public final Suit _suit;

        SuitBid(
            final Position position,
            final int level,
            final Suit suit
        ) {
            super(position, level);
            _suit = suit;
        }

        @Override
        public int compareTo(
            final Bid bid
        ) {
            if (bid instanceof SuitBid) {
                if (_level < ((SuitBid) bid)._level) return -1;
                if (_level > ((SuitBid) bid)._level) return 1;
                return _suit._bidOrder - ((SuitBid) bid)._suit._bidOrder;
            } else if (bid instanceof NoTrumpBid) {
                return (_level <= ((NoTrumpBid) bid)._level) ? -1 : 1;
            } else {
                throw new RuntimeException("Nonsense comparison");
            }
        }

        @Override
        public boolean equals(
            final Object obj
        ) {
            return (obj instanceof SuitBid) && (_level == ((SuitBid) obj)._level) && (_suit == ((SuitBid) obj)._suit);
        }

        @Override
        public String toString() {
            return String.format("%s:%d%s", _position.toString(), _level, _suit.toString());
        }
    }

    public static class NoTrumpBid extends RealBid {

        public NoTrumpBid(
            final Position position,
            final int level
        ) {
            super(position, level);
        }

        @Override
        public int compareTo(
            final Bid bid
        ) {
            if (bid instanceof SuitBid) {
                return _level >= ((SuitBid) bid)._level ? 1 : -1;
            } else if (bid instanceof NoTrumpBid) {
                return _level - ((NoTrumpBid) bid)._level;
            } else {
                throw new RuntimeException("Nonsense comparison");
            }
        }

        @Override
        public boolean equals(
            final Object obj
        ) {
            return (obj instanceof NoTrumpBid) && (_level == ((NoTrumpBid) obj)._level);
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
        public boolean equals(
            final Object obj
        ) {
            return obj instanceof Double;
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
        public boolean equals(
            final Object obj
        ) {
            return obj instanceof Redouble;
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
        public boolean equals(
            final Object obj
        ) {
            return obj instanceof Pass;
        }

        @Override
        public String toString() {
            return String.format("%s:PASS", _position.toString());
        }
    }
}
