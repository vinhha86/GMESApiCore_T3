package vn.gpay.gsmart.core.pcontract_bomhq_npl_poline_sku;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IPContract_bomHQ_npl_poline_sku_repo extends JpaRepository<PContract_bomHQ_npl_poline_sku, Long>, JpaSpecificationExecutor<PContract_bomHQ_npl_poline_sku> {
	@Query(value = "select c from PContract_bomHQ_npl_poline_sku c "
			+ "where pcontract_poid_link = :pcontract_poid_link "
			+ "and product_skuid_link = :product_skuid_link "
			+ "and orgrootid_link = :orgrootid_link "
			+ "and material_skuid_link = :material_skuid_link"
			)
	public List<PContract_bomHQ_npl_poline_sku> getby_po_and_sku(
			@Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("product_skuid_link")final Long product_skuid_link,
			@Param ("orgrootid_link")final Long orgrootid_link,
			@Param ("material_skuid_link")final Long material_skuid_link);
	
	@Query(value = "select c from PContract_bomHQ_npl_poline_sku c "
			+ "where pcontract_poid_link = :pcontract_poid_link "
			+ "and orgrootid_link = :orgrootid_link "
			+ "and material_skuid_link = :material_skuid_link "
			+ "and (productid_link = :productid_link or :productid_link is null)"
			)
	public List<PContract_bomHQ_npl_poline_sku> getby_po(
			@Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("orgrootid_link")final Long orgrootid_link,
			@Param ("material_skuid_link")final Long material_skuid_link,
			@Param ("productid_link")final Long productid_link);
	
	@Query(value = "select c from PContract_bomHQ_npl_poline_sku c "
			+ "where pcontract_poid_link = :pcontract_poid_link "
			+ "and orgrootid_link = :orgrootid_link "
			+ "and material_skuid_link = :material_skuid_link "
			+ "and (productid_link = :productid_link or :productid_link is null) "
			+ "and product_skuid_link = :product_skuid_link"
			)
	public List<PContract_bomHQ_npl_poline_sku> getone(
			@Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("orgrootid_link")final Long orgrootid_link,
			@Param ("material_skuid_link")final Long material_skuid_link,
			@Param ("productid_link")final Long productid_link,
			@Param ("product_skuid_link")final Long product_skuid_link);
	
	@Query(value = "select c from PContract_bomHQ_npl_poline_sku c "
			+ "where c.pcontractid_link = :pcontractid_link"
			)
	public List<PContract_bomHQ_npl_poline_sku> getby_pcontract(
			@Param ("pcontractid_link")final Long pcontractid_link);
	
	@Query(value = "select c from PContract_bomHQ_npl_poline_sku c "
			+ "where (c.orgrootid_link = :orgrootid_link  or :orgrootid_link is null) "
			+ "and (c.pcontractid_link = :pcontractid_link  or :pcontractid_link is null) "
			+ "and (c.productid_link = :productid_link  or :productid_link is null) "
			+ "and (c.pcontract_poid_link = :pcontract_poid_link or :pcontract_poid_link is null) "
			+ "and (c.material_skuid_link = :material_skuid_link  or :material_skuid_link is null) "
			)
	public List<PContract_bomHQ_npl_poline_sku> getby_Pcontract_Product_Material_skuid_link_PcontractPo(
			@Param ("orgrootid_link")final Long orgrootid_link,
			@Param ("pcontractid_link")final Long pcontractid_link,
			@Param ("productid_link")final Long productid_link,
			@Param ("pcontract_poid_link")final Long pcontract_poid_link,
			@Param ("material_skuid_link")final Long material_skuid_link);

}
