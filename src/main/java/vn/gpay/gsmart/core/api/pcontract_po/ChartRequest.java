package vn.gpay.gsmart.core.api.pcontract_po;

import java.util.List;

import vn.gpay.gsmart.core.base.RequestBase;

public class ChartRequest extends RequestBase{
	// BarChartProductShipDate
	public List<Long> productIdList;
	public Integer status;
	public String contract_code;
	public String product_code;
	public String po_code;
	public Long buyer;
	public Long vendor;
	public Long pcontract_poid_link;
}
