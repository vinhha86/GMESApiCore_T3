package vn.gpay.gsmart.core.pcontractproductsku;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.gpay.gsmart.core.attributevalue.Attributevalue;

@Repository
@Transactional
public interface IPContractProductSKURepository extends JpaRepository<PContractProductSKU, Long> {
	@Query(value = "select a from PContractProductSKU a "
			+ "inner join SKU_Attribute_Value c on a.skuid_link = c.skuid_link "
			+ "inner join Attributevalue b on b.id = c.attributevalueid_link "
			+ "where a.orgrootid_link = :orgrootid_link " + "and productid_link = :productid_link "
			+ "and pcontractid_link = :pcontractid_link " + "group by a, b.sortvalue " + "order by b.sortvalue")
	public List<PContractProductSKU> getlistsku_byproduct_and_pcontract(
			@Param("orgrootid_link") final Long orgrootid_link, @Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select c from PContractProductSKU c " + "inner join PContract_PO a on a.id = c.pcontract_poid_link "
			+ "where c.orgrootid_link = :orgrootid_link "
			+ "and c.pcontractid_link = :pcontractid_link and a.pcontractid_link = :pcontractid_link")
	public List<PContractProductSKU> getlistsku_bypcontract(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	//Hung Dai Bang
	@Query(value = "select c from PContractProductSKU c "
			+ "where c.orgrootid_link = :orgrootid_link "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<PContractProductSKU> getlistsku_bypcontract_nolink(@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select c.productid_link as productid_link, c.skuid_link as skuid_link, "
			+ "sum(c.pquantity_porder) as pquantity_porder, "
			+ "sum(c.pquantity_sample) as pquantity_sample, "
			+ "sum(c.pquantity_granted) as pquantity_granted, "
			+ "sum(c.pquantity_total) as pquantity_total "
			+ "from PContractProductSKU c "
			+ "where pcontractid_link = :pcontractid_link and c.productid_link in :ls_productid " 
			+ "group by c.productid_link, c.skuid_link order by c.productid_link")
	public List<Object[]> getsumsku_bypcontract(
			@Param("pcontractid_link") final long pcontractid_link,
			@Param("ls_productid") List<Long> ls_productid
			);

	@Query(value = "select c from PContractProductSKU c " + "where c.pcontract_poid_link = :pcontract_poid_link "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<PContractProductSKU> getlistsku_bypo_and_pcontract(
			@Param("pcontract_poid_link") final long pcontract_poid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select c from PContractProductSKU c " + "where c.pcontract_poid_link = :pcontract_poid_link")
	public List<PContractProductSKU> getlistsku_bypo(@Param("pcontract_poid_link") final long pcontract_poid_link);

	@Query(value = "select c.productid_link, c.skuid_link, " + "sum(c.pquantity_total) as pquantity_total, "
			+ "sum(c.pquantity_production) as pquantity_production, " + "sum(c.pquantity_sample) as pquantity_sample "
			+ "from PContractProductSKU c " + "inner join PContract_PO a on a.id = c.pcontract_poid_link "
			+ "where a.parentpoid_link = :pcontract_poid_link " + "group by c.productid_link, c.skuid_link")
	public List<Object[]> getsumsku_bypo_parent(@Param("pcontract_poid_link") final Long pcontract_poid_link);

	@Query(value = "select c.productid_link, c.skuid_link, " + "sum(c.pquantity_total) as pquantity_total, "
			+ "(select sum(b.pquantity_total) " + "from POrder_Product_SKU b "
			+ "inner join POrder d on d.id= b.porderid_link "
			+ "inner join PContract_PO e on e.id = d.pcontract_poid_link " + "where b.productid_link = :productid_link "
			+ "and b.skuid_link = c.skuid_link "
			+ "and e.parentpoid_link = :pcontract_poid_link) as pquantity_granted, "
			+ "sum(c.pquantity_production) as pquantity_production, " + "sum(c.pquantity_sample) as pquantity_sample "
			+ "from PContractProductSKU c " + "inner join PContract_PO a on a.id = c.pcontract_poid_link "
			+ "where a.parentpoid_link = :pcontract_poid_link and c.productid_link = :productid_link "
			+ "group by c.productid_link, c.skuid_link")
	public List<Object[]> getsumsku_bypo_parent_and_product(
			@Param("pcontract_poid_link") final Long pcontract_poid_link,
			@Param("productid_link") final Long productid_link);

	@Query(value = "select c from PContractProductSKU c " + "where c.pcontract_poid_link = :pcontract_poid_link "
			+ "and c.productid_link = :productid_link")
	public List<PContractProductSKU> getlistsku_bypo_and_product(
			@Param("pcontract_poid_link") final long pcontract_poid_link,
			@Param("productid_link") final long productid_link);
	
	@Query(value = "select c from PContractProductSKU c " 
			+ "where c.pcontract_poid_link = :pcontract_poid_link "
			+ "and :productid_link = :productid_link "
			+ "and c.productid_link in :product_ids"
			)
	public List<PContractProductSKU> getlistsku_bypo_and_product(
			@Param("pcontract_poid_link") final long pcontract_poid_link,
			@Param("productid_link") final long productid_link,
			@Param("product_ids") final List<Long> product_ids
			);

	@Query(value = "select c from PContractProductSKU c " + "where c.skuid_link = :skuid_link "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<PContractProductSKU> getlistsku_bysku_and_pcontract(@Param("skuid_link") final long skuid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select c from PContractProductSKU c " + "where c.skuid_link = :skuid_link "
			+ "and c.productid_link = :productid_link " + "and c.pcontract_poid_link = :pcontract_poid_link")
	public List<PContractProductSKU> getlistsku_bysku_and_product_PO(@Param("skuid_link") final long skuid_link,
			@Param("productid_link") final long productid_link,
			@Param("pcontract_poid_link") final long pcontract_poid_link);

	@Query(value = "select b.id from SKU_Attribute_Value a "
			+ "inner join PContractProductSKU c on a.skuid_link = c.skuid_link "
			+ "inner join Attributevalue b on b.id = a.attributevalueid_link "
			+ "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link and a.attributeid_link= :attributeid_link "
			+ "group by b.id, b.sortvalue " + "order by b.sortvalue asc")
	public List<Long> getvaluesize_in_product(@Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link,
			@Param("attributeid_link") final long attributeid_link);

	@Query(value = "select b.value from SKU_Attribute_Value a "
			+ "inner join PContractProductSKU c on a.skuid_link = c.skuid_link "
			+ "inner join Attributevalue b on b.id = a.attributevalueid_link "
			+ "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link and a.attributeid_link= :attributeid_link "
			+ "group by b.value, b.sortvalue " + "order by b.sortvalue asc")
	public List<String> getvaluename_in_product(@Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link,
			@Param("attributeid_link") final long attributeid_link);

	@Query(value = "select a.skuid_link " + "from SKU_Attribute_Value a "
			+ "inner join PContractProductSKU c on a.skuid_link = c.skuid_link "
			+ "where c.productid_link = :productid_link " + "and c.pcontractid_link = :pcontractid_link "
			+ "and (attributevalueid_link= :colorid_link or :colorid_link is null) " + "group by a.skuid_link")
	public List<Long> getskuid_bycolorid_link(@Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link, @Param("colorid_link") final Long colorid_link);

	@Query(value = "select a.skuid_link " + "from SKU_Attribute_Value a "
			+ "inner join PContractProductSKU c on a.skuid_link = c.skuid_link "
			+ "where c.productid_link = :productid_link " + "and c.pcontractid_link = :pcontractid_link "
			+ "group by a.skuid_link")
	public List<Long> getskuid_byproduct_and_pcontract(@Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select c from SKU_Attribute_Value a "
			+ "inner join PContractProductSKU c on a.skuid_link = c.skuid_link "
			+ "where c.productid_link = :productid_link " + "and c.pcontractid_link = :pcontractid_link and "
			+ "(attributevalueid_link= :colorid_link or :colorid_link is null) " + "group by c")
	public List<PContractProductSKU> getPContractProductSKU_bycolorid_link(
			@Param("productid_link") final long productid_link, @Param("pcontractid_link") final long pcontractid_link,
			@Param("colorid_link") final Long colorid_link);

	@Query(value = "select c from PContractProductSKU c " + "where c.skuid_link = :skuid_link "
			+ "and c.pcontract_poid_link = :pcontract_poid_link ")
	public List<PContractProductSKU> getBySkuAndPcontractPo(@Param("skuid_link") final long skuid_link,
			@Param("pcontract_poid_link") final long pcontract_poid_link);

	@Query(value = "select e.name " + "from PContractProductSKU c "
			+ "inner join SKU_Attribute_Value a on a.skuid_link = c.skuid_link "
			+ "inner join SizeSetAttributeValue d on d.attributevalueid_link = a.attributevalueid_link "
			+ "inner join SizeSet e on e.id = d.sizesetid_link " + "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link " + "group by e.name, e.sortvalue " + "order by e.sortvalue")
	public List<String> getlist_sizeset_by_product(@Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select d " + "from PContractProductSKU c "
			+ "inner join SKU_Attribute_Value a on a.skuid_link = c.skuid_link "
			+ "inner join Attributevalue d on d.id = a.attributevalueid_link " + "where d.attributeid_link = 4 "
			+ "and c.pcontractid_link = :pcontractid_link " + "group by d " + "order by d.sortvalue")
	public List<Attributevalue> getmausanpham_by_pcontract(@Param("pcontractid_link") final long pcontractid_link);

	@Query(value = "select d.attributevalueid_link " + "from PContractProductSKU c "
			+ "inner join SKU_Attribute_Value a on a.skuid_link = c.skuid_link "
			+ "inner join SizeSetAttributeValue d on d.attributevalueid_link = a.attributevalueid_link "
			+ "inner join Attributevalue f on f.id = d.attributevalueid_link "
			+ "inner join SizeSet e on e.id = d.sizesetid_link " + "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link " + "and e.id = :sizesetid_link "
			+ "group by d.attributevalueid_link, f.sortvalue " + "order by f.sortvalue ")
	public List<Long> getlist_size_by_product_and_sizeset(@Param("productid_link") final long productid_link,
			@Param("pcontractid_link") final long pcontractid_link, @Param("sizesetid_link") final long sizesetid_link);

	@Query(value = "select c from PContractProductSKU c " + "where pcontract_poid_link = :pcontract_poid_link "
			+ "and (ismap = false or ismap is null )")
	public List<PContractProductSKU> getsku_notmap(@Param("pcontract_poid_link") final long pcontract_poid_link);

	@Query(value = "select c from PContractProductSKU c " + "where pcontract_poid_link = :pcontract_poid_link "
			+ "and (ismap = false or ismap is null ) and :productid_link = productid_link")
	public List<PContractProductSKU> getsku_notmap_by_product(
			@Param("pcontract_poid_link") final long pcontract_poid_link,
			@Param("productid_link") final long productid_link);
	
	@Query(value = "select distinct c from PContractProductSKU c " 
			+ " inner join SKU d on c.productid_link = d.productid_link "
			+ " inner join SKU_Attribute_Value e on d.id = e.skuid_link "
			+ " where c.pcontract_poid_link = :pcontract_poid_link "
			+ " and c.productid_link = :productid_link "
			+ " and ((e.attributeid_link = 4 and e.attributevalueid_link = :colorId) or :colorId is null) "
			+ " and ((e.attributeid_link = 30 and e.attributevalueid_link = :sizeId) or :sizeId is null) "
			)
	public List<PContractProductSKU> getByPoLine_product_size_color(
			@Param("pcontract_poid_link") final Long pcontract_poid_link,
			@Param("productid_link") final Long productid_link,
			@Param("sizeId") final Long sizeId,
			@Param("colorId") final Long colorId
			);
	
	@Query(value = "select distinct c from PContractProductSKU c " 
			+ " where c.pcontract_poid_link = :pcontract_poid_link "
			+ " and c.productid_link = :productid_link "
			)
	public List<PContractProductSKU> getByPoLine_product(
			@Param("pcontract_poid_link") final Long pcontract_poid_link,
			@Param("productid_link") final Long productid_link
			);

	/*
	 * Written by HungDaiBang
	 */
	@Query(value = "select c from PContractProductSKU c "
			+ "where c.orgrootid_link = :orgrootid_link "
			+ "and c.pcontractid_link = :pcontractid_link "
			+ "and (null = :productidlist or c.productid_link in :productidlist) "
			+ "and (null = :polist or c.pcontract_poid_link in :polist)")
	public List<PContractProductSKU> getlistsku_bylist_prodandpo(
			@Param("orgrootid_link") final Long orgrootid_link,
			@Param("pcontractid_link") final long pcontractid_link,
			@Param("productidlist") List<Long> productidlist,
			@Param("polist") List<Long> polist
			);
}
