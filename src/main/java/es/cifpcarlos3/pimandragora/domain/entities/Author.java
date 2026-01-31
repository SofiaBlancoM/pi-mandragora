package es.cifpcarlos3.pimandragora.domain.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author extends Entity {
    private String fullName;

    @Builder
    private Author(UUID id, Instant createdAt, Instant updatedAt, String fullName) {
        super(id, createdAt, updatedAt);
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name is required");
        this.fullName = fullName.trim();
    }
}
