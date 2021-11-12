package io.qiot.manufacturing.factory.facilitymanager.domain.pojo;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@Cacheable
@Table(name = "machinery", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "serial" }) })
@RegisterForReflection
public class MachineryBean extends PanacheEntityBase {

    @Id
    @GenericGenerator(name = "machinery-uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "machinery-uuid-gen")
    @Type(type = "pg-uuid")
    public UUID id;
    @Column(nullable = false)
    public String serial;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public boolean active = true;
    @Column(name = "registered_on", nullable = false, columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    public Instant registeredOn;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MachineryBean other = (MachineryBean) obj;
        return id == other.id;
    }

}