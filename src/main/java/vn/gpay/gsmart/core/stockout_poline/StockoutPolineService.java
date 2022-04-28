package vn.gpay.gsmart.core.stockout_poline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.stockin_poline.StockinPoline;

@Service
public class StockoutPolineService extends AbstractService<StockoutPoline> implements IStockoutPolineService{
	
	@Autowired
	StockoutPolineRepository repo;

	@Override
	protected JpaRepository<StockoutPoline, Long> getRepository() {
		return repo;
	}

	@Override
	public List<StockoutPoline> getByStockout_Poline(Long stockoutid_link, Long pcontract_poid_link) {
		return repo.getByStockout_Poline(stockoutid_link, pcontract_poid_link);
	}
	
}
