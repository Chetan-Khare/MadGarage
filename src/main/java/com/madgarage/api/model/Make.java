package com.madgarage.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "makes")
public class Make {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // One Brand makes many different Models
    @OneToMany(mappedBy = "make", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<CarModel> models = new ArrayList<>();
}