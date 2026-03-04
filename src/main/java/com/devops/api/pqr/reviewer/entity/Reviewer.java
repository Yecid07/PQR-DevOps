package com.devops.api.pqr.reviewer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reviewer {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
}
