package vn.gpay.gsmart.core.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class PortServiceImpl extends AbstractService<Port> implements IPortService{

	@Autowired
	PortRepository repositoty;

	@Override
	protected JpaRepository<Port, Long> getRepository() {
		// TODO Auto-generated method stub
		return repositoty;
	}

}
