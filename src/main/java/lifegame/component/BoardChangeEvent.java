package lifegame.component;

import lifegame.util.Point;

public record BoardChangeEvent(Point coord, boolean newState) {
}
