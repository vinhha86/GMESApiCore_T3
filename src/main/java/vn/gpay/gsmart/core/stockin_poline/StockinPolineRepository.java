package vn.gpay.gsmart.core.stockin_poline;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StockinPolineRepository extends JpaRepository<StockinPoline, Long>, JpaSpecificationExecutor<StockinPoline> {
	
	@Query(value = "select c from StockinPoline c "
			+ " where c.stockinid_link = :stockinid_link "
			+ " and c.pcontract_poid_link = :pcontract_poid_link "
			)
	public List<StockinPoline> getByStockin_Poline(
			@Param ("stockinid_link")final Long stockinid_link,
			@Param ("pcontract_poid_link")final Long pcontract_poid_link
			);
}
