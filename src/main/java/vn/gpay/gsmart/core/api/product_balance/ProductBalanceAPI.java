package vn.gpay.gsmart.core.api.product_balance;

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

import vn.gpay.gsmart.core.api.porder_balance.POrderBalanceBinding_response;
import vn.gpay.gsmart.core.api.porder_balance.POrderBalance_getByPOrder_request;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.porder.IPOrder_Service;
import vn.gpay.gsmart.core.porder.POrder;
import vn.gpay.gsmart.core.porder_balance.POrderBalance;
import vn.gpay.gsmart.core.porder_balance.POrderBalanceBinding;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_Service;
import vn.gpay.gsmart.core.porder_grant.POrderGrant;
import vn.gpay.gsmart.core.porder_grant_balance.IPOrderGrantBalanceService;
import vn.gpay.gsmart.core.porder_grant_balance.POrderGrantBalance;
import vn.gpay.gsmart.core.product_balance.IProductBalanceService;
import vn.gpay.gsmart.core.product_balance.ProductBalance;
import vn.gpay.gsmart.core.product_balance_process.IProductBalanceProcessService;
import vn.gpay.gsmart.core.product_balance_process.ProductBalanceProcess;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/product_balance")
public class ProductBalanceAPI {
	@Autowired IProductBalanceService productBalanceService;
	@Autowired IProductBalanceProcessService productBalanceProcessService;
	@Autowired IPOrderGrant_Service porderGrantService;
	@Autowired IPOrder_Service porderService;
	@Autowired IPOrderGrantBalanceService porderGrantBalanceService;
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> create(@RequestBody ProductBalance_create_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			
			Long productid_link = entity.productid_link;
			Long pcontractid_link = entity.pcontractid_link;
			String name = entity.name.trim();
			
			List<ProductBalance> productBalance_list = productBalanceService.getByBalanceName_Product(name, productid_link, pcontractid_link);
			if(productBalance_list.size() > 0) {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Cụm công đoạn đã tồn tại");
				return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
			}
			
			List<ProductBalance> listProductBalance = productBalanceService.getByProduct(productid_link, pcontractid_link);
			Integer listSize = listProductBalance.size();
			
			ProductBalance newProductBalance = new ProductBalance();
			newProductBalance.setId(null);
			newProductBalance.setOrgrootid_link(orgrootid_link);
			newProductBalance.setProductid_link(productid_link);
			newProductBalance.setPcontractid_link(pcontractid_link);
			newProductBalance.setBalance_name(name);
			Integer sortValue = listSize;
			newProductBalance.setSortvalue(sortValue);
			productBalanceService.save(newProductBalance);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> delete(@RequestBody ProductBalance_delete_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long id = entity.id;
			
			ProductBalance productBalance = productBalanceService.findOne(id);
			Long productid_link = productBalance.getProductid_link();
			Long pcontractid_link = productBalance.getPcontractid_link();
			
			List<ProductBalance> productBalance_list = productBalanceService.getByProduct(productid_link, pcontractid_link);
			
			// set name, sortvalue
			Boolean isAfterDeleteRec = false;
			for(ProductBalance item : productBalance_list) {
				if(item.getId().equals(id)) {
					isAfterDeleteRec = true;
					continue;
				}
				if(isAfterDeleteRec) {
					Integer sortValue = item.getSortvalue() - 1;
					item.setSortvalue(sortValue);
//					item.setBalance_name("Cụm công đoạn " + sortValue);
					productBalanceService.save(item);
				}
			}
			
			// delete
			List<ProductBalanceProcess> productBalanceProcess_list = productBalance.getProductBalanceProcesses();
			if(productBalanceProcess_list != null) {
				if(productBalanceProcess_list.size() > 0) {
					for(ProductBalanceProcess item : productBalanceProcess_list) {
						productBalanceProcessService.deleteById(item.getId());
					}
				}
			}
			productBalanceService.deleteById(id);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete_multi", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> delete_multi(@RequestBody ProductBalance_delete_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Long> idList = entity.idList;
			
			for(Long id : idList) {
				ProductBalance productBalance = productBalanceService.findOne(id);
				Long productid_link = productBalance.getProductid_link();
				Long pcontractid_link = productBalance.getPcontractid_link();
				
				List<ProductBalance> productBalance_list = productBalanceService.getByProduct(productid_link, pcontractid_link);
				
				// set name, sortvalue
				Boolean isAfterDeleteRec = false;
				for(ProductBalance item : productBalance_list) {
					if(item.getId().equals(id)) {
						isAfterDeleteRec = true;
						continue;
					}
					if(isAfterDeleteRec) {
						Integer sortValue = item.getSortvalue() - 1;
						item.setSortvalue(sortValue);
//						item.setBalance_name("Cụm công đoạn " + sortValue);
						productBalanceService.save(item);
					}
				}
				
				// delete
				List<ProductBalanceProcess> productBalanceProcess_list = productBalance.getProductBalanceProcesses();
				if(productBalanceProcess_list != null) {
					if(productBalanceProcess_list.size() > 0) {
						for(ProductBalanceProcess item : productBalanceProcess_list) {
							productBalanceProcessService.deleteById(item.getId());
						}
					}
				}
				productBalanceService.deleteById(id);
			}
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getByProduct", method = RequestMethod.POST)
	public ResponseEntity<ProductBalance_response> getByProduct(@RequestBody ProductBalance_getByProduct_request entity,
			HttpServletRequest request) {
		ProductBalance_response response = new ProductBalance_response();
		try {
			response.data = productBalanceService.getByProduct(entity.productid_link, entity.pcontractid_link);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ProductBalance_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ProductBalance_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/product_balance_reorder",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> product_balance_reorder(@RequestBody ProductBalance_reorder_request entity,HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
		
			for (ProductBalance productBalance:entity.data){
				ProductBalance pb = productBalanceService.findOne(productBalance.getId());
				if (null != pb){
					pb.setSortvalue(productBalance.getSortvalue());
					productBalanceService.save(pb);
				}
				
			}
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getForScheduleByPorderGrant", method = RequestMethod.POST)
	public ResponseEntity<POrderBalanceBinding_response> getForScheduleByPorderGrant(@RequestBody POrderBalance_getByPOrder_request entity,
			HttpServletRequest request) {
		POrderBalanceBinding_response response = new POrderBalanceBinding_response();
		try {
			// lấy danh sách ProductBalance, sau đó tìm danh sách porder_grant_balance (join bằng trường productbalanceid_link)
			Long pordergrantid_link = entity.pordergrantid_link;
			POrderGrant pordergrant = porderGrantService.findOne(pordergrantid_link);
			Long porderid_link = pordergrant.getPorderid_link();
			POrder porder = null;
			if(porderid_link != null) {
				porder = porderService.findOne(porderid_link);
			}
			List<POrderBalanceBinding> porderBalanceBindingList = new ArrayList<POrderBalanceBinding>();
			if(porder != null) {
				Long productid_link = porder.getProductid_link();
				Long pcontractid_link = porder.getPcontractid_link();
				List<ProductBalance> productBalanceList = productBalanceService.getByProduct(productid_link, pcontractid_link);
				
				for(ProductBalance productBalance : productBalanceList) {
					POrderBalanceBinding porderBalanceBinding = new POrderBalanceBinding();
					porderBalanceBinding.setId(productBalance.getId());
					porderBalanceBinding.setOrgrootid_link(productBalance.getOrgrootid_link());
					porderBalanceBinding.setProductid_link(productBalance.getProductid_link());
					porderBalanceBinding.setPcontractid_link(productBalance.getPcontractid_link());
					porderBalanceBinding.setPorderid_link(porder.getId());
					porderBalanceBinding.setBalance_name(productBalance.getBalance_name());
					porderBalanceBinding.setPrevbalanceid_link(productBalance.getPrevbalanceid_link());
					porderBalanceBinding.setParentbalanceid_link(productBalance.getParentbalanceid_link());
					porderBalanceBinding.setSortvalue(productBalance.getSortvalue());
					porderBalanceBinding.setTimespent_standard(productBalance.getTimespent_standard());
					porderBalanceBinding.setWorkingprocess_name(productBalance.getWorkingprocess_name());
					porderBalanceBinding.setProductBalanceProcesses(productBalance.getProductBalanceProcesses());
					
					List<POrderGrantBalance> listPorderGrantBalance  = 
//							porderGrantBalanceService.getByPorderGrantAndPorderBalance(entity.pordergrantid_link, productBalance.getId());
							porderGrantBalanceService.getByPorderGrantAndProductBalance(entity.pordergrantid_link, productBalance.getId());
					if(listPorderGrantBalance.size() > 0) {
						POrderGrantBalance porderGrantBalance = listPorderGrantBalance.get(0);
						porderBalanceBinding.setPersonnelId(porderGrantBalance.getPersonnelid_link());
						porderBalanceBinding.setPersonnelFullName(porderGrantBalance.getPersonnelFullName());
					}
					porderBalanceBindingList.add(porderBalanceBinding);
				}
			}
			
			response.data = porderBalanceBindingList;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrderBalanceBinding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrderBalanceBinding_response>(response, HttpStatus.OK);
		}
	}
}
