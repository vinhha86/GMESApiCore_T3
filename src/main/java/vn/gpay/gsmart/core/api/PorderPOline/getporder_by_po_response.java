package vn.gpay.gsmart.core.api.PorderPOline;

import java.util.List;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.porder_grant.POrderGrant;

public class getporder_by_po_response extends ResponseBase {
	public List<POrderGrant> data;
}
