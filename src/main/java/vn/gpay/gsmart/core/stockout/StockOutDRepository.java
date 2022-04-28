package vn.gpay.gsmart.core.stockout;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface StockOutDRepository extends JpaRepository<StockOutD, Long>,  JpaSpecificationExecutor<StockOutD>{

	@Query(value = "select distinct c from StockOutD c " 
			+ " inner join StockOut d on d.id = c.stockoutid_link "
			+ " where d.stockoutorderid_link = :stockoutorderid_link "
			+ " and d.status >= 1 " // STOCKOUT_STATUS_APPROVED
			+ " and c.skuid_link = :skuid_link "
			)
	public List<StockOutD> getByStockoutOrder_Sku_Approved(
			@Param ("stockoutorderid_link")final Long stockoutorderid_link,
			@Param ("skuid_link")final Long skuid_link
			);
	
	@Query(value = "select sum(c.totalpackagecheck) from StockOutD c " 
			+ " inner join StockOut d on d.id = c.stockoutid_link "
			+ " left join StockoutPoline e on d.id = e.stockoutid_link "
			+ " where (c.skuid_link = :skuid_link or :skuid_link is null) "
			+ " and (e.pcontract_poid_link = :pcontract_poid_link ) "
			+ " and d.stockouttypeid_link = 21 " // type: 21: xuất thành phẩm theo đơn
			+ " and d.status >= 1 " // STOCKOUT_STATUS_APPROVED
			)
	public Integer getAmountStockoutBySkuAndPoLine(
			@Param ("skuid_link")final Long skuid_link,
			@Param ("pcontract_poid_link")final Long pcontract_poid_link
			);
	
}
