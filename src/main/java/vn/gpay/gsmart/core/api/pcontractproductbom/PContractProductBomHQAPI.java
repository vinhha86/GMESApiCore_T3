package vn.gpay.gsmart.core.api.pcontractproductbom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.api.pcontract_po.get_sku_by_line_request;
import vn.gpay.gsmart.core.attributevalue.Attributevalue;
import vn.gpay.gsmart.core.attributevalue.IAttributeValueService;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.pcontract_bomhq_npl_poline.IPContract_bomHQ_npl_poline_Service;
import vn.gpay.gsmart.core.pcontract_bomhq_npl_poline.PContract_bomHQ_npl_poline;
import vn.gpay.gsmart.core.pcontract_bomhq_npl_poline_sku.IPContract_bomHQ_npl_poline_sku_Service;
import vn.gpay.gsmart.core.pcontract_bomhq_npl_poline_sku.PContract_bomHQ_npl_poline_sku;
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontract_product_bomhq_log.IPContract_bomHQ_sku_log_Service;
import vn.gpay.gsmart.core.pcontract_product_bomhq_log.PContract_bomHQ_sku_log;
import vn.gpay.gsmart.core.pcontractattributevalue.IPContractProductAtrributeValueService;
import vn.gpay.gsmart.core.pcontractbomhqcolor.IPContractBomHQColorService;
import vn.gpay.gsmart.core.pcontractbomhqcolor.PContractBomHQColor;
import vn.gpay.gsmart.core.pcontractbomhqsku.IPContractBOMHQSKUService;
import vn.gpay.gsmart.core.pcontractbomhqsku.PContractBOMHQSKU;
import vn.gpay.gsmart.core.pcontractproduct.IPContractProductService;
import vn.gpay.gsmart.core.pcontractproduct.PContractProduct;
import vn.gpay.gsmart.core.pcontractproductbomhq.IPContractProductBomHQService;
import vn.gpay.gsmart.core.pcontractproductbomhq.PContractProductBomHQ;
import vn.gpay.gsmart.core.pcontractproductbomhq.PContractProductBomHQ_Runnable;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKUService;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.productpairing.IProductPairingService;
import vn.gpay.gsmart.core.productpairing.ProductPairing;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.sku.ISKU_AttributeValue_Service;
import vn.gpay.gsmart.core.sku.ISKU_Service;
import vn.gpay.gsmart.core.sku.SKU;
import vn.gpay.gsmart.core.sku.SKU_Attribute_Value;
import vn.gpay.gsmart.core.task.ITask_Service;
import vn.gpay.gsmart.core.task_checklist.ITask_CheckList_Service;
import vn.gpay.gsmart.core.task_flow.ITask_Flow_Service;
import vn.gpay.gsmart.core.task_object.ITask_Object_Service;
import vn.gpay.gsmart.core.utils.AtributeFixValues;
import vn.gpay.gsmart.core.utils.ProductType;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/pcontractproductbomhq")
public class PContractProductBomHQAPI {
	@Autowired
	IPContractProductBomHQService ppbomhqservice;
	@Autowired
	IPContractBomHQColorService ppbomcolorhqservice;
	@Autowired
	IPContractBOMHQSKUService ppbomhqskuservice;
	@Autowired
	IPContractProductAtrributeValueService ppatt_service;
	@Autowired
	IPContractProductSKUService ppskuService;
	@Autowired
	IPContractProductService ppService;
	@Autowired
	ITask_Object_Service taskobjectService;
	@Autowired
	ITask_Service taskService;
	@Autowired
	ITask_CheckList_Service checklistService;
	@Autowired
	ITask_Flow_Service commentService;
	@Autowired
	ISKU_Service skuService;
	@Autowired
	IAttributeValueService avService;
	@Autowired
	IPContract_bomHQ_npl_poline_Service po_npl_Service;
	@Autowired
	IPContract_bomHQ_npl_poline_sku_Service po_npl_sku_Service;
	@Autowired
	IPContract_POService poService;
	@Autowired
	IProductPairingService pairService;
	@Autowired
	IPContract_bomHQ_sku_log_Service bomlogService;
	@Autowired
	ISKU_AttributeValue_Service skuavService;

