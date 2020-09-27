package org.rjansen.sdk.security;


import java.io.Serializable;
import java.util.Objects;

/**
 * This class is for the .authentication() information. It can be extended in order to add more information. i.e Rol.
 */
public class SecurityUser implements Serializable {

    private String username;

    public SecurityUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityUser that = (SecurityUser) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
