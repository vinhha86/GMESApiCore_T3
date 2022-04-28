package vn.gpay.gsmart.core.timesheet_lunch;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;

@Service
public class TimeSheetLunchService extends AbstractService<TimeSheetLunch> implements ITimeSheetLunchService{
	@Autowired ITimeSheetLunchRepository repo;
	
	@Override
	protected JpaRepository<TimeSheetLunch, Long> getRepository() {
		return repo;
	}

	@Override
	public List<TimeSheetLunch> getForTimeSheetLunch(Long orgid_link, Date workingdate) {
		return repo.getForTimeSheetLunch(orgid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getByPersonnelDateAndShift(Long personnelid_link, Date workingdate,
			Integer shifttypeid_link) {
		return repo.getByPersonnelDateAndShift(personnelid_link, workingdate, shifttypeid_link);
	}

	@Override
	public List<TimeSheetLunch> getByPersonnelDate(Long personnelid_link, Date workingdate_start, Date workingdate_end) {
		return repo.getByPersonnelDate(personnelid_link, workingdate_start,workingdate_end);
	}

	@Override
	public List<TimeSheetLunch> getForUpdateStatusTimeSheetLunch(Long orgid_link, Date workingdate) {
		return repo.getForUpdateStatusTimeSheetLunch(orgid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getForTimeSheetLunchByGrant(Long orgid_link, Date workingdate) {
		return repo.getForTimeSheetLunchByGrant(orgid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getForTimeSheetLunch_byOrg_Date(Long orgid_link, Date workingdate) {
		return repo.getForTimeSheetLunch_byOrg_Date(orgid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getByConfirmStatus(Long timesheetShiftTypeOrg_id, Long orgid_link, Date workingdate, Integer status) {
		return repo.getByConfirmStatus(timesheetShiftTypeOrg_id, orgid_link, workingdate, status);
	}

	@Override
	public List<TimeSheetLunch> getBy_isworking_islunch(Boolean isworking, Boolean islunch) {
		return repo.getBy_isworking_islunch(isworking, islunch);
	}

	@Override
	public List<TimeSheetLunch> getBy_multiShift(Long orgid_link, Date workingdate, List<Long> listIds) {
		return repo.getBy_multiShift(orgid_link, workingdate, listIds);
	}

	@Override
	public List<TimeSheetLunch> getByOrg_Shift(Long orgid_link, Integer shifttypeid_link, Date workingdate) {
		return repo.getByOrg_Shift(orgid_link, shifttypeid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getByOrg_Shift_Them(Long orgid_link, Integer shifttypeid_link, Date workingdate) {
		return repo.getByOrg_Shift_Them(orgid_link, shifttypeid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getByOrg_Shift_DangKy(Long orgid_link, Integer shifttypeid_link, Date workingdate) {
		return repo.getByOrg_Shift_DangKy(orgid_link, shifttypeid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getForTimeSheetLunchBeforeDate(Long orgid_link, Date workingdate) {
		return repo.getForTimeSheetLunchBeforeDay(orgid_link, workingdate);
	}

	@Override
	public List<TimeSheetLunch> getForTimeSheetLunchByGrantManyDay(Long orgid_link, Date date_from, Date date_to) {
		return repo.getForTimeSheetLunchByGrantManyDay(orgid_link, date_from, date_to);
	}

	@Override
	public List<Long> getPersonnelIdByOrgAndDate(Long orgid_link, Date workingdate) {
		return repo.getPersonnelIdByOrgAndDate(orgid_link, workingdate);
	}

	@Override
	public List<Integer> getShiftIdDistinct(Long orgid_link, Date workingdate) {
		return repo.getShiftIdDistinct(orgid_link, workingdate);
	}
	
}
