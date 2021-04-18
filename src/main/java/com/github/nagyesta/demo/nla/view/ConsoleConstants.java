package com.github.nagyesta.demo.nla.view;

public interface ConsoleConstants {
    String RESET = "\033[0;0m";
    String RED = "\033[0;31m";
    String GREEN = "\033[0;32m";
    String YELLOW = "\033[0;33m";
    String BLUE = "\033[0;34m";
    String CYAN = "\033[0;36m";
    String WHITE = "\033[0;37m";

    String CANVAS_TOP_START = CYAN + "┏━━━━━━━━━━━━━━━━━━━━";
    String CANVAS_TOP_END = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓" + RESET;
    String CANVAS_SHAFT = "━━━━━";
    String CANVAS_SHAFT_SEPARATOR = "━━━";
    String CANVAS_BOTTOM_START = YELLOW + "┗━━━━━━━━━━━━━━━━━━━━";
    String CANVAS_BOTTOM_END = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛" + RESET;

    String CAR_CEILING = RED + "═╧═";
    String CAR_FLOOR_STOPPED = RED + "═" + GREEN + "═" + RED + "═";
    String CAR_FLOOR_UP_SLOW = RED + "═" + GREEN + "⇡" + RED + "═";
    String CAR_FLOOR_UP_FAST = RED + "═" + GREEN + "⇈" + RED + "═";
    String CAR_FLOOR_DOWN_SLOW = RED + "═" + GREEN + "⇣" + RED + "═";
    String CAR_FLOOR_DOWN_FAST = RED + "═" + GREEN + "⇊" + RED + "═";
    String CAR_EMPTY = WHITE + "   ";
    String CAR_POPULATED = WHITE + "%d\ud83e\uddcd ";

    String SHAFT_EMPTY = WHITE + "   ";
    String ELEVATOR_CABLE = WHITE + " " + YELLOW + "╎" + WHITE + " ";

    String BUILDING_TOP_SHAFT = "┳━┳━┳";
    String BUILDING_TOP_SHAFT_SEPARATOR = "━━━";
    String BUILDING_TOP_START = CYAN + "┃       " + WHITE + "┏━━━━━━━━━━━━";
    String BUILDING_TOP_END = "━━━━━━┓" + CYAN + "                      ┃" + RESET;
    String BUILDING_FLOOR_START = CYAN + "┃       " + WHITE + "┣━━━━━━━━━━━━╋";
    String BUILDING_FLOOR_END = WHITE + "╋━━━━━━┫" + CYAN + "                      ┃" + RESET;
    String BUILDING_FLOOR_SHAFT_SEPARATOR = WHITE + "╋━━━╋";
    String BUILDING_GROUND_FLOOR_START = GREEN + "┣━━━━━━━" + WHITE + "┻━━━━━━━━━━━━╋";
    String BUILDING_GROUND_FLOOR_END = WHITE + "╋━━━━━━┻" + GREEN + "━━━━━━━━━━━━━━━━━━━━━━┫" + RESET;
    String BUILDING_LEVEL_START_EMPTY = CYAN + "┃       " + WHITE + "┃            ";
    String BUILDING_LEVEL_START_POPULATED = CYAN + "┃       " + WHITE + "┃       %3d\ud83e\uddcd ";
    String BUILDING_LEVEL_SHAFT_SEPARATOR = WHITE + "   ";
    String BUILDING_LEVEL_END = WHITE + "    " + BLUE + "%s" + WHITE + " ┃" + CYAN + "                      ┃";
    String SHAFT_DOOR_CLOSED = CYAN + "┋";
    String SHAFT_DOOR_OPEN = WHITE + " ";
    String BUILDING_FOUNDATION_ROW_START = YELLOW + "┃ Steps: %4d        ";
    String BUILDING_FOUNDATION_ROW_SHAFT = WHITE + "┗━%s━┛" + YELLOW;
    String BUILDING_FOUNDATION_ROW_SHAFT_SEPARATOR = "   ";
    String BUILDING_FOUNDATION_ROW_END = "                 Trips: %4d ┃" + RESET;
}
