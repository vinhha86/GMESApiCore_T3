package vn.gpay.gsmart.core.pcontract_po;

import java.util.List;

public class PContract_POBinding {
	private Long sum;
	private String marketName;
	private Integer status;
	private String statusName;
	private List<Long> productIdList;
	private List<String> productBuyerCodeList;
	
	public Long getSum() {
		return sum;
	}
	public void setSum(Long sum) {
		this.sum = sum;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public List<Long> getProductIdList() {
		return productIdList;
	}
	public void setProductIdList(List<Long> productIdList) {
		this.productIdList = productIdList;
	}
	public List<String> getProductBuyerCodeList() {
		return productBuyerCodeList;
	}
	public void setProductBuyerCodeList(List<String> productBuyerCodeList) {
		this.productBuyerCodeList = productBuyerCodeList;
	}
	
}
