package service;

import java.util.List;

import entity.Appointment;
import entity.Doctor;
import entity.DoctorSlot;
import entity.Patient;

public interface HospitalManagementService {
	void initializeDatabase();

	int addDoctor(Doctor doctor);

	boolean updateDoctor(Doctor doctor);

	boolean removeDoctor(int doctorId);

	List<Doctor> getDoctors();

	int addSlot(DoctorSlot slot);

	boolean removeSlot(int slotId);

	List<DoctorSlot> getSlots();

	List<DoctorSlot> getAvailableSlots();

	List<DoctorSlot> getAvailableSlotsForDoctorOnDate(int doctorId, java.util.Date slotDate);

	int registerPatient(Patient patient);

	boolean removePatient(int patientId);

	List<Patient> getPatients();

	int requestAppointment(int patientId, int slotId, String description);

	boolean approveAppointment(int appointmentId);

	boolean rejectAppointment(int appointmentId);

	boolean cancelAppointment(int appointmentId);

	List<Appointment> getAppointments();

	List<Appointment> getAppointmentsForPatient(int patientId);
}
