package vn.gpay.gsmart.core.cutplan_processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.github.wenhao.jpa.Sorts;
import com.github.wenhao.jpa.Specifications;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.utils.GPAYDateFormat;

@Service
public class CutplanProcessingServiceImpl extends AbstractService<CutplanProcessing> implements ICutplanProcessingService{

	@Autowired
	CutplanProcessingRepository repo;
	
	@Override
	protected JpaRepository<CutplanProcessing, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}
	
	@Override
	public Page<CutplanProcessing> cutplanProcessing_page(Date processingdate_from, Date processingdate_to, 
			int limit, int page, Long porderid_link, Long skuid_link) {
		// TODO Auto-generated method stub
		Specification<CutplanProcessing> specification = Specifications.<CutplanProcessing>and()
//	            .ge((stockindate_from!=null && stockindate_to==null),"stockindate",DateFormat.atStartOfDay(stockindate_from))
//                .le((stockindate_from==null && stockindate_to!=null),"stockindate",DateFormat.atEndOfDay(stockindate_to))
                .between((processingdate_from!=null && processingdate_to!=null),"processingdate", GPAYDateFormat.atStartOfDay(processingdate_from), GPAYDateFormat.atEndOfDay(processingdate_to))
//                .ne("status", -1)
                .eq(porderid_link != null, "cutPlanRow.porderid_link", porderid_link)
                .eq(skuid_link != null, "material_skuid_link", skuid_link)
	            .build();
		Sort sort = Sorts.builder()
		        .desc("processingdate")
		        .build();
	    return repo.findAll(specification,PageRequest.of(page - 1, limit, sort));
	}

	@Override
	public List<CutplanProcessing> getby_cutplanrow(Long cutplanrowid_link) {
		// TODO Auto-generated method stub
		return repo.getby_cutplanrow(cutplanrowid_link);
	}

	@Override
	public List<CutplanProcessing> getForChart_TienDoCat(Long porderid_link, Long skuid_link) {
		List<CutplanProcessing> data = new ArrayList<CutplanProcessing>();
		List<Object[]> objects = repo.getForChart_TienDoCat(porderid_link, skuid_link);
		
		for(Object[] row : objects) {
			Long amountcut = (Long) row[0];
			Date processingdate = (Date) row[1];
			
			Integer amountcutInt = Math.toIntExact(amountcut);
			
			CutplanProcessing temp = new CutplanProcessing();
			temp.setAmountcut(amountcutInt);
			temp.setProcessingdate(processingdate);
			data.add(temp);
		}
		Collections.sort(data, new Comparator<CutplanProcessing>() {
			  public int compare(CutplanProcessing o1, CutplanProcessing o2) {
			      return o1.getProcessingdate().compareTo(o2.getProcessingdate());
			  }
			});
		
		return data;
	}

	@Override
	public Integer getSlCat_by_product_material_porder(Long product_skuid_link, Long material_skuid_link,
			Long porderid_link) {
		List<Object[]> objList = repo.getSlCat_by_product_material_porder(product_skuid_link, material_skuid_link, porderid_link);
		
		Integer amountcut = 0;
		
		// a.amount, a.product_skuid_link, a.cutplanrowid_link,
		// b.material_skuid_link, c.processingdate, d.la_vai
		for(Object[] row : objList) {
			Integer amount = row[0] == null ? 0 : Integer.parseInt(row[0].toString());
			Integer la_vai = row[5] == null ? 0 : Integer.parseInt(row[5].toString());
			amountcut += amount * la_vai;
		}
		
		return amountcut;
	}

}
