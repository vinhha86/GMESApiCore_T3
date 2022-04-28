package vn.gpay.gsmart.core.cutplan;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;
import vn.gpay.gsmart.core.porder.POrder;

public interface ICutPlan_Row_Service extends Operations<CutPlan_Row> {
	List<CutPlan_Row> getby_color(Long porderid_link, Long material_skuid_link, Long colorid_link, Long orgrootid_link);

	List<CutPlan_Row> getby_color(Long pcontractid_link, Long productid_link, Long material_skuid_link,
			Long colorid_link, Long orgrootid_link);

	List<CutPlan_Row> getby_loaiphoi(Long pcontractid_link, Long productid_link, Long material_skuid_link,
			Long colorid_link, Long orgrootid_link, String loaiphoi);

	List<CutPlan_Row> getby_porder_matsku_productsku(Long porderid_link, Long material_skuid_link,
			Long product_skuid_link, Integer type, String name);

	boolean sync_porder_bom(Long material_skuid_link, Long pcontractid_link, Long productid_link, Long colorid_link,
			Long userid_link, Long orgrootid_link);

	boolean sync_porder_bom_from_cutprocesing(Long material_skuid_link, POrder porder, Long colorid_link,
			Long userid_link, Long orgrootid_link);

	public List<CutPlan_Row> findByPOrder(Long porderid_link);

	public List<String> getAllLoaiPhoiMau(Long pcontractid_link, Long productid_link, Long material_skuid_link);

	List<CutPlan_Row> getplanrow_bykey(Long pcontractid_link, Long productid_link, Long material_skuid_link,
			Long colorid_link, String loaiphoimau, Integer type);

	boolean sync_porder_bom_new(Long material_skuid_link, Long pcontractid_link, Long productid_link, Long colorid_link,
			Long userid_link, Long orgrootid_link, String loaiphoimau, Integer typephoimau);

	boolean sync_porderbom_kt(Long pcontractid_link, Long productid_link, Long material_skuid_link, Long colorid_link,
			Long userid_link, Long orgrootid_link);
}
