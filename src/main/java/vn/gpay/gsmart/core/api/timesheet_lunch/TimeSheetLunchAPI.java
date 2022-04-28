package vn.gpay.gsmart.core.api.timesheet_lunch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.personel.IPersonnel_Service;
import vn.gpay.gsmart.core.personel.Personel;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.timesheet_absence.ITimesheetAbsenceService;
import vn.gpay.gsmart.core.timesheet_absence.TimesheetAbsence;
import vn.gpay.gsmart.core.timesheet_lunch.ITimeSheetLunchService;
import vn.gpay.gsmart.core.timesheet_lunch.TimeSheetLunch;
import vn.gpay.gsmart.core.timesheet_lunch.TimeSheetLunchBinding;
import vn.gpay.gsmart.core.timesheet_lunch.TimeSheetLunch_Binding;
import vn.gpay.gsmart.core.timesheet_lunch.TongHopBaoAn;
import vn.gpay.gsmart.core.timesheet_lunch_khach.ITimeSheetLunchKhachService;
import vn.gpay.gsmart.core.timesheet_lunch_khach.TimeSheetLunchKhach;
import vn.gpay.gsmart.core.timesheet_shift_type.ITimesheetShiftTypeService;
import vn.gpay.gsmart.core.timesheet_shift_type.TimesheetShiftType;
import vn.gpay.gsmart.core.timesheet_shift_type_org.ITimesheetShiftTypeOrgService;
import vn.gpay.gsmart.core.timesheet_shift_type_org.TimesheetShiftTypeOrg;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.OrgType;
import vn.gpay.gsmart.core.utils.ResponseMessage;
import vn.gpay.gsmart.core.utils.RiceExcel;

@RestController
@RequestMapping("/api/v1/timesheetlunch")
public class TimeSheetLunchAPI {

	@Autowired
	private ITimeSheetLunchService timeSheetLunchService;
	@Autowired
	private IPersonnel_Service personnelService;
	@Autowired
	private ITimesheetShiftTypeService timesheetshifttypeService;
	@Autowired
	private ITimesheetShiftTypeOrgService timesheetshifttypeOrgService;
	@Autowired
	private IOrgService orgService;
	@Autowired
	private IGpayUserOrgService userOrgService;
	@Autowired
	private ITimesheetAbsenceService timesheetAbsenceService;
	@Autowired
	ITimeSheetLunchKhachService lunchkhachService;
	@Autowired
	ITimeSheetLunchKhachService timeSheetLunchKhachService;
	@Autowired
	Common commonService;
	
	@RequestMapping(value = "/getForTimeSheetLunch", method = RequestMethod.POST)
	public ResponseEntity<TimeSheetLunch_response> getForTimeSheetLunch(@RequestBody TimeSheetLunch_request entity,
			HttpServletRequest request) {
		TimeSheetLunch_response response = new TimeSheetLunch_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Long> list_org_id = new ArrayList<Long>();
			List<GpayUserOrg> list_userorg = userOrgService.getall_byuser_andtype(user.getId(),
					OrgType.ORG_TYPE_FACTORY);
			Long orgrootid_link = user.getRootorgid_link();
			Long orgid_link = entity.orgid_link;
			List<Org> lst_org = new ArrayList<Org>();
			List<Personel> listPersonnel = null;			

			Date date = entity.date;
			Date dateBegin = commonService.getBeginOfDate(date);
			Date dateEnd = commonService.getEndOfDate(date);
//			Date  date_check = Common.Date_Add(dateEnd, -1);
			
			List<TimeSheetLunchBinding> list = new ArrayList<TimeSheetLunchBinding>();

			//neu la ngay hom truoc thi load trong bang timesheet_lunch
			if(dateEnd.after(Common.Date_Add(new Date(), 0))) {
				for (GpayUserOrg userorg : list_userorg) {
					list_org_id.add(userorg.getOrgid_link());
				}
				if (!list_org_id.contains(user.getOrgid_link())) {
					list_org_id.add(user.getOrgid_link());
				}
				if (entity.orgid_link != orgrootid_link) {
					// nếu quản lý nhiều tài khoan
					if (list_org_id.size() > 1) {
//						listPersonnel = personnelService.getby_org(orgid_link, orgrootid_link);
						listPersonnel = personnelService.getTongLaoDongByDate(orgid_link, dateBegin, dateEnd);
					} else {
						// nếu có đơn vị con cụ thể
						if (user.getOrg_grant_id_link() != null) {
							lst_org = orgService.getOrgById(user.getOrg_grant_id_link());
							if (lst_org.size() != 0) {
//								listPersonnel = personnelService.getby_org(user.getOrg_grant_id_link(), orgrootid_link);
								listPersonnel = personnelService.getTongLaoDongByDate(user.getOrg_grant_id_link(),
										dateBegin, dateEnd);
							}
						} else {
//							listPersonnel = personnelService.getby_org(orgid_link, orgrootid_link);
							listPersonnel = personnelService.getTongLaoDongByDate(orgid_link, dateBegin, dateEnd);
						}
					}
				}

				// kiem tra phong ban day thuoc don vi nao - lay id cua don vi do;
				Long id_org = orgService.getParentIdById(orgid_link);
				if (id_org != null && id_org != 1) {
					orgid_link = id_org;
				}
				List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunch(orgid_link, date);
				Map<Long, TimeSheetLunchBinding> mapTmp = new HashMap<>();

				for (Personel personnel : listPersonnel) { // add personnel to map
					TimeSheetLunchBinding temp = new TimeSheetLunchBinding();
					temp.setPersonnelid_link(personnel.getId());
					temp.setPersonnelCode(personnel.getCode());
					temp.setPersonnelFullname(personnel.getFullname());
					temp.setWorkingdate(date);
					temp.setRegister_code(personnel.getRegister_code());
					temp.setOrgid_link(personnel.getOrgid_link());
					temp.setOrgmanagerid_link(personnel.getOrgmanagerid_link());
					temp.setTimesheet_shift_id_list(new ArrayList<Long>());
					mapTmp.put(personnel.getId(), temp);
				}

				// lấy id ca làm việc
				for (TimeSheetLunch timeSheetLunch : listTimeSheetLunch) {
					if (mapTmp.containsKey(timeSheetLunch.getPersonnelid_link())) {
						TimeSheetLunchBinding temp = mapTmp.get(timeSheetLunch.getPersonnelid_link());

						// lay gia tri id time sheet shift type va time sheet shift type org
						List<TimesheetShiftTypeOrg> listTimesheetShiftTypeOrg = timesheetshifttypeOrgService.getByOrgid_link_and_shifttypeId(orgid_link, timeSheetLunch.getShifttypeid_link().longValue());
						if(listTimesheetShiftTypeOrg.size() > 0) {
							TimesheetShiftTypeOrg timesheetShiftTypeOrg = listTimesheetShiftTypeOrg.get(0);
							temp.setTimesheet_shift_id(timesheetShiftTypeOrg.getId());
							temp.getTimesheet_shift_id_list().add(timesheetShiftTypeOrg.getId());
						}
						temp.setTimesheet_shift_type_id_link(timeSheetLunch.getShifttypeid_link().longValue());
						
						// lay gia tri id cua ca khong an trua, set cho binding
						if(timeSheetLunch.getIs_nolunch()) {
							Long shifttypeid_link = timeSheetLunch.getShifttypeid_link().longValue();
							List<TimesheetShiftTypeOrg> timesheetShiftTypeOrg_list = timesheetshifttypeOrgService.getByOrgid_link_and_shifttypeId(
									orgid_link, shifttypeid_link);
//							System.out.println("timesheetShiftTypeOrg_list " + timesheetShiftTypeOrg_list.size());
							if(timesheetShiftTypeOrg_list.size() > 0) {
								TimesheetShiftTypeOrg timesheetShiftTypeOrg = timesheetShiftTypeOrg_list.get(0);
								temp.setNolunch_shift_idlink(timesheetShiftTypeOrg.getId());
							}
						}
						
						temp.setStatus(timeSheetLunch.getStatus());
						mapTmp.put(timeSheetLunch.getPersonnelid_link(), temp);
					}
				}
				list = new ArrayList<TimeSheetLunchBinding>(mapTmp.values());
			}
			else {
				
//				System.out.println("yesterday");
				
				Org org = orgService.findOne(orgid_link);

				Long org_id = (long) 0;
				if(org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_TRUSOCHINH)) {
					
				}else
				if(org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_XUONGSX)) {
					org_id = org.getId();
				}else {
					org_id = org.getParentid_link();
				}
				
				//
				for (GpayUserOrg userorg : list_userorg) {
					list_org_id.add(userorg.getOrgid_link());
				}
				if (!list_org_id.contains(user.getOrgid_link())) {
					list_org_id.add(user.getOrgid_link());
				}
				if (entity.orgid_link != orgrootid_link) {
					// nếu quản lý nhiều tài khoan
					if (list_org_id.size() > 1) {
//						listPersonnel = personnelService.getby_org(orgid_link, orgrootid_link);
						listPersonnel = personnelService.getTongLaoDongByDate(orgid_link, dateBegin, dateEnd);
					} else {
						// nếu có đơn vị con cụ thể
						if (user.getOrg_grant_id_link() != null) {
							lst_org = orgService.getOrgById(user.getOrg_grant_id_link());
							if (lst_org.size() != 0) {
//								listPersonnel = personnelService.getby_org(user.getOrg_grant_id_link(), orgrootid_link);
								listPersonnel = personnelService.getTongLaoDongByDate(user.getOrg_grant_id_link(),
										dateBegin, dateEnd);
							}
						} else {
//							listPersonnel = personnelService.getby_org(orgid_link, orgrootid_link);
							listPersonnel = personnelService.getTongLaoDongByDate(orgid_link, dateBegin, dateEnd);
						}
					}
				}

