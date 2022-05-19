package vn.gpay.gsmart.core.api.porder_grant;

import vn.gpay.gsmart.core.base.RequestBase;

import java.util.Date;

public class POrder_Grant_findOne_request extends RequestBase{
	public Long idPorderGrant;
	public Long productid_link;
	public Date date;
}
