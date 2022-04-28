package vn.gpay.gsmart.core.productpairing;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IProductPairingService extends Operations<ProductPairing> {
	public List<ProductPairing> getproduct_pairing_bycontract(Long orgrootid_link, Long pcontractid_link);

	public List<ProductPairing> getproduct_pairing_detail_bycontract(Long orgrootid_link, Long pcontractid_link,
			Long productpairid_link);

	public List<Long> getproductid_pairing_bycontract(Long orgrootid_link, Long pcontractid_link);

	ProductPairing getproduct_pairing_bykey(Long productid_link, Long productpairid_link);

	List<ProductPairing> getby_product(Long productid_link);

	List<ProductPairing> getbypcontract_product(Long pcontractid_link, Long productid_link, Long orgrootid_link);
	
	List<ProductPairing> getproduct_byproduct_inpair(Long productid_link);
	
	List<ProductPairing> getByProductpairId(Long productpairid_link);
	
	List<Long> getProductIdByProductPair(Long pcontractid_link, Long productid_link);
}
