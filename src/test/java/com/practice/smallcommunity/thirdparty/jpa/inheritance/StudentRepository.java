package com.practice.smallcommunity.thirdparty.jpa.inheritance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
