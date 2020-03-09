/*
 * Copyright (c) 2020 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.kizuna;

public enum Position {
    SOUTH,
    WEST,
    NORTH,
    EAST;

    Position getPartner() {
        switch (this) {
            case SOUTH: return NORTH;
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case WEST: return EAST;
        }
        return null;
    }
}
