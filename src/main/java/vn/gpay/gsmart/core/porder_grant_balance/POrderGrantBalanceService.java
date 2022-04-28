package vn.gpay.gsmart.core.porder_grant_balance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class POrderGrantBalanceService extends AbstractService<POrderGrantBalance> implements IPOrderGrantBalanceService{
	@Autowired IPOrderGrantBalanceRepository repo;

	@Override
	protected JpaRepository<POrderGrantBalance, Long> getRepository() {
		return repo;
	}

	@Override
	public List<POrderGrantBalance> getByPorderGrantAndPorderBalance(Long pordergrantid_link,
			Long porderbalanceid_link) {
		return repo.getByPorderGrantAndPorderBalance(pordergrantid_link, porderbalanceid_link);
	}
	
	@Override
	public List<POrderGrantBalance> getByPorderGrant(Long pordergrantid_link) {
		return repo.getByPorderGrant(pordergrantid_link);
	}

	@Override
	public List<POrderGrantBalance> getByPorderGrantAndPersonnel(Long pordergrantid_link, Long personnelid_link) {
		return repo.getByPorderGrantAndPersonnel(pordergrantid_link, personnelid_link);
	}

	@Override
	public List<POrderGrantBalance> getByPorderGrantAndProductBalance(Long pordergrantid_link,
			Long productbalanceid_link) {
		return repo.getByPorderGrantAndProductBalance(pordergrantid_link, productbalanceid_link);
	}
	

}
