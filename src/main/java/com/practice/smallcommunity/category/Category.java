package com.practice.smallcommunity.category;

import com.practice.smallcommunity.common.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq_gen")
    @SequenceGenerator(name = "category_seq_gen", sequenceName = "category_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    private boolean enable;

    private boolean cudAdminOnly;

    @Builder
    public Category(String code, String name, boolean enable, boolean cudAdminOnly) {
        this.code = code;
        this.name = name;
        this.enable = enable;
        this.cudAdminOnly = cudAdminOnly;
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

    public void setCudAdminOnly(boolean cudAdminOnly) {
        this.cudAdminOnly = cudAdminOnly;
    }
}
