package com.practice.smallcommunity.repository.content.post;

import com.practice.smallcommunity.domain.content.post.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
