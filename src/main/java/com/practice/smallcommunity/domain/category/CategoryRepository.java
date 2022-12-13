package com.practice.smallcommunity.domain.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndEnableIsTrue(Long id);

    List<Category> findAllByEnableIsTrue();

    boolean existsByCode(String code);

    Optional<Category> findByCode(String code);

    Optional<Category> findByCodeAndEnableIsTrue(String code);
}
