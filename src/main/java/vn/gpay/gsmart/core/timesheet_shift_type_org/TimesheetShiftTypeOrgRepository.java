package vn.gpay.gsmart.core.timesheet_shift_type_org;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface TimesheetShiftTypeOrgRepository extends JpaRepository<TimesheetShiftTypeOrg, Long>, JpaSpecificationExecutor<TimesheetShiftTypeOrg>{
//	@Query(value = "select c from TimesheetShiftTypeOrg c where c.name = :name ")
//	public List<TimesheetShiftTypeOrg> getByName(@Param ("name")final  String name);
//	
	@Query(value = "select c from TimesheetShiftTypeOrg c where c.id = 1 ")
	public List<TimesheetShiftTypeOrg>getShift1ForAbsence();
	
	//lay ca theo don vi
	@Query(value = "select c from TimesheetShiftTypeOrg c where c.orgid_link = :orgid_link " 
//			+ " and (c.is_ca_an is null or c.is_ca_an is false ) "
			+ " order by c.timesheet_shift_type_id_link "
			)
	public List<TimesheetShiftTypeOrg> getByOrgid_link(@Param ("orgid_link")final  Long orgid_link);
	
	@Query(value = "select c from TimesheetShiftTypeOrg c " 
			+ " where c.orgid_link = :orgid_link " 
			+ " and c.timesheet_shift_type_id_link = :timesheet_shift_type_id_link " 
			+ " order by c.timesheet_shift_type_id_link "
			)
	public List<TimesheetShiftTypeOrg> getByOrgid_link_and_shifttypeId(
			@Param ("orgid_link")final  Long orgid_link,
			@Param ("timesheet_shift_type_id_link")final Long timesheet_shift_type_id_link
			)
	;
	
	//lay ca an theo don vi
	@Query(value = "select c from TimesheetShiftTypeOrg c " 
			+ " inner join TimesheetShiftType d on d.id = c.timesheet_shift_type_id_link " 
			+ " where c.orgid_link = :orgid_link " 
			+ " and d.is_ca_an is true "
			+ " order by d.id "
			)
	public List<TimesheetShiftTypeOrg> getByOrgid_link_CaAn(@Param ("orgid_link")final  Long orgid_link);
	
	//lay ca an theo don vi ngay hien tai (active)
	@Query(value = "select c from TimesheetShiftTypeOrg c " 
			+ " inner join TimesheetShiftType d on d.id = c.timesheet_shift_type_id_link " 
			+ " where c.orgid_link = :orgid_link " 
			+ " and d.is_ca_an is true "
			+ " and c.is_active is true "
			+ " order by d.name "
			)
	public List<TimesheetShiftTypeOrg> getByOrgid_link_CaAn_active(@Param ("orgid_link")final  Long orgid_link);
	
	//lay ca an theo don vi ngay truoc
	@Query(value = "select c from TimesheetShiftTypeOrg c " 
			+ " inner join TimesheetShiftType d on d.id = c.timesheet_shift_type_id_link " 
			+ " where c.orgid_link = :orgid_link " 
//			+ " and d.is_ca_an is true "
//			+ " and c.is_active is true "
			+ " and c.timesheet_shift_type_id_link in :listId "
			+ " order by d.name "
			)
	public List<TimesheetShiftTypeOrg> getByOrgid_link_CaAn_ById(
		@Param ("orgid_link")final  Long orgid_link,
		@Param ("listId")final  List<Long> listId
		);
	
	@Query(value = "select c from TimesheetShiftTypeOrg c " 
			+ " inner join TimesheetShiftType d on d.id = c.timesheet_shift_type_id_link " 
			+ " where c.orgid_link = :orgid_link " 
			+ " and (d.is_ca_an is null or d.is_ca_an is false ) "
			+ " order by d.id "
			)
	public List<TimesheetShiftTypeOrg> getByOrgid_link_CaLamViec(@Param ("orgid_link")final  Long orgid_link);
	
}
