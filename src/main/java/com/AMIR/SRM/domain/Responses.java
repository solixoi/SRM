package com.AMIR.SRM.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="responses")
public class Responses {
    @Id
    private long id;
}
