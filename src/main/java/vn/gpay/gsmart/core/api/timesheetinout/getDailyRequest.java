package vn.gpay.gsmart.core.api.timesheetinout;

public class getDailyRequest {
	public int month;
	public int year;
	public int day;
	public int orgid_link;
	public int grantid_link = 0;
	public String personnel_code = "";
	public int isdoublecheck = 0;
}
