package com.practice.smallcommunity.domain.attachment;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.member.Member;
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
public class UploadFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Member uploader;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    private Content content;

    @Builder
    public UploadFile(Member uploader, String bucket, String filename, String url) {
        this.uploader = uploader;
        this.bucket = bucket;
        this.filename = filename;
        this.url = url;
    }

    public void attach(Content content) {
        this.content = content;
    }
}
