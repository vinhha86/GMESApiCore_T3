package vn.gpay.gsmart.core.api.timesheet_shift_type_org;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.timesheet_lunch.ITimeSheetLunchService;
import vn.gpay.gsmart.core.timesheet_lunch.TimeSheetLunch;
import vn.gpay.gsmart.core.timesheet_shift_type.ITimesheetShiftTypeService;
import vn.gpay.gsmart.core.timesheet_shift_type.TimesheetShiftType;
import vn.gpay.gsmart.core.timesheet_shift_type.TimesheetShiftTypeService;
import vn.gpay.gsmart.core.timesheet_shift_type_org.ITimesheetShiftTypeOrgService;
import vn.gpay.gsmart.core.timesheet_shift_type_org.TimesheetShiftTypeOrg;
import vn.gpay.gsmart.core.timesheet_shift_type_org.TimesheetShiftTypeOrg_Binding;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/timesheetshifttypeorg")
public class TimesheetShiftTypeOrgAPI {
	@Autowired ITimesheetShiftTypeOrgService timesheetShiftTypeOrgService;
	@Autowired ITimesheetShiftTypeService timesheetShiftTypeService;
	@Autowired IOrgService orgService;
	@Autowired ITimeSheetLunchService timeSheetLunchService;
	@Autowired Common commonService;
	
