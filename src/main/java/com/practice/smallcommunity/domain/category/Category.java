package com.practice.smallcommunity.domain.category;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    private boolean enable;

    @Builder
    public Category(String code, String name, boolean enable) {
        this.code = code;
        this.name = name;
        this.enable = enable;
    }

    public void changeCode(String code) {
        this.code = code;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
