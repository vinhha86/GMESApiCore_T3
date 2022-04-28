package vn.gpay.gsmart.core.api.workingprocess;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.porder_balance.IPOrderBalanceService;
import vn.gpay.gsmart.core.porder_balance_process.IPOrderBalanceProcessService;
import vn.gpay.gsmart.core.porder_balance_process.POrderBalanceProcess;
import vn.gpay.gsmart.core.porder_sewingcost.IPorderSewingCost_Service;
import vn.gpay.gsmart.core.porder_sewingcost.POrderSewingCost;
import vn.gpay.gsmart.core.porder_workingprocess.IPOrderWorkingProcess_Service;
import vn.gpay.gsmart.core.porder_workingprocess.POrderWorkingProcess;
import vn.gpay.gsmart.core.product_balance.IProductBalanceService;
import vn.gpay.gsmart.core.product_balance_process.IProductBalanceProcessService;
import vn.gpay.gsmart.core.product_balance_process.ProductBalanceProcess;
import vn.gpay.gsmart.core.product_sewingcost.IProductSewingCostService;
import vn.gpay.gsmart.core.product_sewingcost.ProductSewingCost;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.utils.ResponseMessage;
import vn.gpay.gsmart.core.workingprocess.IWorkingProcess_Service;
import vn.gpay.gsmart.core.workingprocess.WorkingProcess;
@RestController
@RequestMapping("/api/v1/workingprocess")
public class WorkingProcessAPI {
	// product
    @Autowired IWorkingProcess_Service workingprocessService;
	@Autowired IProductSewingCostService productSewingCostService;
	@Autowired IProductBalanceService productBalanceService;
	@Autowired IProductBalanceProcessService productBalanceProcessService;
    
	// porder
	@Autowired IPOrderWorkingProcess_Service porderWorkingProcessService;
	@Autowired IPorderSewingCost_Service pordersewingService;
	@Autowired IPOrderBalanceService porderBalanceService;
	@Autowired IPOrderBalanceProcessService porderBalanceProcessService;
	
    
    @GetMapping("/workingprocess")
    public List<WorkingProcess> getAllProcess() {
        return workingprocessService.findAll();
    }

    @GetMapping("/workingprocess/{id}")
    public ResponseEntity<WorkingProcess> getProcessById(@PathVariable(value = "id") Long processId){
        WorkingProcess process = workingprocessService.findOne(processId);
        return ResponseEntity.ok().body(process);
    }
    
    @PostMapping("/workingprocess")
    public WorkingProcess createProcess(@Valid @RequestBody WorkingProcess process) {
        return workingprocessService.save(process);
    }

    @PutMapping("/workingprocess/{id}")
    public ResponseEntity<WorkingProcess> updateProcess(@PathVariable(value = "id") Long orgId,
         @Valid @RequestBody WorkingProcess processDetails){
        WorkingProcess process = workingprocessService.findOne(orgId);

        process.setName(processDetails.getName());
        process.setParentid_link(processDetails.getParentid_link());
        final WorkingProcess updatedProcess = workingprocessService.save(process);
        return ResponseEntity.ok(updatedProcess);
    }

    @DeleteMapping("/workingprocess/{id}")
    public Map<String, Boolean> deleteProcess(@PathVariable(value = "id") Long orgId){
        WorkingProcess process = workingprocessService.findOne(orgId);

        workingprocessService.delete(process);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }	
    
