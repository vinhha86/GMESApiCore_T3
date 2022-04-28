package vn.gpay.gsmart.core.sku;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface ISKU_Service extends Operations<SKU> {
	public List<SKU> getlist_byProduct(Long productid_link);

	List<SKU> getSKU_MainMaterial(String code);

	List<SKU> getSKU_ByType(String code, Integer producttypeid_link);

	SKU getSKU_byCode(String code, long orgrootid_link);

	public List<SKU> getProductSKU_ByCode(Integer skutypeid_link, String code);

	public List<SKU> getProductSKU_ByBarCode(Integer skutypeid_link, String barcode);

	public List<SKU> getSKUforHandOver(Long porderid_link);

	public List<SKU> getnpl_by_pcontract(Integer producttypeid_link, Long pcontractid_link);

	public List<SKU> getnpl_by_pcontract_product(Integer producttypeid_link, Long pcontractid_link,
			Long productid_link);

	public List<SKU> getnpl_by_pcontract_product_and_color(Integer producttypeid_link, Long pcontractid_link,
			Long productid_link, Long colorid_link);

	public List<SKU> getSkuByCode(String code);

	public List<SKU> getSkuByCodeAndType(String code, Integer typeFrom, Integer typeTo);
	
	public List<SKU> getSkuForXuatDieuChuyenNguyenLieu(List<Long> skuid_list, Long pcontractid_link);
	
	List<SKU> getBySkuIdList(List<Long> skuNplIdList, Integer skuType);

	List<SKU> getBySkuIdListPhuLieu(List<Long> skuNplIdList, Integer skuType);
}