				// sửa từ đây, thay đổi listPersonnel (đang lấy hết -> lấy có trong timesheetlinch)
				list = new ArrayList<TimeSheetLunchBinding>();
				List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunchBeforeDate(orgid_link, date);
				List<Long> personnelIdList = timeSheetLunchService.getPersonnelIdByOrgAndDate(orgid_link, date);
				if(personnelIdList.size() == 0) personnelIdList.add(0L);
				listPersonnel = personnelService.getPersonnelByListId(personnelIdList);
				
				Map<Long, TimeSheetLunchBinding> mapTmp = new HashMap<>();
				
				for (Personel personnel : listPersonnel) { // add personnel to map
					TimeSheetLunchBinding temp = new TimeSheetLunchBinding();
					temp.setPersonnelid_link(personnel.getId());
					temp.setPersonnelCode(personnel.getCode());
					temp.setPersonnelFullname(personnel.getFullname());
					temp.setWorkingdate(date);
					temp.setRegister_code(personnel.getRegister_code());
					temp.setOrgid_link(personnel.getOrgid_link());
					temp.setOrgmanagerid_link(personnel.getOrgmanagerid_link());
					temp.setTimesheet_shift_id_list(new ArrayList<Long>());
					mapTmp.put(personnel.getId(), temp);
				}
				
				// sửa đến đây
//				System.out.println("orgid_link " + orgid_link);
//				System.out.println("date " + date);
//				System.out.println("personnelIdList " + personnelIdList);
//				System.out.println("listTimeSheetLunch " + listTimeSheetLunch.size());
//				System.out.println("listPersonnel " + listPersonnel.size());
				
