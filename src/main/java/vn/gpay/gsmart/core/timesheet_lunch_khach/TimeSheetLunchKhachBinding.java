package vn.gpay.gsmart.core.timesheet_lunch_khach;

import java.util.Date;

public class TimeSheetLunchKhachBinding {
	// a.id, a.code, a.fullname, b.workingdate, b.shifttypeid_link, b.isworking,
	// b.islunch
	private Long personnelid_link;
	private String personnelCode;
	private String personnelFullname;
	private Date workingdate;
	private boolean isWorkingShift;
	private boolean isWorkingShift1;
	private boolean isWorkingShift2;
	private boolean isWorkingShift3;
	private boolean isWorkingShift4;
	private boolean isLunchShift;
	private boolean isLunchShift1;
	private boolean isLunchShift2;
	private boolean isLunchShift3;
	private boolean isLunchShift4;
	private Integer status;
	private String dataIndex;
	private String register_code;

	public Long getPersonnelid_link() {
		return personnelid_link;
	}

	public void setPersonnelid_link(Long personnelid_link) {
		this.personnelid_link = personnelid_link;
	}

	public String getPersonnelCode() {
		return personnelCode;
	}

	public void setPersonnelCode(String personnelCode) {
		this.personnelCode = personnelCode;
	}

	public String getPersonnelFullname() {
		return personnelFullname;
	}

	public void setPersonnelFullname(String personnelFullname) {
		this.personnelFullname = personnelFullname;
	}

	public Date getWorkingdate() {
		return workingdate;
	}

	public void setWorkingdate(Date workingdate) {
		this.workingdate = workingdate;
	}

	public boolean isWorkingShift() {
		return isWorkingShift;
	}

	public void setWorkingShift(boolean isWorkingShift) {
		this.isWorkingShift = isWorkingShift;
	}

	public boolean isLunchShift() {
		return isLunchShift;
	}

	public void setLunchShift(boolean isLunchShift) {
		this.isLunchShift = isLunchShift;
	}

	public boolean isWorkingShift1() {
		return isWorkingShift1;
	}

	public void setWorkingShift1(boolean isWorkingShift1) {
		this.isWorkingShift1 = isWorkingShift1;
	}

	public boolean isWorkingShift2() {
		return isWorkingShift2;
	}

	public void setWorkingShift2(boolean isWorkingShift2) {
		this.isWorkingShift2 = isWorkingShift2;
	}

	public boolean isWorkingShift3() {
		return isWorkingShift3;
	}

	public void setWorkingShift3(boolean isWorkingShift3) {
		this.isWorkingShift3 = isWorkingShift3;
	}

	public boolean isLunchShift1() {
		return isLunchShift1;
	}

	public void setLunchShift1(boolean isLunchShift1) {
		this.isLunchShift1 = isLunchShift1;
	}

	public boolean isLunchShift2() {
		return isLunchShift2;
	}

	public void setLunchShift2(boolean isLunchShift2) {
		this.isLunchShift2 = isLunchShift2;
	}

	public boolean isLunchShift3() {
		return isLunchShift3;
	}

	public void setLunchShift3(boolean isLunchShift3) {
		this.isLunchShift3 = isLunchShift3;
	}

	public boolean isWorkingShift4() {
		return isWorkingShift4;
	}

	public void setWorkingShift4(boolean isWorkingShift4) {
		this.isWorkingShift4 = isWorkingShift4;
	}

	public boolean isLunchShift4() {
		return isLunchShift4;
	}

	public void setLunchShift4(boolean isLunchShift4) {
		this.isLunchShift4 = isLunchShift4;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getRegister_code() {
		return register_code;
	}

	public void setRegister_code(String register_code) {
		this.register_code = register_code;
	}
}
