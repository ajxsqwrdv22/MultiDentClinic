package com.dentalclinic.dental.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Appointment {
    private Long id;
    private Long patientId;
    private Long dentistId;

    private LocalDateTime scheduledAt;

    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private Long serviceId;

    private AppointmentStatus status;
    private Long clinicId;

    public Appointment() {}

    // EXISTING CONSTRUCTOR (unchanged)
    public Appointment(Long id, Long patientId, Long dentistId,
                       LocalDateTime scheduledAt,
                       AppointmentStatus status, Long clinicId) {
        this.id = id;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.clinicId = clinicId;
    }

    // EXISTING CONSTRUCTOR (unchanged)
    public Appointment(Long id, Long patientId, Long dentistId,
                       LocalDateTime scheduledAt,
                       AppointmentStatus status) {
        this(id, patientId, dentistId, scheduledAt, status, null);
    }
    public Appointment(Long id, Long patientId, Long dentistId,
                       Long clinicId, Long serviceId,
                       LocalDateTime scheduledStart,
                       LocalDateTime scheduledEnd,
                       AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.clinicId = clinicId;
        this.serviceId = serviceId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.status = status;

        // Keep legacy field in sync
        this.scheduledAt = scheduledStart;
    }

    // ---------------- GETTERS / SETTERS ----------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDentistId() { return dentistId; }
    public void setDentistId(Long dentistId) { this.dentistId = dentistId; }

    // EXISTING
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
        // keep new fields consistent if they exist
        this.scheduledStart = scheduledAt;
    }
    public LocalDateTime getScheduledStart() {
        return scheduledStart != null ? scheduledStart : scheduledAt;
    }

    public void setScheduledStart(LocalDateTime scheduledStart) {
        this.scheduledStart = scheduledStart;
        this.scheduledAt = scheduledStart;
    }

    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public Long getClinicId() { return clinicId; }
    public void setClinicId(Long clinicId) { this.clinicId = clinicId; }

    // ---------------- OBJECT METHODS ----------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", dentistId=" + dentistId +
                ", scheduledAt=" + scheduledAt +
                ", scheduledStart=" + scheduledStart +
                ", scheduledEnd=" + scheduledEnd +
                ", serviceId=" + serviceId +
                ", status=" + status +
                ", clinicId=" + clinicId +
                '}';
    }
}
