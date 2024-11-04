package dat.security.dtos;

import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Purpose: To hold information about a user
 * Author: Thomas Hartmann
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String username;
    private String password;
    Set<String> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO dto = (UserDTO) o;
        return Objects.equals(username, dto.username) && Objects.equals(roles, dto.roles);
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, roles);
    }
}

