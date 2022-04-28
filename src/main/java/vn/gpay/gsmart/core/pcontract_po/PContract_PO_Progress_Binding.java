package vn.gpay.gsmart.core.pcontract_po;

public class PContract_PO_Progress_Binding {
	private Long orgid_link;
	private String orgName;
	private String orgCode;
	private Integer sumDone;
	private Integer sumNotDone;
	private Integer total;
	
	public Long getOrgid_link() {
		return orgid_link;
	}
	public void setOrgid_link(Long orgid_link) {
		this.orgid_link = orgid_link;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public Integer getSumDone() {
		return sumDone;
	}
	public void setSumDone(Integer sumDone) {
		this.sumDone = sumDone;
	}
	public Integer getSumNotDone() {
		return sumNotDone;
	}
	public void setSumNotDone(Integer sumNotDone) {
		this.sumNotDone = sumNotDone;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
}
