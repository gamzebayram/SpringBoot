package io.upschool.service;



import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.route.RouteSaveRequest;
import io.upschool.dto.route.RouteSaveResponse;
import io.upschool.entity.Flight;
import io.upschool.entity.Route;
import io.upschool.entity.Airport;
import io.upschool.exception.*;
import io.upschool.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final AirportService airportService;

    @Transactional(readOnly = true)
    public RouteSaveResponse getRouteById(Long id) {
        Route route = routeRepository.getRouteByIdIs(id);
        if (route == null) {
            throw new RouteNotFoundException("Route not found.");
        }
        return RouteSaveResponse.builder()
                .id(route.getId())
                .departureAirportName(route.getDepartureAirport().getAirportName())
                .departureAirportLocation(route.getDepartureAirport().getAirportLocation())
                .arrivalAirportName(route.getArrivalAirport().getAirportName())
                .arrivalAirportLocation(route.getArrivalAirport().getAirportLocation())
                .build();
    }

    @Transactional
    public RouteSaveResponse createRoute(RouteSaveRequest routeDTO) {
        checkDestination(routeDTO);
        checkIsRouteAlreadySaved(routeDTO);

        Route routeResponse = buildRouteAndSave(routeDTO);
        return RouteSaveResponse.builder()
                .id(routeResponse.getId())
                .departureAirportName(routeResponse.getDepartureAirport().getAirportName())
                .departureAirportLocation(routeResponse.getDepartureAirport().getAirportLocation())
                .arrivalAirportName(routeResponse.getArrivalAirport().getAirportName())
                .arrivalAirportLocation(routeResponse.getArrivalAirport().getAirportLocation())
                .build();
    }



    @Transactional(readOnly = true)
    public List<RouteSaveResponse> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        return routes.stream()
                .map(route -> new RouteSaveResponse(
                        route.getId(),
                        route.getDepartureAirport().getAirportName(),
                        route.getDepartureAirport().getAirportLocation(),
                        route.getArrivalAirport().getAirportName(),
                        route.getArrivalAirport().getAirportLocation()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Route getReferenceById(Long id) {
        return routeRepository.getReferenceById(id);
    }


    private Route buildRouteAndSave(RouteSaveRequest routeDTO) {
        Airport departureAirport = airportService.findAirportByIataCode(routeDTO.getDepartureAirportIataCode());
        Airport arrivalAirport = airportService.findAirportByIataCode(routeDTO.getArrivalAirportIataCode());

        if (departureAirport == null || arrivalAirport == null) {
            throw new AirportNotFoundException("Airport not found.");
        }

        Route newRoute = Route.builder()
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .build();
        return routeRepository.save(newRoute);
    }


    private void checkIsRouteAlreadySaved(RouteSaveRequest routeDTO) {
        Route findRouteByDepartureAirport_IataCodeAndArrivalAirport_IataCode = routeRepository.findRouteByDepartureAirport_IataCodeAndArrivalAirport_IataCode(routeDTO.getDepartureAirportIataCode(), routeDTO.getArrivalAirportIataCode());
        if (findRouteByDepartureAirport_IataCodeAndArrivalAirport_IataCode != null ) {
            throw new RouteAlreadySavedException("Route already exists.");
        }
    }
    private void checkDestination(RouteSaveRequest routeDTO) {
        if (routeDTO.getDepartureAirportIataCode().equalsIgnoreCase(routeDTO.getArrivalAirportIataCode())) {
            throw new InvalidRouteException("Invalid route.");
        }
    }


}
