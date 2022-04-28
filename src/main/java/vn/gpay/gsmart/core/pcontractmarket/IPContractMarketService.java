package vn.gpay.gsmart.core.pcontractmarket;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gpay.gsmart.core.base.Operations;
import vn.gpay.gsmart.core.pcontractproduct.PContractProduct;

import java.util.List;

public interface IPContractMarketService extends Operations<PContractMarket> {
    public List<PContractMarket> get_by_pcontractid_link(Long pcontractid_link);
    public List<Long> getmarketid_by_pcontractid_link(Long pcontractid_link);
    public List<PContractMarket> get_by_pcontractid_link_marketid_link(Long pcontractid_link, Long marketid_link);
    public List<PContractMarket> get_by_pcontractid_link_notin_marketid_link(Long pcontractid_link, List<Long> marketid_link);
}
