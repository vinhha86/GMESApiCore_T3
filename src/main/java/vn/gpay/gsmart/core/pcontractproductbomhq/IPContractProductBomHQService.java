package vn.gpay.gsmart.core.pcontractproductbomhq;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IPContractProductBomHQService  extends Operations<PContractProductBomHQ>{
	public List<PContractProductBomHQ> get_pcontract_productBOMbyid(long productid_link, long pcontractid_link);
	public List<PContractProductBomHQ> get_material_in_pcontract_productBOM(long productid_link, long pcontractid_link, Integer skutypeid_link);
	public List<PContractProductBomHQ> getby_pcontract_product_material(long productid_link, long pcontractid_link, long materialid_link);
	List<PContractProductBomHQ> getby_pcontract_material(long pcontractid_link, long materialid_link);
	List<PContractProductBomHQ> getall_bypcontract(long orgrootid_link, long pcontractid_link);

}
