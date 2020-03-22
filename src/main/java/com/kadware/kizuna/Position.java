/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public enum Position {
    SOUTH("SOUTH"),
    WEST("WEST"),
    NORTH("NORTH"),
    EAST("EAST");

    private final String _descriptor;

    Position(
        final String descriptor
    ) {
        _descriptor = descriptor;
    }

    Position getPartner() {
        switch (this) {
            case SOUTH: return NORTH;
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case WEST: return EAST;
        }
        return null;
    }

    @Override
    public String toString() {
        return _descriptor;
    }
}
