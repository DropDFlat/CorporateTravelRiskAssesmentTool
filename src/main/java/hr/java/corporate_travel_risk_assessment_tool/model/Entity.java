package hr.java.corporate_travel_risk_assessment_tool.model;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    private Long id;

    public Entity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
