package org.example;

import java.time.LocalDate;

public class User {
    private String name;
    private Integer age;
    private Boolean blocked;
    private LocalDate birthDate;

    // Constructor
    public User(String name, Integer age, Boolean blocked, LocalDate birthDate) {
        this.name = name;
        this.age = age;
        this.blocked = blocked;
        this.birthDate = birthDate;
    }

    // Getters (named like record methods)
    public String name() {
        return name;
    }

    public Integer age() {
        return age;
    }

    public Boolean blocked() {
        return blocked;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
