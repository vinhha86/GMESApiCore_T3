package vn.gpay.gsmart.core.pcontractbomhqcolor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class PContractBomHQColorService extends AbstractService<PContractBomHQColor> implements IPContractBomHQColorService {
	@Autowired IPContractBomHQColorRepository repo;
	@Override
	protected JpaRepository<PContractBomHQColor, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}
	@Override
	public List<PContractBomHQColor> getall_material_in_productBOMColor(long pcontractid_link, long productid_link,
			long colorid_link, long materialid_link) {
		// TODO Auto-generated method stub
		return repo.getall_material_in_productBOMColor(productid_link, pcontractid_link, colorid_link, materialid_link);
	}
	@Override
	public List<PContractBomHQColor> getcolor_bymaterial_in_productBOMColor(long pcontractid_link, long productid_link,
			long materialid_link) {
		// TODO Auto-generated method stub
		return repo.getcolor_bymaterial_in_productBOMColor(productid_link, pcontractid_link, materialid_link);
	}
	@Override
	public List<PContractBomHQColor> getall_byproduct(long pcontractid_link, long productid_link) {
		// TODO Auto-generated method stub
		return repo.getall_in_productBOMColor(productid_link, pcontractid_link);
	}
	
	@Override
	public List<PContractBomHQColor> getall_bypcontract(long orgrootid_link, long pcontractid_link) {
		// TODO Auto-generated method stub
		return repo.getall_bypcontract(orgrootid_link, pcontractid_link);
	}

}