				for (TimeSheetLunch timeSheetLunch : listTimeSheetLunch) {
					if (mapTmp.containsKey(timeSheetLunch.getPersonnelid_link())) {
						TimeSheetLunchBinding temp = mapTmp.get(timeSheetLunch.getPersonnelid_link());

						// lay gia tri id time sheet shift type va tieme sheet sifht type org
						List<TimesheetShiftTypeOrg> listTimesheetShiftTypeOrg = timesheetshifttypeOrgService.getByOrgid_link_and_shifttypeId(org_id, timeSheetLunch.getShifttypeid_link().longValue());
						if(listTimesheetShiftTypeOrg.size() > 0) {
							TimesheetShiftTypeOrg timesheetShiftTypeOrg = listTimesheetShiftTypeOrg.get(0);
							temp.setTimesheet_shift_id(timesheetShiftTypeOrg.getId());
							temp.getTimesheet_shift_id_list().add(timesheetShiftTypeOrg.getId());
						}
						temp.setTimesheet_shift_type_id_link(timeSheetLunch.getShifttypeid_link().longValue());
						
						// lay gia tri id cua ca khong an trua, set cho binding
						if(timeSheetLunch.getIs_nolunch()) {
							Long shifttypeid_link = timeSheetLunch.getShifttypeid_link().longValue();
							List<TimesheetShiftTypeOrg> timesheetShiftTypeOrg_list = timesheetshifttypeOrgService.getByOrgid_link_and_shifttypeId(
									org_id, shifttypeid_link);
							
//							System.out.println("timesheetShiftTypeOrg_list " + timesheetShiftTypeOrg_list.size());
//							System.out.println("orgid_link " + orgid_link);
//							System.out.println("shifttypeid_link " + shifttypeid_link);
							
							if(timesheetShiftTypeOrg_list.size() > 0) {
								TimesheetShiftTypeOrg timesheetShiftTypeOrg = timesheetShiftTypeOrg_list.get(0);
								temp.setNolunch_shift_idlink(timesheetShiftTypeOrg.getId());
							}
						}
						
						temp.setStatus(timeSheetLunch.getStatus());
						temp.setOrgid_link(orgid_link);
						temp.setOrgmanagerid_link(timeSheetLunch.getOrgmanagerid_link());
						
						mapTmp.put(timeSheetLunch.getPersonnelid_link(), temp);
					}
				}
				list = new ArrayList<TimeSheetLunchBinding>(mapTmp.values());

//				for(TimeSheetLunch ts_lunch : listTimeSheetLunch) {
//					Personel person = personnelService.findOne(ts_lunch.getPersonnelid_link());
//					
//					if (null != person) {
//						TimeSheetLunchBinding binding = new TimeSheetLunchBinding();
//						binding.setOrgid_link(ts_lunch.getOrgid_link());
//						binding.setOrgmanagerid_link(ts_lunch.getOrgmanagerid_link());
//						binding.setPersonnelCode(person.getCode());
//						binding.setPersonnelFullname(person.getFullname());
//						binding.setPersonnelid_link(ts_lunch.getPersonnelid_link());
//						binding.setRegister_code(person.getRegister_code());
//						binding.setStatus(ts_lunch.getStatus());
//						binding.setWorkingdate(ts_lunch.getWorkingdate());
//						
//						// lay gia tri id time sheet shift type va tieme sheet sifht type org
//						List<TimesheetShiftTypeOrg> listTimesheetShiftTypeOrg = timesheetshifttypeOrgService.getByOrgid_link_and_shifttypeId
//								(org_id, ts_lunch.getShifttypeid_link().longValue());
//						
//						if(listTimesheetShiftTypeOrg.size() > 0) {
//							TimesheetShiftTypeOrg timesheetShiftTypeOrg = listTimesheetShiftTypeOrg.get(0);
//							binding.setTimesheet_shift_id(timesheetShiftTypeOrg.getId());
//							if(binding.getTimesheet_shift_id_list() == null) {
//								binding.setTimesheet_shift_id_list(new ArrayList<Long>());
//							}
//							binding.getTimesheet_shift_id_list().add(timesheetShiftTypeOrg.getId());
//						}
//						binding.setTimesheet_shift_type_id_link(ts_lunch.getShifttypeid_link().longValue());
//						
//						// lay gia tri id cua ca khong an trua, set cho binding
//						if(ts_lunch.getIs_nolunch()) {
//							Long shifttypeid_link = ts_lunch.getShifttypeid_link().longValue();
//							List<TimesheetShiftTypeOrg> timesheetShiftTypeOrg_list = timesheetshifttypeOrgService.getByOrgid_link_and_shifttypeId(
//									orgid_link, shifttypeid_link);
//							if(timesheetShiftTypeOrg_list.size() > 0) {
//								TimesheetShiftTypeOrg timesheetShiftTypeOrg = timesheetShiftTypeOrg_list.get(0);
//								binding.setNolunch_shift_idlink(timesheetShiftTypeOrg.getId());
//							}
//						}
//						
//						list.add(binding);
//					} else {
//						System.out.println("Person not found: " + ts_lunch.getPersonnelid_link());
//					}
//				}
			}
			response.data = list;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimeSheetLunch_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimeSheetLunch_response>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> save(@RequestBody TimeSheetLunch_save_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();

			List<TimeSheetLunchBinding> listTimeSheetLunchBinding = entity.data;
			Date now = new Date();

			for (TimeSheetLunchBinding temp : listTimeSheetLunchBinding) {
				Long timesheet_shift_id = temp.getTimesheet_shift_id(); // id timesheet shift type org
				Long timesheet_shift_type_id_link = temp.getTimesheet_shift_type_id_link(); // id timesheet shift type
				Long nolunch_shift_idlink = temp.getNolunch_shift_idlink() == null ? 0L : temp.getNolunch_shift_idlink();
				Long personnelid_link = temp.getPersonnelid_link();
				Date workingdate = temp.getWorkingdate();
				
				boolean isWorkingShift = false;
				boolean isLunchShift = false;
				List<TimeSheetLunch> list = new ArrayList<TimeSheetLunch>();

				isWorkingShift = temp.isWorkingShift();
				isLunchShift = temp.isLunchShift();
				list = timeSheetLunchService.getByPersonnelDateAndShift(personnelid_link, workingdate,
						timesheet_shift_type_id_link.intValue());

				if (list.size() > 0) {
					
//					System.out.println("--- up");
//					System.out.println("nolunch_shift_idlink " + temp.getNolunch_shift_idlink());
//					System.out.println("timesheet_shift_id " + temp.getTimesheet_shift_id());
					
					TimeSheetLunch timeSheetLunch = list.get(0);
					timeSheetLunch.setIsworking(isWorkingShift);
					timeSheetLunch.setIslunch(isLunchShift);
					if(temp.getNolunch_shift_idlink() == null) {
						timeSheetLunch.setIs_nolunch(false);
					}else
					if(temp.getNolunch_shift_idlink().equals(temp.getTimesheet_shift_id())) {
						timeSheetLunch.setIs_nolunch(true);
					}else {
						timeSheetLunch.setIs_nolunch(false);
					}
					
//					// lưu is_nolunch  
//					// nolunch_shift_idlink gửi lên  lấy theo id bảng timesheet_shift_type_org
//					// TimeSheetLunch shifttypeid_link lấy theo id bảng timesheet_shift_type
//					TimesheetShiftTypeOrg timesheetShiftTypeOrg = timesheetshifttypeOrgService.findOne(nolunch_shift_idlink);
//					if(timesheetShiftTypeOrg != null) {
////						Long timesheet_shift_type_id_link = timesheetShiftTypeOrg.getTimesheet_shift_type_id_link();
//						if(timesheet_shift_type_id_link != null) {
//							if(timeSheetLunch.getShifttypeid_link().equals(timesheet_shift_type_id_link.intValue())) {
//								timeSheetLunch.setIs_nolunch(true);
//							}else {
//								timeSheetLunch.setIs_nolunch(false);
//							}
//						}
//					}
//					//
//					if(nolunch_shift_idlink.equals((long)0)) {
//						timeSheetLunch.setIs_nolunch(false);
//					}
//					System.out.println("save tren");
					timeSheetLunchService.save(timeSheetLunch);
				} else {
					
//					System.out.println("--- down");
//					System.out.println("nolunch_shift_idlink " + temp.getNolunch_shift_idlink());
//					System.out.println("timesheet_shift_id " + temp.getTimesheet_shift_id());
					
					TimeSheetLunch timeSheetLunch = new TimeSheetLunch();
					timeSheetLunch.setId(0L);
					timeSheetLunch.setOrgrootid_link(orgrootid_link);
					timeSheetLunch.setPersonnelid_link(personnelid_link);
					timeSheetLunch.setShifttypeid_link(timesheet_shift_type_id_link.intValue());
					timeSheetLunch.setUsercreatedid_link(user.getId());
					timeSheetLunch.setTimecreated(now);
					timeSheetLunch.setWorkingdate(workingdate);
					timeSheetLunch.setIsworking(isWorkingShift);
					timeSheetLunch.setIslunch(isLunchShift);
					timeSheetLunch.setStatus(0);
					timeSheetLunch.setOrgid_link(temp.getOrgid_link());
					timeSheetLunch.setOrgmanagerid_link(temp.getOrgmanagerid_link());
					if(temp.getNolunch_shift_idlink() == null) {
						timeSheetLunch.setIs_nolunch(false);
					}else
					if(temp.getNolunch_shift_idlink().equals(temp.getTimesheet_shift_id())) {
						timeSheetLunch.setIs_nolunch(true);
					}else {
						timeSheetLunch.setIs_nolunch(false);
					}
					
//					// lưu is_nolunch  
//					// nolunch_shift_idlink gửi lên  lấy theo id bảng timesheet_shift_type_org
//					// TimeSheetLunch shifttypeid_link lấy theo id bảng timesheet_shift_type
//					TimesheetShiftTypeOrg timesheetShiftTypeOrg = timesheetshifttypeOrgService.findOne(nolunch_shift_idlink);
//					if(timesheetShiftTypeOrg != null) {
////						Long timesheet_shift_type_id_link = timesheetShiftTypeOrg.getTimesheet_shift_type_id_link();
//						if(timesheet_shift_type_id_link != null) {
//							if(timeSheetLunch.getShifttypeid_link().equals(timesheet_shift_type_id_link.intValue())) {
//								timeSheetLunch.setIs_nolunch(true);
//							}else {
//								timeSheetLunch.setIs_nolunch(false);
//							}
//						}
//					}
//					//
//					if(nolunch_shift_idlink.equals((long)0)) {
//						timeSheetLunch.setIs_nolunch(false);
//					}
//					System.out.println("save duoi");
					timeSheetLunchService.save(timeSheetLunch);
				}
			}
			// xoa nhung timsheetluch co isworking = false, islunch = false
			List<TimeSheetLunch> TimeSheetLunch_toDelete = timeSheetLunchService.getBy_isworking_islunch(false, false);
			for (TimeSheetLunch timeSheetLunch : TimeSheetLunch_toDelete) {
				timeSheetLunchService.deleteById(timeSheetLunch.getId());
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/tonghopbaoan", method = RequestMethod.POST)
	public ResponseEntity<get_TongHopBaoAn_response> TongHopBaoAn(@RequestBody get_tonghopbaoan_request entity,
			HttpServletRequest request) {
		get_TongHopBaoAn_response response = new get_TongHopBaoAn_response();
		try {
			Long orgid_link = entity.orgid_link;
			Date date_from = entity.date_from;
			Date date_to = entity.date_to;

			date_from = commonService.getBeginOfDate(date_from);
			date_to = commonService.getEndOfDate(date_to);
			List<Org> list_org = orgService.getorgChildrenbyOrg(orgid_link, new ArrayList<>());
			List<TongHopBaoAn> list = new ArrayList<TongHopBaoAn>();

			// Thêm đơn vị khách vào báo cáo
			Org org_khach = new Org();
			org_khach.setName("Khách");
			org_khach.setOrgtypeid_link(166);
			org_khach.setId(orgid_link);

			list_org.add(org_khach);

			// Thêm báo thêm của các tổ
			Org org_them = new Org();
			org_them.setName("Báo bổ sung");
			org_them.setOrgtypeid_link(999);
			org_them.setId((long) -1);

			list_org.add(org_them);
			
			for (Org org : list_org) {
				if (org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_DONVIKHACH)) {
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach = lunchkhachService.getby_nhieungay_org(date_from, date_to,
							orgid_link);

					listTimeSheetLunchKhach.removeIf(c -> c.getAmount() == null || c.getAmount() == 0 );

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca1 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca1.removeIf(c -> !c.getShifttypeid_link().equals((long) 4));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca2 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca2.removeIf(c -> !c.getShifttypeid_link().equals((long) 5));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca3 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca3.removeIf(c -> !c.getShifttypeid_link().equals((long) 6));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca4 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca4.removeIf(c -> !c.getShifttypeid_link().equals((long) 7));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca5 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca5.removeIf(c -> !c.getShifttypeid_link().equals((long) 8));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca6 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca6.removeIf(c -> !c.getShifttypeid_link().equals((long) 9));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca7 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca7.removeIf(c -> !c.getShifttypeid_link().equals((long) 10));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca8 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca8.removeIf(c -> !c.getShifttypeid_link().equals((long) 11));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca9 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca9.removeIf(c -> !c.getShifttypeid_link().equals((long) 12));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca10 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca10.removeIf(c -> !c.getShifttypeid_link().equals((long) 13));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca11 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca11.removeIf(c -> !c.getShifttypeid_link().equals((long) 14));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca12 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca12.removeIf(c -> !c.getShifttypeid_link().equals((long) 15));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca13 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca13.removeIf(c -> !c.getShifttypeid_link().equals((long) 16));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca14 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca14.removeIf(c -> !c.getShifttypeid_link().equals((long) 17));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca15 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca15.removeIf(c -> !c.getShifttypeid_link().equals((long) 18));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca16 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca16.removeIf(c -> !c.getShifttypeid_link().equals((long) 19));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca17 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca17.removeIf(c -> !c.getShifttypeid_link().equals((long) 20));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca18 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca18.removeIf(c -> !c.getShifttypeid_link().equals((long) 21));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca19 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca19.removeIf(c -> !c.getShifttypeid_link().equals((long) 22));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca20 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca20.removeIf(c -> !c.getShifttypeid_link().equals((long) 23));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca21 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca21.removeIf(c -> !c.getShifttypeid_link().equals((long) 24));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca22 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca22.removeIf(c -> !c.getShifttypeid_link().equals((long) 25));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca23 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca23.removeIf(c -> !c.getShifttypeid_link().equals((long) 26));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca24 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca24.removeIf(c -> !c.getShifttypeid_link().equals((long) 27));

					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca25 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca25.removeIf(c -> !c.getShifttypeid_link().equals((long) 28));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca26 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca26.removeIf(c -> !c.getShifttypeid_link().equals((long) 29));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca27 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca27.removeIf(c -> !c.getShifttypeid_link().equals((long) 30));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca28 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca28.removeIf(c -> !c.getShifttypeid_link().equals((long) 31));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca29 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca29.removeIf(c -> !c.getShifttypeid_link().equals((long) 32));
					
					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca30 = new ArrayList<>(listTimeSheetLunchKhach);
					listTimeSheetLunchKhach_ca30.removeIf(c -> !c.getShifttypeid_link().equals((long) 33));

					TongHopBaoAn tonghop = new TongHopBaoAn();
					tonghop.setOrg_name(org.getName());
					
					Integer amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca1) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa1(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca2) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa2(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca3) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa3(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca4) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa4(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca5) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa5(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca6) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa6(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca7) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa7(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca8) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa8(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca9) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa9(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca10) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa10(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca11) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa11(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca12) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa12(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca13) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa13(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca14) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa14(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca15) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa15(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca16) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa16(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca17) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa17(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca18) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa18(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca19) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa19(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca20) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa20(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca21) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa21(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca22) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa22(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca23) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa23(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca24) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa24(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca25) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa25(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca26) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa26(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca27) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa27(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca28) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa28(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca29) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa29(amount);
					amount = 0;
					for(TimeSheetLunchKhach timeSheetLunchKhach : listTimeSheetLunchKhach_ca30) {
						amount += timeSheetLunchKhach.getAmount() == null ? 0 : timeSheetLunchKhach.getAmount();
					}
					tonghop.setCa30(amount);
					tonghop.setOrgtypeid_link(org.getOrgtypeid_link());
					list.add(tonghop);

				} else if (org.getOrgtypeid_link().equals(999)) {

				} else {
					List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService
							.getForTimeSheetLunchByGrantManyDay(org.getId(), date_from, date_to);
					
					// chi lay nhung ai da xac nhan
					listTimeSheetLunch.removeIf(c -> !c.getStatus().equals(1));

					List<TimeSheetLunch> listca1 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca1.removeIf(c -> !c.getShifttypeid_link().equals(4) || !c.isIslunch());

					List<TimeSheetLunch> listca2 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca2.removeIf(c -> !c.getShifttypeid_link().equals(5) || !c.isIslunch());

					List<TimeSheetLunch> listca3 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca3.removeIf(c -> !c.getShifttypeid_link().equals(6) || !c.isIslunch());

					List<TimeSheetLunch> listca4 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca4.removeIf(c -> !c.getShifttypeid_link().equals(7) || !c.isIslunch());

					List<TimeSheetLunch> listca5 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca5.removeIf(c -> !c.getShifttypeid_link().equals(8) || !c.isIslunch());
					
					List<TimeSheetLunch> listca6 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca6.removeIf(c -> !c.getShifttypeid_link().equals(9) || !c.isIslunch());

					List<TimeSheetLunch> listca7 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca7.removeIf(c -> !c.getShifttypeid_link().equals(10) || !c.isIslunch());

					List<TimeSheetLunch> listca8 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca8.removeIf(c -> !c.getShifttypeid_link().equals(11) || !c.isIslunch());

					List<TimeSheetLunch> listca9 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca9.removeIf(c -> !c.getShifttypeid_link().equals(12) || !c.isIslunch());

					List<TimeSheetLunch> listca10 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca10.removeIf(c -> !c.getShifttypeid_link().equals(13) || !c.isIslunch());
					
					List<TimeSheetLunch> listca11 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca11.removeIf(c -> !c.getShifttypeid_link().equals(14) || !c.isIslunch());

					List<TimeSheetLunch> listca12 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca12.removeIf(c -> !c.getShifttypeid_link().equals(15) || !c.isIslunch());

					List<TimeSheetLunch> listca13 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca13.removeIf(c -> !c.getShifttypeid_link().equals(16) || !c.isIslunch());

					List<TimeSheetLunch> listca14 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca14.removeIf(c -> !c.getShifttypeid_link().equals(17) || !c.isIslunch());

					List<TimeSheetLunch> listca15 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca15.removeIf(c -> !c.getShifttypeid_link().equals(18) || !c.isIslunch());
					
					List<TimeSheetLunch> listca16 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca16.removeIf(c -> !c.getShifttypeid_link().equals(19) || !c.isIslunch());

					List<TimeSheetLunch> listca17 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca17.removeIf(c -> !c.getShifttypeid_link().equals(20) || !c.isIslunch());

					List<TimeSheetLunch> listca18 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca18.removeIf(c -> !c.getShifttypeid_link().equals(21) || !c.isIslunch());

					List<TimeSheetLunch> listca19 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca19.removeIf(c -> !c.getShifttypeid_link().equals(22) || !c.isIslunch());

					List<TimeSheetLunch> listca20 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca20.removeIf(c -> !c.getShifttypeid_link().equals(23) || !c.isIslunch());
					
					List<TimeSheetLunch> listca21 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca21.removeIf(c -> !c.getShifttypeid_link().equals(24) || !c.isIslunch());

					List<TimeSheetLunch> listca22 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca22.removeIf(c -> !c.getShifttypeid_link().equals(25) || !c.isIslunch());

					List<TimeSheetLunch> listca23 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca23.removeIf(c -> !c.getShifttypeid_link().equals(26) || !c.isIslunch());

					List<TimeSheetLunch> listca24 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca24.removeIf(c -> !c.getShifttypeid_link().equals(27) || !c.isIslunch());

					List<TimeSheetLunch> listca25 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca25.removeIf(c -> !c.getShifttypeid_link().equals(28) || !c.isIslunch());
					
					List<TimeSheetLunch> listca26 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca26.removeIf(c -> !c.getShifttypeid_link().equals(29) || !c.isIslunch());

					List<TimeSheetLunch> listca27 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca27.removeIf(c -> !c.getShifttypeid_link().equals(30) || !c.isIslunch());

					List<TimeSheetLunch> listca28 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca28.removeIf(c -> !c.getShifttypeid_link().equals(31) || !c.isIslunch());

					List<TimeSheetLunch> listca29 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca29.removeIf(c -> !c.getShifttypeid_link().equals(32) || !c.isIslunch());

					List<TimeSheetLunch> listca30 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
					listca30.removeIf(c -> !c.getShifttypeid_link().equals(33) || !c.isIslunch());

					TongHopBaoAn tonghop = new TongHopBaoAn();
					tonghop.setOrg_name(org.getName());
					tonghop.setCa1(listca1.size());
					tonghop.setCa2(listca2.size());
					tonghop.setCa3(listca3.size());
					tonghop.setCa4(listca4.size());
					tonghop.setCa5(listca5.size());
					tonghop.setCa6(listca6.size());
					tonghop.setCa7(listca7.size());
					tonghop.setCa8(listca8.size());
					tonghop.setCa9(listca9.size());
					tonghop.setCa10(listca10.size());
					tonghop.setCa11(listca11.size());
					tonghop.setCa12(listca12.size());
					tonghop.setCa13(listca13.size());
					tonghop.setCa14(listca14.size());
					tonghop.setCa15(listca15.size());
					tonghop.setCa16(listca16.size());
					tonghop.setCa17(listca17.size());
					tonghop.setCa18(listca18.size());
					tonghop.setCa19(listca19.size());
					tonghop.setCa20(listca20.size());
					tonghop.setCa21(listca21.size());
					tonghop.setCa22(listca22.size());
					tonghop.setCa23(listca23.size());
					tonghop.setCa24(listca24.size());
					tonghop.setCa25(listca25.size());
					tonghop.setCa26(listca26.size());
					tonghop.setCa27(listca27.size());
					tonghop.setCa28(listca28.size());
					tonghop.setCa29(listca29.size());
					tonghop.setCa30(listca30.size());
					tonghop.setOrgtypeid_link(org.getOrgtypeid_link());

					list.add(tonghop);
				}
			}

			response.data = list;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<get_TongHopBaoAn_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<get_TongHopBaoAn_response>(HttpStatus.OK);
		}
	}
	
