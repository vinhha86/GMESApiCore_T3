package vn.gpay.gsmart.core.api.sku;


import java.util.ArrayList;
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

import vn.gpay.gsmart.core.pcontractproductbom.IPContractProductBom2Service;
import vn.gpay.gsmart.core.pcontractproductbom.PContractProductBom2;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.sku.ISKU_AttributeValue_Service;
import vn.gpay.gsmart.core.sku.ISKU_Service;
import vn.gpay.gsmart.core.sku.SKU;
import vn.gpay.gsmart.core.utils.ResponseMessage;
import vn.gpay.gsmart.core.utils.SkuType;
import vn.gpay.gsmart.core.warehouse.IWarehouseService;


@RestController
@RequestMapping("/api/v1/sku")
public class SKU_API {
	@Autowired ISKU_AttributeValue_Service savService;
	@Autowired ISKU_Service skuService;
	@Autowired IPContractProductBom2Service pcontract_bom2_Service;
	@Autowired IWarehouseService warehouseService;
	
	@RequestMapping(value = "/getall_byproduct",method = RequestMethod.POST)
	public ResponseEntity<SKU_getbyproduct_response> Product_GetAll(HttpServletRequest request, @RequestBody SKU_getbyproduct_request entity ) {
		SKU_getbyproduct_response response = new SKU_getbyproduct_response();
		try {
			List<SKU> result = skuService.getlist_byProduct(entity.productid_link);
			if(entity.isremove) {
				result.removeIf(c->c.getIs_default());
			}
			
			// tim sl ton kho
			if(entity.stockid_link != null) {
				for(SKU sku : result) {
					Long totalSLTon = warehouseService.getSumBy_Sku_Stock(sku.getId(), entity.stockid_link);
					sku.setTotalSLTon(totalSLTon.intValue());
				}
			}
			
			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<SKU_getbyproduct_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<SKU_getbyproduct_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/createcode",method = RequestMethod.POST)
	public ResponseEntity<SKU_createcode_response> update_SKU(HttpServletRequest request, @RequestBody SKU_Createcode_Request entity ) {
		SKU_createcode_response response = new SKU_createcode_response();
		
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			
			SKU sku_check = skuService.getSKU_byCode(entity.data.getCode(), orgrootid_link);
			if(sku_check != null) {
				SKU sku_old =  skuService.findOne(entity.data.getId());
				if (sku_old.getCode() != sku_check.getCode()){
					response.mesErr = "SKU ???? b??? tr??ng trong h??? th???ng! Li??n h??? qu???n tr??? h??? th???ng ????? ki???m tra l???i";
				} else {
					sku_old.setBarcode(entity.data.getBarcode());
					sku_old.setPartnercode(entity.data.getPartnercode());
					skuService.save(sku_old);
					response.mesErr = "";
				}
			}
			else {
				SKU sku = entity.data;
				if(sku.getId()==null || sku.getId()==0) {
					sku.setOrgrootid_link(user.getRootorgid_link());
				}else {
					SKU sku_old =  skuService.findOne(sku.getId());
					sku.setOrgrootid_link(sku_old.getOrgrootid_link());
				}
				
				skuService.save(sku);
				response.mesErr = "";
			}
			
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<SKU_createcode_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<SKU_createcode_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
    @RequestMapping(value = "/getsku_mainmaterial",method = RequestMethod.POST)
    public List<SKU> getSKU_MainMaterial(@RequestBody SkuByCodeRequest entity, HttpServletRequest request) {
    	return skuService.getSKU_MainMaterial(entity.skucode);
    }
    
    @RequestMapping(value = "/getsku_bytype",method = RequestMethod.POST)
    public List<SKU> getSKU_ByType(@RequestBody SkuByCodeRequest entity, HttpServletRequest request) {
    	return skuService.getSKU_ByType(entity.skucode, entity.skutypeid_link);
    }
    
    @RequestMapping(value = "/getProductSKU_ByCode",method = RequestMethod.POST)
	public ResponseEntity<SKU_getbyproduct_response> getProductSKU_ByCode(HttpServletRequest request, @RequestBody SkuByCodeRequest entity ) {
		SKU_getbyproduct_response response = new SKU_getbyproduct_response();
		
		try {
//			System.out.println(entity.skutypeid_link + " " + entity.skucode);
			List<SKU> skus = skuService.getProductSKU_ByBarCode(entity.skutypeid_link, entity.skucode);
			response.data = new ArrayList<SKU>();
			if(skus.size() > 0) {
				response.data.addAll(skus);
			}else {
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage("M?? v???ch kh??ng t???n t???i");
				return new ResponseEntity<SKU_getbyproduct_response>(response,HttpStatus.OK);
			}
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<SKU_getbyproduct_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<SKU_getbyproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getSkuByCode",method = RequestMethod.POST)
	public ResponseEntity<SKU_getbyproduct_response> getSkuByCode(HttpServletRequest request, @RequestBody SKU_getSkuByCode_request entity ) {
		SKU_getbyproduct_response response = new SKU_getbyproduct_response();
		try {
//			response.data = skuService.getSkuByCode(entity.code);
			if(entity.typeFrom == null) entity.typeFrom = 20;
			if(entity.typeTo == null) entity.typeTo = 30;
			response.data = skuService.getSkuByCodeAndType(entity.code, entity.typeFrom, entity.typeTo); //ex: typeFrom: 20, typeTo: 30
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<SKU_getbyproduct_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<SKU_getbyproduct_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getSkuForXuatDieuChuyenNguyenLieu",method = RequestMethod.POST)
	public ResponseEntity<SKU_getbyproduct_response> getSkuForXuatDieuChuyenNguyenLieu(HttpServletRequest request, @RequestBody SKU_GetForXuatDieuChuyenNguyenLieu_request entity ) {
		SKU_getbyproduct_response response = new SKU_getbyproduct_response();
		try {
			Long pcontractid_link_current = entity.pcontractid_link_current;
			Long pcontractid_link_loanfrom = entity.pcontractid_link_loanfrom;
			Long productid_link = entity.productid_link;
			
			List<SKU> result = new ArrayList<SKU>();
			List<PContractProductBom2> listbom = pcontract_bom2_Service.get_material_in_pcontract_productBOM(productid_link, pcontractid_link_current, 20);
			List<Long> skuid_list = new ArrayList<Long>();
			// l???y danh s??ch id sku lo???i v???i c???a s???n ph???m v?? ????n h??ng hi???n t???i
			for(PContractProductBom2 pcontractProductBom2 : listbom) {
				skuid_list.add(pcontractProductBom2.getMaterialid_link());
			}
			
			if(skuid_list.size() > 0) {
				// t??m danh s??ch sku c???a ????n h??ng ch???a s???n ph???m ch???a c??c lo???i v???i c???a ????n h??ng hi???n t???i
				result = skuService.getSkuForXuatDieuChuyenNguyenLieu(skuid_list, pcontractid_link_loanfrom);
//				System.out.println(result.size());
			}
			
			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<SKU_getbyproduct_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<SKU_getbyproduct_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getBySkuIdList",method = RequestMethod.POST)
	public ResponseEntity<SKU_getbyproduct_response> getBySkuIdList(HttpServletRequest request, @RequestBody SKU_getByListId_request entity ) {
		SKU_getbyproduct_response response = new SKU_getbyproduct_response();
		try {
			List<Long> skuNplIdList = entity.skuNplIdList;
			Integer skuType = entity.skuType;
			List<SKU> result = new ArrayList<SKU>();
			if(skuType == SkuType.SKU_TYPE_VAI) {
				result = skuService.getBySkuIdList(skuNplIdList, skuType);
			}else if(skuType == SkuType.SKU_TYPE_PHULIEU) {
				result = skuService.getBySkuIdListPhuLieu(skuNplIdList, skuType);
			}
			
			
			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<SKU_getbyproduct_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<SKU_getbyproduct_response>(response, HttpStatus.OK);
		}
	}
}