    @RequestMapping(value = "/getby_product",method = RequestMethod.POST)
	public ResponseEntity<getby_product_response> GetByProduct(HttpServletRequest request, @RequestBody getby_product_request entity ) {
		getby_product_response response = new getby_product_response();
		try {
			long productid_link = entity.productid_link;
			
			response.data = workingprocessService.getby_product(productid_link);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_product_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<getby_product_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getby_porder",method = RequestMethod.POST)
	public ResponseEntity<get_porderworkingprocess_response> GetByPorder(HttpServletRequest request, @RequestBody getby_product_request entity ) {
		get_porderworkingprocess_response response = new get_porderworkingprocess_response();
		try {
			long porderid_link = entity.porderid_link;
			
			response.data = porderWorkingProcessService.getby_porder(porderid_link);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<get_porderworkingprocess_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<get_porderworkingprocess_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/create",method = RequestMethod.POST)
	public ResponseEntity<create_workingprocess_response> Create(HttpServletRequest request, @RequestBody create_workingprocess_request entity ) {
		create_workingprocess_response response = new create_workingprocess_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			
			WorkingProcess wp = entity.data;
			String code = wp.getCode();
			Long productid_link = wp.getProductid_link();
			
			if(wp.getId() == null) {
				wp.setUsercreatedid_link(user.getId());
				wp.setTimecreated(new Date());
				wp.setOrgrootid_link(orgrootid_link);
				wp.setProcess_type(1);
				
				List<WorkingProcess> workingProcess_list = workingprocessService.getByCode(code, productid_link);
				if(workingProcess_list.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage("Mã công đoạn đã tồn tại");
				    return new ResponseEntity<create_workingprocess_response>(response, HttpStatus.OK);
				}
			}else {
				Long id = wp.getId();
				List<WorkingProcess> workingProcess_list = workingprocessService.getByCode_NotId(code, productid_link, id);
				if(workingProcess_list.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage("Mã công đoạn đã tồn tại");
				    return new ResponseEntity<create_workingprocess_response>(response, HttpStatus.OK);
				}
			}
			
			response.data = workingprocessService.save(wp);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<create_workingprocess_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<create_workingprocess_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/create_porderworkingprocess",method = RequestMethod.POST)
	public ResponseEntity<create_porderworkingprocess_response> create_porderworkingprocess(HttpServletRequest request, @RequestBody create_porderworkingprocess_request entity ) {
		create_porderworkingprocess_response response = new create_porderworkingprocess_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			
			POrderWorkingProcess wp = entity.data;
			String code = wp.getCode();
			Long productid_link = wp.getProductid_link();
			Long porderid_link = wp.getPorderid_link();
			
			if(wp.getId() == null) {
				wp.setUsercreatedid_link(user.getId());
				wp.setTimecreated(new Date());
				wp.setOrgrootid_link(orgrootid_link);
				wp.setProcess_type(1);
				
				List<POrderWorkingProcess> workingProcess_list = porderWorkingProcessService.getByCode(code, productid_link, porderid_link);
				if(workingProcess_list.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage("Mã công đoạn đã tồn tại");
				    return new ResponseEntity<create_porderworkingprocess_response>(response, HttpStatus.OK);
				}
			}else {
				Long id = wp.getId();
				List<POrderWorkingProcess> workingProcess_list = porderWorkingProcessService.getByCode_NotId(code, productid_link, porderid_link, id);
				if(workingProcess_list.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage("Mã công đoạn đã tồn tại");
				    return new ResponseEntity<create_porderworkingprocess_response>(response, HttpStatus.OK);
				}
			}
			
			response.data = porderWorkingProcessService.save(wp);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<create_porderworkingprocess_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<create_porderworkingprocess_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete",method = RequestMethod.POST)
	public ResponseEntity<workingprocess_delete_response> Delete(HttpServletRequest request, @RequestBody workingprocess_delete_reques entity ) {
		workingprocess_delete_response response = new workingprocess_delete_response();
		try {
//			workingprocessService.deleteById(entity.id);
			
			Long id = entity.id;
			List<ProductSewingCost> productSewingCost_list = productSewingCostService.getby_workingprocess(id);
			for(ProductSewingCost productSewingCost : productSewingCost_list) {
				List<ProductBalanceProcess> productBalanceProcess_list = productBalanceProcessService.getByProductSewingcost(productSewingCost.getId());
				for(ProductBalanceProcess productBalanceProcess : productBalanceProcess_list) {
					productBalanceProcessService.deleteById(productBalanceProcess.getId());
				}
				productSewingCostService.deleteById(productSewingCost.getId());
			}
			workingprocessService.deleteById(id);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<workingprocess_delete_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<workingprocess_delete_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete_porderworkingprocess",method = RequestMethod.POST)
	public ResponseEntity<workingprocess_delete_response> delete_porderworkingprocess(HttpServletRequest request, @RequestBody workingprocess_delete_reques entity ) {
		workingprocess_delete_response response = new workingprocess_delete_response();
		try {
//			porderWorkingProcessService.deleteById(entity.id);
			
			Long id = entity.id;
			
			List<POrderSewingCost> porderSewingCost_list = pordersewingService.getby_workingprocess(id);
			for(POrderSewingCost porderSewingCost : porderSewingCost_list) {
				List<POrderBalanceProcess> porderBalanceProcess_list = porderBalanceProcessService.getByPorderSewingcost(porderSewingCost.getId());
				for(POrderBalanceProcess porderBalanceProcess : porderBalanceProcess_list) {
					porderBalanceProcessService.deleteById(porderBalanceProcess.getId());
				}
				pordersewingService.deleteById(porderSewingCost.getId());
			}
			porderWorkingProcessService.deleteById(id);
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<workingprocess_delete_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<workingprocess_delete_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete_multi",method = RequestMethod.POST)
	public ResponseEntity<workingprocess_delete_response> delete_multi(HttpServletRequest request, @RequestBody workingprocess_delete_reques entity ) {
		workingprocess_delete_response response = new workingprocess_delete_response();
		try {
			List<Long> idList = entity.idList;
			
			for(Long id : idList) {
				List<ProductSewingCost> productSewingCost_list = productSewingCostService.getby_workingprocess(id);
				for(ProductSewingCost productSewingCost : productSewingCost_list) {
					List<ProductBalanceProcess> productBalanceProcess_list = productBalanceProcessService.getByProductSewingcost(productSewingCost.getId());
					for(ProductBalanceProcess productBalanceProcess : productBalanceProcess_list) {
						productBalanceProcessService.deleteById(productBalanceProcess.getId());
					}
					productSewingCostService.deleteById(productSewingCost.getId());
				}
				workingprocessService.deleteById(id);
			}
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<workingprocess_delete_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<workingprocess_delete_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/delete_multi_porderworkingprocess",method = RequestMethod.POST)
	public ResponseEntity<workingprocess_delete_response> delete_multi_porderworkingprocess(HttpServletRequest request, @RequestBody workingprocess_delete_reques entity ) {
		workingprocess_delete_response response = new workingprocess_delete_response();
		try {
			List<Long> idList = entity.idList;
			
			for(Long id : idList) {
				List<POrderSewingCost> porderSewingCost_list = pordersewingService.getby_workingprocess(id);
				for(POrderSewingCost porderSewingCost : porderSewingCost_list) {
					List<POrderBalanceProcess> porderBalanceProcess_list = porderBalanceProcessService.getByPorderSewingcost(porderSewingCost.getId());
					for(POrderBalanceProcess porderBalanceProcess : porderBalanceProcess_list) {
						porderBalanceProcessService.deleteById(porderBalanceProcess.getId());
					}
					pordersewingService.deleteById(porderSewingCost.getId());
				}
				porderWorkingProcessService.deleteById(id);
			}
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<workingprocess_delete_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<workingprocess_delete_response>(response, HttpStatus.OK);
		}
	}
}
