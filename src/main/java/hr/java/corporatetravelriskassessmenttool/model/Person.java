package hr.java.corporatetravelriskassessmenttool.model;

import java.time.LocalDate;
import java.util.Objects;
/**
 * An abstract base class representing a person.
 * <p>
 * Extends {@link Entity} by adding common personal attributes like name and date of birth.
 * This class is intended to be extended by more specific types such as {@link Employee}.
 */
public abstract class Person extends Entity {
    private String name;
    private LocalDate dateOfBirth;
    /**
     * Constructs a {@code Person} with the specified ID, name, and date of birth.
     *
     * @param id           the unique identifier
     * @param name         the name of the person
     * @param dateOfBirth  the person's date of birth
     */
    protected Person(Long id, String name, LocalDate dateOfBirth) {
        super(id);
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }
    /**
     * Returns the name of the person.
     *
     * @return the person's name
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the person.
     *
     * @param name the new name to assign
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns the date of birth of the person.
     *
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    /**
     * Sets the date of birth of the person.
     *
     * @param dateOfBirth the new date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    /**
     * Returns a string representation of the person.
     *
     * @return the name of the person
     */
    @Override
    public String toString() {
        return name;
    }
    /**
     * Checks if this person is equal to another object based on ID.
     *
     * @param o the object to compare with
     * @return true if the other object is a {@code Person} with the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(getId(), person.getId());
    }
    /**
     * Returns the hash code for the person.
     *
     * @return a hash code based on name and date of birth
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, dateOfBirth);
    }
}
