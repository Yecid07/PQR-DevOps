package com.devops.api.pqr.pqr.entity;

import com.devops.api.pqr.document.entity.Document;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pqr {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String type;
    private String customerEmail;
    private String description;

    @OneToMany(mappedBy = "pqr", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Document> documents;
}