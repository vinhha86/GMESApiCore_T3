package vn.gpay.gsmart.core.timesheet_shift_type_org;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface ITimesheetShiftTypeOrgService extends Operations<TimesheetShiftTypeOrg>{
	//public List<TimesheetShiftTypeOrg> getByName(String name);
	public List<TimesheetShiftTypeOrg>getShift1ForAbsence();
	public List<TimesheetShiftTypeOrg>getByOrgid_link(Long orgid_link);
	public List<TimesheetShiftTypeOrg>getByOrgid_link_CaAn(Long orgid_link);
	public List<TimesheetShiftTypeOrg>getByOrgid_link_CaAn_active(Long orgid_link);
	List<TimesheetShiftTypeOrg> getByOrgid_link_CaAn_ById(Long orgid_link, List<Long> listId);
	public List<TimesheetShiftTypeOrg>getByOrgid_link_CaLamViec(Long orgid_link);
	List<TimesheetShiftTypeOrg> getByOrgid_link_and_shifttypeId(Long orgid_link, Long timesheet_shift_type_id_link);
}
