package vn.gpay.gsmart.core.api.pcontract_po;

import java.util.List;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO_NoLink;


public class PContract_getbycontractproduct_response extends ResponseBase{
	public List<PContract_PO> data;
}
