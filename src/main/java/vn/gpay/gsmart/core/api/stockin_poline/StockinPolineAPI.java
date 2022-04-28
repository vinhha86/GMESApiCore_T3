package vn.gpay.gsmart.core.api.stockin_poline;

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
import vn.gpay.gsmart.core.base.ResponseError;
import vn.gpay.gsmart.core.stockin_poline.IStockinPolineService;
import vn.gpay.gsmart.core.stockin_poline.StockinPoline;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/stockin_poline")
public class StockinPolineAPI {
	@Autowired  IStockinPolineService stockinPolineService;
	
	
	@RequestMapping(value = "/stockin_poline_create",method = RequestMethod.POST)
	public ResponseEntity<?> stockin_poline_create(@RequestBody StockinPoline_CreateRequest entity, HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
//			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			Long stockinid_link = entity.stockinid_link;
			List<Long> listPoId = entity.listPoId;
			
			for(Long poId : listPoId) {
				List<StockinPoline> stockinPolineList = stockinPolineService.getByStockin_Poline(stockinid_link, poId);
				if(stockinPolineList.size() == 0) {
					StockinPoline newStockinPoline = new StockinPoline();
					newStockinPoline.setPcontract_poid_link(poId);
					newStockinPoline.setStockinid_link(stockinid_link);
					stockinPolineService.save(newStockinPoline);
				}
			}
					
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/stockin_poline_delete",method = RequestMethod.POST)
	public ResponseEntity<?> stockin_poline_delete(@RequestBody StockinPoline_CreateRequest entity, HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
//			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			Long stockinid_link = entity.stockinid_link;
			List<Long> listPoId = entity.listPoId;
			
			for(Long poId : listPoId) {
				List<StockinPoline> stockinPolineList = stockinPolineService.getByStockin_Poline(stockinid_link, poId);
				if(stockinPolineList.size() > 0) {
					StockinPoline stockinPoline = stockinPolineList.get(0);
					stockinPolineService.delete(stockinPoline);
				}
			}
					
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
