package vn.gpay.gsmart.core.cutplan;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface ICutPlan_Size_Service extends Operations<CutPlan_Size> {
	List<CutPlan_Size> getby_sku_and_porder(Long material_skuid_link, Long porderid_link, Long orgrootid_link);

	List<CutPlan_Size> getby_sku_and_pcontract_product(Long material_skuid_link, Long pcontractid_link,
			Long productid_link, Long orgrootid_link);

	List<CutPlan_Size> getby_sku_and_porder_and_color(Long material_skuid_link, Long porderid_link, Long orgrootid_link,
			Long colorid_link);

	List<CutPlan_Size> getby_sku_and_pcontract_product_and_color(Long material_skuid_link, Long pcontractid_link,
			Long productid_link, Long orgrootid_link, Long colorid_link);

	List<CutPlan_Size> getby_sku_and_pcontract_product_and_color_loaiphoi(Long material_skuid_link,
			Long pcontractid_link, Long productid_link, Long orgrootid_link, Long colorid_link, String loaiphoi);

	List<CutPlan_Size> getby_row(Long orgrootid_link, Long cutplan_rowid_link);

	List<CutPlan_Size> getby_row_loaiphoi(Long orgrootid_link, Long cutplan_rowid_link, String loaiphoi);

	List<CutPlan_Size> getby_row_and_productsku(Long orgrootid_link, Long cutplanrowid_link, Long product_skuid_link);

	List<CutPlan_Size> getby_row_and_productsku_loaiphoi(Long orgrootid_link, Long cutplanrowid_link,
			Long product_skuid_link, String loaiphoi);

	List<CutPlan_Size> getby_porder_matsku_productsku(Long porderid_link, Long material_skuid_link,
			Long product_skuid_link, Integer type, String name);

	List<CutPlan_Size> getby_pcontract_product_matsku_productsku(Long pcontractid_link, Long productid_link,
			Long material_skuid_link, Long product_skuid_link, Integer type, String name);

	List<CutPlan_Size> getby_pcontract_product_matsku_productsku_loaiphoi(Long pcontractid_link, Long productid_link,
			Long material_skuid_link, Long product_skuid_link, Integer type, String name, String loaiphoi);

	Integer getTotalAmount_By_CutPlanRow(Long cutplanrowid_link);

	List<CutPlan_Size> getplansize_bykey(Long cutplan_rowid_link, Long product_skuid_link);

	Integer getsum_plansize_bykey(Long product_skuid_link, Long pcontractid_link, Long productid_link,
			Long material_skuid_link, Long colorid_link, String loaiphoimau, Integer type);

	List<CutPlan_Size> getplansize_byplanrow(Long cutplan_rowid_link);
}
