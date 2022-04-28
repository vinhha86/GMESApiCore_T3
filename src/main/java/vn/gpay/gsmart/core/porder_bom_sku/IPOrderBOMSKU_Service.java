package vn.gpay.gsmart.core.porder_bom_sku;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IPOrderBOMSKU_Service extends Operations<POrderBOMSKU> {

	List<POrderBOMSKU> getByPOrderID_and_type(Long porderid_link, int type);

	List<POrderBOMSKU> getByPContract_ProductID_and_type(Long pcontractid_link, Long productid_link, int type);

	List<POrderBOMSKU> getByPContract_ProductID_and_type_material(Long pcontractid_link, Long productid_link, int type,
			Long matedrial_skuid_link);

	List<POrderBOMSKU_By_Product> getByPOrderID_GroupByProduct(Long porderid_link);

	List<POrderBOMSKU_By_Color> getByPOrderID_GroupByColor(Long porderid_link);

	List<POrderBOMSKU> getSKUByMaterial(Long porderid_link, Long materialid_link);

	List<POrderBOMSKU> getby_porder_and_color(Long porderid_link, Long colorid_link);

	List<POrderBOMSKU> getby_porder_and_material(Long porderid_link, Long materialid_link);

	List<POrderBOMSKU> getby_porder_and_material_and_color(Long porderid_link, Long materialid_link, long colorid_link);

	List<POrderBOMSKU> getby_porder_and_material_and_color_and_size_and_type(Long porderid_link, Long productid_link,
			Long materialid_link, long colorid_link, long sizeid_link, int type);

	List<POrderBOMSKU> getby_porder_and_material_and_sku_and_type(Long porderid_link, Long materialid_link,
			long skuid_link, int type);

	List<POrderBOMSKU> getby_pcontract_product_and_material_and_sku_and_type(Long pcontractid_link, Long productid_link,
			Long materialid_link, long skuid_link, int type);

	List<Long> getMaterialList_By_Porder_Sku(Long porderid_link, Long skuid_link, Integer skutypeid_link);
	
	List<POrderBOMSKU> getByPcontract_Product_Material(Long pcontractid_link, Long productid_link, Long materialid_link);

	List<POrderBOMSKU> getByProductSKU(Long pcontractid_link, Long productid_link, Long materialid_link,
			long skuid_link);
}
