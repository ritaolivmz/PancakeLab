package org.pancakelab.validators;

import java.util.*;

public class BuildingValidator {

    private static final Set<Integer> VALID_BUILDINGS = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

    private static final Map<Integer, Set<Integer>> VALID_ROOMS_BY_BUILDING = new HashMap<>();

    static {
        VALID_ROOMS_BY_BUILDING.put(1, new HashSet<>(Arrays.asList(101, 102, 201)));
        VALID_ROOMS_BY_BUILDING.put(2, new HashSet<>(Arrays.asList(20, 21, 22)));
    }

    public static void validateBuildingExists(int building) {
        if (!VALID_BUILDINGS.contains(building)) {
            throw new IllegalArgumentException("Building %d does not exist.".formatted(building));
        }
    }

    public static void validateRoomExists(int building, int room) {
        if (!VALID_ROOMS_BY_BUILDING.containsKey(building)) {
            throw new IllegalArgumentException("Building %d does not exist.".formatted(building));
        }
        Set<Integer> roomsInBuilding = VALID_ROOMS_BY_BUILDING.get(building);
        if (!roomsInBuilding.contains(room)) {
            throw new IllegalArgumentException("Room %d does not exist in building %d.".formatted(room, building));
        }
    }
}
