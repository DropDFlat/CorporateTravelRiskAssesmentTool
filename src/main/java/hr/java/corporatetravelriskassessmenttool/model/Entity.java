package hr.java.corporatetravelriskassessmenttool.model;

import java.io.Serializable;
/**
 * An abstract base class representing a persistent entity with a unique identifier.
 * <p>
 * All model classes that represent entities stored in a database should extend this class.
 * Implements {@link Serializable} to allow serialization of subclasses.
 * </p>
 */
public abstract class Entity implements Serializable {
    private Long id;
    /**
     * Constructs an {@code Entity} with the given ID.
     *
     * @param id the unique identifier of the entity
     */
    protected Entity(Long id) {
        this.id = id;
    }
    /**
     * Returns the unique identifier of the entity.
     *
     * @return the ID of the entity
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the entity.
     *
     * @param id the new ID to assign
     */
    public void setId(Long id) {
        this.id = id;
    }
}
