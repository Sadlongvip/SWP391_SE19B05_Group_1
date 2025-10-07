package com.luxestay.hotel.controller;


import com.luxestay.hotel.dto.HotelServiceDTO;
import com.luxestay.hotel.model.HotelService;
import com.luxestay.hotel.service.HotelServiceServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private HotelServiceServ hotelService;

    @GetMapping("public/getall")
    private List<HotelServiceDTO> getServices() {
        return hotelService.getAllServices();
    }

    @PostMapping("admin/services")
    private HotelServiceDTO addService(@RequestBody HotelServiceDTO hotelServiceDTO) {
        return hotelService.createService(hotelServiceDTO);
    }
}
