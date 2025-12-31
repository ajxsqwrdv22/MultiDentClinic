package com.dentalclinic.dental.model;

import java.time.Instant;
import java.util.Objects;

public class Patient {
    private Long id;
    private String firstName;
    private String lastName;
    private String contact;
    private String address;
    private Instant createdAt;

    public Patient() {}

    public Patient(Long id, String firstName, String lastName, String contact, String address, Instant createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.address = address;
        this.createdAt = createdAt;
    }

    public Patient(Long id, String firstName, String lastName) {
        this(id, firstName, lastName, null, null, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName;    }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        String name = (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
        return name.trim().isEmpty() ? "Patient#" + id : name;
    }
}
