package com.dentalclinic.dental.model;

import java.util.Objects;

public class Service {

    private Long id;
    private Long clinicId;
    private String name;
    private double price;
    private boolean active = true;

    public Service() {}

    public Service(Long id, Long clinicId, String name, double price, boolean active) {
        this.id = id;
        this.clinicId = clinicId;
        this.name = name;
        this.price = price;
        this.active = active;
    }

    // =====================
    // GETTERS & SETTERS
    // =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // =====================
    // UI & COLLECTION SUPPORT
    // =====================

    @Override
    public String toString() {
        return name + " (₱" + price + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
