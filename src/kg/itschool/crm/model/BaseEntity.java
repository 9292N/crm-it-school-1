package kg.itschool.crm.model;

import java.time.LocalDateTime;

abstract class BaseEntity {

    protected Long id;
    protected LocalDateTime dateCreated;

    protected BaseEntity() {
        dateCreated = LocalDateTime.now();
    }

    public BaseEntity(Long id) {
        this.id = id;
        dateCreated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
