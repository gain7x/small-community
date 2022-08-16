package com.practice.smallcommunity.repository.content;

import com.practice.smallcommunity.domain.content.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

}
