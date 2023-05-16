package com.cda.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tenant {

    @Id
    @Column()
    private String id;

    @Column(nullable = false)
    private String db;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String url;

}
