package vn.gpay.gsmart.core.pcontractbomhqsku;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.sku.ISKU_AttValue_Repository;

@Service
public class PContractBOMHQSKUService extends AbstractService<PContractBOMHQSKU> implements IPContractBOMHQSKUService {
	@Autowired IPContractBOMHQSKURepository repo;
	@Autowired ISKU_AttValue_Repository sku_att_repo;
	@Override
	protected JpaRepository<PContractBOMHQSKU, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}
	@Override
	public List<PContractBOMHQSKU> getall_material_in_productBOMSKU(long pcontractid_link, long productid_link,
			long sizeid_link, long colorid_link, long materialid_link) {
		List<Long> list_sku = sku_att_repo.getskuid_by_valueMau_and_valueCo(colorid_link, sizeid_link, productid_link);
		long skuid_link = 0;
		if(list_sku.size() > 0) {
			skuid_link = list_sku.get(0);
		}
		
		return repo.getall_material_in_productBOMSKU(productid_link, pcontractid_link, skuid_link, materialid_link);
	}
	@Override
	public List<PContractBOMHQSKU> getcolor_bymaterial_in_productBOMSKU(long pcontractid_link, long productid_link, long materialid_link) {
		// TODO Auto-generated method stub
		return repo.getall_bymaterial_in_productBOMSKU(productid_link, pcontractid_link, materialid_link);
	}
	@Override
	public List<PContractBOMHQSKU> getmaterial_bycolorid_link(long pcontractid_link, long productid_link,
			long colorid_link, long materialid_link) {
		// TODO Auto-generated method stub
		return repo.get_material_by_colorid_link(productid_link, pcontractid_link, colorid_link, materialid_link);
	}
	@Override
	public long getskuid_link_by_color_and_size(long colorid_link, long sizeid_link, long productid_link) {
		// TODO Auto-generated method stub
		List<Long> list_sku = sku_att_repo.getskuid_by_valueMau_and_valueCo(colorid_link, sizeid_link, productid_link);
		
		long skuid_link = 0;
		if(list_sku.size() > 0) {
			skuid_link = list_sku.get(0);
		}
		return skuid_link;
	}
	@Override
	public List<Long> getsize_bycolor(long pcontractid_link, long productid_link, long colorid_link) {
		// TODO Auto-generated method stub
		return repo.getsize_by_colorid_link(productid_link, pcontractid_link, colorid_link);
	}
	@Override
	public List<PContractBOMHQSKU> getMaterials_BySKUId(Long skuid_link){
		return repo.getMaterials_BySKUId(skuid_link);
	}
	
	@Override
	public List<PContractBOMHQSKU> getBOM_By_PContractSKU(Long pcontractid_link, Long skuid_link){
		return repo.getBOM_By_PContractSKU(pcontractid_link, skuid_link);
	}
	
	@Override
	public List<PContractBOMHQSKU> getall_bypcontract(long orgrootid_link, long pcontractid_link) {
		// TODO Auto-generated method stub
		return repo.getall_bypcontract(orgrootid_link, pcontractid_link);
	}
	@Override
	public List<PContractBOMHQSKU> getbypcontract_and_product(long pcontractid_link, long productid_link) {
		// TODO Auto-generated method stub
		return repo.getall_bypcontract_andproduct(productid_link, pcontractid_link);
	}
	@Override
	public List<Long> getcolor_bypcontract_product(long pcontractid_link, long productid_link) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<PContractBOMHQSKU> getMateriallist_ByContract(long pcontractid_link) {
		// TODO Auto-generated method stub
		List<Object[]> rs = repo.getMateriallist_ByContract(pcontractid_link);
		List<PContractBOMHQSKU> lsRet = new ArrayList<PContractBOMHQSKU>();
		PContractBOMHQSKU firstSKU = new PContractBOMHQSKU();
		firstSKU.setMaterial_skuid_link(-1L);
		firstSKU.setDescription("Nguy??n ph??? li???u (ch???n t???t)");
		lsRet.add(firstSKU);
		
		for (Object[] record : rs) {
			Long m_id = (Long) record[0];
			String m_code = (String)record[1];
			PContractBOMHQSKU theSKU = new PContractBOMHQSKU();
			theSKU.setMaterial_skuid_link(m_id);
			theSKU.setDescription(m_code);
			lsRet.add(theSKU);
		}
		return lsRet;
	}
	@Override
	public List<PContractBOMHQSKU> getProductlist_ByMaterial(long pcontractid_link, long materialid_link) {
		// TODO Auto-generated method stub
		List<Object[]> rs = repo.getProductlist_ByMaterial(pcontractid_link, materialid_link);
		List<PContractBOMHQSKU> lsRet = new ArrayList<PContractBOMHQSKU>();
		for (Object[] record : rs) {
			Long p_id = (Long) record[0];
			PContractBOMHQSKU theSKU = new PContractBOMHQSKU();
			System.out.println(p_id);
			theSKU.setProductid_link(p_id);
			lsRet.add(theSKU);
		}
		return lsRet;
	}

}
