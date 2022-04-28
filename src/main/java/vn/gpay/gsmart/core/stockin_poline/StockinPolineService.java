package vn.gpay.gsmart.core.stockin_poline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class StockinPolineService extends AbstractService<StockinPoline> implements IStockinPolineService{
	
	@Autowired
	StockinPolineRepository repo;

	@Override
	protected JpaRepository<StockinPoline, Long> getRepository() {
		return repo;
	}

	@Override
	public List<StockinPoline> getByStockin_Poline(Long stockinid_link, Long pcontract_poid_link) {
		return repo.getByStockin_Poline(stockinid_link, pcontract_poid_link);
	}
	
}
