package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import entity.Appointment;
import entity.AppointmentStatus;
import entity.Doctor;
import entity.DoctorSlot;
import entity.Patient;
import entity.SlotStatus;
import util.DBConnection;

public class HospitalManagementServiceImpl implements HospitalManagementService {
	private static final int SLOT_START_HOUR = 9;
	private static final int SLOT_END_HOUR = 20;

	@Override
	public void initializeDatabase() {
		Connection connection = DBConnection.getConnection();
		if (connection == null) {
			throw new IllegalStateException("Database connection could not be created. Check util/db.properties.");
		}

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Doctor ("
					+ "doctorId INT PRIMARY KEY AUTO_INCREMENT,"
					+ "fullName VARCHAR(120) NOT NULL,"
					+ "firstName VARCHAR(60) NULL,"
					+ "lastName VARCHAR(60) NULL,"
					+ "specialization VARCHAR(80) NOT NULL,"
					+ "contactNumber VARCHAR(20) NOT NULL)");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Patient ("
					+ "patientId INT PRIMARY KEY AUTO_INCREMENT,"
					+ "fullName VARCHAR(120) NOT NULL,"
					+ "age INT NOT NULL DEFAULT 0,"
					+ "firstName VARCHAR(60) NULL,"
					+ "lastName VARCHAR(60) NULL,"
					+ "dateOfBirth DATE NULL,"
					+ "contactNumber VARCHAR(20) NULL,"
					+ "address VARCHAR(200) NULL,"
					+ "gender CHAR(1) NOT NULL DEFAULT 'N')");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS DoctorSlot ("
					+ "slotId INT PRIMARY KEY AUTO_INCREMENT,"
					+ "doctorId INT NOT NULL,"
					+ "slotDate DATE NOT NULL,"
					+ "startTime VARCHAR(10) NOT NULL,"
					+ "endTime VARCHAR(10) NOT NULL,"
					+ "status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',"
					+ "FOREIGN KEY (doctorId) REFERENCES Doctor(doctorId) ON DELETE CASCADE)");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Appointment ("
					+ "appointmentId INT PRIMARY KEY AUTO_INCREMENT,"
					+ "patientId INT NOT NULL,"
					+ "patientName VARCHAR(120) NULL,"
					+ "patientContact VARCHAR(20) NULL,"
					+ "doctorId INT NOT NULL,"
					+ "doctorName VARCHAR(120) NULL,"
					+ "slotId INT NULL,"
					+ "appointmentDate DATE NOT NULL,"
					+ "description VARCHAR(300) NULL,"
					+ "status VARCHAR(20) NOT NULL DEFAULT 'PENDING',"
					+ "FOREIGN KEY (patientId) REFERENCES Patient(patientId) ON DELETE CASCADE,"
					+ "FOREIGN KEY (doctorId) REFERENCES Doctor(doctorId) ON DELETE CASCADE,"
					+ "FOREIGN KEY (slotId) REFERENCES DoctorSlot(slotId) ON DELETE SET NULL)");
			addColumnIfMissing(connection, "Doctor", "fullName", "VARCHAR(120) NULL");
			addColumnIfMissing(connection, "Doctor", "firstName", "VARCHAR(60) NULL");
			addColumnIfMissing(connection, "Doctor", "lastName", "VARCHAR(60) NULL");
			addColumnIfMissing(connection, "Doctor", "specialization", "VARCHAR(80) NULL");
			addColumnIfMissing(connection, "Doctor", "contactNumber", "VARCHAR(20) NULL");
			addColumnIfMissing(connection, "Patient", "fullName", "VARCHAR(120) NULL");
			addColumnIfMissing(connection, "Patient", "age", "INT NOT NULL DEFAULT 0");
			addColumnIfMissing(connection, "Patient", "firstName", "VARCHAR(60) NULL");
			addColumnIfMissing(connection, "Patient", "lastName", "VARCHAR(60) NULL");
			addColumnIfMissing(connection, "Patient", "dateOfBirth", "DATE NULL");
			addColumnIfMissing(connection, "Patient", "contactNumber", "VARCHAR(20) NULL");
			addColumnIfMissing(connection, "Patient", "address", "VARCHAR(200) NULL");
			addColumnIfMissing(connection, "Patient", "gender", "CHAR(1) NULL");
			normalizeGenderColumn(connection);
			addColumnIfMissing(connection, "Appointment", "patientName", "VARCHAR(120) NULL");
			addColumnIfMissing(connection, "Appointment", "patientContact", "VARCHAR(20) NULL");
			addColumnIfMissing(connection, "Appointment", "doctorName", "VARCHAR(120) NULL");
			addColumnIfMissing(connection, "Appointment", "slotId", "INT NULL");
			addColumnIfMissing(connection, "Appointment", "status", "VARCHAR(20) NOT NULL DEFAULT 'PENDING'");
			ensureAutoIncrement(connection, "Doctor", "doctorId");
			ensureAutoIncrement(connection, "Patient", "patientId");
			ensureAutoIncrement(connection, "DoctorSlot", "slotId");
			ensureAutoIncrement(connection, "Appointment", "appointmentId");
			backfillNames(connection);
			ensureDefaultSlotsForAllDoctors(today());
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to initialize database schema.", e);
		}
	}

	@Override
	public int addDoctor(Doctor doctor) {
		validateText(doctor.getFullName(), "Doctor name");
		validateText(doctor.getSpecialization(), "Specialization");
		validateText(doctor.getContactNumber(), "Contact number");
		String sql = "INSERT INTO Doctor (fullName, firstName, lastName, specialization, contactNumber) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement statement = prepareWithKeys(sql)) {
			statement.setString(1, doctor.getFullName().trim());
			statement.setString(2, doctor.getFullName().trim());
			statement.setString(3, "");
			statement.setString(4, doctor.getSpecialization().trim());
			statement.setString(5, doctor.getContactNumber().trim());
			statement.executeUpdate();
			int doctorId = readGeneratedId(statement);
			ensureDefaultSlotsForDoctor(doctorId, today());
			return doctorId;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to add doctor.", e);
		}
	}

	@Override
	public boolean updateDoctor(Doctor doctor) {
		validateText(doctor.getFullName(), "Doctor name");
		validateText(doctor.getSpecialization(), "Specialization");
		validateText(doctor.getContactNumber(), "Contact number");
		String sql = "UPDATE Doctor SET fullName = ?, firstName = ?, lastName = ?, specialization = ?, contactNumber = ? WHERE doctorId = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql)) {
			statement.setString(1, doctor.getFullName().trim());
			statement.setString(2, doctor.getFullName().trim());
			statement.setString(3, "");
			statement.setString(4, doctor.getSpecialization().trim());
			statement.setString(5, doctor.getContactNumber().trim());
			statement.setInt(6, doctor.getDoctorId());
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to update doctor.", e);
		}
	}

	@Override
	public boolean removeDoctor(int doctorId) {
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("DELETE FROM Doctor WHERE doctorId = ?")) {
			statement.setInt(1, doctorId);
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to remove doctor.", e);
		}
	}

	@Override
	public List<Doctor> getDoctors() {
		List<Doctor> doctors = new ArrayList<>();
		String sql = "SELECT * FROM Doctor ORDER BY doctorId";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				doctors.add(mapDoctor(resultSet));
			}
			return doctors;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to load doctors.", e);
		}
	}

	@Override
	public int addSlot(DoctorSlot slot) {
		String sql = "INSERT INTO DoctorSlot (doctorId, slotDate, startTime, endTime, status) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement statement = prepareWithKeys(sql)) {
			statement.setInt(1, slot.getDoctorId());
			statement.setDate(2, new Date(slot.getSlotDate().getTime()));
			statement.setString(3, slot.getStartTime());
			statement.setString(4, slot.getEndTime());
			statement.setString(5, SlotStatus.AVAILABLE.name());
			statement.executeUpdate();
			return readGeneratedId(statement);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to add slot.", e);
		}
	}

	@Override
	public boolean removeSlot(int slotId) {
		String sql = "DELETE FROM DoctorSlot WHERE slotId = ? AND status = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql)) {
			statement.setInt(1, slotId);
			statement.setString(2, SlotStatus.AVAILABLE.name());
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to remove slot.", e);
		}
	}

	@Override
	public List<DoctorSlot> getSlots() {
		return loadSlots("SELECT * FROM DoctorSlot ORDER BY slotDate, startTime, slotId");
	}

	@Override
	public List<DoctorSlot> getAvailableSlots() {
		return loadSlots("SELECT * FROM DoctorSlot WHERE status = 'AVAILABLE' ORDER BY slotDate, startTime, slotId");
	}

	@Override
	public List<DoctorSlot> getAvailableSlotsForDoctorOnDate(int doctorId, java.util.Date slotDate) {
		validateDateFromToday(slotDate);
		ensureDefaultSlotsForDoctor(doctorId, slotDate);
		List<DoctorSlot> slots = new ArrayList<>();
		String sql = "SELECT * FROM DoctorSlot WHERE doctorId = ? AND slotDate = ? AND status = 'AVAILABLE' ORDER BY startTime, slotId";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql)) {
			statement.setInt(1, doctorId);
			statement.setDate(2, new Date(stripTime(slotDate).getTime()));
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					slots.add(mapSlot(resultSet));
				}
			}
			return slots;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to load available slots.", e);
		}
	}

	@Override
	public int registerPatient(Patient patient) {
		validateText(patient.getFullName(), "Patient name");
		validateText(patient.getContactNumber(), "Patient phone number");
		if (patient.getAge() <= 0) {
			throw new IllegalArgumentException("Age must be greater than 0.");
		}
		char gender = normalizeGender(patient.getGender());
		String sql = "INSERT INTO Patient (fullName, age, firstName, lastName, dateOfBirth, contactNumber, address, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = prepareWithKeys(sql)) {
			statement.setString(1, patient.getFullName().trim());
			statement.setInt(2, patient.getAge());
			statement.setString(3, patient.getFullName().trim());
			statement.setString(4, "");
			statement.setDate(5, null);
			statement.setString(6, patient.getContactNumber().trim());
			statement.setString(7, "");
			statement.setString(8, String.valueOf(gender));
			statement.executeUpdate();
			return readGeneratedId(statement);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to register patient.", e);
		}
	}

	@Override
	public boolean removePatient(int patientId) {
		Connection connection = DBConnection.getConnection();
		try {
			connection.setAutoCommit(false);
			List<Integer> slotIds = new ArrayList<>();
			try (PreparedStatement slots = connection.prepareStatement("SELECT slotId FROM Appointment WHERE patientId = ? AND slotId IS NOT NULL")) {
				slots.setInt(1, patientId);
				try (ResultSet resultSet = slots.executeQuery()) {
					while (resultSet.next()) {
						slotIds.add(resultSet.getInt("slotId"));
					}
				}
			}
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Patient WHERE patientId = ?")) {
				statement.setInt(1, patientId);
				if (statement.executeUpdate() == 0) {
					connection.rollback();
					return false;
				}
			}
			for (Integer slotId : slotIds) {
				updateSlotStatus(slotId, SlotStatus.AVAILABLE.name());
			}
			connection.commit();
			return true;
		} catch (SQLException e) {
			rollback(connection);
			throw new IllegalStateException("Unable to remove patient.", e);
		} catch (RuntimeException e) {
			rollback(connection);
			throw e;
		} finally {
			restoreAutoCommit(connection);
		}
	}

	@Override
	public List<Patient> getPatients() {
		List<Patient> patients = new ArrayList<>();
		String sql = "SELECT * FROM Patient ORDER BY patientId";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				patients.add(mapPatient(resultSet));
			}
			return patients;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to load patients.", e);
		}
	}

	@Override
	public int requestAppointment(int patientId, int slotId, String description) {
		Connection connection = DBConnection.getConnection();
		try {
			connection.setAutoCommit(false);
			DoctorSlot slot = findAvailableSlot(slotId);
			if (slot == null) {
				throw new IllegalArgumentException("Selected slot is no longer available.");
			}
			Patient patient = findPatient(patientId);
			Doctor doctor = findDoctor(slot.getDoctorId());
			if (patient == null) {
				throw new IllegalArgumentException("Patient was not found.");
			}
			if (doctor == null) {
				throw new IllegalArgumentException("Doctor was not found.");
			}
			String sql = "INSERT INTO Appointment (patientId, patientName, patientContact, doctorId, doctorName, slotId, appointmentDate, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			int appointmentId;
			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				statement.setInt(1, patientId);
				statement.setString(2, patient.getFullName());
				statement.setString(3, patient.getContactNumber());
				statement.setInt(4, slot.getDoctorId());
				statement.setString(5, doctor.getFullName());
				statement.setInt(6, slot.getSlotId());
				statement.setDate(7, new Date(slot.getSlotDate().getTime()));
				statement.setString(8, description);
				statement.setString(9, AppointmentStatus.PENDING.name());
				statement.executeUpdate();
				appointmentId = readGeneratedId(statement);
			}
			updateSlotStatus(slotId, SlotStatus.REQUESTED.name());
			connection.commit();
			return appointmentId;
		} catch (SQLException e) {
			rollback(connection);
			throw new IllegalStateException("Unable to request appointment.", e);
		} catch (RuntimeException e) {
			rollback(connection);
			throw e;
		} finally {
			restoreAutoCommit(connection);
		}
	}

	@Override
	public boolean approveAppointment(int appointmentId) {
		return decideAppointment(appointmentId, AppointmentStatus.APPROVED.name(), SlotStatus.BOOKED.name());
	}

	@Override
	public boolean rejectAppointment(int appointmentId) {
		return decideAppointment(appointmentId, AppointmentStatus.REJECTED.name(), SlotStatus.AVAILABLE.name());
	}

	@Override
	public boolean cancelAppointment(int appointmentId) {
		return deleteAppointment(appointmentId);
	}

	@Override
	public List<Appointment> getAppointments() {
		return loadAppointments("SELECT a.*, s.startTime AS appointmentStartTime, s.endTime AS appointmentEndTime "
				+ "FROM Appointment a LEFT JOIN DoctorSlot s ON a.slotId = s.slotId ORDER BY a.appointmentId DESC");
	}

	@Override
	public List<Appointment> getAppointmentsForPatient(int patientId) {
		List<Appointment> appointments = new ArrayList<>();
		String sql = "SELECT a.*, s.startTime AS appointmentStartTime, s.endTime AS appointmentEndTime "
				+ "FROM Appointment a LEFT JOIN DoctorSlot s ON a.slotId = s.slotId WHERE a.patientId = ? ORDER BY a.appointmentId DESC";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql)) {
			statement.setInt(1, patientId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					appointments.add(mapAppointment(resultSet));
				}
			}
			return appointments;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to load patient appointments.", e);
		}
	}

	private boolean decideAppointment(int appointmentId, String appointmentStatus, String slotStatus) {
		Connection connection = DBConnection.getConnection();
		try {
			connection.setAutoCommit(false);
			Integer slotId = findAppointmentSlot(appointmentId);
			if (slotId == null) {
				connection.rollback();
				return false;
			}
			try (PreparedStatement statement = connection.prepareStatement("UPDATE Appointment SET status = ? WHERE appointmentId = ?")) {
				statement.setString(1, appointmentStatus);
				statement.setInt(2, appointmentId);
				if (statement.executeUpdate() == 0) {
					connection.rollback();
					return false;
				}
			}
			updateSlotStatus(slotId, slotStatus);
			connection.commit();
			return true;
		} catch (SQLException e) {
			rollback(connection);
			throw new IllegalStateException("Unable to update appointment decision.", e);
		} catch (RuntimeException e) {
			rollback(connection);
			throw e;
		} finally {
			restoreAutoCommit(connection);
		}
	}

	private boolean deleteAppointment(int appointmentId) {
		Connection connection = DBConnection.getConnection();
		try {
			connection.setAutoCommit(false);
			Integer slotId = findAppointmentSlot(appointmentId);
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Appointment WHERE appointmentId = ?")) {
				statement.setInt(1, appointmentId);
				if (statement.executeUpdate() == 0) {
					connection.rollback();
					return false;
				}
			}
			if (slotId != null && slotId > 0) {
				updateSlotStatus(slotId, SlotStatus.AVAILABLE.name());
			}
			connection.commit();
			return true;
		} catch (SQLException e) {
			rollback(connection);
			throw new IllegalStateException("Unable to delete appointment.", e);
		} catch (RuntimeException e) {
			rollback(connection);
			throw e;
		} finally {
			restoreAutoCommit(connection);
		}
	}

	private List<DoctorSlot> loadSlots(String sql) {
		List<DoctorSlot> slots = new ArrayList<>();
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				slots.add(mapSlot(resultSet));
			}
			return slots;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to load slots.", e);
		}
	}

	private List<Appointment> loadAppointments(String sql) {
		List<Appointment> appointments = new ArrayList<>();
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				appointments.add(mapAppointment(resultSet));
			}
			return appointments;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to load appointments.", e);
		}
	}

	private PreparedStatement prepareWithKeys(String sql) throws SQLException {
		return DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	}

	private void addColumnIfMissing(Connection connection, String tableName, String columnName, String columnDefinition)
			throws SQLException {
		if (columnExists(connection, tableName, columnName)) {
			return;
		}
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("ALTER TABLE `" + tableName + "` ADD COLUMN `" + columnName + "` " + columnDefinition);
		}
	}

	private void backfillNames(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			if (columnExists(connection, "Doctor", "doctorName")) {
				statement.executeUpdate("UPDATE Doctor SET fullName = doctorName WHERE (fullName IS NULL OR fullName = '') AND doctorName IS NOT NULL AND doctorName <> ''");
			}
			if (columnExists(connection, "Patient", "patientName")) {
				statement.executeUpdate("UPDATE Patient SET fullName = patientName WHERE (fullName IS NULL OR fullName = '') AND patientName IS NOT NULL AND patientName <> ''");
			}
			statement.executeUpdate("UPDATE Doctor SET fullName = TRIM(CONCAT(COALESCE(firstName, ''), ' ', COALESCE(lastName, ''))) "
					+ "WHERE (fullName IS NULL OR fullName = '') AND (firstName IS NOT NULL OR lastName IS NOT NULL)");
			statement.executeUpdate("UPDATE Patient SET fullName = TRIM(CONCAT(COALESCE(firstName, ''), ' ', COALESCE(lastName, ''))) "
					+ "WHERE (fullName IS NULL OR fullName = '') AND (firstName IS NOT NULL OR lastName IS NOT NULL)");
			statement.executeUpdate("UPDATE Doctor SET fullName = CONCAT('Doctor ', doctorId) WHERE fullName IS NULL OR fullName = ''");
			statement.executeUpdate("UPDATE Patient SET fullName = CONCAT('Patient ', patientId) WHERE fullName IS NULL OR fullName = ''");
			statement.executeUpdate("UPDATE Appointment a JOIN Patient p ON a.patientId = p.patientId "
					+ "SET a.patientName = p.fullName WHERE a.patientName IS NULL OR a.patientName = ''");
			statement.executeUpdate("UPDATE Appointment a JOIN Patient p ON a.patientId = p.patientId "
					+ "SET a.patientContact = p.contactNumber WHERE a.patientContact IS NULL OR a.patientContact = ''");
			statement.executeUpdate("UPDATE Appointment a JOIN Doctor d ON a.doctorId = d.doctorId "
					+ "SET a.doctorName = d.fullName WHERE a.doctorName IS NULL OR a.doctorName = ''");
		}
	}

	private void normalizeGenderColumn(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("UPDATE Patient SET gender = UPPER(LEFT(TRIM(gender), 1)) WHERE gender IS NOT NULL AND TRIM(gender) <> ''");
			statement.executeUpdate("UPDATE Patient SET gender = 'N' WHERE gender IS NULL OR TRIM(gender) = '' OR gender NOT IN ('M', 'F', 'O', 'N')");
			statement.executeUpdate("ALTER TABLE Patient MODIFY COLUMN gender CHAR(1) NOT NULL DEFAULT 'N'");
		}
	}

	private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
		String sql = "SHOW COLUMNS FROM `" + tableName + "` LIKE ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, columnName);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private void ensureAutoIncrement(Connection connection, String tableName, String columnName) throws SQLException {
		String sql = "SHOW COLUMNS FROM `" + tableName + "` LIKE ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, columnName);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					String extra = resultSet.getString("Extra");
					if (extra != null && extra.toLowerCase().contains("auto_increment")) {
						return;
					}
				}
			}
		}
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("ALTER TABLE `" + tableName + "` MODIFY COLUMN `" + columnName + "` INT NOT NULL AUTO_INCREMENT");
		}
	}

	private void validateText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " is required.");
		}
	}

	private char normalizeGender(char gender) {
		char normalized = Character.toUpperCase(gender);
		if (normalized == 'M' || normalized == 'F' || normalized == 'O') {
			return normalized;
		}
		throw new IllegalArgumentException("Select a valid gender.");
	}

	private java.util.Date today() {
		return stripTime(new java.util.Date());
	}

	private java.util.Date stripTime(java.util.Date value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private void validateDateFromToday(java.util.Date slotDate) {
		if (slotDate == null) {
			throw new IllegalArgumentException("Date is required.");
		}
		if (stripTime(slotDate).before(today())) {
			throw new IllegalArgumentException("Select today or a future date.");
		}
	}

	private void ensureDefaultSlotsForDoctor(int doctorId, java.util.Date slotDate) {
		validateDateFromToday(slotDate);
		java.util.Date cleanDate = stripTime(slotDate);
		Calendar cursor = Calendar.getInstance();
		cursor.setTime(cleanDate);
		cursor.set(Calendar.HOUR_OF_DAY, SLOT_START_HOUR);
		cursor.set(Calendar.MINUTE, 0);
		Calendar end = Calendar.getInstance();
		end.setTime(cleanDate);
		end.set(Calendar.HOUR_OF_DAY, SLOT_END_HOUR);
		end.set(Calendar.MINUTE, 0);
		while (cursor.before(end)) {
			String start = formatTime(cursor);
			cursor.add(Calendar.MINUTE, 10);
			String finish = formatTime(cursor);
			addSlotIfMissing(doctorId, cleanDate, start, finish);
		}
	}

	private void ensureDefaultSlotsForAllDoctors(java.util.Date slotDate) {
		for (Doctor doctor : getDoctors()) {
			ensureDefaultSlotsForDoctor(doctor.getDoctorId(), slotDate);
		}
	}

	private String formatTime(Calendar calendar) {
		return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}

	private void addSlotIfMissing(int doctorId, java.util.Date slotDate, String startTime, String endTime) {
		String existsSql = "SELECT slotId FROM DoctorSlot WHERE doctorId = ? AND slotDate = ? AND startTime = ? AND endTime = ?";
		try (PreparedStatement exists = DBConnection.getConnection().prepareStatement(existsSql)) {
			exists.setInt(1, doctorId);
			exists.setDate(2, new Date(slotDate.getTime()));
			exists.setString(3, startTime);
			exists.setString(4, endTime);
			try (ResultSet resultSet = exists.executeQuery()) {
				if (resultSet.next()) {
					return;
				}
			}
			try (PreparedStatement insert = DBConnection.getConnection().prepareStatement(
					"INSERT INTO DoctorSlot (doctorId, slotDate, startTime, endTime, status) VALUES (?, ?, ?, ?, ?)")) {
				insert.setInt(1, doctorId);
				insert.setDate(2, new Date(slotDate.getTime()));
				insert.setString(3, startTime);
				insert.setString(4, endTime);
				insert.setString(5, SlotStatus.AVAILABLE.name());
				insert.executeUpdate();
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to create default slots.", e);
		}
	}

	private int readGeneratedId(PreparedStatement statement) throws SQLException {
		try (ResultSet keys = statement.getGeneratedKeys()) {
			if (keys.next()) {
				return keys.getInt(1);
			}
		}
		return 0;
	}

	private DoctorSlot findAvailableSlot(int slotId) throws SQLException {
		String sql = "SELECT * FROM DoctorSlot WHERE slotId = ? AND status = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql)) {
			statement.setInt(1, slotId);
			statement.setString(2, SlotStatus.AVAILABLE.name());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return mapSlot(resultSet);
				}
			}
		}
		return null;
	}

	private Doctor findDoctor(int doctorId) throws SQLException {
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM Doctor WHERE doctorId = ?")) {
			statement.setInt(1, doctorId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return mapDoctor(resultSet);
				}
			}
		}
		return null;
	}

	private Patient findPatient(int patientId) throws SQLException {
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM Patient WHERE patientId = ?")) {
			statement.setInt(1, patientId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return mapPatient(resultSet);
				}
			}
		}
		return null;
	}

	private Integer findAppointmentSlot(int appointmentId) throws SQLException {
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT slotId FROM Appointment WHERE appointmentId = ?")) {
			statement.setInt(1, appointmentId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int slotId = resultSet.getInt("slotId");
					return resultSet.wasNull() ? null : slotId;
				}
			}
		}
		return null;
	}

	private void updateSlotStatus(int slotId, String status) throws SQLException {
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("UPDATE DoctorSlot SET status = ? WHERE slotId = ?")) {
			statement.setString(1, status);
			statement.setInt(2, slotId);
			statement.executeUpdate();
		}
	}

	private void rollback(Connection connection) {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to rollback transaction.", e);
		}
	}

	private void restoreAutoCommit(Connection connection) {
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to restore auto commit.", e);
		}
	}

	private Doctor mapDoctor(ResultSet resultSet) throws SQLException {
		return new Doctor(resultSet.getInt("doctorId"), resultSet.getString("fullName"),
				resultSet.getString("specialization"), resultSet.getString("contactNumber"));
	}

	private Patient mapPatient(ResultSet resultSet) throws SQLException {
		String gender = resultSet.getString("gender");
		return new Patient(resultSet.getInt("patientId"), resultSet.getString("fullName"), resultSet.getInt("age"),
				gender == null || gender.isEmpty() ? 'N' : gender.charAt(0), resultSet.getString("contactNumber"));
	}

	private DoctorSlot mapSlot(ResultSet resultSet) throws SQLException {
		return new DoctorSlot(resultSet.getInt("slotId"), resultSet.getInt("doctorId"), resultSet.getDate("slotDate"),
				resultSet.getString("startTime"), resultSet.getString("endTime"), resultSet.getString("status"));
	}

	private Appointment mapAppointment(ResultSet resultSet) throws SQLException {
		return new Appointment(resultSet.getInt("appointmentId"), resultSet.getInt("patientId"), resultSet.getString("patientName"),
				resultSet.getString("patientContact"), resultSet.getInt("doctorId"), resultSet.getString("doctorName"), resultSet.getInt("slotId"),
				resultSet.getDate("appointmentDate"), resultSet.getString("appointmentStartTime"),
				resultSet.getString("appointmentEndTime"), resultSet.getString("description"),
				resultSet.getString("status"));
	}
}
