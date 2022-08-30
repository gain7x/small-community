package com.practice.smallcommunity.thirdparty.jpa;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class JpaInheritanceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    StudentRepository studentRepository;

    Person person = Person.builder()
        .name("홍길동")
        .age(20)
        .build();

    Worker worker = Worker.workerBuilder()
        .name("김철수")
        .age(25)
        .company("Google")
        .build();

    Student student = Student.studentBuilder()
        .name("김기영")
        .age(16)
        .school("학교")
        .build();

    @Test
    void Person_저장() {
        personRepository.save(person);
        em.flush();
    }

    @Test
    void Worker_저장() {
        workerRepository.save(worker);
        em.flush();
    }

    @Test
    void Worker_조회() {
        workerRepository.save(worker);
        em.flush();
        em.clear();

        Worker findWorker = workerRepository.findById(worker.getId()).orElseThrow();
    }

    @Test
    void Person_저장_조회() {
        personRepository.save(person);
        em.flush();
        em.clear();

        Person findPerson = personRepository.findById(person.getId()).orElseThrow();
    }

    @Test
    void Worker_저장_Person_조회() {
        workerRepository.save(worker);
        em.flush();
        em.clear();

        Person findPerson = personRepository.findById(worker.getId()).orElseThrow();
    }

    @Test
    void Student_Worker_저장_Person_조회() {
        studentRepository.save(student);
        workerRepository.save(worker);
        em.flush();
        em.clear();

        Person person = em.find(Person.class, 1L);
    }
}