	@RequestMapping(value = "/create_pcontract_productbom", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> CreateProductBom(HttpServletRequest request,
			@RequestBody PContractProductBom_create_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Long> listnpl = entity.listnpl;
			Long productid_link = entity.productid_link;
			Long pcontractid_link = entity.pcontractid_link;
			Date date = new Date();

			for (Long materialid_link : entity.listnpl) {
				// them vao bang product_bom
				List<PContractProductBomHQ> listBom = ppbomhqservice.getby_pcontract_product_material(productid_link,
						pcontractid_link, materialid_link);
				PContractProductBomHQ productbomhq = null;
				SKU sku = skuService.findOne(materialid_link);

				if (listBom.size() == 0) {
					productbomhq = new PContractProductBomHQ();
					productbomhq.setProductid_link(productid_link);
					productbomhq.setMaterialid_link(materialid_link);
					productbomhq.setAmount((float) 0);
					productbomhq.setLost_ratio((float) 1);
					productbomhq.setDescription("");
					productbomhq.setCreateduserid_link(user.getId());
					productbomhq.setCreateddate(new Date());
					productbomhq.setOrgrootid_link(user.getRootorgid_link());
					productbomhq.setPcontractid_link(pcontractid_link);
					productbomhq.setUnitid_link(sku.getUnitid_link());
					productbomhq = ppbomhqservice.save(productbomhq);
				}else {
					productbomhq = listBom.get(0);
				}
				
				//Tạo BOMHQ Color
				
				
				//Tạo BOMHQ SKU
				List<Long> listColorId = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link,
						AtributeFixValues.ATTR_COLOR);
				List<Long> listSizeId = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link,
						AtributeFixValues.ATTR_SIZE);
				
				List<PContractProductSKU> pContractProductSKU_list = ppskuService.getlistsku_byproduct_and_pcontract(user.getRootorgid_link(), productid_link, pcontractid_link);
//				System.out.println(pContractProductSKU_list.size());
				for(Long colorId : listColorId) {
					for(Long sizeId : listSizeId) {
						for(PContractProductSKU pContractProductSKU : pContractProductSKU_list) {
							Long pContractProductSKU_color = pContractProductSKU.getColor_id();
							Long pContractProductSKU_size = pContractProductSKU.getSizeid_link();
							if(colorId.equals(pContractProductSKU_color) && sizeId.equals(pContractProductSKU_size)) {
								PContractBOMHQSKU pcontractBOMHQSKU = new PContractBOMHQSKU();
								pcontractBOMHQSKU.setOrgrootid_link(user.getRootorgid_link());
								pcontractBOMHQSKU.setProductid_link(productid_link);
								pcontractBOMHQSKU.setProduct_skuid_link(pContractProductSKU.getSkuid_link());
								pcontractBOMHQSKU.setMaterial_skuid_link(materialid_link);
								pcontractBOMHQSKU.setAmount((float) 1);
								pcontractBOMHQSKU.setLost_ratio((float) 1);
								pcontractBOMHQSKU.setCreateddate(date);
								pcontractBOMHQSKU.setCreateduserid_link(user.getId());
								pcontractBOMHQSKU.setPcontractid_link(pcontractid_link);
								ppbomhqskuservice.save(pcontractBOMHQSKU);
								break;
							}
						}
					}
				}
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/delete_bom", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> DeleteBom(HttpServletRequest request, @RequestBody deletebom_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long productid_link = entity.productid_link;
			long pcontractid_link = entity.pcontractid_link;
			long orgrootid_link = user.getRootorgid_link();

			// xoa trong pcontract_product_bomhq
			List<PContractProductBomHQ> listbom = ppbomhqservice.get_pcontract_productBOMbyid(productid_link,
					pcontractid_link);
			for (PContractProductBomHQ productbomhq : listbom) {
				ppbomhqservice.delete(productbomhq);
			}

			List<PContractBOMHQSKU> listbomsku = ppbomhqskuservice.getbypcontract_and_product(pcontractid_link,
					productid_link);
			for (PContractBOMHQSKU productbomhqsku : listbomsku) {
				ppbomhqskuservice.delete(productbomhqsku);
			}

			// xoa trong pcontractpo_npl
			List<PContract_bomHQ_npl_poline> list_po_npl = po_npl_Service.getby_product_and_npl(productid_link,
					pcontractid_link, (long) 0);
			for (PContract_bomHQ_npl_poline po_npl : list_po_npl) {
				List<PContract_bomHQ_npl_poline_sku> list_po_npl_sku = po_npl_sku_Service.getby_po(orgrootid_link,
						po_npl.getPcontract_poid_link(), po_npl.getNpl_skuid_link(), productid_link);
				for (PContract_bomHQ_npl_poline_sku po_npl_sku : list_po_npl_sku) {
					po_npl_sku_Service.delete(po_npl_sku);
				}

				po_npl_Service.delete(po_npl);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/copy_poline", method = RequestMethod.POST)
	public ResponseEntity<copy_poline_response> CopyPoline(HttpServletRequest request,
			@RequestBody copy_poline_request entity) {
		copy_poline_response response = new copy_poline_response();
		try {
			long pcontractid_link = entity.pcontractid_link;
			long material_skuid_link = entity.material_skuid_link;
			long material_skuid_link_des = entity.material_skuid_link_des;

			List<PContract_bomHQ_npl_poline> list_po = po_npl_Service.getby_pcontract_and_npl(pcontractid_link,
					material_skuid_link);
			// xoa het po-line truocn khi paste
			List<PContract_bomHQ_npl_poline> list_po_des = po_npl_Service.getby_pcontract_and_npl(pcontractid_link,
					material_skuid_link_des);
			for (PContract_bomHQ_npl_poline poline : list_po_des) {
				po_npl_Service.delete(poline);
			}

			for (PContract_bomHQ_npl_poline poline : list_po) {
				PContract_bomHQ_npl_poline poline_new = new PContract_bomHQ_npl_poline();
				poline_new.setId(null);
				poline_new.setNpl_skuid_link(material_skuid_link_des);
				poline_new.setPcontract_poid_link(poline.getPcontract_poid_link());
				poline_new.setPcontractid_link(pcontractid_link);
				po_npl_Service.save(poline_new);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<copy_poline_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<copy_poline_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/confim_bomhq", method = RequestMethod.POST)
	public ResponseEntity<confim_bom2_response> ConfimBomHQ(HttpServletRequest request,
			@RequestBody confim_bom1_request entity) {
		confim_bom2_response response = new confim_bom2_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;

			List<PContractProduct> list_pp = ppService.get_by_product_and_pcontract(orgrootid_link, productid_link,
					pcontractid_link);
			if (list_pp.size() > 0) {
				PContractProduct pp = list_pp.get(0);
				pp.setIsbom2done(true);
				ppService.save(pp);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<confim_bom2_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/select_poline", method = RequestMethod.POST)
	public ResponseEntity<pcontractbomhq_selectpoline_response> SelectPOLine(HttpServletRequest request,
			@RequestBody pcontractbom2_selectpoline_request entity) {
		pcontractbomhq_selectpoline_response response = new pcontractbomhq_selectpoline_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontractid_link = entity.pcontractid_link;
			long pcontract_poid_link = entity.pcontract_poid_link;
			long material_skuid_link = entity.material_skuid_link;

			PContract_PO po = poService.findOne(pcontract_poid_link);
			List<Long> list_product = new ArrayList<Long>();

			if (po.getProduct_typeid_link() == ProductType.SKU_TYPE_PRODUCT_PAIR) {
				List<ProductPairing> list_pair = pairService.getproduct_pairing_detail_bycontract(orgrootid_link,
						pcontractid_link, po.getProductid_link());
				for (ProductPairing pair : list_pair) {
					list_product.add(pair.getProductid_link());
				}
			} else {
				list_product.add(po.getProductid_link());
			}
			// them vao bang npl-poline
			List<PContract_bomHQ_npl_poline> list_poline = po_npl_Service.getby_po_and_npl(pcontract_poid_link,
					material_skuid_link);
			// kiem tra chua co trong db thi them vao
			if (list_poline.size() == 0) {
				PContract_bomHQ_npl_poline poline = new PContract_bomHQ_npl_poline();
				poline.setId(null);
				poline.setNpl_skuid_link(material_skuid_link);
				poline.setPcontract_poid_link(pcontract_poid_link);
				poline.setPcontractid_link(pcontractid_link);

				po_npl_Service.save(poline);
			}

			// them toan bo sku
			List<PContractProductSKU> list_sku = ppskuService.getlistsku_bypo_and_pcontract(orgrootid_link,
					pcontract_poid_link, pcontractid_link);
			for (Long productid_link : list_product) {
				for (PContractProductSKU sku : list_sku) {
					// kiem tra neu chua co thi them moi va neu co roi thi cap nhat lai so luong
					Long product_skuid_link = sku.getSkuid_link();
					List<PContract_bomHQ_npl_poline_sku> list_line_sku = po_npl_sku_Service.getby_po_and_sku(
							orgrootid_link, pcontract_poid_link, product_skuid_link, material_skuid_link);
					if (list_line_sku.size() == 0) {
						PContract_bomHQ_npl_poline_sku line_sku = new PContract_bomHQ_npl_poline_sku();
						line_sku.setId(null);
						line_sku.setMaterial_skuid_link(material_skuid_link);
						line_sku.setOrgrootid_link(orgrootid_link);
						line_sku.setPcontract_poid_link(pcontract_poid_link);
						line_sku.setPcontractid_link(pcontractid_link);
						line_sku.setProduct_skuid_link(product_skuid_link);
						line_sku.setProductid_link(productid_link);
						line_sku.setQuantity(sku.getPquantity_total());

						po_npl_sku_Service.save(line_sku);
					} else {
						PContract_bomHQ_npl_poline_sku line_sku = list_line_sku.get(0);
						line_sku.setQuantity(sku.getPquantity_total());
						po_npl_sku_Service.save(line_sku);
					}
				}
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<pcontractbomhq_selectpoline_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deselect_poline", method = RequestMethod.POST)
	public ResponseEntity<pcontractbom2_selectpoline_response> DeSelectPOLine(HttpServletRequest request,
			@RequestBody pcontractbom2_deselect_poline_request entity) {
		pcontractbom2_selectpoline_response response = new pcontractbom2_selectpoline_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontract_poid_link = entity.pcontract_poid_link;
			long material_skuid_link = entity.material_skuid_link;

			// them vao bang npl-poline
			List<PContract_bomHQ_npl_poline> list_poline = po_npl_Service.getby_po_and_npl(pcontract_poid_link,
					material_skuid_link);
			// kiem tra co trong db thi xoa di
			for (PContract_bomHQ_npl_poline poline : list_poline) {
				po_npl_Service.delete(poline);
			}

			// xoa toan bo sku
			Long productid_link = null; // null la lay het ko theo product
			List<PContract_bomHQ_npl_poline_sku> list_line_sku = po_npl_sku_Service.getby_po(orgrootid_link,
					pcontract_poid_link, material_skuid_link, productid_link);
			for (PContract_bomHQ_npl_poline_sku line_sku : list_line_sku) {
				po_npl_sku_Service.delete(line_sku);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<pcontractbom2_selectpoline_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getsku_byline", method = RequestMethod.POST)
	public ResponseEntity<pcontractbomhq_getsku_byline_response> GetSKUByLine(HttpServletRequest request,
			@RequestBody get_sku_by_line_request entity) {
		pcontractbomhq_getsku_byline_response response = new pcontractbomhq_getsku_byline_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long material_skuid_link = entity.material_skuid_link;
			long pcontract_poid_link = entity.pcontract_poid_link;
			long productid_link = entity.productid_link;

			long orgrootid_link = user.getRootorgid_link();
			response.data = po_npl_sku_Service.getby_po(orgrootid_link, pcontract_poid_link, material_skuid_link,
					productid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<pcontractbomhq_getsku_byline_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/update_pcontract_productbom", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UpdateProductBom(HttpServletRequest request,
			@RequestBody PContractProductBomHQ_update_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			ppbomhqservice.save(entity.data);
			Long colorid_link = entity.colorid_link;
			// Xóa trong bom_color và bom_sku
			long pcontractid_link = entity.data.getPcontractid_link();
			long productid_link = entity.data.getProductid_link();
			long materialid_link = entity.data.getMaterialid_link();

			// cap nhat lai vao bang bom-sku
			float amount = entity.data.getAmount();
			float lost_ratio = entity.data.getLost_ratio();

			List<Long> list_skuid = ppskuService.getsku_bypcontract_and_product(pcontractid_link, productid_link);
//			System.out.println(list_skuid);
			for (Long skuid : list_skuid) {
				SKU sku = skuService.findOne(skuid);

				Long sizeid_link = sku.getSize_id();
				Long colorid = sku.getColor_id();
				List<PContractBOMHQSKU> list_pContractBOMSKU = ppbomhqskuservice.getall_material_in_productBOMSKU(
						pcontractid_link, productid_link, sizeid_link, colorid, materialid_link);
				PContractBOMHQSKU bomsku = null;

				if (list_pContractBOMSKU.size() == 0) {
				} else {
					bomsku = list_pContractBOMSKU.get(0);
					bomsku.setLost_ratio(lost_ratio);
					if (amount > 0) {
						bomsku.setAmount(amount);
						ppbomhqskuservice.save(bomsku);
					}
				}

//				ppbomhqskuservice.save(bomsku);
			}

			if (entity.isUpdateBOM) {
				List<PContractBomHQColor> listcolor = ppbomcolorhqservice
						.getcolor_bymaterial_in_productBOMColor(pcontractid_link, productid_link, materialid_link);
				for (PContractBomHQColor pContractBOMColor : listcolor) {
					ppbomcolorhqservice.delete(pContractBOMColor);
				}
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/update_pcontract_productbomcolor", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UpdateProductBomColor(HttpServletRequest request,
			@RequestBody PContractBOMColor_update_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long pcontractid_link = entity.data.getPcontractid_link();
			long productid_link = entity.data.getProductid_link();
			long materialid_link = entity.data.getMaterialid_link();
			long colorid_link = entity.colorid_link;

			List<PContractBomHQColor> listcolor = ppbomcolorhqservice.getall_material_in_productBOMColor(pcontractid_link,
					productid_link, colorid_link, materialid_link);

			PContractBomHQColor pContractBOMColor = new PContractBomHQColor();
			if (listcolor.size() > 0) {
				pContractBOMColor = listcolor.get(0);
				pContractBOMColor.setAmount(entity.data.getAmount_color());
			} else {
				pContractBOMColor.setAmount(entity.data.getAmount_color());
				pContractBOMColor.setColorid_link(colorid_link);
				pContractBOMColor.setCreateddate(new Date());
				pContractBOMColor.setCreateduserid_link(user.getId());
				pContractBOMColor.setDescription(entity.data.getDescription());
				pContractBOMColor.setId((long) 0);
				pContractBOMColor.setMaterialid_link(materialid_link);
				pContractBOMColor.setOrgrootid_link(user.getRootorgid_link());
				pContractBOMColor.setPcontractid_link(pcontractid_link);
				pContractBOMColor.setProductid_link(productid_link);
			}
			ppbomcolorhqservice.save(pContractBOMColor);

			// update lại các màu khác từ chung về chung màu
			PContractProductBomHQ pContractProductBom = ppbomhqservice.findOne(entity.data.getId());
			float amount_color = pContractProductBom.getAmount();

			if (amount_color > 0) {
				// Lấy các màu trong sản phẩm của đơn hàng
				List<Long> list_color = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link,
						AtributeFixValues.ATTR_COLOR);
				list_color.remove(colorid_link);

				for (Long colorid : list_color) {
					PContractBomHQColor color = new PContractBomHQColor();

					color.setAmount(amount_color);
					color.setColorid_link(colorid);
					color.setCreateddate(new Date());
					color.setCreateduserid_link(user.getId());
					color.setDescription(entity.data.getDescription());
					color.setId((long) 0);
					color.setMaterialid_link(materialid_link);
					color.setOrgrootid_link(user.getRootorgid_link());
					color.setPcontractid_link(pcontractid_link);
					color.setProductid_link(productid_link);

					ppbomcolorhqservice.save(color);
				}
			}

			// update lai bang bom amount = 0
			pContractProductBom.setAmount((float) 0);
			ppbomhqservice.update(pContractProductBom);

			// update lai bang sku bom
			float amount = entity.data.getAmount_color();
			float lost_ratio = entity.data.getLost_ratio();
			List<Long> list_skuid = ppskuService.getsku_bycolor(pcontractid_link, productid_link, colorid_link);

			for (Long skuid : list_skuid) {
				SKU sku = skuService.findOne(skuid);

				long sizeid_link = sku.getSize_id();
				long colorid = sku.getColor_id();
				List<PContractBOMHQSKU> list_pContractBOMSKU = ppbomhqskuservice.getall_material_in_productBOMSKU(
						pcontractid_link, productid_link, sizeid_link, colorid, materialid_link);
				PContractBOMHQSKU bomsku = null;

				if (list_pContractBOMSKU.size() == 0) {
					bomsku = new PContractBOMHQSKU();
					bomsku.setAmount(amount);
					bomsku.setCreateddate(new Date());
					bomsku.setCreateduserid_link(user.getId());
					bomsku.setId(null);
					bomsku.setLost_ratio(lost_ratio);
					bomsku.setMaterial_skuid_link(materialid_link);
					bomsku.setOrgrootid_link(user.getRootorgid_link());
					bomsku.setPcontractid_link(pcontractid_link);
					bomsku.setProduct_skuid_link(sku.getId());
					bomsku.setProductid_link(productid_link);

				} else {
					bomsku = list_pContractBOMSKU.get(0);
					bomsku.setAmount(amount);
				}

				ppbomhqskuservice.save(bomsku);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/update_pcontract_productbomsku", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UpdateProductBomSKU(HttpServletRequest request,
			@RequestBody PContractBOMSKU_update_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long pcontractid_link = entity.data.getPcontractid_link();
			long productid_link = entity.data.getProductid_link();
			long materialid_link = entity.data.getMaterialid_link();
			long sizeid_link = entity.sizeid_link;
			long colorid_link = entity.colorid_link;
			long skuid_link = ppbomhqskuservice.getskuid_link_by_color_and_size(colorid_link, sizeid_link,
					productid_link);
			long orgrootid_link = user.getRootorgid_link();
			
			if (skuid_link > 0) {
				// Kiem tra neu chua co thi insert neu co roi thi update
				List<PContractBOMHQSKU> list_sku = ppbomhqskuservice.getall_material_in_productBOMSKU(pcontractid_link,
						productid_link, sizeid_link, colorid_link, materialid_link);
	
				float amount_old = 0;
				PContractBOMHQSKU pContractBOMSKU = new PContractBOMHQSKU();
				if (list_sku.size() > 0) {
					amount_old = list_sku.get(0).getAmount();
	
					pContractBOMSKU = list_sku.get(0);
					pContractBOMSKU.setAmount(entity.value);
				} else {
					pContractBOMSKU.setAmount(entity.value);
					pContractBOMSKU.setCreateddate(new Date());
					pContractBOMSKU.setCreateduserid_link(user.getId());
					pContractBOMSKU.setDescription(entity.data.getDescription());
					pContractBOMSKU.setId((long) 0);
					pContractBOMSKU.setMaterial_skuid_link(materialid_link);
					pContractBOMSKU.setOrgrootid_link(user.getRootorgid_link());
					pContractBOMSKU.setPcontractid_link(pcontractid_link);
					pContractBOMSKU.setProductid_link(productid_link);
					pContractBOMSKU.setLost_ratio(entity.data.getLost_ratio());
					pContractBOMSKU.setProduct_skuid_link(skuid_link);
				}
				ppbomhqskuservice.save(pContractBOMSKU);
	
				// update lai bang bom amount = 0
				PContractProductBomHQ pContractProductBom = ppbomhqservice.findOne(entity.data.getId());
				pContractProductBom.setAmount((float) 0);
				ppbomhqservice.update(pContractProductBom);
	
				// update lai bang sku bom
				List<PContractBomHQColor> listcolor = ppbomcolorhqservice.getall_material_in_productBOMColor(pcontractid_link,
						productid_link, colorid_link, materialid_link);
	
				for (PContractBomHQColor pColor : listcolor) {
					ppbomcolorhqservice.delete(pColor);
				}
	
				// kiem tra xem da chot dinh muc hay chua thi day vao bang log
				List<PContractProduct> list_pp = ppService.get_by_product_and_pcontract(orgrootid_link, productid_link,
						pcontractid_link);
				if (list_pp.size() > 0) {
					if (list_pp.get(0).getIsbom2done()) {
						PContract_bomHQ_sku_log bom_log = new PContract_bomHQ_sku_log();
						bom_log.setAmount(entity.value);
						bom_log.setAmount_old(amount_old);
						bom_log.setId(null);
						bom_log.setMaterial_skuid_link(materialid_link);
						bom_log.setOrgrootid_link(orgrootid_link);
						bom_log.setPcontractid_link(pcontractid_link);
						bom_log.setProduct_skuid_link(skuid_link);
						bom_log.setProductid_link(productid_link);
						bom_log.setTimeupdate(new Date());
						bom_log.setUserupdateid_link(user.getId());
	
						bomlogService.save(bom_log);
					}
				}
	
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Không có màu, cỡ sản phẩm tương ứng tại Chi tiết PO");
			}
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getlist_pcontract_productbom", method = RequestMethod.POST)
	public ResponseEntity<PContractProductBOMHQ_getbyproduct_response> GetListProductBom(HttpServletRequest request,
			@RequestBody PContractProductBOM_getbyproduct_request entity) {
		PContractProductBOMHQ_getbyproduct_response response = new PContractProductBOMHQ_getbyproduct_response();
		try {
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;

			response.data = ppbomhqservice.get_pcontract_productBOMbyid(productid_link, pcontractid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<PContractProductBOMHQ_getbyproduct_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getpo_by_npl", method = RequestMethod.POST)
	public ResponseEntity<pcontractbomHQ_getpo_bynpl_response> GetPOByNpl(HttpServletRequest request,
			@RequestBody pcontractbom_getpo_bynpl_request entity) {
		pcontractbomHQ_getpo_bynpl_response response = new pcontractbomHQ_getpo_bynpl_response();
		try {
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long material_skuid_link = entity.material_skuid_link;
			
			List<ProductPairing> productPairingList = pairService.getproduct_byproduct_inpair(productid_link);
			Boolean isSpDonTrongBo = false;
			List<Long> productidList = new ArrayList<Long>();
			if(productPairingList.size() > 0) { // sp trong bo
//				productid_link = productPairingList.get(0).getProductpairid_link();
				for(ProductPairing productPairing : productPairingList) {
					productidList.add(productPairing.getProductpairid_link());
				}
				isSpDonTrongBo = true;
			}
			if(!isSpDonTrongBo) {
//				productid = productid_link;
				productidList.add(productid_link);
			}
			
//			List<PContract_bomHQ_npl_poline> PContract_bomHQ_npl_poline_list = po_npl_Service.getby_Pcontract_Product_Material_skuid_link(pcontractid_link, productid_link, material_skuid_link);
			List<PContract_bomHQ_npl_poline> list_po = new ArrayList<PContract_bomHQ_npl_poline>();
			for(Long productid : productidList) {
				List<PContract_bomHQ_npl_poline> pbnp = po_npl_Service.getby_Pcontract_Product_Material_skuid_link(pcontractid_link, productid,
						material_skuid_link);
				list_po.addAll(pbnp);
			}
			
			response.data = list_po;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<pcontractbomHQ_getpo_bynpl_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deselect_sku", method = RequestMethod.POST)
	public ResponseEntity<pcontractbom2_deselect_sku_response> DeselectSKU(HttpServletRequest request,
			@RequestBody pcontractbom2_deselect_sku_request entity) {
		pcontractbom2_deselect_sku_response response = new pcontractbom2_deselect_sku_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long material_skuid_link = entity.material_skuid_link;
			Long pcontract_poid_link = entity.pcontract_poid_link;
			Long product_skuid_link = entity.product_skuid_link;

			List<PContract_bomHQ_npl_poline_sku> list_line_sku = po_npl_sku_Service.getone_rec(orgrootid_link,
					pcontractid_link, productid_link, pcontract_poid_link, product_skuid_link, material_skuid_link);
			for (PContract_bomHQ_npl_poline_sku line_sku : list_line_sku) {
				po_npl_sku_Service.delete(line_sku);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<pcontractbom2_deselect_sku_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/select_sku", method = RequestMethod.POST)
	public ResponseEntity<pcontractbom2_selectsku_response> SelectSKU(HttpServletRequest request,
			@RequestBody pcontractbom2_selectsku_request entity) {
		pcontractbom2_selectsku_response response = new pcontractbom2_selectsku_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long material_skuid_link = entity.material_skuid_link;
			Long pcontract_poid_link = entity.pcontract_poid_link;
			Long product_skuid_link = entity.product_skuid_link;
			int quantity = entity.quantity;

			// kiem tra xem du lieu ton tai hoac xoa thieu thi xoa truoc khi them vao lai
			List<PContract_bomHQ_npl_poline_sku> list_line_sku = po_npl_sku_Service.getone_rec(orgrootid_link,
					pcontractid_link, productid_link, pcontract_poid_link, product_skuid_link, material_skuid_link);
			for (PContract_bomHQ_npl_poline_sku line_sku : list_line_sku) {
				po_npl_sku_Service.delete(line_sku);
			}

			// kiem tra xem line duoc chon chua thi them line vao bang poline-npl
			List<PContract_bomHQ_npl_poline> list_line = po_npl_Service.getby_po_and_npl(pcontract_poid_link,
					material_skuid_link);
			if (list_line.size() == 0) {
				PContract_bomHQ_npl_poline line = new PContract_bomHQ_npl_poline();
				line.setId(null);
				line.setNpl_skuid_link(material_skuid_link);
				line.setPcontract_poid_link(pcontract_poid_link);
				line.setPcontractid_link(pcontractid_link);
				po_npl_Service.save(line);
			}

			// them sku vao bang line-sku
			PContract_bomHQ_npl_poline_sku line_sku = new PContract_bomHQ_npl_poline_sku();
			line_sku.setId(null);
			line_sku.setMaterial_skuid_link(material_skuid_link);
			line_sku.setOrgrootid_link(orgrootid_link);
			line_sku.setPcontract_poid_link(pcontract_poid_link);
			line_sku.setPcontractid_link(pcontractid_link);
			line_sku.setProduct_skuid_link(product_skuid_link);
			line_sku.setProductid_link(productid_link);
			line_sku.setQuantity(quantity);
			po_npl_sku_Service.save(line_sku);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<pcontractbom2_selectsku_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deletematerial", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> DeleteMaterial(HttpServletRequest request,
			@RequestBody PContractProductBom_delete_material_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			Long pcontractid_link = entity.pcontractid_link;
			Long productid_link = entity.productid_link;
			Long materialid_link = entity.materialid_link;
			Long colorid_link = entity.colorid_link;
			
			// Xoa trong bang pcontract_sku_bomhq
			// Tìm danh sách PContractBOMHQSKU theo đơn, sản phẩm, nguyên phụ liệu
			List<PContractBOMHQSKU> list_bom_sku2 = ppbomhqskuservice
					.getcolor_bymaterial_in_productBOMSKU(pcontractid_link, productid_link, materialid_link);
			for (PContractBOMHQSKU pcontractBOMHQSKU : list_bom_sku2) {
				Long productSkuId = pcontractBOMHQSKU.getProduct_skuid_link();
				SKU productSKU = skuService.findOne(productSkuId);
				Long productColorId = productSKU.getColorid_link();
				
				//  trùng với màu gửi lên -> xoá
				if(productColorId.equals(colorid_link)) {
					ppbomhqskuservice.delete(pcontractBOMHQSKU);
				}
				
				// kiểm tra cònđịnh mức cho loại npl này ko -> ko còn xóa bảng PContract_bomHQ_npl_poline
				// bảng PContract_bomHQ_npl_poline gồm trường id, pcontractid_link, pcontract_poid_link, npl_skuid_link
				// tức là một npl chỉ dùng cho 1poline sẽ dùng cho toàn bộ màu sp của poline đó
				// vd: 
				// vải 1 - poline 1 - màu sp 1 -định mức 1
				// vải 1 - không điền poline - màu sp 2 -định mức 2
				// khi lưu vào bảng PContract_bomHQ_npl_poline sẽ không thể phân biệt
				List<PContractBOMHQSKU> list_bom_sku2_bymaterial = ppbomhqskuservice
						.getcolor_bymaterial_in_productBOMSKU(pcontractid_link, productid_link, materialid_link);
				if(list_bom_sku2_bymaterial.size() == 0) {
					List<PContract_bomHQ_npl_poline> list_PContract_bomHQ_npl_poline = po_npl_Service.getby_Pcontract_Product_Material_skuid_link(pcontractid_link, productid_link, materialid_link);
					for(PContract_bomHQ_npl_poline PContract_bomHQ_npl_poline : list_PContract_bomHQ_npl_poline) {
						po_npl_Service.delete(PContract_bomHQ_npl_poline);
					}
				} 
			}
			
			// Nếu danh sách list_bom_sku2 đã hết sau khi xoá => xoá pcontract_product_bomhq
			list_bom_sku2 = ppbomhqskuservice
					.getcolor_bymaterial_in_productBOMSKU(pcontractid_link, productid_link, materialid_link);
			if(list_bom_sku2.size() == 0) {
				List<PContractProductBomHQ> list_bomHQ = ppbomhqservice.getby_pcontract_product_material(productid_link,
						pcontractid_link, materialid_link);
				for (PContractProductBomHQ bom : list_bomHQ) {
					ppbomhqservice.delete(bom);
				}
			}
			

			// xoa trong bang pcontract_product_bomhq
//			List<PContractProductBomHQ> list_bom2 = ppbomhqservice.getby_pcontract_product_material(productid_link,
//					pcontractid_link, materialid_link);
//			for (PContractProductBomHQ bom : list_bom2) {
//				ppbomhqservice.delete(bom);
//			}
//
			// Xoa trong bang pcontract_product_color_bomhq
			List<PContractBomHQColor> list_bom_color2 = ppbomcolorhqservice
					.getcolor_bymaterial_in_productBOMColor(pcontractid_link, productid_link, materialid_link);
			for (PContractBomHQColor color : list_bom_color2) {
				ppbomcolorhqservice.delete(color);
			}
//
//			// Xoa trong bang pcontract_sku_bom2
//			List<PContractBOMHQSKU> list_bom_sku2 = ppbomhqskuservice
//					.getcolor_bymaterial_in_productBOMSKU(pcontractid_link, productid_link, materialid_link);
//			for (PContractBOMHQSKU sku : list_bom_sku2) {
//				ppbomhqskuservice.delete(sku);
//			}
			
				

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getlist_pcontract_productbomcolor", method = RequestMethod.POST)
	public ResponseEntity<PContractProductBOM_getbomcolor_response> GetListProductBomColor(HttpServletRequest request,
			@RequestBody PContractProductBOM_getbomcolor_request entity) {
		PContractProductBOM_getbomcolor_response response = new PContractProductBOM_getbomcolor_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication()
//					.getPrincipal();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long colorid_link = entity.colorid_link;
			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();

			List<PContractProductBomHQ> listbom = ppbomhqservice.get_pcontract_productBOMbyid(productid_link,
					pcontractid_link);
			List<PContractBomHQColor> listbomcolor = ppbomcolorhqservice
					.getall_material_in_productBOMColor(pcontractid_link, productid_link, colorid_link, (long) 0);
			List<PContractBOMHQSKU> listbomsku = ppbomhqskuservice.getmaterial_bycolorid_link(pcontractid_link,
					productid_link, colorid_link, 0);

			List<Long> List_size = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link, (long) 30);
//			List<Long> List_size = ppatt_service.getvalueid_by_product_and_pcontract_and_attribute(orgrootid_link, pcontractid_link, productid_link, (long) 30);
			// ppbomskuservice.getsize_bycolor(pcontractid_link, productid_link,
			// colorid_link);

			for (PContractProductBomHQ pContractProductBom : listbom) {
				Map<String, String> map = new HashMap<String, String>();
				List<PContractBomHQColor> listbomcolorclone = new ArrayList<PContractBomHQColor>(listbomcolor);
				listbomcolorclone
						.removeIf(c -> !c.getMaterialid_link().equals(pContractProductBom.getMaterialid_link()));

				Float amount_color = (float) 0;
				float amount = pContractProductBom.getAmount() == null ? 0 : pContractProductBom.getAmount();
				if (listbomcolorclone.size() > 0)
					amount_color = listbomcolorclone.get(0).getAmount();

				map.put("amount", amount + "");

				map.put("amount_color", "0" + amount_color);

				map.put("coKho", pContractProductBom.getCoKho() + "");

				map.put("createddate", pContractProductBom.getCreateddate() + "");

				map.put("createduserid_link", "0" + pContractProductBom.getCreateduserid_link());

				map.put("description", pContractProductBom.getDescription() + "");

				map.put("id", "0" + pContractProductBom.getId());

				map.put("lost_ratio", "0" + pContractProductBom.getLost_ratio());

				map.put("materialid_link", "0" + pContractProductBom.getMaterialid_link());

				map.put("materialName", pContractProductBom.getMaterialName() + "");

				map.put("materialCode", pContractProductBom.getMaterialCode() + "");

				map.put("orgrootid_link", "0" + pContractProductBom.getOrgrootid_link());

				map.put("pcontractid_link", "0" + pContractProductBom.getPcontractid_link());

				map.put("product_type", pContractProductBom.getProduct_type() + "");

				map.put("product_typename", pContractProductBom.getProduct_typeName() + "");

				map.put("productid_link", pContractProductBom.getProductid_link() + "");

				map.put("tenMauNPL", pContractProductBom.getTenMauNPL() + "");

				map.put("thanhPhanVai", pContractProductBom.getThanhPhanVai() + "");

				map.put("unitName", pContractProductBom.getUnitName() + "");

				map.put("unitid_link", "0" + pContractProductBom.getUnitid_link());

				if (amount == 0 && amount_color == 0) {
					for (Long size : List_size) {
						List<PContractBOMHQSKU> listbomsku_clone = new ArrayList<PContractBOMHQSKU>(listbomsku);
						long skuid_link = ppbomhqskuservice.getskuid_link_by_color_and_size(colorid_link, size,
								productid_link);
						listbomsku_clone.removeIf(
								c -> !c.getMaterial_skuid_link().equals(pContractProductBom.getMaterialid_link())
										|| !c.getProduct_skuid_link().equals(skuid_link));
						Float amount_size = (float) 0;
						if (listbomsku_clone.size() > 0)
							amount_size = listbomsku_clone.get(0).getAmount();
						map.put("" + size, amount_size + "");
					}
				}

				listdata.add(map);
			}

			response.data = listdata;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<PContractProductBOM_getbomcolor_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getbom_by_product", method = RequestMethod.POST)
	public ResponseEntity<get_bom_by_product_response> GetBomByProduct(HttpServletRequest request,
			@RequestBody get_bom_by_product_request entity) {
		get_bom_by_product_response response = new get_bom_by_product_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long colorid_link = 0;
			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();

			List<Long> list_colorid = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link,
					AtributeFixValues.ATTR_COLOR);

			List<PContractProductBomHQ> listbom = ppbomhqservice.get_pcontract_productBOMbyid(productid_link,
					pcontractid_link);
//			List<PContractBomHQColor> listbomcolor = ppbomcolorhqservice.getall_material_in_productBOMColor(pcontractid_link, productid_link, colorid_link, (long)0);
			List<PContractBOMHQSKU> listbomsku = ppbomhqskuservice.getmaterial_bycolorid_link(pcontractid_link,
					productid_link, colorid_link, 0);

			List<Long> List_size = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link, (long) 30);
//			List<Long> List_size = ppatt_service.getvalueid_by_product_and_pcontract_and_attribute(orgrootid_link, pcontractid_link, productid_link, (long) 30);
			// ppbomskuservice.getsize_bycolor(pcontractid_link, productid_link,
			// colorid_link);

			for (PContractProductBomHQ pContractProductBom : listbom) {

				// Lay ds poline cua sku
				List<PContract_bomHQ_npl_poline> list_po = po_npl_Service.getby_product_and_npl(productid_link,
						pcontractid_link, pContractProductBom.getMaterialid_link());
				String str_po = "";
				for (PContract_bomHQ_npl_poline po_npl : list_po) {
					if (str_po == "") {
						str_po = po_npl.getPO_Buyer();
					} else {
						str_po += ", " + po_npl.getPO_Buyer();
					}

				}
				// Chay de lay tung mau san pham
				for (Long colorid : list_colorid) {
					Map<String, String> map = new HashMap<String, String>();

					map.put("coKho",
							pContractProductBom.getCoKho().replace("ALL, ", "").replace(", ALL", "").replace("ALL", "")
									+ "");

					map.put("createddate", pContractProductBom.getCreateddate() + "");

					map.put("createduserid_link", "0" + pContractProductBom.getCreateduserid_link());

					map.put("description", pContractProductBom.getDescription_product() + "");

					map.put("id", "0" + pContractProductBom.getId());

					map.put("lost_ratio", "0" + pContractProductBom.getLost_ratio());

					map.put("materialid_link", "0" + pContractProductBom.getMaterialid_link());

					map.put("materialName", pContractProductBom.getMaterialName() + "");

					map.put("materialCode", pContractProductBom.getMaterialCode() + "");

					map.put("orgrootid_link", "0" + pContractProductBom.getOrgrootid_link());

					map.put("pcontractid_link", "0" + pContractProductBom.getPcontractid_link());

					map.put("product_type", pContractProductBom.getProduct_type() + "");

					map.put("product_typename", pContractProductBom.getProduct_typeName() + "");

					map.put("productid_link", pContractProductBom.getProductid_link() + "");

					map.put("tenMauNPL", pContractProductBom.getTenMauNPL() + "");

					map.put("thanhPhanVai", pContractProductBom.getDescription_product() + "");

					map.put("unitName", pContractProductBom.getUnitName() + "");

					map.put("unitid_link", "0" + pContractProductBom.getUnitid_link());

					map.put("colorid_link", "0" + colorid);

					map.put("nhacungcap", pContractProductBom.getNhaCungCap());

					map.put("po_line", str_po);

					Attributevalue value = avService.findOne(colorid);
					String color_name = value.getValue();
					map.put("color_name", "" + color_name);

					Float total_amount = (float) 0;
					int total_size = 0;

					boolean check = false;
					for (Long size : List_size) {
						List<PContractBOMHQSKU> listbomsku_clone = new ArrayList<PContractBOMHQSKU>(listbomsku);
						long skuid_link = ppbomhqskuservice.getskuid_link_by_color_and_size(colorid, size,
								productid_link);
						listbomsku_clone.removeIf(
								c -> !c.getMaterial_skuid_link().equals(pContractProductBom.getMaterialid_link())
										|| !c.getProduct_skuid_link().equals(skuid_link));
						Float amount_size = (float) 0;
						if (listbomsku_clone.size() > 0)
							amount_size = listbomsku_clone.get(0).getAmount();
						map.put("" + size, amount_size + "");

						if (amount_size > 0) {
							check = true;
							total_amount += amount_size;
							total_size++;
						}
					}

					if (total_size > 0)
						map.put("amount", "0" + (total_amount / total_size));
					else
						map.put("amount", "0");
					if (check)
						listdata.add(map);
				}
			}

			// lay trang thai cua dinh muc
			List<PContractProduct> list_product = ppService.get_by_product_and_pcontract(orgrootid_link, productid_link,
					pcontractid_link);
			if (list_product.size() > 0) {
				response.isbomdone = list_product.get(0).getIsbom2done() == null ? false
						: list_product.get(0).getIsbom2done();
			}

			response.data = listdata;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<get_bom_by_product_response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getbom_by_product_multithread", method = RequestMethod.POST)
	public ResponseEntity<get_bom_by_product_response> GetBomByProductMultiThread(HttpServletRequest request,
			@RequestBody get_bom_by_product_request entity) {
		get_bom_by_product_response response = new get_bom_by_product_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long colorid_link = 0;
			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();

			List<Long> list_colorid = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link,
					AtributeFixValues.ATTR_COLOR);

			List<PContractProductBomHQ> listbom = ppbomhqservice.get_pcontract_productBOMbyid(productid_link,
					pcontractid_link);
//			List<PContractBomHQColor> listbomcolor = ppbomcolorhqservice.getall_material_in_productBOMColor(pcontractid_link, productid_link, colorid_link, (long)0);
			List<PContractBOMHQSKU> listbomsku = ppbomhqskuservice.getmaterial_bycolorid_link(pcontractid_link,
					productid_link, colorid_link, 0);
			
//			System.out.println("listbomsku: " + listbomsku.size());
//			for(PContractBOMHQSKU PContractBOMHQSKU : listbomsku) {
//				System.out.println("PContractBOMHQSKU: " + PContractBOMHQSKU.getId());
//			}

			List<Long> List_size = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link,
					AtributeFixValues.ATTR_SIZE);

			Map<String, Long> mapsku = new HashMap<String, Long>();
			List<SKU_Attribute_Value> list_skuav = skuavService.getlist_byproduct(productid_link);
			for (SKU_Attribute_Value sku_av : list_skuav) {
				mapsku.put(sku_av.getColorid() + "_" + sku_av.getSizeid(), sku_av.getSkuid_link());
			}

			List<Attributevalue> listav = avService.getlist_byidAttribute(AtributeFixValues.ATTR_COLOR);
			Map<Long, String> mapcolor = new HashMap<>();
			for (Attributevalue av : listav) {
				mapcolor.put(av.getId(), av.getValue());
			}

			CountDownLatch latch = new CountDownLatch(listbom.size());
			for (PContractProductBomHQ pContractProductBom : listbom) {
				PContractProductBomHQ_Runnable bomhq = new PContractProductBomHQ_Runnable(productid_link, pcontractid_link,
						pContractProductBom, list_colorid, List_size, listbomsku, listdata, po_npl_Service, poService, pairService, mapsku,
						mapcolor, latch);
				bomhq.start();
			}
			latch.await();

			// lay trang thai cua dinh muc
			List<PContractProduct> list_product = ppService.get_by_product_and_pcontract(orgrootid_link, productid_link,
					pcontractid_link);
			if (list_product.size() > 0) {
				response.isbomdone = list_product.get(0).getIsbom2done() == null ? false
						: list_product.get(0).getIsbom2done();
			}
			
//			// tính sl dh
//			for(Map<String, String> map : listdata) {
////				System.out.println("po_line_id: " + map.get("po_line_id"));
////				System.out.println("po_line: " + map.get("po_line"));
//				String po_line = map.get("po_line");
//				String po_line_id = map.get("po_line_id");
//				
//				List<Long> pcontractPoIdList = new ArrayList<Long>();
//				String[] parts = po_line_id.split(",");
//				for(Integer i=0;i<parts.length;i++) {
//					Long pcontractPoId = Long.parseLong(parts[i]);
//					pcontractPoIdList.add(pcontractPoId);
//				}
//				
//				Integer sldh = 0;
//				for(Long pcontractPoId : pcontractPoIdList) {
//					// tim pcontract_product_skus theo pcontract_poid_link, colorid
//					Long colorid = Long.parseLong(map.get("colorid_link"));
//					Long productid = Long.parseLong(map.get("productid_link"));
//					List<PContractProductSKU> pcontractProductSKUList = ppskuService.getByPoLine_product_size_color(pcontractPoId, productid, null, colorid);
//					
//					for(PContractProductSKU pcontractProductSKU : pcontractProductSKUList){
//						if(pcontractProductSKU.getColor_id().equals(colorid)) {
//							sldh+=pcontractProductSKU.getPquantity_porder() == null ? 0 : pcontractProductSKU.getPquantity_porder();
//						}
//					}
//				}
//				map.put("sldh", "" + sldh);
//			}

			response.data = listdata;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<get_bom_by_product_response>(response, HttpStatus.OK);
	}

	public List<Map<String, String>> GetListProductBomColor(PContractProductBOM_getbomcolor_request entity) {
		PContractProductBOM_getbomcolor_response response = new PContractProductBOM_getbomcolor_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication()
//					.getPrincipal();
			long pcontractid_link = entity.pcontractid_link;
			long productid_link = entity.productid_link;
			long colorid_link = entity.colorid_link;
			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();

			List<PContractProductBomHQ> listbom = ppbomhqservice.get_pcontract_productBOMbyid(productid_link,
					pcontractid_link);
			List<PContractBomHQColor> listbomcolor = ppbomcolorhqservice
					.getall_material_in_productBOMColor(pcontractid_link, productid_link, colorid_link, (long) 0);
			List<PContractBOMHQSKU> listbomsku = ppbomhqskuservice.getmaterial_bycolorid_link(pcontractid_link,
					productid_link, colorid_link, 0);

			List<Long> List_size = ppskuService.getlistvalue_by_product(pcontractid_link, productid_link, (long) 30);
//			List<Long> List_size = ppatt_service.getvalueid_by_product_and_pcontract_and_attribute(orgrootid_link, pcontractid_link, productid_link, (long) 30);
			// ppbomskuservice.getsize_bycolor(pcontractid_link, productid_link,
			// colorid_link);

			for (PContractProductBomHQ pContractProductBom : listbom) {
				Map<String, String> map = new HashMap<String, String>();
				List<PContractBomHQColor> listbomcolorclone = new ArrayList<PContractBomHQColor>(listbomcolor);
				listbomcolorclone
						.removeIf(c -> !c.getMaterialid_link().equals(pContractProductBom.getMaterialid_link()));

				Float amount_color = (float) 0;
				if (listbomcolorclone.size() > 0)
					amount_color = null != listbomcolorclone.get(0).getAmount() ? listbomcolorclone.get(0).getAmount()
							: 0;

				map.put("amount", "0" + pContractProductBom.getAmount());

				map.put("amount_color", "0" + amount_color);

				map.put("coKho", pContractProductBom.getCoKho() + "");

				map.put("createddate", pContractProductBom.getCreateddate() + "");

				map.put("createduserid_link", "0" + pContractProductBom.getCreateduserid_link());

				map.put("description", pContractProductBom.getDescription() + "");

				map.put("id", "0" + pContractProductBom.getId());

				map.put("lost_ratio", "0" + pContractProductBom.getLost_ratio());

				map.put("materialid_link", "0" + pContractProductBom.getMaterialid_link());

				map.put("materialName", pContractProductBom.getMaterialName() + "");

				map.put("materialCode", pContractProductBom.getMaterialCode() + "");

				map.put("orgrootid_link", "0" + pContractProductBom.getOrgrootid_link());

				map.put("pcontractid_link", "0" + pContractProductBom.getPcontractid_link());

				map.put("product_type", pContractProductBom.getProduct_type() + "");

				map.put("product_typename", pContractProductBom.getProduct_typeName() + "");

				map.put("productid_link", pContractProductBom.getProductid_link() + "");

				map.put("tenMauNPL", pContractProductBom.getTenMauNPL() + "");

				map.put("thanhPhanVai", pContractProductBom.getThanhPhanVai() + "");

				map.put("unitName", pContractProductBom.getUnitName() + "");

				map.put("unitid_link", "0" + pContractProductBom.getUnitid_link());

				for (Long size : List_size) {
					List<PContractBOMHQSKU> listbomsku_clone = new ArrayList<PContractBOMHQSKU>(listbomsku);
					Long skuid_link = ppbomhqskuservice.getskuid_link_by_color_and_size(colorid_link, size,
							productid_link);
					listbomsku_clone
							.removeIf(c -> !c.getMaterial_skuid_link().equals(pContractProductBom.getMaterialid_link())
									|| !c.getProduct_skuid_link().equals(skuid_link));
					Float amount_size = (float) 0;
					if (listbomsku_clone.size() > 0)
						amount_size = listbomsku_clone.get(0).getAmount();
					map.put("" + size, amount_size + "");
				}

				listdata.add(map);
			}

			return listdata;
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return null;
		}
	}

	public List<PContractBOMHQSKU> getBOM_By_PContractSKU(Long pcontractid_link, Long skuid_link) {
		return ppbomhqskuservice.getBOM_By_PContractSKU(pcontractid_link, skuid_link);
	}
}
