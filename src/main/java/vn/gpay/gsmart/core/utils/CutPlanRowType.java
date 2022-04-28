package vn.gpay.gsmart.core.utils;

public class CutPlanRowType {
	//Khi hiện ra sơ đồ cắt thì order by desc để dòng yêu cầu số 2 lên đầu, sau đó đến cắt dư và các dòng sơ đồ ở dưới
	public static int sodocat                	= 0;//Hiện số chạy trong sơ đồ
    public static int catdu						= 1;//Hiện số sản phẩm còn dư chưa vào sơ đồ - màu vàng
    public static int yeucau        			= 2;//Hiện số yêu cầu theo size - màu xanh
}
