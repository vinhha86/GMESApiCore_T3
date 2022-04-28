package vn.gpay.gsmart.core.stockout_poline;

import java.util.List;
import vn.gpay.gsmart.core.base.Operations;
import vn.gpay.gsmart.core.stockin_poline.StockinPoline;

public interface IStockoutPolineService extends Operations<StockoutPoline>{
	List<StockoutPoline> getByStockout_Poline(Long stockoutid_link, Long pcontract_poid_link);
}
