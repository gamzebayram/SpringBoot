package io.upschool.controller;


import io.upschool.dto.BaseResponse;
import io.upschool.dto.flight.FlightSaveRequest;
import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<Object> createFlight(@Valid @RequestBody FlightSaveRequest request) {
        FlightSaveResponse flightSaveResponse = flightService.createFlight(request);
        BaseResponse<FlightSaveResponse> response =  BaseResponse.<FlightSaveResponse>builder()
                .status(HttpStatus.CREATED.value())
                .isSuccess(true)
                .data(flightSaveResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(("{flightNumber}"))
    public ResponseEntity<Object>getFlightByFlightNumber(@PathVariable("flightNumber") String flightNumber) {
        FlightSaveResponse flight = flightService.getFlightByFlightNumber(flightNumber);
        BaseResponse<FlightSaveResponse> response =  BaseResponse.<FlightSaveResponse>builder()
                .status(HttpStatus.FOUND.value())
                .isSuccess(true)
                .data(flight)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Object>getAllFlights() {
        List<FlightSaveResponse> flights = flightService.getAllFlights();
        BaseResponse<List<FlightSaveResponse>> response =  BaseResponse.<List<FlightSaveResponse>>builder()
                .status(HttpStatus.OK.value())
                .isSuccess(true)
                .data(flights)
                .build();
        return ResponseEntity.ok(response);
    }


}
