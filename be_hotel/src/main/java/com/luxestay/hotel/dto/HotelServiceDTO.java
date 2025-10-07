package com.luxestay.hotel.dto;

import com.luxestay.hotel.model.HotelService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class HotelServiceDTO {

    private Long serviceId;
    private String serviceName;
    private String description;
    private BigDecimal price;
    private String category;


    public HotelServiceDTO(HotelService service) {
        this.serviceId = service.getServiceId();
        this.serviceName = service.getServiceName();
        this.description = service.getDescription();
        this.price = service.getPrice();
        this.category = service.getCategory();
    }
}