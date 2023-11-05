package ru.hvayon.FlightService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class FlightResponseList {
    int page;
    int pageSize;
    int totalElements;
    List<FlightResponse> items;
}
