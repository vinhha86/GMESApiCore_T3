package vn.gpay.gsmart.core.api.stockout_poline;

import java.util.List;

import vn.gpay.gsmart.core.base.RequestBase;

public class StockoutPoline_CreateRequest extends RequestBase{
	public Long stockoutid_link;
	public List<Long> listPoId;
}
