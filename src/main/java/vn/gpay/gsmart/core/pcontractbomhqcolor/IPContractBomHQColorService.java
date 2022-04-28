package vn.gpay.gsmart.core.pcontractbomhqcolor;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IPContractBomHQColorService  extends Operations<PContractBomHQColor> {
	public List<PContractBomHQColor> getall_material_in_productBOMColor(long pcontractid_link, long productid_link, long colorid_link, long materialid_link);
	public List<PContractBomHQColor> getcolor_bymaterial_in_productBOMColor(long pcontractid_link, long productid_link, long materialid_link);
	public List<PContractBomHQColor> getall_byproduct(long pcontractid_link, long productid_link);
	List<PContractBomHQColor> getall_bypcontract(long orgrootid_link, long pcontractid_link);

}
