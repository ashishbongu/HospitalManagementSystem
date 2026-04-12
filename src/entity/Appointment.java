package entity;

import java.util.Date;

public class Appointment {
	private int appointmentId,patientId,doctorId,slotId;
	private String patientName,patientContact,doctorName,description,status,startTime,endTime;
	private Date appointmentDate;
	
	public Appointment()
	{
		this.appointmentId=0;
		this.patientId=0;
		this.doctorId=0;
		this.slotId=0;
		this.patientName="";
		this.patientContact="";
		this.doctorName="";
		this.description="No input";
		this.status="PENDING";
		this.startTime="";
		this.endTime="";
	}
	public Appointment(int appointmentId, int patientId,int doctorId,Date appointmentDate,String description)
	{
		this.appointmentId=appointmentId;
		this.patientId=patientId;
		this.doctorId=doctorId;
		this.appointmentDate=appointmentDate;
		this.description=description;
		this.status="PENDING";
		this.patientName="";
		this.patientContact="";
	}
	public Appointment(int appointmentId, int patientId, int doctorId, int slotId, Date appointmentDate, String description, String status)
	{
		this.appointmentId=appointmentId;
		this.patientId=patientId;
		this.doctorId=doctorId;
		this.slotId=slotId;
		this.appointmentDate=appointmentDate;
		this.description=description;
		this.status=status;
		this.patientName="";
		this.patientContact="";
		this.startTime="";
		this.endTime="";
	}
	public Appointment(int appointmentId, int patientId, String patientName, int doctorId, String doctorName, int slotId, Date appointmentDate, String description, String status)
	{
		this.appointmentId=appointmentId;
		this.patientId=patientId;
		this.patientName=patientName;
		this.patientContact="";
		this.doctorId=doctorId;
		this.doctorName=doctorName;
		this.slotId=slotId;
		this.appointmentDate=appointmentDate;
		this.description=description;
		this.status=status;
		this.startTime="";
		this.endTime="";
	}
	public Appointment(int appointmentId, int patientId, String patientName, int doctorId, String doctorName, int slotId, Date appointmentDate, String startTime, String endTime, String description, String status)
	{
		this(appointmentId, patientId, patientName, "", doctorId, doctorName, slotId, appointmentDate, startTime, endTime, description, status);
	}
	public Appointment(int appointmentId, int patientId, String patientName, String patientContact, int doctorId, String doctorName, int slotId, Date appointmentDate, String startTime, String endTime, String description, String status)
	{
		this.appointmentId=appointmentId;
		this.patientId=patientId;
		this.patientName=patientName;
		this.patientContact=patientContact;
		this.doctorId=doctorId;
		this.doctorName=doctorName;
		this.slotId=slotId;
		this.appointmentDate=appointmentDate;
		this.startTime=startTime;
		this.endTime=endTime;
		this.description=description;
		this.status=status;
	}
	public int getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientContact() {
		return patientContact;
	}
	public void setPatientContact(String patientContact) {
		this.patientContact = patientContact;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public int getSlotId() {
		return slotId;
	}
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSlotTime() {
		if ((startTime == null || startTime.isEmpty()) && (endTime == null || endTime.isEmpty())) {
			return "";
		}
		return startTime + " - " + endTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Appointment [appointmentId=" + appointmentId + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", patientName=" + patientName + ", patientContact=" + patientContact + ", doctorName=" + doctorName + ", slotId=" + slotId + ", appointmentDate=" + appointmentDate + ", startTime=" + startTime + ", endTime=" + endTime + ", description=" + description
				+ ", status=" + status + "]";
	}

}
