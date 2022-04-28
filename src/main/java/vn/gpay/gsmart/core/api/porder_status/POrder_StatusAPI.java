package vn.gpay.gsmart.core.api.porder_status;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.porder_status.IPOrder_Status_Service;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/porderstatus")
public class POrder_StatusAPI {
	@Autowired private IPOrder_Status_Service porderstatusservice;
	
	@RequestMapping(value = "/findall",method = RequestMethod.POST)
	public ResponseEntity<POrder_Status_getall_response> findAll(HttpServletRequest request ) {
		POrder_Status_getall_response response = new POrder_Status_getall_response();
		try {
			
			response.data = porderstatusservice.findAll(); 
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_Status_getall_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<POrder_Status_getall_response>(response, HttpStatus.BAD_REQUEST);
		}
	}    
}