//	@RequestMapping(value = "/tonghopbaoan", method = RequestMethod.POST)
//	public ResponseEntity<get_TongHopBaoAn_response> TongHopBaoAn(@RequestBody get_tonghopbaoan_request entity,
//			HttpServletRequest request) {
//		get_TongHopBaoAn_response response = new get_TongHopBaoAn_response();
//		try {
//			Long orgid_link = entity.orgid_link;
//			Date date_from = entity.date_from;
//			Date date_to = entity.date_to;
//
//			date_from = commonService.getBeginOfDate(date_from);
//			date_to = commonService.getEndOfDate(date_to);
//			List<Org> list_org = orgService.getorgChildrenbyOrg(orgid_link, new ArrayList<>());
//			List<TongHopBaoAn> list = new ArrayList<TongHopBaoAn>();
//
//			// Thêm đơn vị khách vào báo cáo
//			Org org_khach = new Org();
//			org_khach.setName("Khách");
//			org_khach.setOrgtypeid_link(166);
//			org_khach.setId(orgid_link);
//
//			list_org.add(org_khach);
//
//			// Thêm báo thêm của các tổ
//			Org org_them = new Org();
//			org_them.setName("Báo bổ sung");
//			org_them.setOrgtypeid_link(999);
//			org_them.setId((long) -1);
//
//			list_org.add(org_them);
//			for (Org org : list_org) {
//				if (org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_DONVIKHACH)) {
//					List<TimeSheetLunchKhach> listTimeSheetLunchKhach = lunchkhachService.getby_nhieungay_org(date_from, date_to,
//							orgid_link);
//					
//					listTimeSheetLunchKhach.removeIf(c -> c.getAmount() == null || c.getAmount() == 0 );
//
//					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca1 = new ArrayList<>(listTimeSheetLunchKhach);
//					listTimeSheetLunchKhach_ca1.removeIf(c -> !c.getShifttypeid_link().equals((long) 4));
//
//					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca2 = new ArrayList<>(listTimeSheetLunchKhach);
//					listTimeSheetLunchKhach_ca2.removeIf(c -> !c.getShifttypeid_link().equals((long) 5));
//
//					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca3 = new ArrayList<>(listTimeSheetLunchKhach);
//					listTimeSheetLunchKhach_ca3.removeIf(c -> !c.getShifttypeid_link().equals((long) 6));
//
//					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca4 = new ArrayList<>(listTimeSheetLunchKhach);
//					listTimeSheetLunchKhach_ca4.removeIf(c -> !c.getShifttypeid_link().equals((long) 7));
//
//					List<TimeSheetLunchKhach> listTimeSheetLunchKhach_ca5 = new ArrayList<>(listTimeSheetLunchKhach);
//					listTimeSheetLunchKhach_ca5.removeIf(c -> !c.getShifttypeid_link().equals((long) 8));
//
//					TongHopBaoAn tonghop = new TongHopBaoAn();
//					tonghop.setOrg_name(org.getName());
//					tonghop.setCa1(listTimeSheetLunchKhach_ca1.size() == 0 ? 0
//							: listTimeSheetLunchKhach_ca1.get(0).getAmount());
//					tonghop.setCa2(listTimeSheetLunchKhach_ca2.size() == 0 ? 0
//							: listTimeSheetLunchKhach_ca2.get(0).getAmount());
//					tonghop.setCa3(listTimeSheetLunchKhach_ca3.size() == 0 ? 0
//							: listTimeSheetLunchKhach_ca3.get(0).getAmount());
//					tonghop.setCa4(listTimeSheetLunchKhach_ca4.size() == 0 ? 0
//							: listTimeSheetLunchKhach_ca4.get(0).getAmount());
//					tonghop.setCa5(listTimeSheetLunchKhach_ca5.size() == 0 ? 0
//							: listTimeSheetLunchKhach_ca5.get(0).getAmount());
//					tonghop.setOrgtypeid_link(org.getOrgtypeid_link());
//
//					list.add(tonghop);
//
//				} else if (org.getOrgtypeid_link().equals(999)) {
//
//				} else {
//					List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService
//							.getForTimeSheetLunchByGrantManyDay(org.getId(), date_from, date_to);
//					
//					// chi lay nhung ai da xac nhan
//					listTimeSheetLunch.removeIf(c -> !c.getStatus().equals(1));
//
//					List<TimeSheetLunch> listca1 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
//					listca1.removeIf(c -> !c.getShifttypeid_link().equals(4) || !c.isIslunch());
//
//					List<TimeSheetLunch> listca2 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
//					listca2.removeIf(c -> !c.getShifttypeid_link().equals(5) || !c.isIslunch());
//
//					List<TimeSheetLunch> listca3 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
//					listca3.removeIf(c -> !c.getShifttypeid_link().equals(6) || !c.isIslunch());
//
//					List<TimeSheetLunch> listca4 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
//					listca4.removeIf(c -> !c.getShifttypeid_link().equals(7) || !c.isIslunch());
//
//					List<TimeSheetLunch> listca5 = new ArrayList<TimeSheetLunch>(listTimeSheetLunch);
//					listca5.removeIf(c -> !c.getShifttypeid_link().equals(8) || !c.isIslunch());
//
//					TongHopBaoAn tonghop = new TongHopBaoAn();
//					tonghop.setOrg_name(org.getName());
//					tonghop.setCa1(listca1.size());
//					tonghop.setCa2(listca2.size());
//					tonghop.setCa3(listca3.size());
//					tonghop.setCa4(listca4.size());
//					tonghop.setCa5(listca5.size());
//					tonghop.setOrgtypeid_link(org.getOrgtypeid_link());
//
//					list.add(tonghop);
//				}
//			}
//
//			response.data = list;
//			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
//			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
//			return new ResponseEntity<get_TongHopBaoAn_response>(response, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
//			response.setMessage(e.getMessage());
//			return new ResponseEntity<get_TongHopBaoAn_response>(HttpStatus.OK);
//		}
//	}
//

	@PostMapping
	@RequestMapping(value = "/exportGuestRice")
	public ResponseEntity<ExcelResponse> downloadGuestRiceExcel(@RequestBody get_tonghopbaoan_request request, HttpServletRequest servletRequest) {
		ExcelResponse response = new ExcelResponse();

		List<ExcelRiceDTO> listOfData = new ArrayList<>();

		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String filePattern = "ddMMyyyy";
		SimpleDateFormat fileSDF = new SimpleDateFormat(filePattern);

		Calendar calendar = Calendar.getInstance();

		Long orgIdLink = request.orgid_link;
		Date dateFrom = commonService.getBeginOfDate(request.date_from);
		Date dateTo = commonService.getBeginOfDate(request.date_to);

		String folderPath = servletRequest.getServletContext().getRealPath("report/Export/GuestRice/");
		File uploadFolder = new File(folderPath);

		if (!uploadFolder.exists()) {
			boolean created = uploadFolder.mkdirs();
		}

		String fileName = "ComKhach" + fileSDF.format(dateFrom) + "_" + fileSDF.format(dateTo) + ".xlsx";
		String excelFilePath = folderPath + fileName;

		while (dateFrom.getTime() <= dateTo.getTime()) {
			List<TimeSheetLunchKhach> listTimeSheetLunchGuest = lunchkhachService.getby_ngay_org(dateFrom, orgIdLink);

			int numberOfMeals = 0;
			for (TimeSheetLunchKhach data : listTimeSheetLunchGuest) numberOfMeals += data.getAmount() == null ? 0 : data.getAmount();

			listOfData.add(new ExcelRiceDTO(simpleDateFormat.format(dateFrom), numberOfMeals));

			calendar.setTime(dateFrom);
			calendar.add(Calendar.DATE, 1);
			dateFrom = calendar.getTime();
		}

		try {
			File excelFile = RiceExcel.createGuestRice(excelFilePath, listOfData);
			InputStream dataInputStream = new FileInputStream(excelFile);

			response.setData(IOUtils.toByteArray(dataInputStream));
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

			dataInputStream.close();
			boolean deleted = excelFile.delete();

			return ResponseEntity.ok(response);
		} catch (Exception exception) {
			response.setData(null);
			response.setRespcode(ResponseMessage.KEY_RC_APPROVE_FAIL);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_APPROVE_FAIL));
			return ResponseEntity.badRequest().body(response);
		}
	}

	private final List<Integer> LIST_EXTRA_RICE_ID = new ArrayList<Integer>(){{
		// Ca 1
        // Ca 2
        // Ca 3
        // Ca 4
		add(8); // Ca 5
		// Ca 6

        // Ca 22
        // Ca 23
        // Ca 24
        // Ca 25
        // Ca 26
        // Ca 27
	}};

	@PostMapping
	@RequestMapping(value = "/exportRice")
	public ResponseEntity<ExcelResponse> downloadRiceData(@RequestBody get_tonghopbaoan_request request, HttpServletRequest servletRequest) {
		ExcelResponse response = new ExcelResponse();

		List<ExcelRiceDTO> listOfData = new ArrayList<>();

		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String filePattern = "ddMMyyyy";
		SimpleDateFormat fileSDF = new SimpleDateFormat(filePattern);

		Calendar calendar = Calendar.getInstance();

		Long orgIdLink = request.orgid_link;
		Date dateFrom = commonService.getBeginOfDate(request.date_from);
		Date dateTo = commonService.getBeginOfDate(request.date_to);

		String folderPath = servletRequest.getServletContext().getRealPath("report/Export/Rice/");
		File uploadFolder = new File(folderPath);

		if (!uploadFolder.exists()) {
			boolean created = uploadFolder.mkdirs();
		}

		String fileName = "TongHopComCa" + fileSDF.format(dateFrom) + "_" + fileSDF.format(dateTo) + ".xlsx";
		String excelFilePath = folderPath + fileName;

		List<Org> listOrg = orgService.getorgChildrenbyOrg(orgIdLink, new ArrayList<>());

		while (dateFrom.getTime() <= dateTo.getTime()) {
			int numberOfMeals = 0;

			for (Org org : listOrg) {
				List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunchByGrant(org.getId(), dateFrom);

				// Remove all unconfirmed people and haven't lunch
				listTimeSheetLunch.removeIf(p -> !p.getStatus().equals(1) || !p.isIslunch());

				// Remove all extra rice
				LIST_EXTRA_RICE_ID.forEach(id ->
						listTimeSheetLunch.removeIf(p -> p.getShifttypeid_link().equals(id)));

				numberOfMeals += listTimeSheetLunch.size();
			}

			listOfData.add(new ExcelRiceDTO(simpleDateFormat.format(dateFrom), numberOfMeals));

			calendar.setTime(dateFrom);
			calendar.add(Calendar.DATE, 1);
			dateFrom = calendar.getTime();
		}

		try {
			File excelFile = RiceExcel.createTotalRice(excelFilePath, listOfData);
			InputStream dataInputStream = new FileInputStream(excelFile);

			response.setData(IOUtils.toByteArray(dataInputStream));
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

			dataInputStream.close();
			boolean deleted = excelFile.delete();

			return ResponseEntity.ok(response);
		} catch (Exception exception) {
			response.setData(null);
			response.setRespcode(ResponseMessage.KEY_RC_APPROVE_FAIL);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_APPROVE_FAIL));
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping
	@RequestMapping(value = "/exportExtraRice")
	public ResponseEntity<ExcelResponse> downloadExtraRiceData(@RequestBody get_tonghopbaoan_request request, HttpServletRequest servletRequest) {
		ExcelResponse response = new ExcelResponse();

		List<ExcelRiceDTO> listOfData = new ArrayList<>();

		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String filePattern = "ddMMyyyy";
		SimpleDateFormat fileSDF = new SimpleDateFormat(filePattern);

		Calendar calendar = Calendar.getInstance();

		Long orgIdLink = request.orgid_link;
		Date dateFrom = commonService.getBeginOfDate(request.date_from);
		Date dateTo = commonService.getBeginOfDate(request.date_to);

		String folderPath = servletRequest.getServletContext().getRealPath("report/Export/ExtraRice/");
		File uploadFolder = new File(folderPath);

		if (!uploadFolder.exists()) {
			boolean created = uploadFolder.mkdirs();
		}

		String fileName = "TongHopComTangCa" + fileSDF.format(dateFrom) + "_" + fileSDF.format(dateTo) + ".xlsx";
		String excelFilePath = folderPath + fileName;

		List<Org> listOrg = orgService.getorgChildrenbyOrg(orgIdLink, new ArrayList<>());

		while (dateFrom.getTime() <= dateTo.getTime()) {
			int numberOfMeals = 0;

			for (Org org : listOrg) {
				List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunchByGrant(org.getId(), dateFrom);

				// Remove all unconfirmed people and haven't lunch
				listTimeSheetLunch.removeIf(p -> !p.getStatus().equals(1) || !p.isIslunch());

				// Remove all not extra rice
				LIST_EXTRA_RICE_ID.forEach(id ->
						listTimeSheetLunch.removeIf(p -> !p.getShifttypeid_link().equals(id)));

				numberOfMeals += listTimeSheetLunch.size();
			}

			listOfData.add(new ExcelRiceDTO(simpleDateFormat.format(dateFrom), numberOfMeals));

			calendar.setTime(dateFrom);
			calendar.add(Calendar.DATE, 1);
			dateFrom = calendar.getTime();
		}

		try {
			File excelFile = RiceExcel.createTotalExtraFile(excelFilePath, listOfData);
			InputStream dataInputStream = new FileInputStream(excelFile);

			response.setData(IOUtils.toByteArray(dataInputStream));
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

			dataInputStream.close();
			boolean deleted = excelFile.delete();

			return ResponseEntity.ok(response);
		} catch (Exception exception) {
			response.setData(null);
			response.setRespcode(ResponseMessage.KEY_RC_APPROVE_FAIL);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_APPROVE_FAIL));
			return ResponseEntity.badRequest().body(response);
		}
	}

	    @PostMapping
    @RequestMapping(value = "/exportComCa")
    public ResponseEntity<ExcelResponse> downloadComCa(@RequestBody get_tonghopbaoan_request request, HttpServletRequest servletRequest) {
        ExcelResponse response = new ExcelResponse();

        // Tạo map chứa dữ liệu để vẽ bảng
        HashMap<Date, HashMap<String, Integer>> allData = new LinkedHashMap<>();

        // Tạo định dạng cho ngày tháng truyền vào
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String filePattern = "ddMMyyyy";
        SimpleDateFormat fileSDF = new SimpleDateFormat(filePattern);

        Calendar calendar = Calendar.getInstance();

        // Lấy dữ liệu từ request
        Long orgIdLink = request.orgid_link;
        Date dateFrom = commonService.getBeginOfDate(request.date_from);
        Date dateTo = commonService.getBeginOfDate(request.date_to);

        // Tạo thư mục để chứa file xuất ra
        String folderPath = servletRequest.getServletContext().getRealPath("report/Export/ExtraRice/");
        File uploadFolder = new File(folderPath);

        if (!uploadFolder.exists()) {
            boolean created = uploadFolder.mkdirs();
        }

        String fileName = "ComCa" + fileSDF.format(dateFrom) + "_" + fileSDF.format(dateTo) + ".xlsx";
        String excelFilePath = folderPath + fileName;

        Org factory = orgService.findOne(orgIdLink);
        List<Org> listOrg = orgService.getorgChildrenbyOrg(orgIdLink, new ArrayList<>()); // Các đơn vị trong phân xưởng

        // TODO: Lấy lấy thời gian ăn
        String timeText = "";

        while (dateFrom.getTime() <= dateTo.getTime()) {
            HashMap<String, Integer> data = new LinkedHashMap<>();

            for (Org org : listOrg) {
                List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunchByGrant(org.getId(), dateFrom);

                listTimeSheetLunch.removeIf(p -> !p.getStatus().equals(1) || !p.isIslunch());
                LIST_EXTRA_RICE_ID.forEach(id ->
                        listTimeSheetLunch.removeIf(p -> p.getShifttypeid_link().equals(id)));

                data.put(org.getName(), listTimeSheetLunch.size());
            }

            allData.put(dateFrom, data);

            calendar.setTime(dateFrom);
            calendar.add(Calendar.DATE, 1);
            dateFrom = calendar.getTime();
        }

        try {
            File excelFile = RiceExcel.exportComCa(excelFilePath, factory.getName(), timeText, allData);
            InputStream dataInputStream = new FileInputStream(excelFile);

            response.setData(IOUtils.toByteArray(dataInputStream));
            response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

            dataInputStream.close();
            boolean deleted = excelFile.delete();

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            response.setData(null);
            response.setRespcode(ResponseMessage.KEY_RC_APPROVE_FAIL);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_APPROVE_FAIL));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    @RequestMapping(value = "/exportComTangCa")
    public ResponseEntity<ExcelResponse> downloadComTangCa(@RequestBody get_tonghopbaoan_request request, HttpServletRequest servletRequest) {
        ExcelResponse response = new ExcelResponse();

        // Tạo map chứa dữ liệu để vẽ bảng
        HashMap<Date, HashMap<String, Integer>> allData = new LinkedHashMap<>();

        // Tạo định dạng cho ngày tháng truyền vào
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String filePattern = "ddMMyyyy";
        SimpleDateFormat fileSDF = new SimpleDateFormat(filePattern);

        Calendar calendar = Calendar.getInstance();

        // Lấy dữ liệu từ request
        Long orgIdLink = request.orgid_link;
        Date dateFrom = commonService.getBeginOfDate(request.date_from);
        Date dateTo = commonService.getBeginOfDate(request.date_to);

        // Tạo thư mục để chứa file xuất ra
        String folderPath = servletRequest.getServletContext().getRealPath("report/Export/ExtraRice/");
        File uploadFolder = new File(folderPath);

        if (!uploadFolder.exists()) {
            boolean created = uploadFolder.mkdirs();
        }

        String fileName = "ComTangCa" + fileSDF.format(dateFrom) + "_" + fileSDF.format(dateTo) + ".xlsx";
        String excelFilePath = folderPath + fileName;

        Org factory = orgService.findOne(orgIdLink);
        List<Org> listOrg = orgService.getorgChildrenbyOrg(orgIdLink, new ArrayList<>()); // Các đơn vị trong phân xưởng

        while (dateFrom.getTime() <= dateTo.getTime()) {
            HashMap<String, Integer> data = new LinkedHashMap<>();

            for (Org org : listOrg) {
                List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunchByGrant(org.getId(), dateFrom);

                listTimeSheetLunch.removeIf(p -> !p.getStatus().equals(1) || !p.isIslunch());
                LIST_EXTRA_RICE_ID.forEach(id ->
                        listTimeSheetLunch.removeIf(p -> !p.getShifttypeid_link().equals(id)));

                data.put(org.getName(), listTimeSheetLunch.size());
            }

            allData.put(dateFrom, data);

            calendar.setTime(dateFrom);
            calendar.add(Calendar.DATE, 1);
            dateFrom = calendar.getTime();
        }

        try {
            File excelFile = RiceExcel.exportComTangCa(excelFilePath, factory.getName(), allData);
            InputStream dataInputStream = new FileInputStream(excelFile);

            response.setData(IOUtils.toByteArray(dataInputStream));
            response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

            dataInputStream.close();
            boolean deleted = excelFile.delete();

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            response.setData(null);
            response.setRespcode(ResponseMessage.KEY_RC_APPROVE_FAIL);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_APPROVE_FAIL));
            return ResponseEntity.badRequest().body(response);
        }
    }


	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> updateStatus(@RequestBody TimeSheetLunch_updateStatus_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			Date date = new Date();
			List<Long> selectIds = entity.selectIds;
			Long orgid_link = entity.orgid_link;
			Date workingdate = entity.workingdate;
			String comment = entity.comment;
			Long shifttypeid_link = entity.shifttypeid_link;

			Org org = orgService.findOne(orgid_link);
			List<TimeSheetLunch> listTimeSheetLunch_select = new ArrayList<TimeSheetLunch>();

			if (org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_XUONGSX)) {
//				listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunch(orgid_link, workingdate);
				if (selectIds.size() > 0) {
					listTimeSheetLunch_select = timeSheetLunchService.getBy_multiShift(orgid_link, workingdate,
							selectIds);
				}
			} else if (!org.getId().equals((long) 1)) {
//				listTimeSheetLunch = timeSheetLunchService.getForUpdateStatusTimeSheetLunch(orgid_link, workingdate);
				if (selectIds.size() > 0) {
					listTimeSheetLunch_select = timeSheetLunchService.getBy_multiShift(orgid_link, workingdate,
							selectIds);
				}
			}

