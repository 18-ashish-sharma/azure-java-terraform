package com.onedoorway.project.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "houses")
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private long id;

    @Column(name = "house_code", unique = true)
    @NotBlank
    private String houseCode;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address1")
    private String addrLine1;

    @Column(name = "address2")
    private String addrLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "post_code")
    @Pattern(regexp = "\\d{4}")
    private String postCode;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "house_clients",
            joinColumns = @JoinColumn(name = "house_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    @JsonManagedReference
    private List<Client> clients;
}
