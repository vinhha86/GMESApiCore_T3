package vn.gpay.gsmart.core.api.personnel;

import java.util.Date;

import vn.gpay.gsmart.core.base.ResponseBase;

public class create_personnel_response extends ResponseBase {
	public Long id;
	public String bike_number;
    public String bankname;
    public Date date_household_grant;
}
