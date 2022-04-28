package vn.gpay.gsmart.core.pcontractbomhqsku;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IPContractBOMHQSKUService extends Operations<PContractBOMHQSKU>{
	public List<PContractBOMHQSKU> getall_material_in_productBOMSKU(long pcontractid_link, long productid_link, long sizeid_link, long colorid_link, long materialid_link);
	public List<PContractBOMHQSKU> getcolor_bymaterial_in_productBOMSKU(long pcontractid_link, long productid_link, long materialid_link);
	public long getskuid_link_by_color_and_size(long colorid_link, long sizeid_link, long productid_link);
	public List<PContractBOMHQSKU> getmaterial_bycolorid_link(long pcontractid_link, long productid_link, long colorid_link, long materialid_link);
	public List<Long> getsize_bycolor(long pcontractid_link, long productid_link, long colorid_link);
	public List<Long> getcolor_bypcontract_product(long pcontractid_link, long productid_link);
	List<PContractBOMHQSKU> getMaterials_BySKUId(Long skuid_link);
	List<PContractBOMHQSKU> getall_bypcontract(long orgrootid_link, long pcontractid_link);
	public List<PContractBOMHQSKU> getbypcontract_and_product(long pcontractid_link, long productid_link);
	List<PContractBOMHQSKU> getBOM_By_PContractSKU(Long pcontractid_link, Long skuid_link);
	List<PContractBOMHQSKU> getMateriallist_ByContract(long pcontractid_link);
	List<PContractBOMHQSKU> getProductlist_ByMaterial(long pcontractid_link, long materialid_link);

}
