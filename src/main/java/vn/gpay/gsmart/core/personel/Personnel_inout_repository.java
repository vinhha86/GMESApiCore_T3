package vn.gpay.gsmart.core.personel;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface Personnel_inout_repository extends JpaRepository<Personnel_inout, Long>,JpaSpecificationExecutor<Personnel_inout> {
	@Query(value = "select c from Personnel_inout c "
			+ "where time_out is null "
			+ "and personnel_code = :personnel_code"
			)
	public List<Personnel_inout> getlist_not_checkout(
			@Param ("personnel_code")final String personnel_code);
	
	@Query(value = "select c from Personnel_inout c "
			+ "where bike_number = :bike_number "
			+ "and  time_in = :timein"
			)
	public List<Personnel_inout> getby_bikenumber_and_timein(
			@Param ("bike_number")final String bike_number,
			@Param ("timein")final Date timein
			);
	
}