//			System.out.println("orgid_link " + orgid_link);
//			System.out.println("workingdate " + workingdate);
//			System.out.println("selectIds " + selectIds);
//			System.out.println("listTimeSheetLunch_select " + listTimeSheetLunch_select.size());
			// getForUpdateStatusTimeSheetLunch

			for (TimeSheetLunch timeSheetLunch : listTimeSheetLunch_select) {
				if (timeSheetLunch.getStatus().equals(0)) {
//					if (shifttypeid_link != null) {
//						if (timeSheetLunch.getShifttypeid_link().equals(shifttypeid_link.intValue())) {
//							timeSheetLunch.setComment(comment);
//							timeSheetLunch.setIs_bo_sung(true);
//						}
//					}

					timeSheetLunch.setStatus(1);
					timeSheetLunch.setTime_approve(date);
					timeSheetLunchService.save(timeSheetLunch);
				}

			}

//			for (TimeSheetLunch timeSheetLunch : listTimeSheetLunch_unselect) {
//				timeSheetLunch.setStatus(0);
//				timeSheetLunch.setTime_approve(null);
//				timeSheetLunchService.save(timeSheetLunch);
//			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/cancel_approve", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> cancel_approve(@RequestBody TimeSheetLunch_updateStatus_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			String comment = entity.comment;
			Date workingdate = entity.workingdate; // ngay
			Integer shifttypeid_link = entity.shifttypeid_link.intValue(); // bang timesheet_shift_type
			Long orgid_link = entity.orgid_link; // p/xuong, to chuyen
			
			// tim danh sach cac timesheet lunch da xac nhan theo orgid_link, shifttypeid_link, workingdate
			// update comment va status -> 0
			
			Org org = orgService.findOne(orgid_link);
			List<TimeSheetLunch> timeSheetLunch_list = new ArrayList<TimeSheetLunch>();
			if(org.getId().equals((long) 1)) {
				// dha
			}else if(org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_XUONGSX)) {
				// phan xuong
				timeSheetLunch_list = timeSheetLunchService.getByOrg_Shift_DangKy(
						orgid_link, shifttypeid_link, workingdate);
			}else {
				// to chuyen ...
				timeSheetLunch_list = timeSheetLunchService.getByOrg_Shift_DangKy(
						orgid_link, shifttypeid_link, workingdate);
			}
			
