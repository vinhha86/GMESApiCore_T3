package vn.gpay.gsmart.core.pcontract_product_bomhq_log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class PContract_bomHQ_sku_log_Service extends AbstractService<PContract_bomHQ_sku_log> implements IPContract_bomHQ_sku_log_Service{
	@Autowired IPContract_bomHQ_sku_log_Repo repo;
	
	@Override
	protected JpaRepository<PContract_bomHQ_sku_log, Long> getRepository() {
		return repo;
	}

}
