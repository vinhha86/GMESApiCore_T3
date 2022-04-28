package vn.gpay.gsmart.core.stockout_order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface Stockout_order_repository extends JpaRepository<Stockout_order, Long>, JpaSpecificationExecutor<Stockout_order> {
	@Query(value = "select c from Stockout_order c where porderid_link = :porderid_link ")
	public List<Stockout_order> getby_porder(
			@Param ("porderid_link")final  long porderid_link);
	
	@Query(value = "select c from Stockout_order c "
			+ "inner join Stockout_order_d a on c.id = a.stockoutorderid_link "
			+ "where (porderid_link = :porderid_link or :porderid_link is null or :porderid_link = 0L) "
			+ "and a.material_skuid_link = :material_skuid_link "
			+ "group by c")
	public List<Stockout_order> getby_porder_npl(
			@Param ("porderid_link")final  long porderid_link,
			@Param ("material_skuid_link")final  long material_skuid_link);
	
	@Query(value = "select distinct c from Stockout_order c " 
			+ "where c.pcontract_poid_link = :pcontract_poid_link "
			+ "and c.status >= 0 "
			)
	public List<Stockout_order> getByPoLine(
			@Param ("pcontract_poid_link")final Long pcontract_poid_link);
}
