package com.luxestay.hotel.repositories;

import com.luxestay.hotel.model.HotelService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {

}
