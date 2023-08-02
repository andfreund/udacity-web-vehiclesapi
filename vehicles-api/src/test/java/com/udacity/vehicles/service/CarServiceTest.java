package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.udacity.vehicles.Assertions.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    private CarService carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private PriceClient priceClient;
    @Mock
    private MapsClient mapsClient;

    @Before
    public void setUp() {
        carService = new CarService(carRepository, priceClient, mapsClient);
    }

    // TODO what happens if price is not found for this vehicleId?
    // TODO what happens if location is not found for this vehicleId?

    @Test
    public void listCars() {
        Car defaultCar = getDefaultCar();
        given(carRepository.findAll()).willReturn(List.of(defaultCar));

        List<Car> cars = carService.list();

        assertEquals(1, cars.size());
        assertCarEquals(defaultCar, cars.get(0));
    }

    @Test
    public void findCarByIdCallsPricingAPI() {
        given(carRepository.findById(any())).willReturn(Optional.of(getDefaultCar()));
        given(priceClient.getPrice(any())).willReturn("USD 1234.56");

        Car car = carService.findById(1L);

        assertEquals("USD 1234.56", car.getPrice());
        verify(priceClient, times(1)).getPrice(1L);
    }

    @Test
    public void findCarByIdCallsLocationAPI() {
        Car defaultCar = getDefaultCar();
        Location defaultLocation = getDefaultLocation();
        given(carRepository.findById(any())).willReturn(Optional.of(defaultCar));
        given(mapsClient.getAddress(any())).willReturn(defaultLocation);

        Car car = carService.findById(1L);

        assertLocationEquals(defaultLocation, car.getLocation());
        verify(mapsClient, times(1)).getAddress(
                argThat((Location loc) ->
                        Objects.equals(loc.getLat(), defaultCar.getLocation().getLat()) &&
                        Objects.equals(loc.getLon(), defaultCar.getLocation().getLon())));
    }

    @Test
    public void findCarByIdReturnsCorrectCar() {
        Car defaultCar = getDefaultCar();
        Location defaultLocation = getDefaultLocation();
        given(carRepository.findById(any())).willReturn(Optional.of(defaultCar));
        given(mapsClient.getAddress(any())).willReturn(defaultLocation);

        Car car = carService.findById(1L);

        assertCarEquals(defaultCar, car);
    }

    @Test(expected = CarNotFoundException.class)
    public void findCarByIdFailsWithCarNotFoundException() {
        given(carRepository.findById(any())).willReturn(Optional.empty());

        Car car = carService.findById(1L);

        assertNull(car);
    }

    @Test
    public void saveExistingCarUpdatesCar() {
        Car defaultCar = getDefaultCar();
        Car anotherCar = getAnotherCar();
        anotherCar.setId(defaultCar.getId());
        given(carRepository.findById(any())).willReturn(Optional.of(defaultCar));
        given(carRepository.save(any())).willReturn(anotherCar);

        carService.save(anotherCar);

        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(
                argThat((Car car) ->
                    car.getCondition() == anotherCar.getCondition() &&
                    Objects.equals(car.getId(), anotherCar.getId()) &&
                    Objects.equals(car.getLocation().getLat(), anotherCar.getLocation().getLat()) &&
                    Objects.equals(car.getLocation().getLon(), anotherCar.getLocation().getLon()) &&
                    Objects.equals(car.getDetails().getManufacturer().getCode(), anotherCar.getDetails().getManufacturer().getCode()) &&
                    Objects.equals(car.getDetails().getManufacturer().getName(), anotherCar.getDetails().getManufacturer().getName()) &&
                    Objects.equals(car.getDetails().getBody(), anotherCar.getDetails().getBody()) &&
                    Objects.equals(car.getDetails().getEngine(), anotherCar.getDetails().getEngine()) &&
                    Objects.equals(car.getDetails().getNumberOfDoors(), anotherCar.getDetails().getNumberOfDoors())));
    }

    @Test
    public void saveNewCar() {
        Car anotherCar = getAnotherCar();
        anotherCar.setId(null);
        given(carRepository.save(any())).willReturn(anotherCar);

        Car newCar = carService.save(anotherCar);

        verify(carRepository, times(0)).findById(any());
        verify(carRepository, times(1)).save(any());
        assertCarEquals(anotherCar, newCar);
    }

    @Test
    public void deleteExistingCar() {
        Car defaultCar = getDefaultCar();
        given(carRepository.findById(any())).willReturn(Optional.of(defaultCar));

        carService.delete(defaultCar.getId());

        verify(carRepository, times(1)).findById(defaultCar.getId());
        verify(carRepository, times(1)).delete(defaultCar);
    }

    @Test(expected = CarNotFoundException.class)
    public void deleteFailsWithCarNotFoundException() {
        Car defaultCar = getDefaultCar();
        given(carRepository.findById(any())).willReturn(Optional.empty());

        carService.delete(defaultCar.getId());

        verify(carRepository, times(1)).findById(defaultCar.getId());
        verify(carRepository, times(0)).delete(defaultCar);
    }

    private Car getDefaultCar() {
        Car car = new Car();
        car.setId(1L);
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }

    private Car getAnotherCar() {
        Car car = new Car();
        car.setId(2L);
        car.setLocation(new Location(42.4242, -47.11));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(102, "Volkswagen");
        details.setManufacturer(manufacturer);
        details.setModel("Golf");
        details.setMileage(65498);
        details.setExternalColor("red");
        details.setBody("somebody");
        details.setEngine("1.2L");
        details.setFuelType("Gasoline");
        details.setModelYear(2010);
        details.setProductionYear(2010);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.NEW);
        return car;
    }

    private Location getDefaultLocation() {
        Location defaultLocation = new Location(
                getDefaultCar().getLocation().getLat(),
                getDefaultCar().getLocation().getLon());
        defaultLocation.setAddress("374 William S Canning Blvd");
        defaultLocation.setCity("Fall River");
        defaultLocation.setZip("2721");
        defaultLocation.setState("MA");
        return defaultLocation;
    }
}
