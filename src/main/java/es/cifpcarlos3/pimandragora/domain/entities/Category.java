package es.cifpcarlos3.pimandragora.domain.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends Entity {
    private String name;
    private String description;

    @Builder
    private Category(UUID id, Instant createdAt, Instant updatedAt, String name, String description) {
        super(id, createdAt, updatedAt);
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        this.name = name.trim();
        this.description = (description == null || description.isBlank()) ? null : description.trim();
    }
}
