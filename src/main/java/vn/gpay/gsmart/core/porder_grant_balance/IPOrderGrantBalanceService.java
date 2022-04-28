package vn.gpay.gsmart.core.porder_grant_balance;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IPOrderGrantBalanceService extends Operations<POrderGrantBalance>{
	List<POrderGrantBalance> getByPorderGrantAndPorderBalance(Long pordergrantid_link, Long porderbalanceid_link);
	
	List<POrderGrantBalance> getByPorderGrantAndProductBalance(Long pordergrantid_link, Long productbalanceid_link);
	
	List<POrderGrantBalance> getByPorderGrantAndPersonnel(Long pordergrantid_link, Long personnelid_link);

	List<POrderGrantBalance> getByPorderGrant(Long pordergrantid_link);
}
