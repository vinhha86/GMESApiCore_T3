package vn.gpay.gsmart.core.api.Report;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.reports.ICMP_Service;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/report_cmp")
public class CMP_ReportAPI {
	@Autowired ICMP_Service cmpReportService;
	@Autowired IOrgService orgService;
	
	@RequestMapping(value = "/orgcmp", method = RequestMethod.POST)
	public ResponseEntity<cmp_report_response> orgcmp(HttpServletRequest request,
			@RequestBody cmp_report_request entity) {
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		cmp_report_response response = new cmp_report_response();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(entity.reportdate);
			response.data = cmpReportService.getData_ByMonth(user.getRootorgid_link(), user.getOrgid_link(), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), entity.reportmonths);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<cmp_report_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<cmp_report_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/orgcmp_tosx", method = RequestMethod.POST)
	public ResponseEntity<cmp_report_response> orgcmp_tosx(HttpServletRequest request,
			@RequestBody cmp_report_request entity) {
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		cmp_report_response response = new cmp_report_response();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(entity.reportdate);
			int month = calendar.get(Calendar.MONTH) +1;
			response.data = cmpReportService.getData_ByMonth_ToSX(user.getRootorgid_link(), user.getOrgid_link(), month, calendar.get(Calendar.YEAR), entity.reportmonths);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<cmp_report_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<cmp_report_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/orgcmp_tosx_byparentcode", method = RequestMethod.POST)
	public ResponseEntity<cmp_report_response> orgcmp_tosx_byparentcode(HttpServletRequest request,
			@RequestBody cmp_report_request entity) {
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		cmp_report_response response = new cmp_report_response();
		try {
			List<Org> lsOrg = orgService.getbycode(entity.org_code, user.getRootorgid_link());
			if(lsOrg.size()>0){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(entity.reportdate);
				int month = calendar.get(Calendar.MONTH) +1;
				response.data = cmpReportService.getData_ByMonth_ToSX(user.getRootorgid_link(), lsOrg.get(0).getId(), month, calendar.get(Calendar.YEAR), entity.reportmonths);
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				return new ResponseEntity<cmp_report_response>(response, HttpStatus.OK);
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Sai mã đơn vị: " + entity.org_code);
				return new ResponseEntity<cmp_report_response>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<cmp_report_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
}
