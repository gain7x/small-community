package com.practice.smallcommunity.repository.post;

import com.practice.smallcommunity.domain.post.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
