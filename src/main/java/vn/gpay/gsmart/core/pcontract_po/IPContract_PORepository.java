package vn.gpay.gsmart.core.pcontract_po;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IPContract_PORepository
		extends JpaRepository<PContract_PO, Long>, JpaSpecificationExecutor<PContract_PO> {
	@Query(value = "select c from PContract_PO c " + "where c.orgrootid_link = :orgrootid_link "
			+ "and c.parentpoid_link = null " + "and c.pcontractid_link = :pcontractid_link "
			+ "and (c.productid_link = :productid_link or 0 = :productid_link) "
			+ "and (:userid_link is null or c.merchandiserid_link = :userid_link) " + "and po_typeid_link = 10")
	public List<PContract_PO> getPOByContractProduct(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("productid_link") final Long productid_link,
			@Param("userid_link") final Long userid_link);

	@Query(value = "select c from PContract_PO c " + "where id = :pcontractpoid_link")
	public List<PContract_PO> getbyId(@Param("pcontractpoid_link") final Long pcontractpoid_link);

	@Query(value = "select c from PContract_PO c " + "where c.orgrootid_link = :orgrootid_link "
			+ "and c.parentpoid_link = null " + "and c.pcontractid_link = :pcontractid_link "
			+ "and (c.productid_link = :productid_link or 0 = :productid_link) "
			+ "and (:userid_link is null or c.merchandiserid_link = :userid_link) " + "and c.po_typeid_link in (0,1)")
	public List<PContract_PO> getPO_Chaogia(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("productid_link") final Long productid_link,
			@Param("userid_link") final Long userid_link);

	@Query(value = "select c from PContract_PO c " + "where c.orgrootid_link = :orgrootid_link "
			+ "and c.parentpoid_link = null " + "and c.pcontractid_link = :pcontractid_link "
			+ "and (c.productid_link = :productid_link or 0 = :productid_link) "
			+ "and (:userid_link is null or c.merchandiserid_link = :userid_link) "
			+ "and (c.po_typeid_link = 0 or c.po_typeid_link = 1 or c.po_typeid_link = 11)")
	public List<PContract_PO> getPO_Duyet(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("productid_link") final Long productid_link,
			@Param("userid_link") final Long userid_link);

	@Query(value = "select c from PContract_PO c " + "where c.pcontractid_link = :pcontractid_link "
			+ "and c.productid_link = :productid_link")
	public List<PContract_PO> getPOByContractAndProduct(@Param("pcontractid_link") final Long pcontractid_link,
			@Param("productid_link") final Long productid_link);

	@Query(value = "select c from PContract_PO c " + "where c.parentpoid_link = :pcontractpo_id_link")
	public List<PContract_PO> getby_parentid_link(@Param("pcontractpo_id_link") final Long pcontractpo_id_link);

	@Query(value = "select c from PContract_PO c " + "where c.parentpoid_link = :parentid_link "
			+ "and c.po_typeid_link =:po_typeid_link " + "order by shipdate asc")
	public List<PContract_PO> getby_parent_and_type(@Param("parentid_link") final Long parentid_link,
			@Param("po_typeid_link") final Integer po_typeid_link);

	@Query(value = "select c from PContract_PO c " 
			+ "left join PContractProductSKU a on c.id = a.pcontract_poid_link "
			+ "left join SKU_Attribute_Value b on b.skuid_link = a.skuid_link "
			+ "where c.parentpoid_link = :parentid_link " 
			+ "and c.po_typeid_link =:po_typeid_link "
			+ "and (b.attributevalueid_link = :mausanphamid_link or :mausanphamid_link is null) " 
			+ "group by c "
			+ "order by c.shipdate asc")
	public List<PContract_PO> getby_parent_and_type_and_mausp(@Param("parentid_link") final Long parentid_link,
			@Param("mausanphamid_link") final Long mausanphamid_link,
			@Param("po_typeid_link") final Integer po_typeid_link);
	
    @Query(value = "select c from PContract_PO c "
            + "left join PContractProductSKU a on c.id = a.pcontract_poid_link "
            + "left join SKU_Attribute_Value b on b.skuid_link = a.skuid_link "
            + "where c.parentpoid_link = :parentid_link "
            + "and c.po_typeid_link =:po_typeid_link "
            + "and (b.attributevalueid_link = :mausanphamid_link or :mausanphamid_link is null) "
            + "and (c.shipdate >= :shipdate_from and c.shipdate <= :shipdate_to)"
            + "group by c "
            + "order by c.shipdate asc")
    public List<PContract_PO> getby_parent_and_type_and_mausp_and_shipdate(
    		@Param("parentid_link") final Long parentid_link,
			@Param("mausanphamid_link") final Long mausanphamid_link,
			@Param("po_typeid_link") final Integer po_typeid_link,
			@Param("shipdate_from") final Date shipdate_from,
			@Param("shipdate_to") final Date shipdate_to
			);

	@Query(value = "select c from PContract_PO c "
			+ "where (c.shipdate >= :shipdate_from) "
			+ "and c.shipdate <= :shipdate_to "
			+ "and c.po_typeid_link = :po_typeid_link")
	public List<PContract_PO> getby_month_year(
			@Param("shipdate_from") final Date shipdate_from,
			@Param("shipdate_to") final Date shipdate_to,
			@Param("po_typeid_link") final Integer po_typeid_link
	);


	@Query(value = "select distinct c from PContract_PO c " 
			+ "inner join PContract_PO d on d.id = c.parentpoid_link "
			+ "where d.pcontractid_link = :pcontractid_link " 
			+ "and c.productid_link = :productid_link "
			+ "and c.po_typeid_link = 11 "
			+ "group by c "
			+ "order by c.parentpoid_link, c.shipdate asc")
	public List<PContract_PO> getPoLineByPcontract(
			@Param("pcontractid_link") final Long pcontractid_link,
			@Param("productid_link") final Long productid_link
			);
	
	@Query(value = "select a from PContract_PO a " 
			+ "inner join PContractProductSKU b on a.id = b.pcontract_poid_link "
			+ "where b.pcontractid_link = :pcontractid_link " 
			+ "and a.po_typeid_link in :type "
			+ "and (b.productid_link = :productid_link or a.productid_link = :productid_link) " 
			+ "group by a")
	public List<PContract_PO> getby_pcontract_and_type_and_product(@Param("type") final List<Integer> type,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("productid_link") final Long productid_link);
	
	@Query(value = "select distinct c from PContract_PO c " 
			+ "inner join PContract_PO d on d.id = c.parentpoid_link "
			+ "inner join PContractProductPairing e on e.pcontractid_link = c.pcontractid_link "
			+ "inner join ProductPairing f on f.productpairid_link = e.productpairid_link "
			+ "where d.pcontractid_link = :pcontractid_link " 
			+ "and f.productid_link = :productid_link "
			+ "and c.po_typeid_link = 11 "
			+ "group by c "
			+ "order by c.parentpoid_link, c.shipdate asc")
	public List<PContract_PO> getPoLineByPcontract_ProductInPair(
			@Param("pcontractid_link") final Long pcontractid_link,
			@Param("productid_link") final Long productid_link
			);
	
	@Query(value = "select SUM(c.po_quantity) from PContract_PO c " 
			+ "left join PContractProductSKU a on c.id = a.pcontract_poid_link "
			+ "left join SKU_Attribute_Value b on b.skuid_link = a.skuid_link "
			+ "where c.parentpoid_link = :parentid_link " 
			+ "and c.po_typeid_link =:po_typeid_link "
			+ "and (b.attributevalueid_link = :mausanphamid_link or :mausanphamid_link is null) " 
			+ "group by c "
			+ "order by c.shipdate asc"
			)
	public Integer getSumPoQuantity_by_parent_and_type_and_mausp(@Param("parentid_link") final Long parentid_link,
			@Param("mausanphamid_link") final Long mausanphamid_link,
			@Param("po_typeid_link") final Integer po_typeid_link);

	@Query(value = "select c from PContract_PO c " + "where c.orgrootid_link = :orgrootid_link "
			+ "and c.pcontractid_link = :pcontractid_link " + "and c.po_typeid_link < 10")
	public List<PContract_PO> getPOByContract(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final Long pcontractid_link);

	//Lấy các PO Line đã chốt, chưa có Lệnh xuất kho và có ngày giao hàng <= ngày giới hạn
	@Query(value = "select c from PContract_PO c " + "where c.orgrootid_link = :orgrootid_link "
			+ "and c.po_typeid_link = 11 and c.ismap = true " 
			+ "and (c.status is null or (c.status >= 0 and c.status < 2)) " 
			+ "and (c.shipdate >= :shipdate_from and c.shipdate <= :shipdate_to)")
	public List<PContract_PO> getPO_HavetoShip(
			@Param("orgrootid_link") final Long orgrootid_link,
			@Param("shipdate_from") final Date shipdate_from,
			@Param("shipdate_to") final Date shipdate_to
			);

	@Query(value = "select c from PContract_PO c " + "where c.pcontractid_link = :pcontractid_link "
			+ "and (:productid_link is null or c.productid_link = :productid_link) " + "and c.parentpoid_link != null "
			+ "and c.status > -3")
	public List<PContract_PO> getPOLeafOnlyByContract(@Param("pcontractid_link") final Long pcontractid_link,
			@Param("productid_link") final Long productid_link);

	@Query(value = "select c from PContract_PO c " + "where c.pcontractid_link = :pcontractid_link "
			+ "and (:productid_link is null or c.productid_link = :productid_link) " + "and c.parentpoid_link = null "
			+ "and c.status = 0 ")
	public List<PContract_PO> getPO_Offer_Accept_ByPContract(@Param("pcontractid_link") final Long pcontractid_link,
			@Param("productid_link") final Long productid_link);

	@Query(value = "select parent from PContract_PO parent "
			+ "inner join PContract_PO chil on parent.id = chil.parentpoid_link "
			+ "inner join POrder_Req a on parent.id = chil.parentpoid_link "
			+ "where parent.pcontractid_link = :pcontractid_link "
			+ "and (:productid_link is null or parent.productid_link = :productid_link) "
			+ "and parent.parentpoid_link is null " + "and parent.status = 0 "
			+ "and a.granttoorgid_link in :orgid_link " + "group by parent")
	public List<PContract_PO> getPO_Offer_Accept_ByPContract_AndOrg(
			@Param("pcontractid_link") final Long pcontractid_link, @Param("productid_link") final Long productid_link,
			@Param("orgid_link") final List<Long> orgid_link);

	@Query(value = "select c from PContract_PO c " + "where c.orgrootid_link = :orgrootid_link "
			+ "and c.pcontractid_link = :pcontractid_link " + "and c.productid_link = :productid_link "
			+ "and c.shipdate > :shipdate ")
	public List<PContract_PO> getPO_LaterShipdate(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("productid_link") final Long productid_link,
			@Param("shipdate") final Date shipdate);

	@Query(value = "select c from PContract_PO c " + "where c.po_buyer = :po_buyer "
			+ "and (c.shipmodeid_link = :shipmodeid_link or :shipmodeid_link is null) "
			+ "and c.productid_link = :productid_link " + "and c.shipdate = :shipdate "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<PContract_PO> getone_by_template_set(@Param("po_buyer") final String po_buyer,
			@Param("shipmodeid_link") final Long shipmodeid_link, @Param("productid_link") final Long productid_link,
			@Param("shipdate") final Date shipdate, @Param("pcontractid_link") final Long pcontractid_link);

	@Query(value = "select c from PContract_PO c " + "where c.shipmodeid_link = :shipmodeid_link "
			+ "and c.productid_link = :productid_link " + "and (c.shipdate = :shipdate) "
			+ "and c.pcontractid_link = :pcontractid_link " + "and (c.po_buyer = :po_buyer or :po_buyer is null)"
			+ "and c.po_typeid_link = 0")
	public List<PContract_PO> getone_by_template(@Param("shipmodeid_link") final Long shipmodeid_link,
			@Param("productid_link") final Long productid_link, @Param("shipdate") final Date shipdate,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("po_buyer") final String po_buyer);

	@Query(value = "select c from PContract_PO c " + "where c.productid_link = :productid_link "
			+ "and c.shipdate = :shipdate " + "and c.pcontractid_link = :pcontractid_link "
			+ "and c.po_typeid_link = 10 " + "and c.parentpoid_link = :parentpoid_link")
	public List<PContract_PO> getone_line_giaohang(@Param("productid_link") final Long productid_link,
			@Param("shipdate") final Date shipdate, @Param("pcontractid_link") final Long pcontractid_link,
			@Param("parentpoid_link") final Long parentpoid_link);

	@Query(value = "select c from PContract_PO c "
			+ "where trim(lower(replace(c.po_buyer,' ',''))) = trim(lower(replace(:po_buyer, ' ',''))) "
			+ "and (c.shipmodeid_link = :shipmodeid_link or :shipmodeid_link is null) " + "and c.shipdate = :shipdate "
			+ "and c.pcontractid_link = :pcontractid_link " + "and c.parentpoid_link = :parentid_link "
			+ "and c.po_typeid_link = 11")
	public List<PContract_PO> getone_po_upload(@Param("po_buyer") final String po_buyer,
			@Param("shipmodeid_link") final Long shipmodeid_link, @Param("shipdate") final Date shipdate,
			@Param("pcontractid_link") final Long pcontractid_link, @Param("parentid_link") final Long parentid_link);

	@Query(value = "select c from PContract_PO c "
			+ "where trim(lower(replace(c.po_buyer,' ',''))) = trim(lower(replace(:po_no, ' ',''))) "
			+ "and c.pcontractid_link = :pcontractid_link ")
	public List<PContract_PO> getone_po_byPO_no(@Param("po_no") final String po_no,
			@Param("pcontractid_link") final Long pcontractid_link);

	@Query(value = "select c from PContract_PO c " + "inner join Product b on b.id = c.productid_link "
			+ "where c.pcontractid_link = :pcontractid_link "
			+ "and lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) "
			+ "and lower(b.buyercode) like lower(concat('%',:buyercode,'%')) "
//			+ "and c.parentpoid_link != null "
	)
	public List<PContract_PO> getPcontractPoByPContractAndPOBuyer(
			@Param("pcontractid_link") final Long pcontractid_link, @Param("po_buyer") final String po_buyer,
			@Param("buyercode") final String buyercode);

	@Query(value = "select c from PContract_PO c " + "inner join POrder_Req b on b.pcontract_poid_link = c.id "
			+ "where lower(c.po_buyer) like lower(concat('%',:po_code,'%')) " + "and b.granttoorgid_link in :orgs ")
	public List<PContract_PO> getBySearch(@Param("po_code") final String po_code, @Param("orgs") final List<Long> orgs);

	@Query(value = "select c from PContract_PO c " + "inner join POrder_Req b on b.pcontract_poid_link = c.id "
			+ "where lower(c.po_buyer) like lower(concat('%',:po_code,'%')) ")
	public List<PContract_PO> getBySearch_ProductOnly(@Param("po_code") final String po_code);

	@Query(value = "select a.id from PContract a " + "left join PContract_PO c on a.id = c.pcontractid_link "
			+ "left join POrder_Req b on b.pcontract_poid_link = c.id "
			+ "where (lower(c.po_buyer) like lower(concat('%',:po_code,'%')) or :po_code ='') "
			+ "and (b.granttoorgid_link in :orgs or :orgs is null) " + "group by a.id")
	public List<Long> getPContractBySearch_OrgOnly(@Param("po_code") final String po_code,
			@Param("orgs") final List<Long> orgs);

	@Query(value = "select c from PContract_PO c " + "left join POrder_Req b on b.pcontract_poid_link = c.id "
			+ "where lower(c.po_buyer) like lower(concat('%',:po_code,'%')) " + "and b.granttoorgid_link in :orgs ")
	public List<PContract_PO> getBySearch_OrgOnly(@Param("po_code") final String po_code,
			@Param("orgs") final List<Long> orgs);

	@Query(value = "select c from PContract_PO c " + "inner join PContract_PO parent on c.parentpoid_link = parent.id "
			+ "inner join PContract_PO plan on plan.parentpoid_link = parent.id "
			+ "left join POrder_Req b on b.pcontract_poid_link = plan.id "
			+ "where lower(c.po_buyer) like lower(concat('%',:po_code,'%')) "
			+ "and (b.granttoorgid_link in :orgs or :orgs is null) " + "and c.po_typeid_link =:po_type " + "group by c")
	public List<PContract_PO> getBySearch_OrgAndType(@Param("po_code") final String po_code,
			@Param("orgs") final List<Long> orgs, @Param("po_type") final Integer po_type);

	@Query(value = "select c from PContract_PO c " + "inner join PContract_PO parent on c.parentpoid_link = parent.id "
			+ "inner join PContract_PO plan on plan.parentpoid_link = parent.id "
			+ "inner join POrder porder on porder.pcontract_poid_link = plan.id "
			+ "where (porder.granttoorgid_link in :orgs or :orgs is null) " + "and c.po_typeid_link =:po_type "
			+ "and c.shipdate >= :shipdate_from " + "and c.shipdate <= :shipdate_to "
			+ "and (c.ismap = :ismap or :ismap is null) " + "group by c " + "order by c.shipdate ASC ")
	public List<PContract_PO> getby_process_shipping(@Param("shipdate_from") final Date shipdate_from,
			@Param("shipdate_to") final Date shipdate_to, @Param("orgs") final List<Long> orgs,
			@Param("po_type") final Integer po_type, @Param("ismap") final Boolean ismap);

	@Query(value = "select c from PContract_PO c " + "left join POrder_Req b on b.pcontract_poid_link = c.id "
			+ "where (lower(c.po_buyer) like lower(concat('%',:po_code,'%')) or :po_code = '')")
	public List<PContract_PO> getBySearch_CodeOnly(@Param("po_code") final String po_code);

	@Query(value = "select distinct c from PContract_PO c " + "inner join PContractProductSKU a on c.id = a.pcontract_poid_link "
			+ "where (trim(lower(c.po_buyer)) = trim(lower(:po_code)) " + "and po_typeid_link = :type) "
			+ "and c.pcontractid_link = :pcontractid_link " + "and a.productid_link = :productid_link")
	public List<PContract_PO> getbycode_and_type_and_product(@Param("pcontractid_link") final Long pcontractid_link,
			@Param("po_code") final String po_code, @Param("type") final int type,
			@Param("productid_link") final Long productid_link);

	@Query(value = "select c from PContract_PO c " + "where po_typeid_link = :type "
			+ "and pcontractid_link = :pcontractid_link " + "and lower(c.po_buyer) not in :list_po_code")
	public List<PContract_PO> getnotin_list_pono(@Param("pcontractid_link") final Long pcontractid_link,
			@Param("list_po_code") final List<String> list_po_code, @Param("type") final int type);

	@Query(value = "select c from PContract_PO c " + "inner join POrderGrant_SKU b on b.pcontract_poid_link = c.id "
			+ "inner join POrderGrant a on a.id = b.pordergrantid_link " + "where a.porderid_link = :porderid_link "
			+ "group by c")
	public List<PContract_PO> getby_porder(@Param("porderid_link") final Long porderid_link);

	@Query(value = "select  sum(a.po_quantity), c.name " + "from PContract_PO a "
			+ "inner join PContract b on a.pcontractid_link = b.id "
			+ "full join MarketType c on b.marketypeid_link = c.id " + "where a.parentpoid_link is not null "
			+ "group by c.name " + "order by c.name ")
	public List<Object[]> getForMarketTypeChart();

	@Query(value = "select a from PContract_PO a " + "inner join PContract_PO c on a.id = c.parentpoid_link "
			+ "inner join POrder_Req b on c.id = b.pcontract_poid_link " + "where a.po_typeid_link = 10 "
			+ "and b.granttoorgid_link in :orgid_link " + "and a.parentpoid_link is null " + "and b.status = -1 "
			+ "group by a")
	public List<PContract_PO> getOffers_byOrg(@Param("orgid_link") final List<Long> orgid_link);

	@Query(value = "select a from PContract_PO a " + "where pcontractid_link = :pcontractid_link "
			+ "and po_typeid_link in :type")
	public List<PContract_PO> getby_pcontract_and_type(@Param("type") final List<Integer> type,
			@Param("pcontractid_link") final Long pcontractid_link);

	@Query(value = "select sum(po_quantity) from PContract_PO " + "where pcontractid_link = :pcontractid_link "
			+ "and productid_link = :productid_link and po_typeid_link = 10")
	public Integer getTotalProductinPcontract(@Param("pcontractid_link") final Long pcontractid_link,
			@Param("productid_link") final Long productid_link);

	@Query(value = "select distinct a.id from PContract_PO a " + "where a.pcontractid_link = :pcontractid_link "
			+ "and po_typeid_link in (0,1) and a.id not in (select b.parentpoid_link from PContract_PO b where b.po_typeid_link = 11 "
			+ "and b.pcontractid_link = :pcontractid_link) and a.status = 0")
	public List<Long> getPOConfimNotLine(@Param("pcontractid_link") final Long pcontractid_link);

	@Query(value = "select distinct a.id from PContract_PO a " + "where a.pcontractid_link = :pcontractid_link "
			+ "and po_typeid_link = 11 and (ismap = false or ismap is null) ")
	public List<Long> getPOLineNotMaps(@Param("pcontractid_link") final Long pcontractid_link);

	@Query(value = "select c from PContract_PO c " + "where c.parentpoid_link = :parentid_link "
			+ "and c.po_typeid_link =:po_typeid_link and (ismap = false or ismap is null) " + "order by shipdate asc")
	public List<PContract_PO> getby_parent_and_type_notmap(@Param("parentid_link") final Long parentid_link,
			@Param("po_typeid_link") final Integer po_typeid_link);

	@Query(value = "select c from PContract_PO c " + "inner join PContract_PO parent on c.parentpoid_link = parent.id "
			+ "inner join PContract_PO plan on plan.parentpoid_link = parent.id "
			+ "inner join PContractProductSKU a on a.pcontract_poid_link = c.id "
			+ "where c.po_typeid_link = 11 and plan.po_typeid_link = 10 "
			+ "and ((a.skuid_link in :listsku and (a.ismap = 'false' or a.ismap is null)) or :listsku is null) "
			+ "and (c.productid_link = :productid_link or a.productid_link = :productid_link) group by c "
			+ "order by c.shipdate ASC ")
	public List<PContract_PO> getby_product_color_sizeset(@Param("productid_link") final Long productid_link, @Param("listsku") final List<Long> listsku);
	
	@Query(value = "select distinct c from PContract_PO c " 
			+ "inner join StockinPoline d on d.pcontract_poid_link = c.id "
			+ "where d.stockinid_link = :stockinid_link "
			+ "order by c.id ASC ")
	public List<PContract_PO> getByStockin(
			@Param("stockinid_link") final Long stockinid_link
			);
	
	@Query(value = "select distinct c.id from PContract_PO c " 
			+ "inner join StockinPoline d on d.pcontract_poid_link = c.id "
			+ "where d.stockinid_link = :stockinid_link "
			+ "order by c.id ASC ")
	public List<Long> getPoLineIdByStockin(
			@Param("stockinid_link") final Long stockinid_link
			);
	
	@Query(value = "select c from PContract_PO c " 
			+ "inner join Product d on d.id = c.productid_link "
			+ "inner join PContract e on e.id = c.pcontractid_link "
			+ "where ( lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) or :po_buyer is null ) "
			+ "and ( lower(d.buyercode) like lower(concat('%',:productbuyercode,'%')) or :productbuyercode is null ) "
			+ "and ( lower(e.contractcode) like lower(concat('%',:pcontractcode,'%')) or :pcontractcode is null ) "
//			+ "where :po_buyer = :po_buyer "
//			+ "and :productbuyercode = :productbuyercode "
//			+ "and :pcontractcode = :pcontractcode "
			+ "and ( c.id not in :poLineIdList or :poLineIdList is null ) "
			+ "and c.po_typeid_link = 11 "
			+ "and (c.shipdate >= :shipdateFrom or date(:shipdateFrom) is null) "
			+ "and (c.shipdate <= :shipdateTo or date(:shipdateTo) is null) "
			+ "order by e.id asc, c.parentpoid_link asc, c.shipdate asc, c.id asc ")
	public List<PContract_PO> getBySearch_POLine_Stockin(
			@Param("po_buyer") final String po_buyer,
			@Param("productbuyercode") final String productbuyercode,
			@Param("pcontractcode") final String pcontractcode,
			@Param("shipdateFrom") final Date shipdateFrom,
			@Param("shipdateTo") final Date shipdateTo,
			@Param("poLineIdList") final List<Long> poLineIdList
			);
	
	@Query(value = "select distinct c from PContract_PO c " 
			+ "inner join StockoutPoline d on d.pcontract_poid_link = c.id "
			+ "where d.stockoutid_link = :stockoutid_link "
			+ "order by c.id ASC ")
	public List<PContract_PO> getByStockout(
			@Param("stockoutid_link") final Long stockoutid_link
			);
	
	@Query(value = "select distinct c.id from PContract_PO c " 
			+ "inner join StockoutPoline d on d.pcontract_poid_link = c.id "
			+ "where d.stockoutid_link = :stockoutid_link "
			+ "order by c.id ASC ")
	public List<Long> getPoLineIdByStockout(
			@Param("stockoutid_link") final Long stockoutid_link
			);
	
	@Query(value = "select c from PContract_PO c " 
			+ "inner join Product d on d.id = c.productid_link "
			+ "inner join PContract e on e.id = c.pcontractid_link "
			+ "where ( lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) or :po_buyer is null ) "
			+ "and ( lower(d.buyercode) like lower(concat('%',:productbuyercode,'%')) or :productbuyercode is null ) "
			+ "and ( lower(e.contractcode) like lower(concat('%',:pcontractcode,'%')) or :pcontractcode is null ) "
//			+ "where :po_buyer = :po_buyer "
//			+ "and :productbuyercode = :productbuyercode "
//			+ "and :pcontractcode = :pcontractcode "
			+ "and ( c.id not in :poLineIdList or :poLineIdList is null ) "
			+ "and c.po_typeid_link = 11 "
			+ "and (c.shipdate >= :shipdateFrom or date(:shipdateFrom) is null) "
			+ "and (c.shipdate <= :shipdateTo or date(:shipdateTo) is null) "
			+ "order by e.id asc, c.parentpoid_link asc, c.shipdate asc, c.id asc ")
	public List<PContract_PO> getBySearch_POLine_Stockout(
			@Param("po_buyer") final String po_buyer,
			@Param("productbuyercode") final String productbuyercode,
			@Param("pcontractcode") final String pcontractcode,
			@Param("shipdateFrom") final Date shipdateFrom,
			@Param("shipdateTo") final Date shipdateTo,
			@Param("poLineIdList") final List<Long> poLineIdList
			);
	// po_buyer, productbuyercode, pcontractcode, shipdateFrom, shipdateTo, poLineIdList
	
	@Query(value = "select distinct c.productid_link from PContract_PO c " 
			+ " inner join Product d on d.id = c.productid_link "
			+ " inner join PContract e on e.id = c.pcontractid_link "
			+ " where ( lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) or :po_buyer is null ) "
			+ " and ( lower(d.buyercode) like lower(concat('%',:productbuyercode,'%')) or :productbuyercode is null ) "
			+ " and ( lower(e.contractcode) like lower(concat('%',:contractcode,'%')) or :contractcode is null ) "
			+ " and c.po_typeid_link = 11 "
			+ " and (c.shipdate >= :shipdateFrom or date(:shipdateFrom) is null) "
			+ " and (c.shipdate <= :shipdateTo or date(:shipdateTo) is null) "
			+ " and (e.orgbuyerid_link = :orgbuyerid_link or :orgbuyerid_link is null) "
			+ " and (e.orgvendorid_link = :orgvendorid_link or :orgvendorid_link is null) "
			+ " and (c.orggrantid_link is null or c.orggrantid_link = :orgid_link or :orgid_link is null) "
//			+ "order by e.id asc, c.parentpoid_link asc, c.shipdate asc, c.id asc "
			)
	public List<Long> getMaHangCanGiao(
			@Param("orgid_link") final Long orgid_link,
			@Param("shipdateFrom") final Date shipdateFrom,
			@Param("shipdateTo") final Date shipdateTo,
			@Param("contractcode") final String contractcode,
			@Param("productbuyercode") final String productbuyercode,
			@Param("po_buyer") final String po_buyer,
			@Param("orgbuyerid_link") final Long buyer,
			@Param("orgvendorid_link") final Long vendor
			);
	
	@Query(value = "select distinct c from PContract_PO c " 
//			+ " inner join Product d on d.id = c.productid_link "
			+ " inner join PContract e on e.id = c.pcontractid_link "
			+ " where ( lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) or :po_buyer is null ) "
//			+ " and ( lower(d.buyercode) like lower(concat('%',:productbuyercode,'%')) or :productbuyercode is null ) "
			+ " and (c.productid_link = :productid_link or :productid_link is null) "
			+ " and ( lower(e.contractcode) like lower(concat('%',:contractcode,'%')) or :contractcode is null ) "
			+ " and c.po_typeid_link = 11 "
			+ " and (c.shipdate >= :shipdateFrom or date(:shipdateFrom) is null) "
			+ " and (c.shipdate <= :shipdateTo or date(:shipdateTo) is null) "
			+ " and (e.orgbuyerid_link = :orgbuyerid_link or :orgbuyerid_link is null) "
			+ " and (e.orgvendorid_link = :orgvendorid_link or :orgvendorid_link is null) "
			+ " and (c.orggrantid_link is null or c.orggrantid_link = :orgid_link or :orgid_link is null) "
			+ " and (c.status < :status or :status is null) "
			+ " order by c.id "
			)
	public List<PContract_PO> getPOLineChuaGiao(
			@Param("orgid_link") final Long orgid_link,
			@Param("shipdateFrom") final Date shipdateFrom,
			@Param("shipdateTo") final Date shipdateTo,
			@Param("contractcode") final String contractcode,
			@Param("productid_link") final Long productid_link,
			@Param("po_buyer") final String po_buyer,
			@Param("orgbuyerid_link") final Long buyer,
			@Param("orgvendorid_link") final Long vendor,
			@Param("status") final Integer status
			);
	
	@Query(value = "select distinct c from PContract_PO c " 
			+ " inner join PContract e on e.id = c.pcontractid_link "
			+ " where ( lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) or :po_buyer is null ) "
			+ " and (c.productid_link = :productid_link or :productid_link is null) "
			+ " and ( lower(e.contractcode) like lower(concat('%',:contractcode,'%')) or :contractcode is null ) "
			+ " and c.po_typeid_link = 11 "
//			+ " and (c.shipdate >= :shipdateFrom or date(:shipdateFrom) is null) "
			+ " and (c.shipdate < :today or date(:today) is null) "
			+ " and (e.orgbuyerid_link = :orgbuyerid_link or :orgbuyerid_link is null) "
			+ " and (e.orgvendorid_link = :orgvendorid_link or :orgvendorid_link is null) "
			+ " and (c.orggrantid_link is null or c.orggrantid_link = :orgid_link or :orgid_link is null) "
			+ " and (c.status < :status or :status is null) "
			+ " order by c.id "
			)
	public List<PContract_PO> getPOLineChuaGiao_Cham(
			@Param("orgid_link") final Long orgid_link,
			@Param("today") final Date today,
			@Param("contractcode") final String contractcode,
			@Param("productid_link") final Long productid_link,
			@Param("po_buyer") final String po_buyer,
			@Param("orgbuyerid_link") final Long buyer,
			@Param("orgvendorid_link") final Long vendor,
			@Param("status") final Integer status
			);
	
	@Query(value = "select distinct c from PContract_PO c " 
			+ " inner join Product d on d.id = c.productid_link "
			+ " inner join PContract e on e.id = c.pcontractid_link "
			+ " where ( lower(c.po_buyer) like lower(concat('%',:po_buyer,'%')) or :po_buyer is null ) "
			+ " and ( lower(d.buyercode) like lower(concat('%',:productbuyercode,'%')) or :productbuyercode is null ) "
			+ " and ( lower(e.contractcode) like lower(concat('%',:contractcode,'%')) or :contractcode is null ) "
			+ " and c.po_typeid_link = 11 "
			+ " and (c.shipdate >= :shipdateFrom or date(:shipdateFrom) is null) "
			+ " and (c.shipdate <= :shipdateTo or date(:shipdateTo) is null) "
			+ " and (e.orgbuyerid_link = :orgbuyerid_link or :orgbuyerid_link is null) "
			+ " and (e.orgvendorid_link = :orgvendorid_link or :orgvendorid_link is null) "
			+ " and (c.orggrantid_link is null or c.orggrantid_link = :orgid_link or :orgid_link is null) "
//			+ "order by e.id asc, c.parentpoid_link asc, c.shipdate asc, c.id asc "
			)
	public List<PContract_PO> getDsPoLineCanGiao(
			@Param("orgid_link") final Long orgid_link,
			@Param("shipdateFrom") final Date shipdateFrom,
			@Param("shipdateTo") final Date shipdateTo,
			@Param("contractcode") final String contractcode,
			@Param("productbuyercode") final String productbuyercode,
			@Param("po_buyer") final String po_buyer,
			@Param("orgbuyerid_link") final Long buyer,
			@Param("orgvendorid_link") final Long vendor
			);
	
	@Query(value = "select distinct c from PContract_PO c " 
			+ "inner join POrderGrant_SKU b on b.pcontract_poid_link = c.id "
//			+ "inner join POrderGrant a on a.id = b.pordergrantid_link " 
			+ "where b.pordergrantid_link = :pordergrantid_link "
			+ "order by c.shipdate asc "
			)
	public List<PContract_PO> getbyPOrderGrant(
			@Param("pordergrantid_link") final Long pordergrantid_link
			);
}
