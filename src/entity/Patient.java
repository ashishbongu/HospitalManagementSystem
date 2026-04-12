package entity;

import java.util.Date;

public class Patient {
	private int patientId;
	private String fullName,contactNumber,address;
	private int age;
	private Date dateOfBirth; 
	private char gender;//M fore male , f for female, O for others,N for none
	public Patient()
	{
		this.patientId=0;
		this.fullName="No input"; 
		this.age=0;
		this.contactNumber= "No input";
		this.address = "No input";
		this.gender='N';
	}
	public Patient(int patientId,String firstName,String lastName,Date dateOfBirth,String contactNumber,String address,char gender)
	{

		this.patientId=patientId;
		this.fullName=((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim(); 
		this.dateOfBirth=dateOfBirth;
		this.contactNumber= contactNumber;
		this.address = address;
		this.gender=gender;	
	}
	public Patient(int patientId,String fullName,int age,char gender)
	{
		this.patientId=patientId;
		this.fullName=fullName;
		this.age=age;
		this.gender=gender;
		this.contactNumber="";
		this.address="";
	}
	public Patient(int patientId,String fullName,int age,char gender,String contactNumber)
	{
		this.patientId=patientId;
		this.fullName=fullName;
		this.age=age;
		this.gender=gender;
		this.contactNumber=contactNumber;
		this.address="";
	}
	@Override
	public String toString() {
		return "Patient [patientId=" + patientId + ", fullName=" + fullName + ", age=" + age
				+ ", dateOfBirth=" + dateOfBirth + ", contactNumber=" + contactNumber + ", address=" + address
				+ ", gender=" + gender + "]";
	}
	
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
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
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public char getGender() {
		return gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	

}
