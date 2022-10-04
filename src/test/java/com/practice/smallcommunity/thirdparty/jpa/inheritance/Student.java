package com.practice.smallcommunity.thirdparty.jpa.inheritance;

import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Student extends Person{

    private String school;

    @Builder(builderMethodName = "studentBuilder")
    public Student(String name, int age, String school) {
        super(name, age);
        this.school = school;
    }
}
