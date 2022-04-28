package vn.gpay.gsmart.core.sku;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;


@Service
public class SKU_AttributeValue_Service extends AbstractService<SKU_Attribute_Value> implements ISKU_AttributeValue_Service {
	@Autowired ISKU_AttValue_Repository repo;
	
	@Override
	public List<SKU_Attribute_Value> getlist_byProduct_and_value(Long productid_link, Long attributevalueid_link) {
		// TODO Auto-generated method stub
		return repo.getone_byproduct_and_value(productid_link, attributevalueid_link);
	}

	@Override
	protected JpaRepository<SKU_Attribute_Value, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	@Override
	public List<SKU_Attribute_Value> getlist_byProduct_and_attribute(Long productid_link, Long attributeid_link) {
		// TODO Auto-generated method stub
		return repo.get_byproduct_andAttribute(productid_link, attributeid_link);
	}

	@Override
	public List<SKU_Attribute_Value> getlist_byproduct(Long productid_link) {
		// TODO Auto-generated method stub
		return repo.get_byproduct(productid_link);
	}

	@Override
	public long getsku_byproduct_and_valuemau_valueco(long productid_link, long value_mau, long value_co) {
		// TODO Auto-generated method stub
//		List<SKU_Attribute_Value> lst = repo.getList_by_valueMau_and_valueCo(value_mau, value_co, productid_link);
//		if(lst.size() > 1) {
//			long skuid = lst.get(0).getSkuid_link();
//			for(int i =1; i<lst.size(); i++) {
//				if(skuid != (long)lst.get(i).getSkuid_link())
//					return 0;
//			}
//			return skuid;
//		}
//		return 0;
		List<Long> list = repo.getskuid_by_valueMau_and_valueCo(value_mau, value_co, productid_link);
		if(list.size() >0)
			return list.get(0);
		return 0;
	}

	@Override
	public List<SKU_Attribute_Value> getlist_bysku(Long skuid_link) {
		// TODO Auto-generated method stub
		return repo.getby_skuid_link(skuid_link);
	}

	@Override
	public List<SKU_Attribute_Value> get_bycolor(long pcontractid_link, long productid_link, long colorid_link) {
		// TODO Auto-generated method stub
		return repo.get_bycolorid_link(productid_link, pcontractid_link, colorid_link);
	}

	@Override
	public long get_npl_sku_byproduct_and_valuemau_valueco(long productid_link, long valuemau, long valueco) {
		// TODO Auto-generated method stub
		List<SKU_Attribute_Value> list_color = repo.getone_byproduct_and_value(productid_link, valuemau);
		Map<Long, Long> map = new HashMap<Long, Long>();
		for(SKU_Attribute_Value av : list_color) {
			map.put(av.getSkuid_link(), av.getSkuid_link());
		}
		
		List<SKU_Attribute_Value> list_size = repo.getone_byproduct_and_value(productid_link, valueco);
		for(SKU_Attribute_Value av : list_size) {
			if(map.get(av.getSkuid_link()) != null) {
				return av.getSkuid_link();
			}
		}
		
		return 0;
	}

}
