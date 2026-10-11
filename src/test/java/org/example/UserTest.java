package org.example;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


public class UserTest {
    User user = new User("marco", 38, false, LocalDate.now().minusYears(38));

    @BeforeAll
    static void setup() {
        System.out.println("setup for user class");
    }

    @AfterAll
    static void cleanUp() {
        System.out.println("clean up for user class");
    }

    @BeforeEach
    void testSetup() {
        System.out.println("setting up for individual test");
    }

    @Test
    void userShouldBeAtLeast18(){
        assertThat(user.age()).isGreaterThanOrEqualTo(12);
    }

    @Test
    void userShouldBeNameMarco(){
        assertThat(user.name()).startsWith("mar");
    }

    @Test
    void blockedShouldBeFalse(){
        assertThat(user.blocked()).as("user %s should not be blocked", user.name()).isFalse();
    }


    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, 8})
    void testIsEven(int number) {
        assertThat(number).isEven();
    }


}
