package es.cifpcarlos3.pimandragora.domain.entities;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author extends Entity {
    private String fullName;
    private String bio;


    @Builder
    private Author(UUID id, Instant createdAt, Instant updatedAt, String fullName, String bio) {
        super(id, createdAt, updatedAt);
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name is required");
        this.fullName = fullName.trim();
        this.bio = bio;

    }
}