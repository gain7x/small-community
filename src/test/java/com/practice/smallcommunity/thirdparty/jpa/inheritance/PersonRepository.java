package com.practice.smallcommunity.thirdparty.jpa.inheritance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
