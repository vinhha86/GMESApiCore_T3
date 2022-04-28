package vn.gpay.gsmart.core.porder_bom_product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class POrderBomProduct_Service extends AbstractService<POrderBomProduct> implements IPOrderBomProduct_Service {
	@Autowired POrderBomProduct_Repository repo;
	
	@Override
	protected JpaRepository<POrderBomProduct, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	@Override
	public List<POrderBomProduct> getby_porder(Long porderid_link) {
		// TODO Auto-generated method stub
		return repo.getby_porder(porderid_link);
	}

	@Override
	public List<POrderBomProduct> getby_porder_and_material(Long porderid_link, Long material_skuid_link) {
		// TODO Auto-generated method stub
		return repo.getby_porder_and_material(porderid_link, material_skuid_link);
	}

	@Override
	public List<POrderBomProduct> getby_porder_and_type(Long porderid_link, int type_from, int type_to) {
		// TODO Auto-generated method stub
		return repo.getby_porder_and_type(porderid_link, type_from, type_to);
	}

	@Override
	public List<POrderBomProduct> getby_pcontract_product_and_material(Long pcontractid_link, Long productid_link,
			Long material_skuid_link) {
		// TODO Auto-generated method stub
		return repo.getby_pcontract_product_and_material(pcontractid_link, productid_link, material_skuid_link);
	}

	@Override
	public List<POrderBomProduct> getby_pcontract_product(Long pcontractid_link, Long productid_link) {
		// TODO Auto-generated method stub
		return repo.getby_pcontract_product(pcontractid_link, productid_link);
	}

}
