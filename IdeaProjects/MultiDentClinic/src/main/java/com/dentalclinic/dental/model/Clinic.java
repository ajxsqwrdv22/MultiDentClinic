package com.dentalclinic.dental.model;

import java.time.Instant;
import java.util.Objects;

public class Clinic {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Instant createdAt;

    public Clinic() {}

    // Full constructor
    public Clinic(Long id, String name, String address, String phone, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    // Partial: id, name, address
    public Clinic(Long id, String name, String address) {
        this(id, name, address, null, null);
    }

    // Partial: id, name, address, phone
    public Clinic(Long id, String name, String address, String phone) {
        this(id, name, address, phone, null);
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clinic)) return false;
        Clinic clinic = (Clinic) o;
        return Objects.equals(id, clinic.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return name == null ? "Clinic#" + id : name;
    }
}