	@RequestMapping(value = "/getall", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_response> timesheetshifttype_GetAll(HttpServletRequest request) {
		TimesheetShiftTypeOrg_response response = new TimesheetShiftTypeOrg_response();
		try {
			response.data = timesheetShiftTypeOrgService.findAll();
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/getbyorgid_link", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_response> timesheetshifttype_GetByorgid_link(@RequestBody TimesheetShiftTypeOrg_load_byorgid_link entity,HttpServletRequest request) {
		TimesheetShiftTypeOrg_response response = new TimesheetShiftTypeOrg_response();
		try {
			
//			List<Org> px_list = orgService.findOrgByType(1L, 0L, 13);
////			System.out.println(px_list.size());
//			List<TimesheetShiftType> shiftType = timesheetShiftTypeService.getAll_caAn();
//			for(Org px : px_list) {
//				for(TimesheetShiftType shift : shiftType) {
//					List<TimesheetShiftTypeOrg> list_shift_org = timesheetShiftTypeOrgService.getByOrgid_link_and_shifttypeId(
//							px.getId(), shift.getId());
//					
//					if(list_shift_org.size() == 0) { //chua ton tai -> add
//						TimesheetShiftTypeOrg newTimesheetShiftTypeOrg = new TimesheetShiftTypeOrg();
//						newTimesheetShiftTypeOrg.setId(null);
//						newTimesheetShiftTypeOrg.setFrom_hour(0);
//						newTimesheetShiftTypeOrg.setFrom_minute(0);
//						newTimesheetShiftTypeOrg.setIs_active(false);
//						newTimesheetShiftTypeOrg.setIs_atnight(false);
//						newTimesheetShiftTypeOrg.setOrgid_link(px.getId());
//						newTimesheetShiftTypeOrg.setTimesheet_shift_type_id_link(shift.getId());
//						newTimesheetShiftTypeOrg.setTo_hour(0);
//						newTimesheetShiftTypeOrg.setTo_minute(0);
//						timesheetShiftTypeOrgService.save(newTimesheetShiftTypeOrg);
//					}
//				}
//			}
			
			
			long id = entity.orgid_link;
			//kiem tra phong ban day thuoc don vi nao - lay id cua don vi do; 
			Long id_org =orgService.getParentIdById(id);
			if(id_org != null && id_org != 1) {
				id= id_org;
			}
//			if(entity.is_ca_an == null) entity.is_ca_an = false;
//			if(entity.is_ca_an) {
//				response.data = timesheetShiftTypeOrgService.getByOrgid_link_CaAn(id);
//			}else {
//				response.data = timesheetShiftTypeOrgService.getByOrgid_link(id);
//			}
			response.data = timesheetShiftTypeOrgService.getByOrgid_link(id);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getbyorgid_link_caLamViec", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_response> timesheetshifttype_GetByorgid_link_calamViec(@RequestBody TimesheetShiftTypeOrg_load_byorgid_link entity,HttpServletRequest request) {
		TimesheetShiftTypeOrg_response response = new TimesheetShiftTypeOrg_response();
		try {
			
			long id = entity.orgid_link;
			//kiem tra phong ban day thuoc don vi nao - lay id cua don vi do; 
			Long id_org =orgService.getParentIdById(id);
			if(id_org != null && id_org != 1) {
				id= id_org;
			}
//			if(entity.is_ca_an == null) entity.is_ca_an = false;
//			if(entity.is_ca_an) {
//				response.data = timesheetShiftTypeOrgService.getByOrgid_link_CaAn(id);
//			}else {
//				response.data = timesheetShiftTypeOrgService.getByOrgid_link(id);
//			}
			
			response.data = new ArrayList<TimesheetShiftTypeOrg>();
//			Boolean isHavingNullValue = entity.isHavingNullValue == null ? false : entity.isHavingNullValue;
//			if(isHavingNullValue) {
//				TimesheetShiftTypeOrg timesheetShiftTypeOrg_NullValue = new TimesheetShiftTypeOrg();
//				timesheetShiftTypeOrg_NullValue.setTimesheet_shift_type_id_link((long)0);
//				timesheetShiftTypeOrg_NullValue.setId((long)0);
//				response.data.add(timesheetShiftTypeOrg_NullValue);
//			}
			
			List<TimesheetShiftTypeOrg> result = timesheetShiftTypeOrgService.getByOrgid_link_CaLamViec(id);
			response.data.addAll(result);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getbyorgid_link_caAn_All", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_response> timesheetshifttype_GetByorgid_link_caAnAll(@RequestBody TimesheetShiftTypeOrg_load_byorgid_link entity,HttpServletRequest request) {
		TimesheetShiftTypeOrg_response response = new TimesheetShiftTypeOrg_response();
		try {
			
			Long id = entity.orgid_link;
			Date date = entity.date;
			Date dateEnd = null;
			if(date != null){
				dateEnd = commonService.getEndOfDate(date);
			}
			
			//kiem tra phong ban day thuoc don vi nao - lay id cua don vi do; 
			Long id_org =orgService.getParentIdById(id);
			if(id_org != null && id_org != 1) {
				id= id_org;
			}
			
			response.data = new ArrayList<TimesheetShiftTypeOrg>();
			List<TimesheetShiftTypeOrg> result = new ArrayList<TimesheetShiftTypeOrg>();
			if(dateEnd.after(Common.Date_Add(new Date(), 0))) {
				// hôm nay
				result = timesheetShiftTypeOrgService.getByOrgid_link_CaAn_active(id);
			}else {
				// hôm qua
				// tìm danh sách id các ca của px tương ứng với ngày gửi lên
				List<Integer> listShiftId = timeSheetLunchService.getShiftIdDistinct(id, date);
				if(listShiftId.size() > 0) {
					List<Long> listShiftIdLong = new ArrayList<Long>();
					for(Integer i=0; i<listShiftId.size(); i++) {
						listShiftIdLong.add(listShiftId.get(i).longValue());
					}
					result = timesheetShiftTypeOrgService.getByOrgid_link_CaAn_ById(id, listShiftIdLong);
//					System.out.println("listShiftId " + listShiftId);
//					System.out.println("id org " + id);
				}
			}
			response.data.addAll(result);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getbyorgid_link_caAn", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_Binding_response> timesheetshifttype_GetByorgid_link_caAn(@RequestBody TimesheetShiftTypeOrg_load_byorgid_link entity,HttpServletRequest request) {
		TimesheetShiftTypeOrg_Binding_response response = new TimesheetShiftTypeOrg_Binding_response();
		try {
			
			Long id = entity.orgid_link;
			Date date = entity.date;
			Date dateEnd = null;
			if(date != null) {
				dateEnd = commonService.getEndOfDate(date);
			}
			
			//kiem tra phong ban day thuoc don vi nao - lay id cua don vi do; 
			Long id_org =orgService.getParentIdById(id);
			if(id_org != null && id_org != 1) {
				id= id_org;
			}
			
			response.data = new ArrayList<TimesheetShiftTypeOrg_Binding>();

			List<TimesheetShiftTypeOrg_Binding> result = new ArrayList<TimesheetShiftTypeOrg_Binding>();
			List<TimesheetShiftTypeOrg> list = new ArrayList<TimesheetShiftTypeOrg>();
			list = timesheetShiftTypeOrgService.getByOrgid_link_CaAn_active(id);
			
			for(TimesheetShiftTypeOrg timesheetShiftTypeOrg : list) {
				TimesheetShiftTypeOrg_Binding newTimesheetShiftTypeOrg_Binding = new TimesheetShiftTypeOrg_Binding();
				newTimesheetShiftTypeOrg_Binding.setId(timesheetShiftTypeOrg.getId());
				newTimesheetShiftTypeOrg_Binding.setFrom_hour(timesheetShiftTypeOrg.getFrom_hour());
				newTimesheetShiftTypeOrg_Binding.setFrom_minute(timesheetShiftTypeOrg.getFrom_minute());
				newTimesheetShiftTypeOrg_Binding.setTo_hour(timesheetShiftTypeOrg.getTo_hour());
				newTimesheetShiftTypeOrg_Binding.setTo_minute(timesheetShiftTypeOrg.getTo_minute());
				newTimesheetShiftTypeOrg_Binding.setIs_atnight(timesheetShiftTypeOrg.getIs_atnight());
				newTimesheetShiftTypeOrg_Binding.setLunch_minute(timesheetShiftTypeOrg.getLunch_minute());
				newTimesheetShiftTypeOrg_Binding.setOrgid_link(timesheetShiftTypeOrg.getOrgid_link());
				newTimesheetShiftTypeOrg_Binding.setTimesheet_shift_type_id_link(timesheetShiftTypeOrg.getTimesheet_shift_type_id_link());
				newTimesheetShiftTypeOrg_Binding.setIs_ca_an(timesheetShiftTypeOrg.getIs_ca_an());
				newTimesheetShiftTypeOrg_Binding.setName(timesheetShiftTypeOrg.getName());
				newTimesheetShiftTypeOrg_Binding.setTenLoaiCa(timesheetShiftTypeOrg.getTenLoaiCa());
				newTimesheetShiftTypeOrg_Binding.setIs_default(timesheetShiftTypeOrg.getIs_default());
				
				String fromHourString = timesheetShiftTypeOrg.getFrom_hour() < 10 ? "0" + timesheetShiftTypeOrg.getFrom_hour() : "" + timesheetShiftTypeOrg.getFrom_hour();
				String fromMinString = timesheetShiftTypeOrg.getFrom_minute() < 10 ? "0" + timesheetShiftTypeOrg.getFrom_minute() : "" + timesheetShiftTypeOrg.getFrom_minute();
				String toHourString = timesheetShiftTypeOrg.getTo_hour() < 10 ? "0" + timesheetShiftTypeOrg.getTo_hour() : "" + timesheetShiftTypeOrg.getTo_hour();
				String toMinString = timesheetShiftTypeOrg.getTo_minute() < 10 ? "0" + timesheetShiftTypeOrg.getTo_minute() : "" + timesheetShiftTypeOrg.getTo_minute();
				
				newTimesheetShiftTypeOrg_Binding.setGio(
						fromHourString + ":" + fromMinString + " - " +
						toHourString + ":" + toMinString 
						);
				
				result.add(newTimesheetShiftTypeOrg_Binding);
			}
			response.data.addAll(result);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimesheetShiftTypeOrg_Binding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_Binding_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getbyorgid_link_caAn_forConfirm", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_Binding_response> getbyorgid_link_caAn_forConfirm(@RequestBody TimesheetShiftTypeOrg_load_byorgid_link entity,HttpServletRequest request) {
		TimesheetShiftTypeOrg_Binding_response response = new TimesheetShiftTypeOrg_Binding_response();
		try {
			
			Long id = entity.orgid_link;
			Date date = entity.date;
			Date dateEnd = null;
			if(date != null) {
				dateEnd = commonService.getEndOfDate(date);
			}
			//kiem tra phong ban day thuoc don vi nao - lay id cua don vi do; 
			Long id_org =orgService.getParentIdById(id);
			if(id_org != null && id_org != 1) {
				id= id_org;
			}
			List<TimesheetShiftTypeOrg> data = new ArrayList<TimesheetShiftTypeOrg>();
			if(dateEnd.after(Common.Date_Add(new Date(), 0))) {
				data = timesheetShiftTypeOrgService.getByOrgid_link_CaAn_active(id);
			}else {
				List<Integer> listShiftId = timeSheetLunchService.getShiftIdDistinct(id, date);
				if(listShiftId.size() > 0) {
					List<Long> listShiftIdLong = new ArrayList<Long>();
					for(Integer i=0; i<listShiftId.size(); i++) {
						listShiftIdLong.add(listShiftId.get(i).longValue());
					}
					data = timesheetShiftTypeOrgService.getByOrgid_link_CaAn_ById(id, listShiftIdLong);
//					System.out.println("listShiftId " + listShiftId);
//					System.out.println("id org " + id);
				}
			}
			List<TimesheetShiftTypeOrg_Binding> result = new ArrayList<TimesheetShiftTypeOrg_Binding>();
			
			for(TimesheetShiftTypeOrg timesheetShiftTypeOrg : data) {
				TimesheetShiftTypeOrg_Binding newTimesheetShiftTypeOrg_Binding = new TimesheetShiftTypeOrg_Binding();
				newTimesheetShiftTypeOrg_Binding.setId(timesheetShiftTypeOrg.getId());
				newTimesheetShiftTypeOrg_Binding.setFrom_hour(timesheetShiftTypeOrg.getFrom_hour());
				newTimesheetShiftTypeOrg_Binding.setFrom_minute(timesheetShiftTypeOrg.getFrom_minute());
				newTimesheetShiftTypeOrg_Binding.setTo_hour(timesheetShiftTypeOrg.getTo_hour());
				newTimesheetShiftTypeOrg_Binding.setTo_minute(timesheetShiftTypeOrg.getTo_minute());
				newTimesheetShiftTypeOrg_Binding.setIs_atnight(timesheetShiftTypeOrg.getIs_atnight());
				newTimesheetShiftTypeOrg_Binding.setLunch_minute(timesheetShiftTypeOrg.getLunch_minute());
				newTimesheetShiftTypeOrg_Binding.setOrgid_link(timesheetShiftTypeOrg.getOrgid_link());
				newTimesheetShiftTypeOrg_Binding.setTimesheet_shift_type_id_link(timesheetShiftTypeOrg.getTimesheet_shift_type_id_link());
				newTimesheetShiftTypeOrg_Binding.setIs_ca_an(timesheetShiftTypeOrg.getIs_ca_an());
				newTimesheetShiftTypeOrg_Binding.setName(timesheetShiftTypeOrg.getName());
				newTimesheetShiftTypeOrg_Binding.setTenLoaiCa(timesheetShiftTypeOrg.getTenLoaiCa());
				newTimesheetShiftTypeOrg_Binding.setIs_default(timesheetShiftTypeOrg.getIs_default());
				
				String fromHourString = timesheetShiftTypeOrg.getFrom_hour() < 10 ? "0" + timesheetShiftTypeOrg.getFrom_hour() : "" + timesheetShiftTypeOrg.getFrom_hour();
				String fromMinString = timesheetShiftTypeOrg.getFrom_minute() < 10 ? "0" + timesheetShiftTypeOrg.getFrom_minute() : "" + timesheetShiftTypeOrg.getFrom_minute();
				String toHourString = timesheetShiftTypeOrg.getTo_hour() < 10 ? "0" + timesheetShiftTypeOrg.getTo_hour() : "" + timesheetShiftTypeOrg.getTo_hour();
				String toMinString = timesheetShiftTypeOrg.getTo_minute() < 10 ? "0" + timesheetShiftTypeOrg.getTo_minute() : "" + timesheetShiftTypeOrg.getTo_minute();
				
				newTimesheetShiftTypeOrg_Binding.setGio(
						fromHourString + ":" + fromMinString + " - " +
						toHourString + ":" + toMinString 
						);
				
				// tinh theo db da xac nhan hay chua
				Boolean isConfirm = false;
				//
				Org org = orgService.findOne(entity.orgid_link);
				List<TimeSheetLunch> TimeSheetLunch_list_unconfirm = timeSheetLunchService.getByConfirmStatus(timesheetShiftTypeOrg.getId(), org.getId(), date, 0);
				List<TimeSheetLunch> TimeSheetLunch_list_confirm = timeSheetLunchService.getByConfirmStatus(timesheetShiftTypeOrg.getId(), org.getId(), date, 1);
				
//				System.out.println("TimeSheetLunch_list_unconfirm " + TimeSheetLunch_list_unconfirm.size());
//				System.out.println("TimeSheetLunch_list_confirm " + TimeSheetLunch_list_confirm.size());
				
				if(TimeSheetLunch_list_unconfirm.size() == 0 && TimeSheetLunch_list_confirm.size() == 0) {
					isConfirm = false;
				}else if(TimeSheetLunch_list_confirm.size() == 0) {
					isConfirm = false;
				}else if(TimeSheetLunch_list_unconfirm.size() == 0) {
					isConfirm = true;
				}
				
				newTimesheetShiftTypeOrg_Binding.setIsConfirm(isConfirm);
				result.add(newTimesheetShiftTypeOrg_Binding);
			}
//			System.out.println("result " + result.size());
			
			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimesheetShiftTypeOrg_Binding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_Binding_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> timesheetshifttype_Create(@RequestBody TimesheetShiftTypeOrg_create_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			Long timesheet_shift_type_id_link = entity.timesheet_shift_type_id_link;
			Long id = entity.id;
			Date timefrom = entity.timefrom;
			Date timeto = entity.timeto;
			boolean checkboxfrom = entity.checkboxfrom;
			boolean checkboxto = entity.checkboxto;
			boolean checkboxactive = entity.checkboxactive;
			Long orgid_link  =entity.orgid_link;
			Boolean is_ca_an = entity.is_ca_an;
			Long working_shift = entity.working_shift;
//			List<TimesheetShiftType> listTimesheetShiftType = timesheetShiftTypeService.getByName(name);
//			if(listTimesheetShiftType.size() > 0) {
//				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
//				response.setMessage("Tên ca làm việc đã tồn tại");
//				return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
//			}
			
			Calendar timefromcal = Calendar.getInstance();
			timefromcal.setTime(timefrom);  
			Integer from_hour = timefromcal.get(Calendar.HOUR_OF_DAY);
			Integer from_minute = timefromcal.get(Calendar.MINUTE);
			
			Calendar timetocal = Calendar.getInstance();
			timetocal.setTime(timeto);  
			Integer to_hour = timetocal.get(Calendar.HOUR_OF_DAY);
			Integer to_minute = timetocal.get(Calendar.MINUTE);
			
//			if(checkboxfrom) from_hour+=24;
//			if(checkboxto) to_hour+=24;
			Boolean is_atnight = false;
			if(checkboxto) is_atnight=true;
			Boolean is_active = false;
			if(checkboxactive) is_active=true; 

			TimesheetShiftTypeOrg timesheetShiftType = new TimesheetShiftTypeOrg();
			timesheetShiftType.setId(id);
			timesheetShiftType.setTimesheet_shift_type_id_link(timesheet_shift_type_id_link);
			timesheetShiftType.setFrom_hour(from_hour);
			timesheetShiftType.setFrom_minute(from_minute);
			timesheetShiftType.setTo_hour(to_hour);
			timesheetShiftType.setTo_minute(to_minute);
			timesheetShiftType.setIs_atnight(is_atnight);
			timesheetShiftType.setIs_active(is_active);
			
			timesheetShiftType.setOrgid_link(orgid_link);
			timesheetShiftType.setIs_ca_an(is_ca_an);
			timesheetShiftType.setWorking_shift(working_shift);
			timesheetShiftTypeOrgService.save(timesheetShiftType);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> timesheetshifttype_Delete(@RequestBody TimesheetShiftTypeOrg_delete_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();

		try {
			
				timesheetShiftTypeOrgService.deleteById(entity.id);
			

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getShift1ForAbsence", method = RequestMethod.POST)
	public ResponseEntity<TimesheetShiftTypeOrg_getShift1ForAbsence_response> getShift1ForAbsence(HttpServletRequest request) {
		TimesheetShiftTypeOrg_getShift1ForAbsence_response response = new TimesheetShiftTypeOrg_getShift1ForAbsence_response();

		try {
			List<TimesheetShiftTypeOrg> list = timesheetShiftTypeOrgService.getShift1ForAbsence();
			if(list.size() > 0) {
				TimesheetShiftTypeOrg temp = list.get(0);
				Integer fromHour = temp.getFrom_hour();
				Integer fromMinute = temp.getFrom_minute();
				Integer toHour = temp.getTo_hour();
				Integer toMinute = temp.getTo_minute();
				
				String timeFrom = "";
				String timeTo = "";
				if(fromMinute < 10) {
					timeFrom = timeFrom + fromHour + ":0" + fromMinute;
				}else {
					timeFrom = timeFrom + fromHour + ":" + fromMinute;
				}
				if(toMinute < 10) {
					timeTo = timeTo + toHour + ":0" + toMinute;
				}else {
					timeTo = timeTo + toHour + ":" + toMinute;
				}
				
				response.timeFrom = timeFrom;
				response.timeTo = timeTo;
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			}else {
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage("Không tìm thấy ca 1 (id 1)");
			}
			return new ResponseEntity<TimesheetShiftTypeOrg_getShift1ForAbsence_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimesheetShiftTypeOrg_getShift1ForAbsence_response>(response, HttpStatus.OK);
		}
	}
}
