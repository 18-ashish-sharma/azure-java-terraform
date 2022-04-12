package com.onedoorway.project.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "lookup")
public class Lookup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lookup_id")
    private long id;

    private String name;

    @Column(name = "lookup_type")
    @Enumerated(EnumType.ORDINAL)
    private LookupType lookupType;
}
