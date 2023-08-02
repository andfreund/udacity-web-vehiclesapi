package com.udacity.vehicles;

import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;

import static junit.framework.TestCase.assertEquals;

public class Assertions {
    public static void assertLocationEquals(Location expected, Location actual) {
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getZip(), actual.getZip());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getLat(), actual.getLat());
        assertEquals(expected.getLon(), actual.getLon());
    }

    public static void assertCarEquals(Car expected, Car actual) {
        assertEquals(expected.getCondition(), actual.getCondition());
        assertEquals(expected.getLocation().getLat(), actual.getLocation().getLat());
        assertEquals(expected.getLocation().getLon(), actual.getLocation().getLon());

        assertCarDetailEquals(expected.getDetails(), actual.getDetails());

        if (expected.getId() != null) {
            assertEquals((long) expected.getId(), (long) actual.getId());
        }

        if (expected.getPrice() != null) {
            assertEquals(expected.getPrice(), actual.getPrice());
        }
    }

    public static void assertCarDetailEquals(Details expected, Details actual) {
        assertEquals((long) expected.getManufacturer().getCode(),
                (long) actual.getManufacturer().getCode());
        assertEquals(expected.getManufacturer().getName(),
                actual.getManufacturer().getName());
        assertEquals(expected.getBody(), actual.getBody());
        assertEquals(expected.getEngine(), actual.getEngine());
        assertEquals(expected.getFuelType(), actual.getFuelType());
        assertEquals(expected.getExternalColor(), actual.getExternalColor());
        assertEquals(expected.getMileage(), actual.getMileage());
        assertEquals(expected.getModel(), actual.getModel());
        assertEquals(expected.getModelYear(), actual.getModelYear());
        assertEquals(expected.getProductionYear(), actual.getProductionYear());
        assertEquals(expected.getNumberOfDoors(), actual.getNumberOfDoors());
    }
}
