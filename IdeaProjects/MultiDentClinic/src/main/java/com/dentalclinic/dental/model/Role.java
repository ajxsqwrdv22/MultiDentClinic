package com.dentalclinic.dental.model;

import java.util.Objects;

public class Role {
    public static final String ADMIN = "ADMIN";
    public static final String STAFF = "STAFF";
    public boolean isAdmin() {
        return ADMIN.equalsIgnoreCase(name);
    }

    public boolean isStaff() {
        return STAFF.equalsIgnoreCase(name);
    }
    private Long id;
    private String name;
    public Role() {}
    public Role(Long id, String name) { this.id = id; this.name = name; }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Role)) return false;

        Role role = (Role) obj;
        return Objects.equals(id, role.id);


    }

}
