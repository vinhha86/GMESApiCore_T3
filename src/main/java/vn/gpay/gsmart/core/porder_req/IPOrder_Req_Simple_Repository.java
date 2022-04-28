package vn.gpay.gsmart.core.porder_req;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface IPOrder_Req_Simple_Repository extends JpaRepository<POrder_Req_Simple, Long>, JpaSpecificationExecutor<POrder_Req_Simple> {
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontractid_link = :pcontractid_link")
	public List<POrder_Req_Simple> getByContract(@Param ("pcontractid_link")final Long pcontractid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontractid_link = :pcontractid_link and c.productid_link = :productid_link")
	public List<POrder_Req_Simple> getByContractAndProduct(@Param ("pcontractid_link")final Long pcontractid_link, @Param ("productid_link")final Long productid_link);

	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontract_poid_link = :pcontract_poid_link and c.productid_link = :productid_link")
	public List<POrder_Req_Simple> getByPOAndProduct(@Param ("pcontract_poid_link")final Long pcontract_poid_link, @Param ("productid_link")final Long productid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c "
			+ "inner join PContract_PO a on c.pcontract_poid_link = a.id "
			+ "where a.parentpoid_link = :pcontract_poid_link "
			+ "and c.productid_link = :productid_link "
			+ "and c.status = 0 "
			+ "and c.granttoorgid_link = :orgid_link "
			+ "and a.po_typeid_link = 10"
			+ "order by a.shipdate asc")
	public List<POrder_Req_Simple> getByOfferAndProduct(
			@Param ("pcontract_poid_link")final Long pcontract_poid_link, 
			@Param ("productid_link")final Long productid_link,
			@Param ("orgid_link")final Long orgid_link);

	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontractid_link = :pcontractid_link and c.pcontract_poid_link = :pcontract_poid_link")
	public List<POrder_Req_Simple> getByContractAndPO(@Param ("pcontractid_link")final Long pcontractid_link, @Param ("pcontract_poid_link")final Long pcontract_poid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontractid_link = :pcontractid_link and c.pcontract_poid_link = :pcontract_poid_link "
			+ "and c.granttoorgid_link = :granttoorgid_link "
			+ "and c.productid_link = :productid_link")
	public List<POrder_Req_Simple> getByContractAndPO_and_org(
			@Param ("pcontractid_link")final Long pcontractid_link, 
			@Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("granttoorgid_link")final Long granttoorgid_link,
			@Param ("productid_link")final Long productid_link);

	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontract_poid_link = :pcontract_poid_link")
	public List<POrder_Req_Simple> getByPO( @Param ("pcontract_poid_link")final Long pcontract_poid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c "
			+ "inner join PContract_PO a on c.pcontract_poid_link = a.id "
			+ "where a.parentpoid_link = :pcontract_poid_link")
	public List<POrder_Req_Simple> getByPO_Offer( @Param ("pcontract_poid_link")final Long pcontract_poid_link);
		
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontract_poid_link = :pcontract_poid_link "
			+ "and (c.granttoorgid_link = :orgid_link or 1 = :orgid_link) ")
	public List<POrder_Req_Simple> getByPO_and_org( @Param ("pcontract_poid_link")final Long pcontract_poid_link,
			 @Param ("orgid_link")final Long orgid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontract_poid_link = :pcontract_poid_link and c.productid_link = :productid_link and c.granttoorgid_link = :granttoorgid_link")
	public List<POrder_Req_Simple> getByOrg_PO_Product( @Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("productid_link")final Long productid_link,
			@Param ("granttoorgid_link")final Long granttoorgid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontract_poid_link = :pcontract_poid_link and is_calculate = 'False'")
	public List<POrder_Req_Simple> getByPO_calculate( @Param ("pcontract_poid_link")final Long pcontract_poid_link);
	
	@Query("SELECT c FROM POrder_Req_Simple c where c.pcontract_poid_link = :pcontract_poid_link and c.granttoorgid_link = :orgid_link")
	public List<POrder_Req_Simple> getByPO_AndOrg( 
			@Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("orgid_link")final Long orgid_link);

	@Query("SELECT c FROM POrder_Req_Simple c where c.status = :status")
	public List<POrder_Req_Simple> getByStatus(@Param ("status")final Integer status);
	
	@Query("SELECT c FROM POrder_Req_Simple c "
			+ "inner join PContract_PO a on c.pcontract_poid_link = a.id "
			+ "where c.granttoorgid_link = :orgid_link "
			+ "and a.status <= -1 "
			+ "and a.shipdate = "
			+ "(select min(b.shipdate) from POrder_Req d "
			+ "inner join PContract_PO b on d.pcontract_poid_link = b.id "
			+ "where b.parentpoid_link = a.parentpoid_link "
			+ "and d.granttoorgid_link = :orgid_link) "
			+ "order by a.shipdate asc")
	public List<POrder_Req_Simple> getByOrg(@Param ("orgid_link")final Long orgid_link);
	
}
