package vn.gpay.gsmart.core.api.timesheetinout;

public class updateDailyRequest {
	public int row_month;
	public int row_year;
	public int row_day;//Ngay trong thang dang sua thong tin
	
    public String in_time;//Thời gian check-in trong ngày (sớm nhất)
    public String out_time;//Thời gian check-out trong ngày (muộn nhất)
    public String lunchstart_time;//Thời giat bắt đầu ăn trưa
    public String lunchend_time;//Thời gian kết thúc ăn trưa
    public String totalworking_time;//Tổng thời gian 
    
    public Long in_time_rowid;
    public Long out_time_rowid;
    public Long lunchstart_rowid;
    public Long lunchend_rowid;
    public Long totalworking_time_rowid;
}
