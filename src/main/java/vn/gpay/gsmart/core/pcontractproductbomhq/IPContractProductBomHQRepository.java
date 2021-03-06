package vn.gpay.gsmart.core.pcontractproductbomhq;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IPContractProductBomHQRepository  extends JpaRepository<PContractProductBomHQ, Long>, JpaSpecificationExecutor<PContractProductBomHQ>{
	@Query(value = "select c from PContractProductBomHQ c "
			+ "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<PContractProductBomHQ> getall_material_in_pcontract_productBOM(
			@Param ("productid_link")final  Long productid_link, 
			@Param ("pcontractid_link")final  Long pcontractid_link);
	
	@Query(value = "select c from PContractProductBomHQ c "
			+ "inner join SKU d on c.materialid_link = d.id "
			+ "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link "
			+ "and (d.skutypeid_link = :skutypeid_link or :skutypeid_link is null) "
			)
	public List<PContractProductBomHQ> get_material_in_pcontract_productBOM(
			@Param ("productid_link")final  Long productid_link, 
			@Param ("pcontractid_link")final  Long pcontractid_link,
			@Param ("skutypeid_link")final  Integer skutypeid_link
			);
	
	@Query(value = "select c from PContractProductBomHQ c "
			+ "where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link "
			+ "and c.materialid_link = :materialid_link")
	public List<PContractProductBomHQ> getby_material_pcontract_product(
			@Param ("productid_link")final  Long productid_link, 
			@Param ("pcontractid_link")final  Long pcontractid_link,
			@Param ("materialid_link")final  Long materialid_link);
	
	@Query(value = "select c from PContractProductBomHQ c "
			+ "where c.pcontractid_link = :pcontractid_link "
			+ "and c.materialid_link = :materialid_link")
	public List<PContractProductBomHQ> getby_pcontract_material(
			@Param ("pcontractid_link")final  Long pcontractid_link,
			@Param ("materialid_link")final  Long materialid_link);
	
	@Query(value = "select c.materialid_link from PContractProductBomHQ c"
			+ " where c.productid_link = :productid_link "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<Long> getall_materialid_in_pcontract_productBOM(
			@Param ("productid_link")final  Long productid_link,
			@Param ("pcontractid_link")final  Long pcontractid_link);
	
	@Query(value = "select c from PContractProductBomHQ c "
			+ "where c.orgrootid_link = :orgrootid_link "
			+ "and c.pcontractid_link = :pcontractid_link")
	public List<PContractProductBomHQ> getall_bypcontract(
			@Param ("orgrootid_link")final  Long orgrootid_link, 
			@Param ("pcontractid_link")final  Long pcontractid_link);
}
