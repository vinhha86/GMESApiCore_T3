package vn.gpay.gsmart.core.api.pcontract;

import java.util.List;

import vn.gpay.gsmart.core.base.RequestBase;

public class PContract_export_excel_request extends RequestBase {
	public Long id;
	public List<Long> product_ids;
	public Long pcontract_poid_link;
}
