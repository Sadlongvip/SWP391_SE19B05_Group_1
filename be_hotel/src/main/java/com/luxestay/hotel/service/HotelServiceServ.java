package com.luxestay.hotel.service;

import com.luxestay.hotel.dto.HotelServiceDTO;


import java.util.List;

public interface HotelServiceServ {
    List<HotelServiceDTO> getAllServices();

    HotelServiceDTO createService(HotelServiceDTO hotelServiceDTO);
}
