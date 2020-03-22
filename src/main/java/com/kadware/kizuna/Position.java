/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public enum Position {
    SOUTH("SOUTH", 1),
    WEST("WEST", 2),
    NORTH("NORTH", 3),
    EAST("EAST", 4);

    private final String _descriptor;
    private final int _ordinal;

    Position(
        final String descriptor,
        final int ordinal
    ) {
        _descriptor = descriptor;
        _ordinal = ordinal;
    }

    public Position getPartner() {
        switch (this) {
            case SOUTH: return NORTH;
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case WEST: return EAST;
        }
        return null;
    }

    public static Position[] getValuesByOrdinal() {
        Position[] result = new Position[4];
        for (Position pos : Position.values()) {
            result[pos._ordinal - 1] = pos;
        }
        return result;
    }

    @Override
    public String toString() {
        return _descriptor;
    }
}
