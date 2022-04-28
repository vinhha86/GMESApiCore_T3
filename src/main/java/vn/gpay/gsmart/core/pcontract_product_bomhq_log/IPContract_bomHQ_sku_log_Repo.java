package vn.gpay.gsmart.core.pcontract_product_bomhq_log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface IPContract_bomHQ_sku_log_Repo extends JpaRepository<PContract_bomHQ_sku_log, Long>, JpaSpecificationExecutor<PContract_bomHQ_sku_log> {

}
