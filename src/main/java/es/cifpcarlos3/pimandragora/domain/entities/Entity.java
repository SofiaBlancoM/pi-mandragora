package es.cifpcarlos3.pimandragora.domain.entities;


import lombok.*;

import java.time.Instant;
import java.util.UUID;


/**
 * Clase base para las entidades
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Entity {

    protected UUID id;
    protected Instant createdAt = Instant.now();
    protected Instant updatedAt;

    @Override
    public final int hashCode() {
        return (id == null) ? System.identityHashCode(this) : id.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity other)) return false;
        // Two transient entities are never equal
        if (this.id == null || other.id == null) return false;
        return this.id.equals(other.id);
    }
}
