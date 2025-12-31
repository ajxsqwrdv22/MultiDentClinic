package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.AppointmentDao;
import com.dentalclinic.dental.model.Appointment;
import com.dentalclinic.dental.model.AppointmentStatus;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAppointmentDao implements AppointmentDao {
    @Override
    public boolean existsDuplicate(
            Long dentistId,
            Long clinicId,
            LocalDateTime scheduledAt,
            Long excludeId
    ) throws Exception {

        String sql = """
        SELECT COUNT(*) 
        FROM appointments
        WHERE dentist_id = ?
          AND clinic_id = ?
          AND scheduled_at = ?
          AND (? IS NULL OR id <> ?)
    """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dentistId);
            ps.setLong(2, clinicId);
            ps.setTimestamp(3, Timestamp.valueOf(scheduledAt));

            // for CREATE → excludeId = null
            // for UPDATE → excludeId = appointment id
            ps.setObject(4, excludeId);
            ps.setObject(5, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }


    @Override
    public Optional<Appointment> findById(Long id) throws Exception {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() throws Exception {
        String sql = "SELECT * FROM appointments";
        List<Appointment> list = new ArrayList<>();

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) throws Exception {
        String sql = "SELECT * FROM appointments WHERE patient_id = ?";
        List<Appointment> list = new ArrayList<>();

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Long create(Appointment a) throws Exception {
        String sql = """
            INSERT INTO appointments
            (patient_id, dentist_id, clinic_id, service_id, scheduled_at, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, a.getPatientId());
            ps.setLong(2, a.getDentistId());
            ps.setLong(3, a.getClinicId());
            ps.setLong(4, a.getServiceId());
            ps.setTimestamp(5, Timestamp.valueOf(a.getScheduledAt()));
            ps.setString(6, a.getStatus().name());

            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getLong(1);
        }
        return null;
    }

    @Override
    public boolean update(Appointment a) throws Exception {
        String sql = """
            UPDATE appointments
            SET patient_id=?, dentist_id=?, clinic_id=?, service_id=?,
                scheduled_at=?, status=?
            WHERE id=?
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, a.getPatientId());
            ps.setLong(2, a.getDentistId());
            ps.setLong(3, a.getClinicId());
            ps.setLong(4, a.getServiceId());
            ps.setTimestamp(5, Timestamp.valueOf(a.getScheduledAt()));
            ps.setString(6, a.getStatus().name());
            ps.setLong(7, a.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long id) throws Exception {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Appointment map(ResultSet rs) throws Exception {
        return new Appointment(
                rs.getLong("id"),
                rs.getLong("patient_id"),
                rs.getLong("dentist_id"),
                rs.getTimestamp("scheduled_at").toLocalDateTime(),
                AppointmentStatus.valueOf(rs.getString("status")),
                rs.getLong("clinic_id")
        );
    }
}
