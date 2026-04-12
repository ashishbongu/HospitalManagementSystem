package entity;

import java.util.Date;

public class DoctorSlot {
	private int slotId;
	private int doctorId;
	private Date slotDate;
	private String startTime;
	private String endTime;
	private String status;

	public DoctorSlot() {
		this.slotId = 0;
		this.doctorId = 0;
		this.status = SlotStatus.AVAILABLE.name();
	}

	public DoctorSlot(int slotId, int doctorId, Date slotDate, String startTime, String endTime, String status) {
		this.slotId = slotId;
		this.doctorId = doctorId;
		this.slotDate = slotDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	public int getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}

	public Date getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(Date slotDate) {
		this.slotDate = slotDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DoctorSlot [slotId=" + slotId + ", doctorId=" + doctorId + ", slotDate=" + slotDate
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", status=" + status + "]";
	}
}
