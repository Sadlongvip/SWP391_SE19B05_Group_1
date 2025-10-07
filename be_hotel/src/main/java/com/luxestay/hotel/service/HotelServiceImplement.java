package com.luxestay.hotel.service;

import com.luxestay.hotel.dto.HotelServiceDTO;
import com.luxestay.hotel.model.HotelService;
import com.luxestay.hotel.repositories.HotelServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelServiceImplement implements HotelServiceServ {

    private final HotelServiceRepository hotelServiceRepository;

    @Autowired
    public HotelServiceImplement(HotelServiceRepository hotelServiceRepository) {
        this.hotelServiceRepository = hotelServiceRepository;
    }

    @Override
    public List<HotelServiceDTO> getAllServices() {
        List<HotelService> serviceEntities = hotelServiceRepository.findAll();

        return serviceEntities.stream()
                .map(HotelServiceDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public HotelServiceDTO createService(HotelServiceDTO hotelServiceDTO) {
        HotelService hotelService = new HotelService(hotelServiceDTO);
        HotelService savedHotelService = hotelServiceRepository.save(hotelService);
        return new HotelServiceDTO(savedHotelService);
    }



}