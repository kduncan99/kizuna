package com.kadware.kizuna;

public class Bid {

    public final Position _position;

    private Bid(
        final Position position
    ) {
        _position = position;
    }

    public class SuitBid extends Bid {
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
    }

    public class NoTrumpBid extends Bid {
        public final int _level;
        public NoTrumpBid(
            final Position position,
            final int level
        ) {
            super(position);
            _level = level;
        }
    }

    public class Double extends Bid {

        public Double(
            final Position position
        ) {
            super(position);
        }
    }

    public class Redouble extends Bid {

        public Redouble(
            final Position position
        ) {
            super(position);
        }
    }

    public class Pass extends Bid {

        public Pass(
            final Position position
        ) {
            super(position);
        }
    }
}
