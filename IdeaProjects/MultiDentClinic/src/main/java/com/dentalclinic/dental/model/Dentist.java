package com.dentalclinic.dental.model;

import java.time.Instant;
import java.util.Objects;

public class Dentist {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;
    private Long clinicId;
    private Instant createdAt;

    public Dentist() {}

    public Dentist(Long id, String firstName, String lastName, String specialty, Long clinicId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.clinicId = clinicId;
    }

    // backwards-compatible
    public Dentist(Long id, String firstName, String lastName, String specialty) {
        this(id, firstName, lastName, specialty, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public Long getClinicId() { return clinicId; }
    public void setClinicId(Long clinicId) { this.clinicId = clinicId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dentist)) return false;
        Dentist dentist = (Dentist) o;
        return Objects.equals(id, dentist.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        String name = (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
        return name.trim().isEmpty() ? "Dentist#" + id : name;
    }
}
