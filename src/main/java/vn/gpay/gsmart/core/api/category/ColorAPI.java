package vn.gpay.gsmart.core.api.category;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.api.org.create_color_request;
import vn.gpay.gsmart.core.api.org.delete_color_request;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.base.ResponseError;
import vn.gpay.gsmart.core.category.Color;
import vn.gpay.gsmart.core.category.IColorService;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/color")
public class ColorAPI {
	@Autowired IColorService colorService;
	
	@RequestMapping(value = "/getColorAll",method = RequestMethod.POST)
	public ResponseEntity<?> GetColorAll(HttpServletRequest request ) {
		ColorResponse response = new ColorResponse();
		try {
			response.data = colorService.findAll();
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ColorResponse>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> deleteColor(HttpServletRequest request, @RequestBody delete_color_request entity) {
		ResponseBase response = new ResponseBase();
		long id = entity.data.getId();
		System.out.println(id);
		try{
			colorService.deleteById(id);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_SERVER_ERROR);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> createColor(HttpServletRequest request, @RequestBody create_color_request entity) {
		ResponseBase response = new ResponseBase();
		String name = entity.data.getName();
		String name_en = ((entity.data.getName_en() == null) || (entity.data.getName_en().trim().equals(""))) ? name : entity.data.getName_en();
		String code = ((entity.data.getCode() == null) || (entity.data.getCode().trim().equals(""))) ? name : entity.data.getCode();
		String rgbvalue = "#" + entity.data.getRgbvalue();
		try {
			Color newColor = new Color();
			newColor.setCode(code);
			newColor.setName(name);
			newColor.setName_en(name_en);
			newColor.setRgbvalue(rgbvalue);
			colorService.save(newColor);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_SERVER_ERROR);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
}
