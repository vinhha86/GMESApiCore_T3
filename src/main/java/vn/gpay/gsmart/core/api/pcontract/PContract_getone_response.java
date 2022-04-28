package vn.gpay.gsmart.core.api.pcontract;

import java.util.List;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.pcontract.PContract;

public class PContract_getone_response extends ResponseBase {
	public PContract data;
    public List<Long> market;
}
