package vn.gpay.gsmart.core.api.pcontract_po;

import java.util.List;

import vn.gpay.gsmart.core.base.RequestBase;

public class deselectPContractBom2Npl_request extends RequestBase{
	public Long material_skuid_link;
	public Long pcontractid_link;
	public Long productid_link;
	public List<Long> pcontract_poid_link_list;
}
