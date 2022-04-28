package vn.gpay.gsmart.core.api.pcontractsku;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;

public class PContractSKU_update_response extends ResponseBase {
	public int amount;
	public boolean checkamount;
	public Integer poAmountAfterUpdate; // sl cua po line sau khi update sl sku
	public PContract_PO poAfterUpdate;
	public PContract_PO poParentAfterUpdate;
}
