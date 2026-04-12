package entity;

public class Doctor {
	private int doctorId;
	private String fullName,specialization,contactNumber;
	
	public Doctor()
	{
		this.doctorId=0;
		this.fullName="No input";
		this.specialization="No input";
		this.contactNumber ="No input";
	}
	public Doctor(int doctorId,String firstName,String lastName,String specialization,String contactNumber)
	{
		this.doctorId=doctorId;
		this.fullName=((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
		this.specialization=specialization;
		this.contactNumber =contactNumber;
	}
	public Doctor(int doctorId,String fullName,String specialization,String contactNumber)
	{
		this.doctorId=doctorId;
		this.fullName=fullName;
		this.specialization=specialization;
		this.contactNumber =contactNumber;
	}
	@Override
	public String toString() {
		return "Doctor [doctorId=" + doctorId + ", fullName=" + fullName
				+ ", specialization=" + specialization + ", contactNumber=" + contactNumber + "]";
	}
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getFirstName() {
		return fullName;
	}
	public void setFirstName(String firstName) {
		this.fullName = firstName;
	}
	public String getLastName() {
		return "";
	}
	public void setLastName(String lastName) {
		if (lastName != null && !lastName.trim().isEmpty()) {
			this.fullName = ((this.fullName == null ? "" : this.fullName) + " " + lastName).trim();
		}
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

}
