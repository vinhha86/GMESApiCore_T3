package vn.gpay.gsmart.core.stockout;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;
@Service
public class StockOutDServiceImpl extends AbstractService<StockOutD> implements IStockOutDService{

	@Autowired
	StockOutDRepository repository; 
	@Override
	protected JpaRepository<StockOutD, Long> getRepository() {
		return repository;
	}
	@Override
	public List<StockOutD> getByStockoutOrder_Sku_Approved(Long stockoutorderid_link, Long skuid_link) {
		return repository.getByStockoutOrder_Sku_Approved(stockoutorderid_link, skuid_link);
	}
	@Override
	public Integer getAmountStockoutBySkuAndPoLine(Long skuid_link, Long pcontract_poid_link) {
		return repository.getAmountStockoutBySkuAndPoLine(skuid_link, pcontract_poid_link);
	}
	
	

}
