package es.cifpcarlos3.pimandragora.domain.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Entity {
    private String username;
    private String email;
    private String displayname;
    private String role;

    @Builder
    private User(UUID id, Instant createdAt, Instant updatedAt, String username, String email, String displayname, String role) {
        this.username = username;
        this.email = email;
        this.displayname = displayname;
        this.role = role;
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}
