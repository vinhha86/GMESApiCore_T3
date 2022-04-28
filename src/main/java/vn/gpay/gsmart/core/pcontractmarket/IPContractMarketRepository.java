package vn.gpay.gsmart.core.pcontractmarket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.gpay.gsmart.core.pcontractmarket.PContractMarket;

import java.util.List;

@Repository
@Transactional
public interface IPContractMarketRepository extends JpaRepository<PContractMarket, Long>, JpaSpecificationExecutor<PContractMarket> {
    @Query(value = "select c from PContractMarket c "
            + "where c.pcontractid_link = :pcontractid_link ")
    public List<PContractMarket> get_by_pcontractid_link(
            @Param("pcontractid_link")final  Long pcontractid_link);

    @Query(value = "select c.marketid_link from PContractMarket c "
            + "where c.pcontractid_link = :pcontractid_link ")
    public List<Long> getmarketid_by_pcontractid_link(
            @Param("pcontractid_link")final  Long pcontractid_link);

    @Query(value = "select c from PContractMarket c "
            + "where c.pcontractid_link = :pcontractid_link "
            + "and c.marketid_link = :marketid_link")
    public List<PContractMarket> get_by_pcontractid_link_marketid_link(
            @Param("pcontractid_link")final  Long pcontractid_link,
            @Param("marketid_link")final  Long marketid_link
            );

    @Query(value = "select c from PContractMarket c "
            + "where c.pcontractid_link = :pcontractid_link "
            + "and c.marketid_link not in :marketid_link")
    public List<PContractMarket> get_by_pcontractid_link_notin_marketid_link(
            @Param("pcontractid_link")final  Long pcontractid_link,
            @Param("marketid_link")final  List<Long> marketid_link
    );

}
