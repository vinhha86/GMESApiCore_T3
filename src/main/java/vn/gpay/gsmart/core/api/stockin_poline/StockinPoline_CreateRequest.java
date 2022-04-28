package vn.gpay.gsmart.core.api.stockin_poline;

import java.util.List;

import vn.gpay.gsmart.core.base.RequestBase;

public class StockinPoline_CreateRequest extends RequestBase{
	public Long stockinid_link;
	public List<Long> listPoId;
}
