package com.luxestay.hotel.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity @Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", unique = true)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    //========================================================
    @OneToMany(mappedBy = "role")
    private List<Account> accounts;
}
