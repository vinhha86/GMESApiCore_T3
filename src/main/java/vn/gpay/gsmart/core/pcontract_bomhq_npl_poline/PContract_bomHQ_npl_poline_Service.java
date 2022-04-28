package vn.gpay.gsmart.core.pcontract_bomhq_npl_poline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class PContract_bomHQ_npl_poline_Service extends AbstractService<PContract_bomHQ_npl_poline> implements IPContract_bomHQ_npl_poline_Service {
	@Autowired
	IPContract_bomHQ_npl_poline_repo repo;
	
	@Override
	protected JpaRepository<PContract_bomHQ_npl_poline, Long> getRepository() {
	// TODO Auto-generated method stub
	return repo;
	}
	
	//@Override
	//List<PContractPO_NPL> getby_po_and_npl(Long pcontractpoid_link,Long material_skuid_link) {
	//// TODO Auto-generated method stub
	//return repo.getby_po(pcontractpoid_link);
	//}
	@Override
	public List<PContract_bomHQ_npl_poline> getby_po_and_npl(Long pcontractpoid_link, Long material_skuid_link) {
	// TODO Auto-generated method stub
	return repo.getby_po_and_npl(pcontractpoid_link, material_skuid_link);
	}
	
	@Override
	public List<PContract_bomHQ_npl_poline> getby_pcontract_and_npl(Long pcontractid_link, Long material_skuid_link) {
	// TODO Auto-generated method stub
	return repo.getby_pcontract_and_npl(pcontractid_link, material_skuid_link);
	}
	
	@Override
	public List<PContract_bomHQ_npl_poline> getby_pcontract(Long pcontractid_link) {
	return repo.getby_pcontract(pcontractid_link);
	}
	
	@Override
	public List<PContract_bomHQ_npl_poline> getby_product_and_npl(Long productid_link, Long pcontractid_link,
		Long material_skuid_link) {
	material_skuid_link = material_skuid_link == 0 ? null : material_skuid_link;
	List<PContract_bomHQ_npl_poline> list = repo.getby_product(pcontractid_link, productid_link);
	
	return list;
	}
	
	@Override
	public List<PContract_bomHQ_npl_poline> getby_Pcontract_Product_Material_skuid_link(Long pcontractid_link,
		Long productid_link, Long material_skuid_link) {
	material_skuid_link = material_skuid_link == 0 ? null : material_skuid_link;
	return repo.getby_Pcontract_Product_Material_skuid_link(pcontractid_link, productid_link, material_skuid_link);
	}
	
	@Override
	public List<PContract_bomHQ_npl_poline> getby_Pcontract_Product_Material_skuid_link_PcontractPo(
		Long pcontractid_link, Long productid_link, Long npl_skuid_link, Long pcontract_poid_link) {
	return repo.getby_Pcontract_Product_Material_skuid_link_PcontractPo(pcontractid_link, productid_link, npl_skuid_link, pcontract_poid_link);
	}
}
