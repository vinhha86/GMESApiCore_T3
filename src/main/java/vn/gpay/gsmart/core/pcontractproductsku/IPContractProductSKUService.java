package vn.gpay.gsmart.core.pcontractproductsku;

import java.util.List;

import vn.gpay.gsmart.core.attributevalue.Attributevalue;
import vn.gpay.gsmart.core.base.Operations;

public interface IPContractProductSKUService extends Operations<PContractProductSKU> {
	public List<PContractProductSKU> getlistsku_byproduct_and_pcontract(long orgrootid_link, long productid_link,
			long pcontractid_link);

	public List<PContractProductSKU> getlistsku_bysku_and_pcontract(long skuid_link, long pcontractid_link);

	public List<Long> getlistvalue_by_product(long pcontractid_link, long productid_link, long attributeid_link);

	public List<String> getlistnamevalue_by_product(long pcontractid_link, long productid_link, long attributeid_link);

	public List<Long> getsku_bycolor(long pcontractid_link, long productid_link, Long colorid_link);

	public List<Long> getsku_bypcontract_and_product(long pcontractid_link, long productid_link);

	public List<PContractProductSKU> getsku_bycolorid_link(long pcontractid_link, long productid_link,
			long colorid_link);

	public List<PContractProductSKU> getlistsku_bypo_and_pcontract(long orgrootid_link, long pcontract_poid_link,
			long pcontractid_link);

	public List<PContractProductSKU> getbypo_and_product(long pcontract_poid_link, long productid_link);
	
	public List<PContractProductSKU> getbypo_and_product(long pcontract_poid_link, long productid_link, List<Long> product_ids);

	public List<PContractProductSKU> getlistsku_bysku_and_product_PO(long skuid_link, long pcontract_poid_link,
			long productid_link);

	List<PContractProductSKU> getlistsku_bypo_and_pcontract_free(long orgrootid_link, long pcontract_poid_link,
			long pcontractid_link);

	List<PContractProductSKU> getbypo_and_product_free(long porderreqid_link, long pcontractid_link,
			long pcontract_poid_link, long productid_link);

	List<PContractProductSKU> getlistsku_bypcontract(long orgrootid_link, long pcontractid_link);

	List<PContractProductSKU> getPOSKU_Free_ByProduct(long productid_link, long pcontract_poid_link);

	public List<PContractProductSKU> getlistsku_bypo(Long pcontract_poid_link);

	public List<PContractProductSKU> getBySkuAndPcontractPo(Long skuid_link, Long pcontract_poid_link);

	List<PContractProductSKU> getsumsku_bypo_parent(Long pcontract_poid_link);

	List<POLineSKU> gettotalsku_bypo_parent_and_product(Long pcontract_poid_link, Long productid_link);

	List<PContractProductSKU> getsumsku_bypcontract(long pcontractid_link, List<Long> ls_productid);

	List<String> getlist_sizeset_by_product(Long pcontractid_link, Long productid_link);

	List<Long> getlist_size_by_product_and_sizeset(Long pcontractid_link, Long productid_link, Long sizesetid_link);

	List<Attributevalue> getmausanpham_by_pcontract(Long pcontractid_link);

	List<PContractProductSKU> getsku_notmap(Long pcontract_poid_link);

	List<PContractProductSKU> getsku_notmap_by_product(Long pcontract_poid_link, Long productid_link);

	List<PContractProductSKU> getlistsku_bypcontract_nolink(long orgrootid_link, long pcontractid_link);
	
	List<PContractProductSKU> getByPoLine_product_size_color(Long pcontract_poid_link, Long productid_link, Long sizeId, Long colorId);
	
	List<PContractProductSKU> getByPoLine_product(Long pcontract_poid_link, Long productid_link);

	List<PContractProductSKU> getlistsku_bylist_prodandpo(long orgrootid_link, long pcontractid_link,
			List<Long> productidlist, List<Long> polist);
}
