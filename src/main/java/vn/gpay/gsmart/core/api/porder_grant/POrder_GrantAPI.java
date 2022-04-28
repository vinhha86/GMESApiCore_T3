package vn.gpay.gsmart.core.api.porder_grant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.porder.IPOrder_Service;
import vn.gpay.gsmart.core.porder.POrder;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_SKUService;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_Service;
import vn.gpay.gsmart.core.porder_grant.KeHoachVaoChuyen;
import vn.gpay.gsmart.core.porder_grant.POrderGrant;
import vn.gpay.gsmart.core.porder_grant.POrderGrant_SKU;
import vn.gpay.gsmart.core.porder_grant_sku_plan.IPOrderGrant_SKU_Plan_Service;
import vn.gpay.gsmart.core.porder_grant_sku_plan.POrderGrant_SKU_Plan;
import vn.gpay.gsmart.core.porderprocessing.IPOrderProcessing_Service;
import vn.gpay.gsmart.core.porderprocessing.POrderProcessing;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.POrderStatus;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/porder_grant")
public class POrder_GrantAPI {
	@Autowired
	private IPOrderGrant_Service porderGrantService;
	@Autowired
	private IPOrderGrant_SKUService porderGrant_SKUService;
	@Autowired
	private IPOrder_Service porderService;
	@Autowired
	private IPOrderProcessing_Service pprocessRepository;
	@Autowired
	IGpayUserOrgService userOrgService;
	@Autowired
	IPContract_POService poService;
	@Autowired
	IOrgService orgService;
	@Autowired
	Common commonService;
	@Autowired
	private IPOrderGrant_SKU_Plan_Service porderGrant_SKU_Plan_Service;
//	ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/getone", method = RequestMethod.POST)
	public ResponseEntity<POrder_Grant_GetOne_Response> POrderGetOne(@RequestBody POrder_Grant_GetOne_Request entity,
			HttpServletRequest request) {
		POrder_Grant_GetOne_Response response = new POrder_Grant_GetOne_Response();
		try {

			response.data = porderGrantService.getByOrderIDAndOrg(entity.granttoorgid_link, entity.porderid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_Grant_GetOne_Response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_Grant_GetOne_Response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getByPcontractPo", method = RequestMethod.POST)
	public ResponseEntity<POrder_Grant_GetOne_Response> getByPcontractPo(@RequestBody POrder_Grant_GetOne_Request entity,
			HttpServletRequest request) {
		POrder_Grant_GetOne_Response response = new POrder_Grant_GetOne_Response();
		try {

			response.data = porderGrantService.getByOrderIDAndOrg(entity.granttoorgid_link, entity.porderid_link);
			//
			POrderGrant porderGrant = new POrderGrant();
			// tim porder theo pcontract_poid_link
			Long pcontract_poid_link = entity.pcontract_poid_link;
//			List<POrder> porder_list = porderService.getByPcontractPO(pcontract_poid_link);
			// tim grant theo pcontract_poid_link
			
			// sua tim theo pcontract_poid_link
			List<POrderGrant> porderGrant_list = porderGrantService.getby_pcontract_po_id(pcontract_poid_link);
			
			if(porderGrant_list.size() > 0) {
				porderGrant = porderGrant_list.get(0);
			}
			
			response.data = porderGrant;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_Grant_GetOne_Response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_Grant_GetOne_Response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/findone", method = RequestMethod.POST)
	public ResponseEntity<POrder_Grant_GetOne_Response> POrderFindOne(@RequestBody POrder_Grant_findOne_request entity,
			HttpServletRequest request) {
		POrder_Grant_GetOne_Response response = new POrder_Grant_GetOne_Response();
		try {

			response.data = porderGrantService.findOne(entity.idPorderGrant);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_Grant_GetOne_Response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_Grant_GetOne_Response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getby_poconfirm", method = RequestMethod.POST)
	public ResponseEntity<GetByPO_reseponse> GetByPOConfirm(@RequestBody GetByPO_request entity,
			HttpServletRequest request) {
		GetByPO_reseponse response = new GetByPO_reseponse();
		try {

			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgid_link = user.getOrgid_link();
			Long pcontract_poid_link = entity.pcontract_poid_link;
			PContract_PO po_line = poService.findOne(pcontract_poid_link);
			List<Long> orgs = new ArrayList<Long>();

			List<String> orgTypes = new ArrayList<String>();
			orgTypes.add("13");

			orgs.add(orgid_link);
			List<Org> lsOrgChild = orgService.getorgChildrenbyOrg(orgid_link, orgTypes);

			for (Org theOrg : lsOrgChild) {
				if (!orgs.contains(theOrg.getId())) {
					orgs.add(theOrg.getId());
				}
			}

			if (orgid_link != 1) {
				List<GpayUserOrg> list_user_org = userOrgService.getall_byuser(user.getId());
				for (GpayUserOrg userorg : list_user_org) {
					if (!orgs.contains(userorg.getOrgid_link())) {
						orgs.add(userorg.getOrgid_link());
					}
				}
			}

			response.data = porderGrantService.getByOfferAndOrg(po_line.getParentpoid_link(), orgs);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<GetByPO_reseponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<GetByPO_reseponse>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<POrder_GrantCreate_response> Create(HttpServletRequest request,
			@RequestBody POrder_GrantCreate_request entity) {
		POrder_GrantCreate_response response = new POrder_GrantCreate_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();

			POrderGrant porder_grant = entity.data;

			// Lay thong tin PO
			POrder thePOrder = porderService.findOne(porder_grant.getPorderid_link());

			if (porder_grant.getId() == null || porder_grant.getId() == 0) {
				porder_grant.setOrgrootid_link(orgrootid_link);
				porder_grant.setOrdercode(thePOrder.getOrdercode());

				porder_grant.setGrantdate(new Date());
				porder_grant.setUsercreatedid_link(user.getId());
				porder_grant.setStatus(POrderStatus.PORDER_STATUS_GRANTED);
				porder_grant.setTimecreated(new Date());
			}

			porderGrantService.save(porder_grant);
			response.id = porder_grant.getId();

			// Xoa List SKU cu
			List<POrderGrant_SKU> list_sku = porderGrant_SKUService.getPOrderGrant_SKU(porder_grant.getId());
			for (POrderGrant_SKU sku : list_sku) {
				porderGrant_SKUService.delete(sku);
			}

			// them list moi
			for (POrderGrant_SKU theGrantSKU : porder_grant.getPorder_grant_sku()) {
				POrderGrant_SKU newGrantSKU = new POrderGrant_SKU();
				newGrantSKU.setId(null);
				newGrantSKU.setOrgrootid_link(orgrootid_link);
				newGrantSKU.setPordergrantid_link(porder_grant.getId());
				newGrantSKU.setSkuid_link(theGrantSKU.getSkuid_link());
				newGrantSKU.setGrantamount(theGrantSKU.getGrantamount());
				porderGrant_SKUService.save(newGrantSKU);
			}
			// Create line on Porder Procesing
			if (porder_grant.getId() != null || porder_grant.getId() != 0) {
				POrderProcessing pprocess = new POrderProcessing();

				pprocess.setOrgrootid_link(porder_grant.getOrgrootid_link());
				pprocess.setPorderid_link(porder_grant.getPorderid_link());
//		        pprocess.setOrdercode(thePOrder.getOrdercode());
				pprocess.setGranttoorgid_link(porder_grant.getGranttoorgid_link());
//		        pprocess.setGranttoorgname(porder_grant.getGranttoorgname());
				pprocess.setTotalorder(porder_grant.getGrantamount());

				pprocess.setProcessingdate(new Date());

				pprocess.setAmountcut(porder_grant.getGrantamount());
				pprocess.setAmountcutsum(porder_grant.getGrantamount());
				pprocess.setAmountcutsumprev(0);

				pprocess.setAmountinput(0);
				pprocess.setAmountinputsum(0);
				pprocess.setAmountinputsumprev(0);

				pprocess.setAmountoutput(0);
				pprocess.setAmountoutputsum(0);
				pprocess.setAmountoutputsumprev(0);

				pprocess.setAmounterror(0);
				pprocess.setAmounterrorsum(0);
				pprocess.setAmounterrorsumprev(0);

				pprocess.setAmountkcs(0);
				pprocess.setAmountkcssum(0);
				pprocess.setAmountkcssumprev(0);

				pprocess.setAmountpacked(0);
				pprocess.setAmountpackedsum(0);
				pprocess.setAmountpackedsumprev(0);

				pprocess.setAmountstocked(0);
				pprocess.setAmountstockedsum(0);
				pprocess.setAmountstockedsumprev(0);

				pprocess.setStatus(POrderStatus.PORDER_STATUS_GRANTED);
				pprocess.setUsercreatedid_link(user.getId());
				pprocess.setTimecreated(new Date());

				pprocessRepository.save(pprocess);
			}

			thePOrder.setStatus(POrderStatus.PORDER_STATUS_GRANTED);
			porderService.save(thePOrder);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_GrantCreate_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_GrantCreate_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getByOrderId", method = RequestMethod.POST)
	public ResponseEntity<POrder_Grant_findByPorderId_response> getByOrderId(
			@RequestBody POrder_Grant_findByPorderId_request entity, HttpServletRequest request) {
		POrder_Grant_findByPorderId_response response = new POrder_Grant_findByPorderId_response();
		try {

			response.data = porderGrantService.getByOrderId(entity.porderid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_Grant_findByPorderId_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_Grant_findByPorderId_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getProcessingByOrgId", method = RequestMethod.POST)
	public ResponseEntity<POrder_Grant_findByPorderId_response> getProcessingByOrgId(
			@RequestBody POrder_Grant_findByOrgId_request entity, HttpServletRequest request) {
		POrder_Grant_findByPorderId_response response = new POrder_Grant_findByPorderId_response();
		try {

			response.data = porderGrantService.getProcessingByOrgId(entity.orgid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_Grant_findByPorderId_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_Grant_findByPorderId_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/get_grant_change", method = RequestMethod.POST)
	public ResponseEntity<POrder_grant_getchange_response> getCHange(HttpServletRequest request) {
		POrder_grant_getchange_response response = new POrder_grant_getchange_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgid_link = user.getOrgid_link();
			List<Long> orgs = new ArrayList<Long>();
			if (orgid_link != 0 && orgid_link != 1) {
				for (GpayUserOrg userorg : userOrgService.getall_byuser(user.getId())) {
					orgs.add(userorg.getOrgid_link());
				}
				// Them chinh don vi cua user
				orgs.add(orgid_link);

				response.data = new ArrayList<POrderGrant>();
				for (Long orgid : orgs) {
					response.data.addAll(porderGrantService.get_grant_change(orgid));
				}
			} else
				response.data = porderGrantService.get_grant_change(null);
//			response.data = porderGrantService.get_grant_change(entity.orgid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_grant_getchange_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_grant_getchange_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/get_kehoachvaochuyen", method = RequestMethod.POST)
	public ResponseEntity<get_KeHoachVaoChuyen_response> getKeHoachVaoChuyen(HttpServletRequest request) {
		get_KeHoachVaoChuyen_response response = new get_KeHoachVaoChuyen_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgid_link = user.getOrgid_link();
			Date date_from = new Date();
			Date date_to = Common.Date_Add(date_from, 6);
			List<Long> list_orgid = new ArrayList<Long>();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			
			if (orgid_link != 1) {
				list_orgid.add(orgid_link);
				
				List<GpayUserOrg> list_user_org = userOrgService.getall_byuser(user.getId());
				for (GpayUserOrg userorg : list_user_org) {
					if (!list_orgid.contains(userorg.getOrgid_link())) {
						list_orgid.add(userorg.getOrgid_link());
					}
				}
			}
			
			// list_orgid : ds phan xuong
			// grant lay theo to chuyen -> tao ds to chuyen
			List<Long> list_idToChuyen = new ArrayList<Long>();
			for(Long idpx : list_orgid) {
				List<Org> listToChuyen = orgService.findOrgAllByParent(idpx);
				for(Org toChuyen : listToChuyen) {
					list_idToChuyen.add(toChuyen.getId());
				}
			}
			
//			List<POrderGrant> list_grant = porderGrantService.get_KehoachVaoChuyen(date_from, date_to, list_orgid);
			List<POrderGrant> list_grant = porderGrantService.get_KehoachVaoChuyen(date_from, date_to, list_idToChuyen);
			Date day0 = date_from;
			Date day1 = Common.Date_Add(date_from, 1);
			Date day2 = Common.Date_Add(date_from, 2);
			Date day3 = Common.Date_Add(date_from, 3);
			Date day4 = Common.Date_Add(date_from, 4);
			Date day5 = Common.Date_Add(date_from, 5);
			Date day6 = Common.Date_Add(date_from, 6);
			
			List<KeHoachVaoChuyen> list_kehoach = new ArrayList<KeHoachVaoChuyen>();
			for(POrderGrant grant : list_grant) {
				KeHoachVaoChuyen kehoach = new KeHoachVaoChuyen();
				
				kehoach.setBuyername(grant.getBuyername());
				kehoach.setDonvi(grant.getDonvi());
				kehoach.setGranttoorgname(grant.getGranttoorgname());
				kehoach.setId(grant.getId());
				kehoach.setPo_Lines(grant.getpo_Lines());
				kehoach.setPorderid_link(grant.getPorderid_link());
				kehoach.setProductcode(grant.getProductcode());
				
				int soluong_0 = 0, soluong_1 = 0,soluong_2 = 0,soluong_3 = 0,soluong_4 = 0,soluong_5 = 0,soluong_6 = 0;
				List<POrderGrant_SKU_Plan> list_grant_plan = porderGrant_SKU_Plan_Service.getByPOrderGrant_Date(grant.getId(), date_from, date_to);
				for(POrderGrant_SKU_Plan plan : list_grant_plan) {
					if(df.format(plan.getDate()).equals(df.format(day0))) {
						soluong_0 += plan.getAmount();
					}
					else if(df.format(plan.getDate()).equals(df.format(day1))) {
						soluong_1 += plan.getAmount();
					}
					else if(df.format(plan.getDate()).equals(df.format(day2))) {
						soluong_2 += plan.getAmount();
					}
					else if(df.format(plan.getDate()).equals(df.format(day3))) {
						soluong_3 += plan.getAmount();
					}
					else if(df.format(plan.getDate()).equals(df.format(day4))) {
						soluong_4 += plan.getAmount();
					}
					else if(df.format(plan.getDate()).equals(df.format(day5))) {
						soluong_5 += plan.getAmount();
					}
					else if(df.format(plan.getDate()).equals(df.format(day6))) {
						soluong_6 += plan.getAmount();
					}
				}
				
				kehoach.setDay0(soluong_0);
				kehoach.setDay1(soluong_1);
				kehoach.setDay2(soluong_2);
				kehoach.setDay3(soluong_3);
				kehoach.setDay4(soluong_4);
				kehoach.setDay5(soluong_5);
				kehoach.setDay6(soluong_6);
				
				list_kehoach.add(kehoach);
			}

			response.data = list_kehoach;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<get_KeHoachVaoChuyen_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<get_KeHoachVaoChuyen_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getDanhSachLenhKeHoach", method = RequestMethod.POST)
	public ResponseEntity<POrder_grant_getchange_response> getDanhSachLenhKeHoach(@RequestBody POrder_Grant_findOne_request entity, HttpServletRequest request) {
		POrder_grant_getchange_response response = new POrder_grant_getchange_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgid_link = user.getOrgid_link();
			Long productid_link = entity.productid_link;
			List<Long> orgs = new ArrayList<Long>();
			if (orgid_link != 0 && orgid_link != 1) {
				// lay cua phan xuong
				for (GpayUserOrg userorg : userOrgService.getall_byuser(user.getId())) {
					orgs.add(userorg.getOrgid_link());
				}
				// Them chinh don vi cua user
				orgs.add(orgid_link);
//				System.out.println("px");
//				System.out.println(orgs);

				response.data = new ArrayList<POrderGrant>();
				for (Long orgid : orgs) {
					// lay danh sach cac lenh ke hoach cua px
					response.data.addAll(porderGrantService.get_dsLenhKeHoach_byProduct(productid_link, orgid));
				}
			} else {
				// lay all
//				System.out.println("all");
//				System.out.println(orgs);
				// lay danh sach cac lenh ke hoach
				response.data = porderGrantService.get_dsLenhKeHoach_byProduct(productid_link, null);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<POrder_grant_getchange_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<POrder_grant_getchange_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
}
