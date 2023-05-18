package com.cda.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
public class Tenant {
    @Id
    @Column()
    private String id;

    @Column(nullable = false, unique = true)
    private String db;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String url;

}
