package vn.gpay.gsmart.core.stockout;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IStockOutDService extends Operations<StockOutD>{
	List<StockOutD> getByStockoutOrder_Sku_Approved(Long stockoutorderid_link, Long skuid_link);
	Integer getAmountStockoutBySkuAndPoLine(Long skuid_link, Long pcontract_poid_link);
}
