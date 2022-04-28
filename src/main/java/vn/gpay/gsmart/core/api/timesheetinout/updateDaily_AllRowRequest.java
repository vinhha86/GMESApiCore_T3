package vn.gpay.gsmart.core.api.timesheetinout;

public class updateDaily_AllRowRequest {
	public int row_month;
	public int row_year;
	public int row_day;//Ngay trong thang dang sua thong tin
	
	public Long orgid_link;
	public Long grantid_link;
	
    public String value;//Gia tri (L: Nghi le; F-Nghi phep; RO Nghi khong luong)

}
