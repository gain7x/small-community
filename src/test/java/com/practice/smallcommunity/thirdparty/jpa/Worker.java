package com.practice.smallcommunity.thirdparty.jpa;

import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Worker extends Person {

    private String company;

    @Builder(builderMethodName = "workerBuilder")
    public Worker(String name, int age, String company) {
        super(name, age);
        this.company = company;
    }
}
