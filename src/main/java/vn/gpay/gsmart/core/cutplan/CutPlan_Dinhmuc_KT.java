package vn.gpay.gsmart.core.cutplan;

public class CutPlan_Dinhmuc_KT {
	private String loaiphoimau;
	private Double dinhmuc_kt = (double)0;
	public CutPlan_Dinhmuc_KT(String loaiphoimau,Double dinhmuc_kt) {
		this.loaiphoimau = loaiphoimau;
		this.dinhmuc_kt = dinhmuc_kt;
	}
	
	public String getLoaiphoimau() {
		return loaiphoimau;
	}
	public void setLoaiphoimau(String loaiphoimau) {
		this.loaiphoimau = loaiphoimau;
	}
	public Double getDinhmuc_kt() {
		return dinhmuc_kt;
	}
	public void setDinhmuc_kt(Double dinhmuc_kt) {
		this.dinhmuc_kt = dinhmuc_kt;
	}


}
