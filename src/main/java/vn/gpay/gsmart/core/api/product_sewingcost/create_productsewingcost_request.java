package vn.gpay.gsmart.core.api.product_sewingcost;

import java.util.List;

import vn.gpay.gsmart.core.product_sewingcost.ProductSewingCost;

public class create_productsewingcost_request {
	public Long productid_link;
	public Long pcontractid_link;
	public List<Long> list_working;
	public List<ProductSewingCost> data;
}
