package vn.gpay.gsmart.core.warehouse;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.gpay.gsmart.core.warehouse.Warehouse;


@Repository
@Transactional
public interface WarehouseRepository extends JpaRepository<Warehouse, String>{

	@Query(value = "select c from Warehouse c where spaceepc_link =:spaceepc")
	public List<Warehouse> findBySpaceepc(@Param ("spaceepc")final String spaceepc);
	
	@Query(value = "select c from Warehouse c where upper(lotnumber) =upper(:lotnumber)")
	public List<Warehouse> findByLotNumber(@Param ("lotnumber")final String lotnumber);	
	
	@Query(value = "select c from Warehouse c where epc =:epc and stockid_link =:stockid_link")
	public List<Warehouse> findMaterialByEPC(
			@Param ("epc")final String epc, 
			@Param ("stockid_link")final long stockid_link);
	
	//@Query(value = "select a from Warehouse a inner join EpcWarehouseCheck b on a.epc = b.epc   where b.token =:token")
	@Query(value = "select a from Warehouse a")
	public List<Warehouse> findCheckedEPC(@Param ("token")final UUID token);
	@Query(value="select a.skuid_link,SUM(a.yds),b.unitprice,SUM(a.yds)*b.unitprice,b.unitid_link " + 
			"from Warehouse a  " + 
			"inner join StockInD b on a.stockindid_link=b.id " + 
			"where a.stockid_link =:orgfrom_code "+
			"group by a.skuid_link,b.unitprice")
	public List<Object[]> invcheck_sku(@Param ("orgfrom_code")final Long orgfrom_code);
	//public List<Object[]> invcheck_sku(@Param ("bossid")final String bossid,@Param ("orgfrom_code")final String orgfrom_code,@Param ("productcode")final String productcode);
	
	
	@Query(value = "select c.epc, c.skuid_link,c.yds,c.unitprice,c.unitid_link from Warehouse c where c.stockid_link =:orgfrom_code")
	public List<Object[]> invcheck_epc(@Param ("orgfrom_code")final Long orgfrom_code);
	//public List<Object[]> invcheck_epc(@Param ("bossid")final String bossid,@Param ("orgfrom_code")final String orgfrom_code,@Param ("productcode")final String productcode);
	
	@Query(value = "select a from Warehouse a "
			+ "inner join StockInD b on a.stockindid_link = b.id "
			+ "where b.stockinid_link =:stockid_link")
	List<Warehouse> inv_getby_stockinid_link(@Param ("stockid_link")final long stockid_link) ;
	
	@Modifying
	@Query(value = "delete from Warehouse c where stockid_link =:stockid_link and epc=:epc")
	public void deleteByEpc(@Param ("epc")final String epc,@Param ("stockid_link")final long stockid_link);
	
	@Query(value = "select a.spaceepc_link as spaceepc_link, count(a.id) as itemcount, sum(a.met) as itemtotal from Warehouse a "
			+ "where a.skuid_link = :skuid_link "
			+ "group by spaceepc_link")
	List<Object[]> getspaces_bysku(
			@Param ("skuid_link")final Long skuid_link) ;
	
	@Query(value = "select c from Warehouse c where epc =:epc")
	public List<Warehouse> findMaterialByEPC(
			@Param ("epc")final String epc);
	
	@Query(value = "select count(c.id) from Warehouse c "
			+ " where c.skuid_link = :skuid_link "
			+ " and c.stockid_link = :stockid_link "
			) 
	public Long getSumBy_Sku_Stock(
			@Param ("skuid_link")final Long skuid_link,
			@Param ("stockid_link")final Long stockid_link
			);
	@Query(value = "select count(c.id) from Warehouse c "
			+ " where c.skuid_link = :skuid_link "
			) 
	public Integer getSumBy_Sku(
			@Param ("skuid_link")final Long skuid_link
			);
	
	@Query(value = "select count(c.id) from Warehouse c "
			+ " inner join Org o on o.id = c.stockid_link " 
			+ " where c.skuid_link = :skuid_link "
			+ " and o.parentid_link = :orgparentid_link "
			) 
	public Integer getSumBy_Sku_orgParent(
			@Param ("skuid_link")final Long skuid_link,
			@Param ("orgparentid_link")final Long orgparentid_link
			);
	
}
