package vn.gpay.gsmart.core.pcontractmarket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.pcontractproduct.PContractProduct;

import java.util.List;

@Service
public class PContractMarketService extends AbstractService<PContractMarket> implements IPContractMarketService {
    @Autowired
    IPContractMarketRepository repo;

    @Override
    public List<PContractMarket> get_by_pcontractid_link(Long pcontractid_link){
        return repo.get_by_pcontractid_link(pcontractid_link);
    }

    @Override
    public List<Long> getmarketid_by_pcontractid_link(Long pcontractid_link) {
        return repo.getmarketid_by_pcontractid_link(pcontractid_link);
    }

    @Override
    public List<PContractMarket> get_by_pcontractid_link_marketid_link(Long pcontractid_link, Long marketid_link) {
        return repo.get_by_pcontractid_link_marketid_link(pcontractid_link, marketid_link);
    }

    @Override
    public List<PContractMarket> get_by_pcontractid_link_notin_marketid_link(Long pcontractid_link, List<Long> marketid_link) {
        return repo.get_by_pcontractid_link_notin_marketid_link(pcontractid_link, marketid_link);
    }

    @Override
    protected JpaRepository<PContractMarket, Long> getRepository() {
        return repo;
    }

}
