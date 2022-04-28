package vn.gpay.gsmart.core.api.pcontract;

import java.util.List;

import vn.gpay.gsmart.core.pcontract.PContract;

public class PContract_create_request  {
	public PContract data;
    public List<Long> markettypeArray; // Mang luu tru marketid_link
}
