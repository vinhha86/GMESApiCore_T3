package vn.gpay.gsmart.core.timesheet_shift_type;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;


@Service
public class TimesheetShiftTypeService extends AbstractService<TimesheetShiftType> implements ITimesheetShiftTypeService {
	@Autowired ITimesheetShiftTypeRepository repo;

	@Override
	protected JpaRepository<TimesheetShiftType, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	@Override
	public List<TimesheetShiftType> getTimesheetShiftType_ByIdOrgid_link(Long id) {
		// TODO Auto-generated method stub
		return repo.getTimesheetShiftType_ByIdOrgid_link(id);
	}

	@Override
	public Long getTimesheetShiftTypeID_ByName(String name) {
		// TODO Auto-generated method stub
		return repo.getTimesheetShiftTypeID_ByName(name);
	}

	@Override
	public List<TimesheetShiftType> getShift_ByIdOrgid_link(Long orgid_link) {
		// TODO Auto-generated method stub
		return repo.getShift_ByIdOrgid_link(orgid_link);
	}

	@Override
	public List<TimesheetShiftType> getTimesheetShiftType_CaAn_ByIdOrgid_link(Long id) {
		return repo.getTimesheetShiftType_CaAn_ByIdOrgid_link(id);
	}

	@Override
	public List<TimesheetShiftType> getShift_CaAn_ByIdOrgid_link(Long orgid_link) {
		return repo.getShift_CaAn_ByIdOrgid_link(orgid_link);
	}

	@Override
	public List<TimesheetShiftType> getAll_caAn() {
		return repo.getAll_caAn();
	}

	@Override
	public List<TimesheetShiftType> getTimesheetShiftType_ByIdOrgid_link_shift_type_org(Long id,
			Long timesheet_shift_type_id_link) {
		return repo.getTimesheetShiftType_ByIdOrgid_link_shift_type_org(id, timesheet_shift_type_id_link);
	}
}
