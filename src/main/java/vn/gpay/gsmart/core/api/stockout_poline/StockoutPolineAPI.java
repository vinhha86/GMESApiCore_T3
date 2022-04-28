package vn.gpay.gsmart.core.api.stockout_poline;

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
import vn.gpay.gsmart.core.stockout_poline.IStockoutPolineService;
import vn.gpay.gsmart.core.stockout_poline.StockoutPoline;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/stockout_poline")
public class StockoutPolineAPI {
	@Autowired  IStockoutPolineService stockoutPolineService;
	
	
	@RequestMapping(value = "/stockout_poline_create",method = RequestMethod.POST)
	public ResponseEntity<?> stockout_poline_create(@RequestBody StockoutPoline_CreateRequest entity, HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
//			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			Long stockoutid_link = entity.stockoutid_link;
			List<Long> listPoId = entity.listPoId;
			
			for(Long poId : listPoId) {
				List<StockoutPoline> stockoutPolineList = stockoutPolineService.getByStockout_Poline(stockoutid_link, poId);
				if(stockoutPolineList.size() == 0) {
					StockoutPoline newStockoutPoline = new StockoutPoline();
					newStockoutPoline.setPcontract_poid_link(poId);
					newStockoutPoline.setStockoutid_link(stockoutid_link);
					stockoutPolineService.save(newStockoutPoline);
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
	
	@RequestMapping(value = "/stockout_poline_delete",method = RequestMethod.POST)
	public ResponseEntity<?> stockout_poline_delete(@RequestBody StockoutPoline_CreateRequest entity, HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
//			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			Long stockoutid_link = entity.stockoutid_link;
			List<Long> listPoId = entity.listPoId;
			
			for(Long poId : listPoId) {
				List<StockoutPoline> stockoutPolineList = stockoutPolineService.getByStockout_Poline(stockoutid_link, poId);
				if(stockoutPolineList.size() > 0) {
					StockoutPoline stockoutPoline = stockoutPolineList.get(0);
					stockoutPolineService.delete(stockoutPoline);
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
