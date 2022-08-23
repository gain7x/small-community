package com.practice.smallcommunity.thirdparty.violation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

public class JavaxValidatorTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 엔티티가_제약조건을_지키면_위반_항목이_없다() {
        //given
        TestEntity entity = new TestEntity("text");

        //when
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        //then
        assertThat(violations).isEmpty();
    }

    @Test
    void 엔티티가_제약조건을_위배하면_위반_항목이_존재한다() {
        //given
        TestEntity entity = new TestEntity("");

        //when
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);

        //then
        assertThat(violations).isNotEmpty();
    }

    @Test
    void 위반데이터_확인용() {
        //given
        TestEntity entity = new TestEntity("");

        //when
        Set<ConstraintViolation<TestEntity>> violations = validator.validate(entity);
        ConstraintViolation<TestEntity> violation = violations.iterator().next();

        //then
        System.out.println(violation.getPropertyPath().toString() + ": " + violation.getMessage());
    }

    @Getter
    @AllArgsConstructor
    static class TestEntity {

        @NotBlank
        private String text;
    }
}
