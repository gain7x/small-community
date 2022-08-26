package com.practice.smallcommunity.domain.board;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import com.practice.smallcommunity.domain.category.Category;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    private boolean enable;

    @Builder
    public Board(Category category, String name, boolean enable) {
        this.category = category;
        this.name = name;
        this.enable = enable;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
