package com.cda.model;

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
    @Column(name = "tenant_id")
    private String id;

    @Column()
    private String db;

    @Column()
    private String password;

    @Column()
    private String url;

}
