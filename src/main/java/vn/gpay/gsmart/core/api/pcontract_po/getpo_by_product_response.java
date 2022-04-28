package vn.gpay.gsmart.core.api.pcontract_po;

import java.util.List;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKUBinding;

public class getpo_by_product_response extends ResponseBase{
	public List<PContract_PO> data;
	public List<PContractProductSKU> data2;
	public List<PContractProductSKUBinding> data3;
}
