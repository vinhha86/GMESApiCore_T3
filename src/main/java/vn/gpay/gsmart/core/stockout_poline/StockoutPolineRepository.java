package vn.gpay.gsmart.core.stockout_poline;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StockoutPolineRepository extends JpaRepository<StockoutPoline, Long>, JpaSpecificationExecutor<StockoutPoline> {
	
	@Query(value = "select c from StockoutPoline c "
			+ " where c.stockoutid_link = :stockoutid_link "
			+ " and c.pcontract_poid_link = :pcontract_poid_link "
			)
	public List<StockoutPoline> getByStockout_Poline(
			@Param ("stockoutid_link")final Long stockoutid_link,
			@Param ("pcontract_poid_link")final Long pcontract_poid_link
			);
}
