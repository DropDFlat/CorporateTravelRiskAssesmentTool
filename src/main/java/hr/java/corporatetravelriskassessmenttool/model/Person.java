package hr.java.corporatetravelriskassessmenttool.model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Person extends Entity {
    private String name;
    private LocalDate dateOfBirth;

    protected Person(Long id, String name, LocalDate dateOfBirth) {
        super(id);
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(getId(), person.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateOfBirth);
    }
}
