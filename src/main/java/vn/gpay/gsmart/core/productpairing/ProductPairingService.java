package vn.gpay.gsmart.core.productpairing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.pcontractproduct.IPContractProductRepository;
import vn.gpay.gsmart.core.pcontractproductpairing.IPContractProductPairingRepository;

@Service
public class ProductPairingService extends AbstractService<ProductPairing> implements IProductPairingService {
	@Autowired
	IProductPairingRepository repo;
	@Autowired
	IPContractProductPairingRepository pppair_repo;
	@Autowired
	IPContractProductRepository pp_repo;

	@Override
	protected JpaRepository<ProductPairing, Long> getRepository() {
		return repo;
	}

	@Override
	public List<ProductPairing> getproduct_pairing_bycontract(Long orgrootid_link, Long pcontractid_link) {
		return repo.getall_product_pair_bypcontract(pcontractid_link, orgrootid_link);
	}

	@Override
	public List<Long> getproductid_pairing_bycontract(Long orgrootid_link, Long pcontractid_link) {
		return repo.getall_productid_pair_bypcontract(pcontractid_link, orgrootid_link);
	}

	@Override
	public List<ProductPairing> getproduct_pairing_detail_bycontract(Long orgrootid_link, Long pcontractid_link,
			Long productpairid_link) {
//		Long pairid = productpairid_link == 0 ? null : productpairid_link;
		return repo.getall_product_pair_detail_bypcontract(pcontractid_link, productpairid_link, orgrootid_link);
	}

	@Override
	public ProductPairing getproduct_pairing_bykey(Long productid_link, Long productpairid_link) {
		List<ProductPairing> a = repo.getproduct_pairing_bykey(productid_link, productpairid_link);
		if (a.size() > 0)
			return a.get(0);
		else
			return null;
	}

	@Override
	public List<ProductPairing> getby_product(Long productid_link) {
		return repo.getproduct_bypairing(productid_link);
	}

	@Override
	public List<ProductPairing> getbypcontract_product(Long pcontractid_link, Long productid_link,
			Long orgrootid_link) {
		return repo.getall_pairdetail_bypcontract_product(pcontractid_link, productid_link, orgrootid_link);
	}

	@Override
	public List<ProductPairing> getproduct_byproduct_inpair(Long productid_link) {
		return repo.getproduct_byproduct_inpair(productid_link);
	}

	@Override
	public List<ProductPairing> getByProductpairId(Long productpairid_link) {
		return repo.getByProductpairId(productpairid_link);
	}

	@Override
	public List<Long> getProductIdByProductPair(Long pcontractid_link, Long productid_link) {
		return repo.getProductIdByProductPair(pcontractid_link, productid_link);
	}
}
