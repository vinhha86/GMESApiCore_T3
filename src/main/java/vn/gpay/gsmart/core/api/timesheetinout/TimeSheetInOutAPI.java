package vn.gpay.gsmart.core.api.timesheetinout;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.io.Files;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.gpay.gsmart.core.api.org.GetOrgById_response;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.gpay.gsmart.core.api.timesheet_absence.TimesheetAbsenceAPI;
import vn.gpay.gsmart.core.attributevalue.Attributevalue;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.org.OrgServiceImpl;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.personel.IPersonnel_Service;
import vn.gpay.gsmart.core.personel.Personel;
import vn.gpay.gsmart.core.timesheetinout.TimeSheetDaily;
import vn.gpay.gsmart.core.timesheetinout.TimeSheetInOut;
import vn.gpay.gsmart.core.timesheetinout.TimeSheetMonth;
import vn.gpay.gsmart.core.utils.AtributeFixValues;
import vn.gpay.gsmart.core.utils.HttpPost;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/timesheetinout")
public class TimeSheetInOutAPI {
	@Autowired
	IPersonnel_Service personService;
    @Autowired
    IOrgService orgService;

	// lấy tất cả danh sách
	@RequestMapping(value = "/getall", method = RequestMethod.POST)
	public ResponseEntity<TimeSheetInOut_load_response> timesheetinout_GetAll(
			@RequestBody TimeSheetInOut_load_request entity) {
		TimeSheetInOut_load_response response = new TimeSheetInOut_load_response();
		try {
			String date_from = entity.fromdate;
			String date_to = entity.todate;
			long orgid_link = entity.orgid_link;

			String urlPush = AtributeFixValues.url_timesheet + "/timesheet/getlist";
			URL url = new URL(urlPush);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
//            conn.setRequestProperty("authorization", token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("POST");

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();

			// truyen param theo
			appParNode.put("date_from", date_from);
			appParNode.put("date_to", date_to);
			appParNode.put("orgid_link", orgid_link);
			String jsonReq = objectMapper.writeValueAsString(appParNode);

			OutputStream os = conn.getOutputStream();
			os.write(jsonReq.getBytes());
			os.flush();

			String result = "";
			String line;

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();

			conn.disconnect();

			DateFormat df_gio = new SimpleDateFormat("H:m:s");
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			DateFormat df_ngay = new SimpleDateFormat("dd-MM-yyyy");
			List<TimeSheetInOut> lsttimesheetinout = objectMapper.readValue(result,
					new TypeReference<List<TimeSheetInOut>>() {
					});
			for (TimeSheetInOut inout : lsttimesheetinout) {
				Personel person = personService.getPersonelBycode(inout.getPersonel_code());
				if (person != null) {
					inout.setFullname(person.getFullname());
					inout.setPersonel_code(person.getCode());
					String time = df_gio.format(df2.parse(inout.getTime()));
					String ngay = df_ngay.format(df2.parse(inout.getTime()));
					inout.setTime(time);
					inout.setDay(ngay);
				}

			}
			lsttimesheetinout.removeIf(c -> c.getFullname() == null);
			response.data = lsttimesheetinout;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			System.out.println(e.getMessage());
		}
		return new ResponseEntity<TimeSheetInOut_load_response>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/get_daily", method = RequestMethod.POST)
	public ResponseEntity<getDailyResponse> getDaily(@RequestBody getDailyRequest entity) {
		getDailyResponse response = new getDailyResponse();
		try {
			int month = entity.month;
			int year = entity.year;
			long grantid_link = entity.grantid_link;
			long orgid_link = entity.orgid_link;

			String urlPush = AtributeFixValues.url_timesheet + "/timesheet/getlist_daily";
			URL url = new URL(urlPush);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
//            conn.setRequestProperty("authorization", token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("POST");

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();

			// truyen param theo
			appParNode.put("month", month);
			appParNode.put("year", year);
			appParNode.put("orgid_link", orgid_link);
			appParNode.put("grantid_link", grantid_link);
			appParNode.put("personnel_code", entity.personnel_code);
			appParNode.put("isdoublecheck", entity.isdoublecheck);
			String jsonReq = objectMapper.writeValueAsString(appParNode);

			OutputStream os = conn.getOutputStream();
			os.write(jsonReq.getBytes());
			os.flush();

			String result = "";
			String line;

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();

			conn.disconnect();

			List<TimeSheetDaily> lsttimesheetinout = objectMapper.readValue(result,
					new TypeReference<List<TimeSheetDaily>>() {
					});
//			List<TimeSheetDaily> list_remove = new ArrayList<TimeSheetDaily>();

			// Lay ds nhan vien theo orgid
//			List<Personel> list_person = personService.getby_org(orgid_link, 1);
//			Map<Integer, Personel> map_person = new HashedMap<>();
//			for (Personel person : list_person) {
//				int i_person_code = Integer.parseInt(person.getCode());
//				map_person.put(i_person_code, person);
//			}

//			for (TimeSheetDaily daily : lsttimesheetinout) {
				// Neu nhan su co trong danh sach nhan su cua don vi thi moi tinh, neu khong
				// loai khoi danh sach
//				if (map_person.containsKey(Integer.parseInt(daily.getPersonnel_code()))) {
//					Personel person = map_person.get(Integer.parseInt(daily.getPersonnel_code()));
//
//					daily.setFullname(person.getFullname());
//					daily.setPersonnel_code(person.getCode());
//
//					if (grantid_link != 0) {
//						if (!person.getOrgid_link().equals(grantid_link)) {
//							list_remove.add(daily);
//						}
//					}
//				} else {
//					list_remove.add(daily);
//				}
				
//				Personel person = personService.getPersonelBycode_orgmanageid_link(daily.getPersonnel_code(),orgid_link);
//				if(person!=null) {
//					daily.setFullname(person.getFullname());
//					daily.setPersonnel_code(person.getCode());
//				}
//				if(grantid_link!= 0) {
//					if(!person.getOrgid_link().equals(grantid_link)) {
//						list_remove.add(daily);
//					}
//				}
//			}
//			lsttimesheetinout.removeIf(c -> c.getFullname() == null);
//			lsttimesheetinout.removeAll(list_remove);

			response.data = lsttimesheetinout;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			System.out.println(e.getMessage());
		}
		return new ResponseEntity<getDailyResponse>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/calculate_daily", method = RequestMethod.POST)
	public ResponseEntity<getDailyResponse> calculateDaily(@RequestBody getDailyRequest entity) {
		getDailyResponse response = new getDailyResponse();
		try {
			long orgid_link = entity.orgid_link;

			String urlPost = AtributeFixValues.url_timesheet + "/timesheet/calculate_daily";

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();

			// truyen param theo
			appParNode.put("orgid_link", orgid_link);
			appParNode.put("year", entity.year);
			appParNode.put("month", entity.month);
			appParNode.put("day", entity.day);
			String jsonReq = objectMapper.writeValueAsString(appParNode);

			HttpPost http = new HttpPost();
			String result = http.getDataFromHttpPost(jsonReq, urlPost);
			if ("\"OK\"".equals(result))
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			else
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<getDailyResponse>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/calculate_month", method = RequestMethod.POST)
	public ResponseEntity<getDailyResponse> calculateMonth(@RequestBody getDailyRequest entity) {
		getDailyResponse response = new getDailyResponse();
		try {
			long orgid_link = entity.orgid_link;
			int month = entity.month;
			int year = entity.year;

			String urlPost = AtributeFixValues.url_timesheet + "/timesheet/calculate_month";

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();

			// truyen param theo
			appParNode.put("month", month);
			appParNode.put("year", year);
			appParNode.put("orgid_link", orgid_link);
			String jsonReq = objectMapper.writeValueAsString(appParNode);

			HttpPost http = new HttpPost();
			String result = http.getDataFromHttpPost(jsonReq, urlPost);
			if ("\"OK\"".equals(result))
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			else
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<getDailyResponse>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/get_timesheet_month", method = RequestMethod.POST)
	public ResponseEntity<GetTimeSheetMonthResponse> getTimeMonth(@RequestBody getDailyRequest entity) {
		GetTimeSheetMonthResponse response = new GetTimeSheetMonthResponse();
		try {
			long orgid_link = entity.orgid_link;
			int month = entity.month;
			int year = entity.year;

			String urlPost = AtributeFixValues.url_timesheet + "/timesheet/get_timesheet_month";

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();

			// truyen param theo
			appParNode.put("month", month);
			appParNode.put("year", year);
			appParNode.put("orgid_link", orgid_link);
			String jsonReq = objectMapper.writeValueAsString(appParNode);

			HttpPost http = new HttpPost();
			String result = http.getDataFromHttpPost(jsonReq, urlPost);
			if ("\"ERR\"".equals(result))
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			else {
				List<TimeSheetMonth> lsttimesheetmonth = objectMapper.readValue(result,
						new TypeReference<List<TimeSheetMonth>>() {
						});
				response.data = lsttimesheetmonth;
			}

			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<GetTimeSheetMonthResponse>(response, HttpStatus.OK);

	}
	
    private final Logger LOGGER = LoggerFactory.getLogger(TimeSheetInOutAPI.class);
    @RequestMapping (value = "/exportExcelBaoCao_Cong", method = RequestMethod.POST)
    public ResponseEntity<BaoCaoCong_Excel_response> downloadBaoCaoCong(@RequestBody BaoCaoCong_Excel_request request,
        HttpServletRequest servletRequest) {
        BaoCaoCong_Excel_response response = new BaoCaoCong_Excel_response();
//        System.out.println("Ok Ok");
        try {
//            System.out.println("Ok 1");
            long orgid_link = request.orgid_link;
            GetOrgById_response org = new GetOrgById_response();
            org.data = orgService.findOne(orgid_link);
            String orgName= org.data.getName();

            int month = request.month;
            int year = request.year;
            System.out.println(year);
            String folderPath = servletRequest.getServletContext().getRealPath("report/Export/BaoCaoCong");
            File uploadFolder = new File(folderPath);

            if (!uploadFolder.exists()) {
                boolean created = uploadFolder.mkdirs();
            }
            Date current_time = new Date();
            File FileExport = new File(uploadFolder + "/BaoCaoCong_month.xlsx");

            File FileCopy = new File(uploadFolder + "/Template_timesheet_daily_" + current_time.getTime() + ".xlsx"); // copy cua
            // template
            // de chinh
            // sua tren
            // do
            File file = new File(uploadFolder + "/BangBaoCaoCong_month" + request.month + "_" + request.orgid_link + "_" + current_time.getTime() + ".xlsx"); // file de export
            // tao file copy cua template
            Files.copy(FileExport, FileCopy);
            FileInputStream is_filecopy = new FileInputStream(FileCopy);
//            System.out.println("Ok 1");
            String urlPost = AtributeFixValues.url_timesheet + "/timesheet/get_timesheet_month";
            String fileName = "BaoCaoNS_" + "month"+ month+"year"+year + ".xlsx";
            String excelFilePath = folderPath + fileName;
//            System.out.println("Ok 2");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode appParNode = objectMapper.createObjectNode();

            // truyen param theo
            appParNode.put("month", month);
            appParNode.put("year", year);
            appParNode.put("orgid_link", orgid_link);
            String jsonReq = objectMapper.writeValueAsString(appParNode);
//            System.out.println("Ok 3");
            HttpPost http = new HttpPost();
//            System.out.println("Ok 4");
            String result = http.getDataFromHttpPost(jsonReq, urlPost);
//            HttpPost http = new HttpPost();
//            System.out.println("Ok 5");
            List<TimeSheetMonth> listOfData = objectMapper.readValue(result, new TypeReference<List<TimeSheetMonth>>(){});
            
            // sort
            Comparator<TimeSheetMonth> compareByCode = (TimeSheetMonth a1, TimeSheetMonth a2) -> a1
					.getPersonnel_code().compareTo(a2.getPersonnel_code());
			Collections.sort(listOfData, compareByCode);

//            System.out.println(result);
            File excelFile = BaoCaoCong_Excel.createBaoCaoCong(excelFilePath,listOfData,month,year,orgName,is_filecopy);
            InputStream dataInputStream = new FileInputStream(excelFile);

            response.setData(IOUtils.toByteArray(dataInputStream));
            response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

            dataInputStream.close();
            FileCopy.delete();

            return ResponseEntity.ok(response);

        }catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            response.setData(null);
            response.setRespcode(ResponseMessage.KEY_RC_APPROVE_FAIL);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_APPROVE_FAIL));
            return ResponseEntity.badRequest().body(response);
        }
    }

	
	@RequestMapping(value = "/update_daily", method = RequestMethod.POST)
	public ResponseEntity<updateDailyResponse> updateDaily(@RequestBody updateDailyRequest entity) {
		updateDailyResponse response = new updateDailyResponse();
		try {
			/*
			 * Tinh toan cac gia tri trong ngay duoc truyen len. Neu tong thoi gian > 8 thi bao loi
			 */
			
			//Qui doi cac gia tri ra Date, neu khong doi duoc thi bao loi
            Calendar cal_in_time = Calendar.getInstance();
            Boolean is_intime_edit = false;
            if (entity.in_time.length() > 0) {
            	try {
            		if (entity.in_time.toLowerCase().contains("x")) {
    	            	if (!update_daily_DB(entity.in_time_rowid,entity.row_day,"x")) {
    	                	//Bao loi
    	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    	        			response.setMessage("Lỗi cập nhật giờ vào");
    	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
    	            	}
    	            	if (!update_daily_DB(entity.totalworking_time_rowid,entity.row_day,"")) {
    	                	//Bao loi
    	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    	        			response.setMessage("Lỗi cập nhật tổng giờ làm");
    	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
    	            	}
    	            	response.data = "";
    					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
    					return new ResponseEntity<updateDailyResponse>(response, HttpStatus.OK);
            		} else {
		            	//Neu gia tri co chua "/1" --> Co sua thong tin thu cong
		                String[] ls_in_time = entity.in_time.split("/");
		                String s_value = "";
		                if (ls_in_time.length > 1){
		                    s_value = ls_in_time[0];
		                    is_intime_edit = true;
		                } else
		                	s_value = ls_in_time[0];
		                
		                if (s_value != "") {
			                String[] time_str = s_value.split(":");
			                int hour = Integer.parseInt(time_str[0]);
			                int min = Integer.parseInt(time_str[1]);
			                
			            	cal_in_time.set(entity.row_year, entity.row_month, entity.row_day, hour, min);
		                }
            		}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
            else {
            	//Bao loi
    			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage("Giờ vào không hợp lệ");
    			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            }
			
            //Gio ra
            Calendar cal_out_time = Calendar.getInstance();
            Boolean is_outtime_edit = false;
            if (entity.out_time.length() > 0) {
            	try {
            		if (entity.out_time.toLowerCase().contains("x")) {
    	            	if (!update_daily_DB(entity.out_time_rowid,entity.row_day,"x")) {
    	                	//Bao loi
    	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    	        			response.setMessage("Lỗi cập nhật giờ ra");
    	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
    	            	}
    	            	if (!update_daily_DB(entity.totalworking_time_rowid,entity.row_day,"")) {
    	                	//Bao loi
    	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    	        			response.setMessage("Lỗi cập nhật tổng giờ làm");
    	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
    	            	}
    	            	response.data = "";
    					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
    					return new ResponseEntity<updateDailyResponse>(response, HttpStatus.OK);
            		} else {
		            	//Neu gia tri co chua "/1" --> Co sua thong tin thu cong
		                String[] ls_out_time = entity.out_time.split("/");
		                String s_value = "";
		                if (ls_out_time.length > 1){
		                    s_value = ls_out_time[0];
		                    is_outtime_edit = true;
		                } else
		                	s_value = ls_out_time[0];
		                
		                if (s_value != "") {
			                String[] time_str = s_value.split(":");
			                int hour = Integer.parseInt(time_str[0]);
			                int min = Integer.parseInt(time_str[1]);
			                
			            	cal_out_time.set(entity.row_year, entity.row_month, entity.row_day, hour, min);
			            	
			            	//Neu gio ra nho hon gio vao --> Bao loi
			            	if (cal_out_time.getTimeInMillis() < cal_in_time.getTimeInMillis()) {
	    	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
	    	        			response.setMessage("Giờ ra không được nhỏ hơn giờ vào");
	    	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
			            	}
		                }
            		}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
            else {
            	//Bao loi
    			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage("Giờ ra không hợp lệ");
    			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            }
            
            //Gio bat dau an
            Calendar cal_lunchstart_time = Calendar.getInstance();
            Boolean is_lunchstart_edit = false;
            if (entity.lunchstart_time.length() > 0) {
            	try {
            		if (entity.lunchstart_time.toLowerCase().contains("x")) {
            			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
            			response.setMessage("Giờ ăn phải có giá trị hh:mm");
            			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            		} else {
		            	//Neu gia tri co chua "/1" --> Co sua thong tin thu cong
		                String[] ls_lunchstart_time = entity.lunchstart_time.split("/");
		                String s_value = "";
		                if (ls_lunchstart_time.length > 1){
		                    s_value = ls_lunchstart_time[0];
		                    is_lunchstart_edit = true;
		                } else
		                	s_value = ls_lunchstart_time[0];
		                
		                if (s_value != "") {
			                String[] time_str = s_value.split(":");
			                int hour = Integer.parseInt(time_str[0]);
			                int min = Integer.parseInt(time_str[1]);
			                
			            	cal_lunchstart_time.set(entity.row_year, entity.row_month, entity.row_day, hour, min);
		                }
            		}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
            else {
            	//Bao loi
    			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage("Giờ bắt đầu ăn không hợp lệ");
    			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            }
            
            //Gio ket thuc an
            Calendar cal_lunchend_time = Calendar.getInstance();
            Boolean is_lunchend_edit = false;
            if (entity.lunchend_time.length() > 0) {
            	try {
            		if (entity.lunchstart_time.toLowerCase().contains("x")) {
            			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
            			response.setMessage("Giờ ăn phải có giá trị hh:mm");
            			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            		} else {
		            	//Neu gia tri co chua "/1" --> Co sua thong tin thu cong
		                String[] ls_lunchend_time = entity.lunchend_time.split("/");
		                String s_value = "";
		                if (ls_lunchend_time.length > 1){
		                    s_value = ls_lunchend_time[0];
		                    is_lunchend_edit = true;
		                } else
		                	s_value = ls_lunchend_time[0];
		                
		                if (s_value != "") {
			                String[] time_str = s_value.split(":");
			                int hour = Integer.parseInt(time_str[0]);
			                int min = Integer.parseInt(time_str[1]);
			                
			            	cal_lunchend_time.set(entity.row_year, entity.row_month, entity.row_day, hour, min);
		                }
            		}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
            else {
            	//Bao loi
    			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage("Giờ kết thúc ăn không hợp lệ");
    			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            }
            
            //Tinh tong gio lam trong ngay
            long total_lunch_mili = cal_lunchend_time.getTimeInMillis() - cal_lunchstart_time.getTimeInMillis();
            long total_working_mili = 0;
            if (cal_in_time.getTimeInMillis() < cal_lunchstart_time.getTimeInMillis()){
                if (cal_out_time.getTimeInMillis() > cal_lunchend_time.getTimeInMillis()){
                    //Giờ vào < giờ ăn và Giờ ra > giờ ăn --> Giờ làm = Giờ ra - Giờ vào - Giờ ăn
                	total_working_mili = cal_out_time.getTimeInMillis() - cal_in_time.getTimeInMillis() - total_lunch_mili;
                } else {
                    if (cal_out_time.getTimeInMillis() > cal_lunchstart_time.getTimeInMillis()){
                        //Giờ ra nằm giữa giờ ăn --> Giờ làm = Giờ bắt đầu ăn - Giờ vào
                    	total_working_mili = cal_lunchstart_time.getTimeInMillis() - cal_in_time.getTimeInMillis();
                    } else {
                        //Giờ ra nhỏ hơn giờ ăn --> Giờ làm = Giờ ra - Giờ vào
                    	total_working_mili = cal_out_time.getTimeInMillis() - cal_in_time.getTimeInMillis();
                    }
                }
            } else {
                if (cal_in_time.getTimeInMillis() < cal_lunchend_time.getTimeInMillis()){
                    if (cal_out_time.getTimeInMillis() < cal_lunchend_time.getTimeInMillis()){
                        //Giờ vào và giờ ra đều vào giữa giờ ăn --> Giờ làm = 0
                    	total_working_mili = 0;
                    } else {
                        //Giờ vào giữa giờ ăn và Giờ ra sau giờ ăn --> Giờ làm = Giờ ra - Giờ kết thúc ăn
                    	total_working_mili = cal_out_time.getTimeInMillis() - cal_lunchend_time.getTimeInMillis();
                    }
                } else {
                    //Giờ vào và giờ ra đều sau giờ ăn --> Giờ làm = Giờ vào - Giờ ra
                	total_working_mili = cal_out_time.getTimeInMillis() - cal_in_time.getTimeInMillis();
                }
            }
            
            int total_work_minute = (int)total_working_mili/60000;
            
            //Neu lam qua 8 tieng --> bao loi
            if (total_work_minute > 480) {
    			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage("Tổng giờ làm lớn hơn 8h/ngày");
    			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            } else {
	            int w_hour = total_work_minute / 60;
	            int w_min = total_work_minute % 60;
	            String s_min = w_min <10?"0"+w_min:""+w_min;
	            String s_total_working = w_hour + ":" + s_min + "/1";//Sua thu cong them "/1"
	            
	            //Update gio vao
	            if (is_intime_edit) {
	            	if (!update_daily_DB(entity.in_time_rowid,entity.row_day,entity.in_time)) {
	                	//Bao loi
	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
	        			response.setMessage("Lỗi cập nhật giờ vào");
	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
	            	}
	            }
	            //Update gio ra
	            if (is_outtime_edit) {
	            	if (!update_daily_DB(entity.out_time_rowid,entity.row_day,entity.out_time)) {
	                	//Bao loi
	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
	        			response.setMessage("Lỗi cập nhật giờ ra");
	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
	            	}
	            }
	            //Update gio bat dau an
	            if (is_lunchstart_edit) {
	            	if (!update_daily_DB(entity.lunchstart_rowid,entity.row_day,entity.lunchstart_time)) {
	                	//Bao loi
	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
	        			response.setMessage("Lỗi cập nhật giờ bắt đầu ăn");
	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
	            	}
	            }
	            //Update gio ket thuc an
	            if (is_lunchend_edit) {
	            	if (!update_daily_DB(entity.lunchend_rowid,entity.row_day,entity.lunchend_time)) {
	                	//Bao loi
	        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
	        			response.setMessage("Lỗi cập nhật giờ kết thúc ăn");
	        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
	            	}
	            }
	            //Update tong gio lam
            	if (!update_daily_DB(entity.totalworking_time_rowid,entity.row_day,s_total_working)) {
                	//Bao loi
        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
        			response.setMessage("Lỗi cập nhật tổng giờ làm");
        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            	}
	            
            	response.data = s_total_working;
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				return new ResponseEntity<updateDailyResponse>(response, HttpStatus.OK);
            }

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/update_daily_allrow", method = RequestMethod.POST)
	public ResponseEntity<updateDailyResponse> updateDaily_AllRow(@RequestBody updateDaily_AllRowRequest entity) {
		updateDailyResponse response = new updateDailyResponse();
		try {
            	if (!update_daily_AllRow_DB(entity.row_year,entity.row_month, entity.row_day,entity.orgid_link, entity.grantid_link, entity.value)) {
                	//Bao loi
        			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
        			response.setMessage("Lỗi cập nhật ngày làm");
        			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
            	}
	            
            	response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				return new ResponseEntity<updateDailyResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<updateDailyResponse>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	private Boolean update_daily_DB(long row_id, int row_day, String value) {
		try {
			String urlPost = AtributeFixValues.url_timesheet + "/timesheet/update_daily";
	
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();
	
			// truyen param theo
			appParNode.put("row_id", row_id);
			appParNode.put("row_day", row_day);
			appParNode.put("value", value);
			String jsonReq = objectMapper.writeValueAsString(appParNode);
	
			HttpPost http = new HttpPost();
			String result = http.getDataFromHttpPost(jsonReq, urlPost);
			if ("\"OK\"".equals(result))
				return true;
			else
				return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	private Boolean update_daily_AllRow_DB(int year, int month, int day, Long orgid_link, Long grantid_link, String value) {
		try {
			String urlPost = AtributeFixValues.url_timesheet + "/timesheet/update_daily_allrow";
	
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode appParNode = objectMapper.createObjectNode();
	
			// truyen param theo
			appParNode.put("year", year);
			appParNode.put("month", month);
			appParNode.put("day", day);
			appParNode.put("orgid_link", orgid_link);
			appParNode.put("grantid_link", grantid_link);
			appParNode.put("value", value);
			String jsonReq = objectMapper.writeValueAsString(appParNode);
	
			HttpPost http = new HttpPost();
			String result = http.getDataFromHttpPost(jsonReq, urlPost);
			if ("\"OK\"".equals(result))
				return true;
			else
				return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