//			System.out.println(timeSheetLunch_list.size());
			for(TimeSheetLunch timeSheetLunch : timeSheetLunch_list) {
				timeSheetLunch.setComment(comment);
				timeSheetLunch.setStatus(0);
				timeSheetLunchService.save(timeSheetLunch);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/isconfirm", method = RequestMethod.POST)
	public ResponseEntity<TimeSheetLunch_isconfirm_response> isconfirm(@RequestBody TimeSheetLunch_request entity,
			HttpServletRequest request) {
		TimeSheetLunch_isconfirm_response response = new TimeSheetLunch_isconfirm_response();
		try {
			Long orgid_link = entity.orgid_link;
			Date date = entity.date;

			Org org = orgService.findOne(orgid_link);
			List<TimeSheetLunch> listTimeSheetLunch = new ArrayList<TimeSheetLunch>();
			if (org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_XUONGSX)) {
				listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunch(orgid_link, date);
			} else {
				listTimeSheetLunch = timeSheetLunchService.getForUpdateStatusTimeSheetLunch(orgid_link, date);
			}

//			List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getForTimeSheetLunch(orgid_link, date);

			if (listTimeSheetLunch.size() > 0) {
				TimeSheetLunch temp = listTimeSheetLunch.get(0);
				if (temp.getStatus() == 0) {
					response.isConfirm = false;
				}
				if (temp.getStatus() == 1) {
					response.isConfirm = true;
				}
			} else {
				response.isConfirm = false;
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimeSheetLunch_isconfirm_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimeSheetLunch_isconfirm_response>(HttpStatus.OK);
		}
	}


	@RequestMapping(value = "/getListCheckCaAnAuto", method = RequestMethod.POST)
	public ResponseEntity<TimeSheetLunch_Binding_response> getListCheckCaAnAuto(
			@RequestBody TimeSheetLunch_request entity, HttpServletRequest request) {
		TimeSheetLunch_Binding_response response = new TimeSheetLunch_Binding_response();
		try {
			Long orgid_link = entity.orgid_link;
			Date date = entity.date;
			List<TimesheetShiftTypeOrg> listCa = entity.listCa;

			List<TimeSheetLunch_Binding> timeSheetLunch_Binding_list = new ArrayList<TimeSheetLunch_Binding>();

			Org org = orgService.findOne(orgid_link);
			// lấy danh sách nghỉ trong ngày
			List<TimesheetAbsence> timesheetAbsence_list = new ArrayList<TimesheetAbsence>();
			if (org.getOrgtypeid_link().equals(OrgType.ORG_TYPE_XUONGSX)) {
				timesheetAbsence_list = timesheetAbsenceService.getByOrgAndDate(orgid_link, date);
			} else {
				timesheetAbsence_list = timesheetAbsenceService.GetByOrgPhongBanAndDate(orgid_link, date);
			}

			for (TimesheetAbsence timesheetAbsence : timesheetAbsence_list) {
				Date date_abs_from = timesheetAbsence.getAbsencedate_from();
				Date date_abs_to = timesheetAbsence.getAbsencedate_to();

				for (TimesheetShiftTypeOrg timesheetShiftTypeOrg : listCa) {
					timesheetShiftTypeOrg = timesheetshifttypeOrgService.findOne(timesheetShiftTypeOrg.getId());

					Integer fromHour = timesheetShiftTypeOrg.getFrom_hour();
					Integer fromMinute = timesheetShiftTypeOrg.getFrom_minute();
					Integer toHour = timesheetShiftTypeOrg.getTo_hour();
					Integer toMinute = timesheetShiftTypeOrg.getTo_minute();
					Boolean is_atnight = timesheetShiftTypeOrg.getIs_atnight();

					// (StartA <= EndB) and (EndA >= StartB) -> overlap
					Calendar cal = Calendar.getInstance();
					Date caFrom = date;
					cal.setTime(caFrom);
					cal.set(Calendar.HOUR_OF_DAY, fromHour);
					cal.set(Calendar.MINUTE, fromMinute);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					caFrom = cal.getTime();

					Date caTo = date;
					cal.setTime(caTo);
					if (is_atnight != null) {
						if (is_atnight) {
							cal.add(Calendar.DATE, 1);
						}
					}
					cal.set(Calendar.HOUR_OF_DAY, toHour);
					cal.set(Calendar.MINUTE, toMinute);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					caTo = cal.getTime();

//					System.out.println("-----");
//					System.out.println(date_abs_from);
//					System.out.println(date_abs_to);
//					System.out.println(caFrom);
//					System.out.println(caTo);
//					System.out.println(date_abs_from.before(caTo));
//					System.out.println(caFrom.before(date_abs_to));

					if (date_abs_from.before(caTo) && caFrom.before(date_abs_to)) {
						TimeSheetLunch_Binding newBinding = new TimeSheetLunch_Binding();
						newBinding.setPersonnelid_link(timesheetAbsence.getPersonnelid_link());
						newBinding.setIsCheck(true);

//						String name = timesheetShiftTypeOrg.getName();
						Long id = timesheetShiftTypeOrg.getTimesheet_shift_type_id_link();
//						System.out.println(timesheetShiftTypeOrg.getTimesheet_shift_type_id_link());
//						System.out.println(name);
						if (id.equals((long) 4)
//								&& name.equals("Ca ăn 1")
						) {
							newBinding.setLunchShift(1);
						}
						if (id.equals((long) 5)
//								&& name.equals("Ca ăn 2")
						) {
							newBinding.setLunchShift(2);
						}
						if (id.equals((long) 6)
//								&& name.equals("Ca ăn 3")
						) {
							newBinding.setLunchShift(3);
						}
						if (id.equals((long) 7)
//								&& name.equals("Ca ăn 4")
						) {
							newBinding.setLunchShift(4);
						}
						if (id.equals((long) 8)
//								&& name.equals("Ca ăn 5")
						) {
							newBinding.setLunchShift(5);
						}
						timeSheetLunch_Binding_list.add(newBinding);
					}
				}
//				TimeSheetLunch_Binding a = new TimeSheetLunch_Binding();
			}

			response.data = timeSheetLunch_Binding_list;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimeSheetLunch_Binding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimeSheetLunch_Binding_response>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getForTimeSheetLunch_Mobile", method = RequestMethod.POST)
	public ResponseEntity<TimeSheetLunch_Binding_response> getForTimeSheetLunch_Mobile(
			@RequestBody TimeSheetLunch_request entity, HttpServletRequest request) {
		TimeSheetLunch_Binding_response response = new TimeSheetLunch_Binding_response();
		try {
			Long orgid_link = entity.orgid_link; // id phan xuong
			Date date = entity.date; // ngay

			List<TimeSheetLunch_Binding> result = new ArrayList<TimeSheetLunch_Binding>();

			// lay danh sach ca cua phan xuong
//			List<TimesheetShiftTypeOrg> shift_list = timesheetshifttypeOrgService.getByOrgid_link_CaAn(orgid_link);
			List<TimesheetShiftTypeOrg> shift_list = timesheetshifttypeOrgService.getByOrgid_link_CaAn_active(orgid_link);

			for (TimesheetShiftTypeOrg shift : shift_list) {
				TimeSheetLunch_Binding newTimeSheetLunch_Binding = new TimeSheetLunch_Binding();
				String name = shift.getName() + " ";
				name += shift.getFrom_hour() < 10 ? "0" + shift.getFrom_hour() : shift.getFrom_hour();
				name += ":";
				name += shift.getFrom_minute() < 10 ? "0" + shift.getFrom_minute() : shift.getFrom_minute();
				name += " - ";
				name += shift.getTo_hour() < 10 ? "0" + shift.getTo_hour() : shift.getTo_hour();
				name += ":";
				name += shift.getTo_minute() < 10 ? "0" + shift.getTo_minute() : shift.getTo_minute();
				newTimeSheetLunch_Binding.setCaName(name);

//				System.out.println(name);

				// sl

//				List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getByOrg_Shift(orgid_link,
//						shift.getTimesheet_shift_type_id_link().intValue(), date);
				List<TimeSheetLunch> listTimeSheetLunch_DangKy = timeSheetLunchService.getByOrg_Shift_DangKy(orgid_link,
						shift.getTimesheet_shift_type_id_link().intValue(), date);
				List<TimeSheetLunch> listTimeSheetLunch_Them = timeSheetLunchService.getByOrg_Shift_Them(orgid_link,
						shift.getTimesheet_shift_type_id_link().intValue(), date);
				newTimeSheetLunch_Binding.setSoDangKy(listTimeSheetLunch_DangKy.size());
				newTimeSheetLunch_Binding.setSoThem(listTimeSheetLunch_Them.size());
				
				// Tinh so khach - // orgid_link, shifttype_orgid_link, date
				Long shifttype_orgid_link = shift.getId();
				List<TimeSheetLunchKhach> TimeSheetLunchKhach_list = timeSheetLunchKhachService.getbyCa_ngay_org(shifttype_orgid_link, date, orgid_link);
				Integer khach_amount = 0;
				if(TimeSheetLunchKhach_list.size() > 0) {
					khach_amount = TimeSheetLunchKhach_list.get(0).getAmount() == null ? 0 : TimeSheetLunchKhach_list.get(0).getAmount();
				}
//				System.out.println("-----");
//				System.out.println(TimeSheetLunchKhach_list.size());
//				System.out.println(shifttype_orgid_link);
//				System.out.println(date);
//				System.out.println(orgid_link);
//				System.out.println(khach_amount);
				
				newTimeSheetLunch_Binding.setSoKhach(khach_amount);
				newTimeSheetLunch_Binding.setSoTong(listTimeSheetLunch_DangKy.size() + listTimeSheetLunch_Them.size() + khach_amount);

				newTimeSheetLunch_Binding.setTimesheet_shift_type_id_link(shift.getTimesheet_shift_type_id_link());
				newTimeSheetLunch_Binding.setTimesheet_shift_type_org_id_link(shift.getId());

				result.add(newTimeSheetLunch_Binding);
			}

			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimeSheetLunch_Binding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimeSheetLunch_Binding_response>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getForTimeSheetLunch_Mobile_Detail", method = RequestMethod.POST)
	public ResponseEntity<TimeSheetLunch_Binding_response> getForTimeSheetLunch_Mobile_Detail(
			@RequestBody TimeSheetLunch_request entity, HttpServletRequest request) {
		TimeSheetLunch_Binding_response response = new TimeSheetLunch_Binding_response();
		try {
			Long orgid_link = entity.orgid_link; // id phan xuong
			Date date = entity.date; // ngay
			Long timesheet_shift_type_id_link = entity.timesheet_shift_type_id_link;
//			Long timesheet_shift_type_org_id_link = entity.timesheet_shift_type_org_id_link;

			List<TimeSheetLunch_Binding> result = new ArrayList<TimeSheetLunch_Binding>();
			List<Org> org_list = orgService.getorgChildrenbyOrg(orgid_link, new ArrayList<>());

			// Thêm đơn vị khách vào báo cáo
			Org org_khach = new Org();
			org_khach.setName("Khách");
			org_khach.setCode("Khách");
			org_khach.setOrgtypeid_link(166);
			org_khach.setId(orgid_link);

			org_list.add(org_khach);

			for (Org org : org_list) {
				TimeSheetLunch_Binding newTimeSheetLunch_Binding = new TimeSheetLunch_Binding();
				newTimeSheetLunch_Binding.setOrgCode(org.getCode());
				newTimeSheetLunch_Binding.setOrgName(org.getName());
				newTimeSheetLunch_Binding.setOrgType(org.getOrgtypeid_link());
				if (org.getOrgtypeid_link().equals(166)) {
					List<TimeSheetLunchKhach> list_lunch_khach = lunchkhachService
							.getbyCa_ngay_org(timesheet_shift_type_id_link, date, orgid_link);
					if (list_lunch_khach.size() > 0) {
						newTimeSheetLunch_Binding.setSoDangKy(list_lunch_khach.get(0).getAmount());
						newTimeSheetLunch_Binding.setSoTong(list_lunch_khach.get(0).getAmount());
					}

				} else {

					List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService.getByOrg_Shift(org.getId(),
							timesheet_shift_type_id_link.intValue(), date);
					List<TimeSheetLunch> listTimeSheetLunch_DangKy = timeSheetLunchService
							.getByOrg_Shift_DangKy(org.getId(), timesheet_shift_type_id_link.intValue(), date);
					List<TimeSheetLunch> listTimeSheetLunch_Them = timeSheetLunchService
							.getByOrg_Shift_Them(org.getId(), timesheet_shift_type_id_link.intValue(), date);
					newTimeSheetLunch_Binding.setSoDangKy(listTimeSheetLunch_DangKy.size());
					newTimeSheetLunch_Binding.setSoThem(listTimeSheetLunch_Them.size());
					newTimeSheetLunch_Binding.setSoTong(listTimeSheetLunch.size());

				}
				result.add(newTimeSheetLunch_Binding);
			}

			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<TimeSheetLunch_Binding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<TimeSheetLunch_Binding_response>(HttpStatus.OK);
		}
	}

//	@RequestMapping(value = "/getForTimeSheetLunch_Mobile_old", method = RequestMethod.POST)
//	public ResponseEntity<TimeSheetLunch_Binding_response> getForTimeSheetLunch_Mobile_old(
//			@RequestBody TimeSheetLunch_request entity, HttpServletRequest request) {
//		TimeSheetLunch_Binding_response response = new TimeSheetLunch_Binding_response();
//		try {
//			Long orgid_link = entity.orgid_link; // id phan xuong
//			Date date = entity.date; // ngay
//
//			List<TimeSheetLunch_Binding> result = new ArrayList<TimeSheetLunch_Binding>();
//
//			// lay danh sach cac to cua phan xuong
////			List<Org> org_list = orgService.getByParentId_for_TimeSheetLunchMobile(orgid_link);
//			List<Org> org_list = orgService.getorgChildrenbyOrg(orgid_link, new ArrayList<>());
//
////			System.out.println(org_list.size());
//
//			for (Org org : org_list) {
//				TimeSheetLunch_Binding newTimeSheetLunch_Binding = new TimeSheetLunch_Binding();
//				newTimeSheetLunch_Binding.setOrgId(org.getId());
//				newTimeSheetLunch_Binding.setOrgCode(org.getCode());
//				newTimeSheetLunch_Binding.setOrgName(org.getName());
//				newTimeSheetLunch_Binding.setOrgType(org.getOrgtypeid_link());
//
//				Integer sumCa1 = 0, sumCa2 = 0, sumCa3 = 0, sumCa4 = 0, sumCa5 = 0;
//				List<TimeSheetLunch> listTimeSheetLunch = timeSheetLunchService
//						.getForTimeSheetLunch_byOrg_Date(org.getId(), date);
//				for (TimeSheetLunch timeSheetLunch : listTimeSheetLunch) {
//					Integer shifttypeid_link = timeSheetLunch.getShifttypeid_link();
//					TimesheetShiftType timesheetShiftType = timesheetshifttypeService.findOne(shifttypeid_link);
//					String shiftName = timesheetShiftType.getName();
//					Long id = timesheetShiftType.getId();
//					if (id.equals((long) 4) 
////							&& shiftName.equals("Ca ăn 1")
//					) {
//						sumCa1++;
//					}
//					if (id.equals((long) 5)
////							&& shiftName.equals("Ca ăn 2")
//					) {
//						sumCa2++;
//					}
//					if (id.equals((long) 6)
////							&& shiftName.equals("Ca ăn 3")
//					) {
//						sumCa3++;
//					}
//					if (id.equals((long) 7)
////							&& shiftName.equals("Ca ăn 4")
//					) {
//						sumCa4++;
//					}
//					if (id.equals((long) 8)
////							&& shiftName.equals("Ca ăn 5")
//					) {
//						sumCa5++;
//					}
//				}
//
//				newTimeSheetLunch_Binding.setSumCa1(sumCa1);
//				newTimeSheetLunch_Binding.setSumCa2(sumCa2);
//				newTimeSheetLunch_Binding.setSumCa3(sumCa3);
//				newTimeSheetLunch_Binding.setSumCa4(sumCa4);
//				newTimeSheetLunch_Binding.setSumCa5(sumCa5);
//				result.add(newTimeSheetLunch_Binding);
//			}
//
//			response.data = result;
//			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
//			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
//			return new ResponseEntity<TimeSheetLunch_Binding_response>(response, HttpStatus.OK);
//		} catch (Exception e) {
//			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
//			response.setMessage(e.getMessage());
//			return new ResponseEntity<TimeSheetLunch_Binding_response>(HttpStatus.OK);
//		}
//	}
}
