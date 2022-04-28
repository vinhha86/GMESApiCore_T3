package vn.gpay.gsmart.core.api.pcontract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.Files;

import vn.gpay.gsmart.core.api.Report.porder_report_response;
import vn.gpay.gsmart.core.api.pcontract_po.getpo_by_product_response;
import vn.gpay.gsmart.core.api.timesheetinout.getDailyRequest;
import vn.gpay.gsmart.core.api.timesheetinout.getDailyResponse;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.pcontract.IPContractService;
import vn.gpay.gsmart.core.pcontract.IPContract_AutoID_Service;
import vn.gpay.gsmart.core.pcontract.PContract;
import vn.gpay.gsmart.core.pcontract.PContractChart;


import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontractbomcolor.IPContractBOMColorService;
import vn.gpay.gsmart.core.pcontractbomcolor.IPContractBom2ColorService;
import vn.gpay.gsmart.core.pcontractbomcolor.PContractBOMColor;
import vn.gpay.gsmart.core.pcontractbomcolor.PContractBom2Color;
import vn.gpay.gsmart.core.pcontractbomsku.IPContractBOM2SKUService;
import vn.gpay.gsmart.core.pcontractbomsku.IPContractBOMSKUService;
import vn.gpay.gsmart.core.pcontractbomsku.PContractBOM2SKU;
import vn.gpay.gsmart.core.pcontractbomsku.PContractBOMSKU;
import vn.gpay.gsmart.core.pcontractmarket.IPContractMarketRepository;
//import vn.gpay.gsmart.core.pcontractmarket.IPContractMarketService;
import vn.gpay.gsmart.core.pcontractmarket.IPContractMarketService;
import vn.gpay.gsmart.core.pcontractmarket.PContractMarket;
import vn.gpay.gsmart.core.pcontractproduct.IPContractProductService;
import vn.gpay.gsmart.core.pcontractproduct.PContractProduct;
import vn.gpay.gsmart.core.pcontractproductbom.IPContractProductBom2Service;
import vn.gpay.gsmart.core.pcontractproductbom.IPContractProductBomService;
import vn.gpay.gsmart.core.pcontractproductbom.PContractProductBom;
import vn.gpay.gsmart.core.pcontractproductbom.PContractProductBom2;
import vn.gpay.gsmart.core.pcontractproductdocument.IPContractProducDocumentService;
import vn.gpay.gsmart.core.pcontractproductdocument.PContractProductDocument;
import vn.gpay.gsmart.core.pcontractproductpairing.IPContractProductPairingService;
import vn.gpay.gsmart.core.pcontractproductpairing.PContractProductPairing;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKUService;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKUBinding;
import vn.gpay.gsmart.core.porder.IPOrder_Service;
import vn.gpay.gsmart.core.porder.POrderBinding;
import vn.gpay.gsmart.core.porder_req.IPOrder_Req_Service;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.productpairing.IProductPairingService;
import vn.gpay.gsmart.core.productpairing.ProductPairing;
import vn.gpay.gsmart.core.productpairing.ProductPairingService;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.sku.ISKU_Service;
import vn.gpay.gsmart.core.sku.SKU;
import vn.gpay.gsmart.core.timesheetinout.TimeSheetDaily;
import vn.gpay.gsmart.core.utils.ColumnExcel;
import vn.gpay.gsmart.core.utils.OrgType;
import vn.gpay.gsmart.core.utils.POType;
import vn.gpay.gsmart.core.utils.ProductType;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/pcontract")
public class PContractAPI {
//	@Autowired
//	IPContractMarketRepository repo;
	@Autowired
	IPContractMarketService pcontract_market_Serive;
	@Autowired
	IPContractService pcontractService;
	@Autowired
	IPContract_AutoID_Service pcontract_AutoID_Service;
	@Autowired
	IOrgService orgService;
	@Autowired
	IPContract_POService poService;
	@Autowired
	IPOrder_Service porderService;
	@Autowired
	IPContractProductSKUService pcontract_sku_Service;
	@Autowired
	IPContractProductService pcontract_product_Service;
	@Autowired
	IPContractProductPairingService pcontract_pairing_Service;
	@Autowired
	IPContractProducDocumentService pcontract_document_Service;
	@Autowired
	IPContractProductBomService pcontract_bom_Service;
	@Autowired
	IPContractProductBom2Service pcontract_bom2_Service;
	@Autowired
	IPContractBOMColorService pcontract_bom_color_Service;
	@Autowired
	IPContractBom2ColorService pcontract_bom2_color_Service;
	@Autowired
	IPContractBOMSKUService pcontract_bom_sku_Service;
	@Autowired
	IPContractBOM2SKUService pcontract_bom2_sku_Service;
	@Autowired
	IPOrder_Req_Service porderReqService;
	@Autowired
	IGpayUserOrgService userOrgService;
	@Autowired
	IProductService productService;
	@Autowired
	IProductPairingService pairService;
	@Autowired
	IPContract_POService pcontract_POService;
	@Autowired
	IPContractProductSKUService pskuservice;
	@Autowired
	ISKU_Service skuService;
	@Autowired
	IProductPairingService productPairingService;
	@Autowired
	IPContractMarketService pcontractMarketService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<PContract_create_response> PContractCreate(@RequestBody PContract_create_request entity,
																	 HttpServletRequest request) {
		System.out.println(entity);
		PContract_create_response response = new PContract_create_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long usercreatedid_link = user.getId();

			PContract pcontract = entity.data;
			if (pcontract.getId() == 0 || pcontract.getId() == null) {
				if (null == pcontract.getContractcode() || pcontract.getContractcode().length() == 0) {
					Org theBuyer = orgService.findOne(pcontract.getOrgbuyerid_link());
					if (null != theBuyer)
						pcontract.setContractcode(pcontract_AutoID_Service.getLastID(theBuyer.getCode()));
					else
						pcontract.setContractcode(pcontract_AutoID_Service.getLastID("UNKNOWN"));
				} else {
					String contractcode = pcontract.getContractcode();
					long pcontractid_link = pcontract.getId();

					List<PContract> lstcheck = pcontractService.getby_code(orgrootid_link, contractcode,
							pcontractid_link);
					if (lstcheck.size() > 0) {
						response.setRespcode(ResponseMessage.KEY_RC_BAD_REQUEST);
						response.setMessage("Mã đã tồn tại trong hệ thống!");
						return new ResponseEntity<PContract_create_response>(response, HttpStatus.BAD_REQUEST);
					}
				}
				pcontract.setOrgrootid_link(orgrootid_link);
				pcontract.setUsercreatedid_link(usercreatedid_link);
				pcontract.setDatecreated(new Date());
			} else {
				PContract pc_old = pcontractService.findOne(pcontract.getId());
				pcontract.setOrgrootid_link(pc_old.getOrgrootid_link());
				pcontract.setUsercreatedid_link(pc_old.getUsercreatedid_link());
				pcontract.setDatecreated(pc_old.getDatecreated());
			}
			pcontract = pcontractService.save(pcontract);


			Long id = pcontract.getId();
			List<Long> marketArray = entity.markettypeArray;

			if(marketArray.size() == 0) {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Ban chua chon thi truong");
				return new ResponseEntity<PContract_create_response>(response, HttpStatus.BAD_REQUEST);
			}

//			List<PContractMarket> list = pcontract_market_Serive.get_by_pcontractid(id);
			List<PContractMarket> list = pcontract_market_Serive.get_by_pcontractid_link_notin_marketid_link(id, marketArray);

			if(list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					pcontract_market_Serive.deleteById(list.get(i).getId());
				}
			}
			for(int i = 0; i < marketArray.size(); i++) {
				list = pcontract_market_Serive.get_by_pcontractid_link_marketid_link(id, marketArray.get(i));
				if(list.size() == 0) {
				pcontract_market_Serive.save(new PContractMarket(id, marketArray.get(i)));
				}
			}


			response.id = pcontract.getId();
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_create_response>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_create_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getbypaging", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbypaging_response> PContractGetpage(
			@RequestBody PContract_getbypaging_request entity, HttpServletRequest request) {
		PContract_getbypaging_response response = new PContract_getbypaging_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			Page<PContract> pcontract = pcontractService.getall_by_orgrootid_paging(orgrootid_link, entity);

			response.data = pcontract.getContent();
			response.totalCount = pcontract.getTotalElements();

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getpcontractchart", method = RequestMethod.POST)
	public ResponseEntity<GetProductNotBomResponse> GetProductNotBom(@RequestBody GetProductNotBomRequest entity,
																	 HttpServletRequest request) {
		GetProductNotBomResponse response = new GetProductNotBomResponse();
		try {
			int year = entity.year;
			int type = entity.type;

			List<PContract> list_pcontract = pcontractService.getPContractByYear(year);

			List<PContractChart> list_chart = new ArrayList<PContractChart>();
			for (PContract pcontract : list_pcontract) {
				long pcontractid_link = pcontract.getId();
				PContractChart chart = new PContractChart();
				int soluong = 0;
				if (type == 0)
					soluong = pcontractService.getProductNotBom(pcontractid_link);
				else if (type == 1)
					soluong = poService.getPOConfimNotLine(pcontractid_link);
				else if (type == 2)
					soluong = poService.getPOLineNotMaps(pcontractid_link);
				chart.setMahang(pcontract.getContractcode());
				chart.setSoluong(soluong);
				list_chart.add(chart);
			}

			response.data = list_chart;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<GetProductNotBomResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<GetProductNotBomResponse>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getlistbypaging", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbypaging_response> PContractGetpageList(
			@RequestBody PContract_getbypaging_request entity, HttpServletRequest request) {
		PContract_getbypaging_response response = new PContract_getbypaging_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			List<PContract> pcontract = pcontractService.getalllist_by_orgrootid_paging(orgrootid_link, entity);

			response.data = new ArrayList<PContract>();

			for (PContract pc : pcontract) {
				String cc = pc.getContractcode().toLowerCase();
				String pl = pc.getProductlist().toLowerCase();
				String pol = pc.getPolist().toLowerCase();
				if (!cc.contains(entity.contractcode.toLowerCase()))
					continue;
				if (!pl.contains(entity.style.toLowerCase()))
					continue;
				if (!pol.contains(entity.po.toLowerCase()))
					continue;
				response.data.add(pc);
			}
			response.totalCount = response.data.size();

			PageRequest page = PageRequest.of(entity.page - 1, entity.limit);
			int start = (int) page.getOffset();
			int end = (start + page.getPageSize()) > response.data.size() ? response.data.size()
					: (start + page.getPageSize());
			Page<PContract> pageToReturn = new PageImpl<PContract>(response.data.subList(start, end), page,
					response.data.size());

			response.data = pageToReturn.getContent();
//			response.totalCount = pcontract.getTotalElements();

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getone", method = RequestMethod.POST)
	public ResponseEntity<PContract_getone_response> PContractGetOne(@RequestBody PContract_getone_request entity,
																	 HttpServletRequest request) {
		PContract_getone_response response = new PContract_getone_response();
		try {

			response.market = pcontract_market_Serive.getmarketid_by_pcontractid_link(entity.id);
			response.data = pcontractService.findOne(entity.id);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getone_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getone_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> PContractDelete(@RequestBody PContract_delete_request entity,
														HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			long orgrootid_link = user.getRootorgid_link();
			// Check if having PO? refuse deleting if have
			if (poService.getPOByContract(orgrootid_link, entity.id).size() > 0) {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage(
						"Hiện vẫn đang có đơn hàng (PO) trong hợp đồng! Cần xóa hết PO trước khi xóa hợp đồng");
				return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
			}
			// Check if having POrder? refuse deleting if have
			if (porderService.getByContract(entity.id).size() > 0) {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage(
						"Hiện vẫn đang có Lệnh SX trong hợp đồng! Cần xóa hết Lệnh SX trước khi xóa hợp đồng");
				return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);

			}
			// Delete products
			// pcontract_products;pcontract_product_sku;pcontract_product_pairing;pcontract_product_document
			for (PContractProduct theProduct : pcontract_product_Service.get_by_pcontract(orgrootid_link, entity.id)) {
				pcontract_product_Service.delete(theProduct);
			}
			for (PContractProductSKU theSku : pcontract_sku_Service.getlistsku_bypcontract(orgrootid_link, entity.id)) {
				pcontract_sku_Service.delete(theSku);
			}
			for (PContractProductPairing thePairing : pcontract_pairing_Service.getall_bypcontract(orgrootid_link,
					entity.id)) {
				pcontract_pairing_Service.delete(thePairing);
			}
			for (PContractProductDocument theDocument : pcontract_document_Service.getlist_bycontract(orgrootid_link,
					entity.id)) {
				pcontract_document_Service.delete(theDocument);
			}

			// Delete BOM
			// pcontract_bom_sku;pcontract_bom_product;pcontract_bom_color
			// pcontract_bom2_sku;pcontract_bom2_product;pcontract_bom2_color
			for (PContractProductBom theBom : pcontract_bom_Service.getall_bypcontract(orgrootid_link, entity.id)) {
				pcontract_bom_Service.delete(theBom);
			}
			for (PContractBOMColor theBomcolor : pcontract_bom_color_Service.getall_bypcontract(orgrootid_link,
					entity.id)) {
				pcontract_bom_color_Service.delete(theBomcolor);
			}
			for (PContractBOMSKU theBomsku : pcontract_bom_sku_Service.getall_bypcontract(orgrootid_link, entity.id)) {
				pcontract_bom_sku_Service.delete(theBomsku);
			}

			for (PContractProductBom2 theBom2 : pcontract_bom2_Service.getall_bypcontract(orgrootid_link, entity.id)) {
				pcontract_bom2_Service.delete(theBom2);
			}
			for (PContractBom2Color theBom2color : pcontract_bom2_color_Service.getall_bypcontract(orgrootid_link,
					entity.id)) {
				pcontract_bom2_color_Service.delete(theBom2color);
			}
			for (PContractBOM2SKU theBom2sku : pcontract_bom2_sku_Service.getall_bypcontract(orgrootid_link,
					entity.id)) {
				pcontract_bom2_sku_Service.delete(theBom2sku);
			}

			// Delete PContract
			pcontractService.deleteById(entity.id);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getbysearch", method = RequestMethod.POST)
    public ResponseEntity<PContract_getbypaging_response> PContractGetBySearch(
            @RequestBody PContract_getbysearch_request entity, HttpServletRequest request) {
        PContract_getbypaging_response response = new PContract_getbypaging_response();
        GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//		//fix dieu kien tim kiem cho vendor cua DHA
//		if (user.getUsername().toLowerCase().trim().contains("hansoll"))
//			entity.orgvendorid_link = 197;
//		else
//			if (user.getUsername().toLowerCase().trim().contains("paroman"))
//				entity.orgvendorid_link = 200;
//		else
//			if (user.getUsername().toLowerCase().trim().contains("ekline"))
//				entity.orgvendorid_link = 189;

        // Lay danh sach Vendor duoc phep quan ly
        List<GpayUserOrg> lsVendor = userOrgService.getall_byuser_andtype(user.getId(), OrgType.ORG_TYPE_VENDOR);
        List<GpayUserOrg> lsBuyer = userOrgService.getall_byuser_andtype(user.getId(), OrgType.ORG_TYPE_BUYER);


        try {
            List<Long> vendors = new ArrayList<Long>();
            for (GpayUserOrg vendor : lsVendor) {
                vendors.add(vendor.getOrgid_link());
            }

            List<Long> buyers = new ArrayList<Long>();
            for (GpayUserOrg buyer : lsBuyer) {
                buyers.add(buyer.getOrgid_link());
            }

            List<Long> orgs = new ArrayList<Long>();
            Long orgid_link = user.getOrgid_link();
            if (orgid_link != 0 && orgid_link != 1 && orgid_link != null) {
                // Lay danh sach cac phan xuong ma nguoi dung duoc phep quan ly
                for (GpayUserOrg userorg : userOrgService.getall_byuser_andtype(user.getId(),
                        OrgType.ORG_TYPE_FACTORY)) {
                    orgs.add(userorg.getOrgid_link());
                }
                // Them chinh don vi cua user
                orgs.add(orgid_link);
            }

            // Lay danh sach product thoa man dieu kien
            List<Long> products = new ArrayList<Long>();
            if (entity.productbuyer_code.length() > 0) {
                List<Product> product_lst = productService.getProductByLikeBuyercode(entity.productbuyer_code);
                for (Product theProduct : product_lst) {
                    // kiem tra xem san pham co nam trong bo ko thì lay san pham bo vao
                    List<ProductPairing> list_pair = pairService.getby_product(theProduct.getId());
                    for (ProductPairing pair : list_pair) {
                        products.add(pair.getProductpairid_link());
                    }

                    products.add(theProduct.getId());
                }

            }

            List<Long> pos = poService.getpcontract_BySearch(entity.po_code, orgs);
            // Lay danh sach PO thoa man dieu kien
//			List<PContract_PO> lstPO = poService.getBySearch(entity.po_code, orgs);
//			for (PContract_PO thePO : lstPO)
//				if(!pos.contains(thePO.getPcontractid_link()))
//					pos.add(thePO.getPcontractid_link());

            List<Long> product = new ArrayList<Long>();
            // Lay danh sach PO thoa man dieu kien
            List<Long> list_product = pcontract_product_Service.getby_product(products, entity.productbuyer_code);
            for (Long p : list_product) {
                product.add(p);
            }

			List<PContract> finalList = new ArrayList<>();
            List<PContract> list = pcontractService.getBySearch_PosList(entity, pos, product, vendors, buyers);

            //Tìm Pcontract theo tháng của shipdate

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (entity.firstDayOfMonth_shipDate != "" && entity.lastDayOfMonth_shipDate != "") {
                Date firstDay = sdf.parse(entity.firstDayOfMonth_shipDate);
                Date lastDay = sdf.parse(entity.lastDayOfMonth_shipDate);
                for (PContract pcontract : list) {
                    boolean check = false;
                    List<PContract_PO> pcontract_PO_List = pcontract_POService.getPO_Offer_Accept_ByPContract_AndOrg(
                            pcontract.getId(), 0L, orgs);
                    for (PContract_PO pContract_po : pcontract_PO_List) {
                        List<PContract_PO> listPContractPO = pcontract_POService
                                .get_by_parent_and_type_and_MauSP_and_Shipdate(pContract_po.getId(), POType.PO_LINE_CONFIRMED, null,firstDay,lastDay);

                        if(listPContractPO.size()>0){
//                            if(finalList.contains(pcontract)== false){
//                                finalList.add(pcontract);
//                            }
                        	check = true;
                        }
//                        else break;
                        if(check) break;
                    }
                    if(check) finalList.add(pcontract);
                }
            }
            else {
//                for (PContract pcontract : list) {
//                    finalList.add(pcontract);
//                }
                finalList.addAll(list);
            }


            for (PContract pcontract : finalList) {
                List<PContractMarket> pcontractMarketList = pcontractMarketService.get_by_pcontractid_link(pcontract.getId());
                String allMarketString = "";
                for (PContractMarket pcontractMarket : pcontractMarketList) {
                    String marketString = pcontractMarket.getMarketTypeName();
                    if (marketString != null && !marketString.trim().equals("")) {
                        if (allMarketString.equals("")) {
                            allMarketString += marketString;
                        } else {
                            allMarketString += "; " + marketString;
                        }
                    }
                }
                pcontract.setMarketTypeString(allMarketString);
            }

            response.data = finalList;
            response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
            response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
            return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
            response.setMessage(e.getMessage());
            return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
        }
    }

	@RequestMapping(value = "/findByContractcode", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbypaging_response> findByContractcode(
			@RequestBody PContract_findByContractcode_request entity, HttpServletRequest request) {
		PContract_getbypaging_response response = new PContract_getbypaging_response();
//		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try {
			response.data = pcontractService.findByContractcode(entity.contractcode);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/findByExactContractcode", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbypaging_response> findByExactContractcode(
			@RequestBody PContract_findByContractcode_request entity, HttpServletRequest request) {
		PContract_getbypaging_response response = new PContract_getbypaging_response();
//		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try {
			List<PContract> result = pcontractService.findByExactContractcode(entity.contractcode);
			if (result.size() > 0) {
				response.data = result;
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage("Mã đơn hàng không tồn tại");
			}
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getByProduct", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbypaging_response> getByProduct(
			@RequestBody PContract_findByContractcode_request entity, HttpServletRequest request) {
		PContract_getbypaging_response response = new PContract_getbypaging_response();
//		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try {
			Long productid_link = entity.productid_link;
			response.data = pcontractService.getByProduct(productid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		}
	}


	@RequestMapping(value = "/getByMaterial_of_Product_Pcontract", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbypaging_response> getByMaterial_of_Product_Pcontract(
			@RequestBody PContract_findByContractcode_request entity, HttpServletRequest request) {
		PContract_getbypaging_response response = new PContract_getbypaging_response();
//		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try {
			Long productid_link = entity.productid_link;
			Long pcontractid_link = entity.pcontractid_link;

			// mượn cây vải từ đơn hàng khác

			List<PContract> result = new ArrayList<PContract>();
			//  lấy danh sách loại vải của sản phẩm
			List<PContractProductBom2> listbom = pcontract_bom2_Service.get_material_in_pcontract_productBOM(productid_link, pcontractid_link, 20);
			List<Long> skuid_list = new ArrayList<Long>();
			for(PContractProductBom2 pcontractProductBom2 : listbom) {
				skuid_list.add(pcontractProductBom2.getMaterialid_link());
			}

			// tìm những đơn hàng khác có chứa sản phẩm có loại vải dùng bởi sản phẩm của đơn hàng request
			if(skuid_list.size() > 0) {
				List<PContract> pcontract_list = pcontractService.getByBom_Sku(skuid_list);
				for(PContract pcontract : pcontract_list) {
					if(pcontract.getId().equals(pcontractid_link)) {
						continue;
					}
					result.add(pcontract);
				}
			}


			response.data = result;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/get_TongHopBaoCaoKHSX", method = RequestMethod.POST)
	public ResponseEntity<porder_report_response> get_TongHopBaoCaoKHSX(@RequestBody PContract_export_excel_request entity,
																		HttpServletRequest request) {
		porder_report_response response = new porder_report_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgid_link = user.getOrgid_link();
			//
			String orgcode = user.getOrgcode();
			List<String> orgs = new ArrayList<String>();
			List<Long> list_org = new ArrayList<Long>();
			if (orgid_link != 0 && orgid_link != 1) {
				for (GpayUserOrg userorg : userOrgService.getall_byuser_andtype(user.getId(),
						OrgType.ORG_TYPE_FACTORY)) {
					orgs.add(userorg.getOrgcode());
					list_org.add(userorg.getOrgid_link());
				}
				// Them chinh don vi cua user
				orgs.add(orgcode);
				if (!list_org.contains(orgid_link))
					list_org.add(orgid_link);
			}
			//
			List<Integer> type = new ArrayList<Integer>();
			type.add(10);
			Long pcontract_id = entity.id;
			List<Long> product_ids_req = entity.product_ids;
			List<Long> product_ids = new ArrayList<Long>();
			PContract pcontract = pcontractService.findOne(pcontract_id);
			String maHang = "";

			// lấy danh sach product_id (neu in khsx tu menu -> thong tin don hang)
			if(product_ids_req != null) {
				for(Long product_id : product_ids_req) {
					product_ids.add(product_id);
					Product product = productService.findOne(product_id);
					maHang = product.getBuyercode().trim();
				}
			}

			// lay id pcontract_poid_link (neu in khsx tu po ke hoach menu chi tiet po)
			Long pcontract_poid_link = entity.pcontract_poid_link;
			PContract_PO pcontractpo_req = null;
			if(pcontract_poid_link != null) {
				pcontractpo_req = pcontract_POService.findOne(pcontract_poid_link);
				Long productid_link = pcontractpo_req.getProductid_link();
				if(productid_link != null) {
					Product product = productService.findOne(productid_link);
					maHang = product.getBuyercode().trim();
				}
			}

			// danh sach obj de chuan bi them vao file
			List<PContractProductSKUBinding> pcontractProductSKUBinding_list = new ArrayList<PContractProductSKUBinding>();

			// list po ke hoach
			List<PContract_PO> pcontract_PO_list = new ArrayList<PContract_PO>();
//			if(pcontractpo_req == null) {
//				pcontract_PO_list = pcontract_POService.getPO_Offer_Accept_ByPContract_AndOrg(
//						pcontract.getId(), (long)0, list_org);
//			}else
			if(product_ids.size() > 0){
				pcontract_PO_list = pcontract_POService.getPO_Offer_Accept_ByPContract_AndOrg(
						pcontract.getId(), product_ids.get(0), list_org);
			}else if(pcontractpo_req != null){
				pcontract_PO_list.add(pcontractpo_req);
			}


			// list po thuc te
			List<PContract_PO> PContract_PO_thucte_list = new ArrayList<PContract_PO>();
			for(PContract_PO pcontract_po : pcontract_PO_list) {
				List<PContract_PO> listPContractPO = pcontract_POService
						.get_by_parent_and_type_and_MauSP(pcontract_po.getId(), POType.PO_LINE_CONFIRMED, null);
				PContract_PO_thucte_list.addAll(listPContractPO);
//				System.out.println("size thuc te " + listPContractPO.size());
			}
//			response.data3 = new ArrayList<PContractProductSKUBinding>();

			for(PContract_PO pcontract_po : PContract_PO_thucte_list) {
				// ds mau co -> sl -> set vao binding

				String maPo = pcontract_po.getPo_buyer() == null ? "" : pcontract_po.getPo_buyer();
				Date shipdate = pcontract_po.getShipdate();
				String dateString = "";
				if(shipdate != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("d/M");
					dateString = formatter.format(shipdate);
				}

				List<PContractProductSKU> PContractProductSKU_list = new ArrayList<PContractProductSKU>();

				Product product = productService.findOne(pcontract_po.getProductid_link());
				if(product != null) {
					Integer producttypeid_link = product.getProducttypeid_link();
					if(producttypeid_link.equals(5)) { // sp bo -> lay ds sp con
						List<ProductPairing> dsSpCon = productPairingService.getby_product(product.getId());
//							// old lấy chi tiết các sku
//							for(ProductPairing productPairing : dsSpCon) {
//								List<PContractProductSKU> pcontractProductSKU_list_spCOn = pskuservice.getbypo_and_product(
//										pcontract_po.getId(), productPairing.getProductid_link()
//										);
//								PContractProductSKU_list.addAll(pcontractProductSKU_list_spCOn);
//							}

						// new gộp các sku thành bộ, tính sl bộ
						if(dsSpCon.size() > 0) {
							ProductPairing sp1trongBo = dsSpCon.get(0); // sp con dau tien, lay roi so sanh voi cac sp con con lai trong bo -> tinh ra tong so bo
							Long idsp1trongBo = sp1trongBo.getProductid_link();
							List<PContractProductSKU> pcontractProductSKU_list_spConDauTien = pskuservice.getbypo_and_product(
									pcontract_po.getId(), idsp1trongBo
							);

							for(PContractProductSKU pcontractProductSKU : pcontractProductSKU_list_spConDauTien) {
								Long sizeId = pcontractProductSKU.getSizeid_link();
								Long colorId = pcontractProductSKU.getColor_id();

								Integer slBo = 0;
								Integer sp1_sl = pcontractProductSKU.getPquantity_total() == null ? 0 : pcontractProductSKU.getPquantity_total();
								Integer sp1_amountTrongBo = sp1trongBo.getAmount() == null ? 1 : sp1trongBo.getAmount();
								slBo = sp1_sl / sp1_amountTrongBo;

//								System.out.println("1 " + sp1_amountTrongBo);
//								System.out.println("sp1_sl " + sp1_sl);
//								System.out.println("slBo " + slBo);

								for(ProductPairing productPairing : dsSpCon) {
									// loop qua cac sp con lai
									if(!sp1trongBo.getProductid_link().equals(productPairing.getProductid_link())) {
										// tim ds cac sku cua sp con lai voi mau va co nhu tren
//											List<PContractProductSKU> list_PContractProductSKU =
//													pskuservice.getByPoLine_product_size_color(pcontract_po.getId(), productPairing.getProductid_link(), sizeId, colorId);
										List<PContractProductSKU> list_PContractProductSKU =
												pskuservice.getByPoLine_product(pcontract_po.getId(), productPairing.getProductid_link());
//											System.out.println("753: " + list_PContractProductSKU.size());

										PContractProductSKU temp = null;
										Boolean isExist = false;
										for(PContractProductSKU ppsku : list_PContractProductSKU) {
											if(ppsku.getSizeid_link().equals(sizeId) && ppsku.getColor_id().equals(colorId)) {
												isExist = true;
												temp = ppsku;
												break;
											}
										}

										if(!isExist || temp == null) {
											slBo = 0;
//											System.out.println("2 " + "null");
											break;
										}else {
											PContractProductSKU ppsku = temp;
											Integer sp_sl = ppsku.getPquantity_total() == null ? 0 : ppsku.getPquantity_total();
											Integer sp_amountTrongBo = productPairing.getAmount() == null ? 1 : productPairing.getAmount();
											if(slBo > sp_sl / sp_amountTrongBo) {
												slBo = sp_sl / sp_amountTrongBo;
//												System.out.println("2 " + sp_amountTrongBo);
											}
										}
									}
								}

								// neu slBo > 0 -> them vao danh sach
								if(slBo > 0) {
									SKU sku = skuService.findOne(pcontractProductSKU.getSkuid_link());
									PContractProductSKUBinding newPContractProductSKUBinding = new PContractProductSKUBinding();
									newPContractProductSKUBinding.setMaSanPham(sku.getProduct_code());
									newPContractProductSKUBinding.setMauSanPham(pcontractProductSKU.getMauSanPham());
									newPContractProductSKUBinding.setCoSanPham(pcontractProductSKU.getCoSanPham());
									newPContractProductSKUBinding.setSizeId(sku.getSize_id());
									newPContractProductSKUBinding.setSort_size(sku.getSort_size());
									newPContractProductSKUBinding.setPquantity_total(slBo);
									newPContractProductSKUBinding.setMaSanPhamPONgayGiao(sku.getProduct_code() + "(PO:" + maPo + ")" + dateString);
									newPContractProductSKUBinding.setPONgayGiao("(PO:" + maPo + ")(" + dateString + ")");
									newPContractProductSKUBinding.setPONgayGiao2("PO:" + maPo + "(" + dateString + ")");
									newPContractProductSKUBinding.setMauMaSanPham(pcontractProductSKU.getMauSanPham() + "-" + sku.getProduct_code());
									newPContractProductSKUBinding.setPo_CodeExtra(pcontract_po.getCode_extra());
									pcontractProductSKUBinding_list.add(newPContractProductSKUBinding);

								}
							}

						}

					}else {
						PContractProductSKU_list = pskuservice.getbypo_and_product(
								pcontract_po.getId(), pcontract_po.getProductid_link()
						);
					}
				}

				for(PContractProductSKU pcontractProductSKU : PContractProductSKU_list) {
					SKU sku = skuService.findOne(pcontractProductSKU.getSkuid_link());
					PContractProductSKUBinding newPContractProductSKUBinding = new PContractProductSKUBinding();
					// Mã SP, Màu, Cỡ, SL
					newPContractProductSKUBinding.setMaSanPham(sku.getProduct_code());
					newPContractProductSKUBinding.setMauSanPham(pcontractProductSKU.getMauSanPham());
					newPContractProductSKUBinding.setCoSanPham(pcontractProductSKU.getCoSanPham());
					newPContractProductSKUBinding.setSizeId(sku.getSize_id());
					newPContractProductSKUBinding.setSort_size(sku.getSort_size());
					newPContractProductSKUBinding.setPquantity_total(pcontractProductSKU.getPquantity_total());
					newPContractProductSKUBinding.setMaSanPhamPONgayGiao(sku.getProduct_code() + "(PO:" + maPo + ")" + dateString);
					newPContractProductSKUBinding.setPONgayGiao("(PO:" + maPo + ")(" + dateString + ")");
					newPContractProductSKUBinding.setPONgayGiao2("PO:" + maPo + "(" + dateString + ")");
					newPContractProductSKUBinding.setMauMaSanPham(pcontractProductSKU.getMauSanPham() + "-" + sku.getProduct_code());
					newPContractProductSKUBinding.setPo_CodeExtra(pcontract_po.getCode_extra());
					pcontractProductSKUBinding_list.add(newPContractProductSKUBinding);
				}
			}

			// sort theo MaSanPhamPONgayGiao -> dung cho danh sach tren
			Collections.sort(pcontractProductSKUBinding_list, new Comparator<PContractProductSKUBinding>() {
				public int compare(PContractProductSKUBinding o1, PContractProductSKUBinding o2) {
//					return o1.getMaSanPhamPONgayGiao().compareTo(o2.getMaSanPhamPONgayGiao());

					String x1 = o1.getMaSanPhamPONgayGiao();
					String x2 = o2.getMaSanPhamPONgayGiao();
					int sComp = x1.compareTo(x2);

					if (sComp != 0) {
						return sComp;
					}

					String y1 = o1.getMauSanPham();
					String y2 = o2.getMauSanPham();
					return y1.compareTo(y2);

				}
			});

			// copy danh sach -> dung cho danh sach duoi
			List<PContractProductSKUBinding> pcontractProductSKUBinding_list_2 = new ArrayList<PContractProductSKUBinding>(pcontractProductSKUBinding_list);
			Collections.sort(pcontractProductSKUBinding_list_2, new Comparator<PContractProductSKUBinding>() {
				public int compare(PContractProductSKUBinding o1, PContractProductSKUBinding o2) {
//					return o1.getMaSanPhamPONgayGiao().compareTo(o2.getMaSanPhamPONgayGiao());

					String x1 = o1.getMauMaSanPham();
					String x2 = o2.getMauMaSanPham();
					int sComp = x1.compareTo(x2);

					if (sComp != 0) {
						return sComp;
					}

					Integer y1 = o1.getSort_size();
					Integer y2 = o2.getSort_size();
					return y1.compareTo(y2);
				}
			});

			// them vao file excel
			String uploadRoot = request.getServletContext().getRealPath("report/Export/KeHoachSX");
			File uploadRootDir = new File(uploadRoot);
			// Tạo thư mục gốc upload nếu không tồn tại.
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}

			Date current_time = new Date();
			File FileExport = new File(uploadRoot + "/Template_KeHoachSX.xlsx");
			File FileCopy = new File(uploadRoot + "/Template_KeHoachSX_" + current_time.getTime() + ".xlsx"); // copy cua
			// template
			// de chinh
			// sua tren
			// do
			File file = new File(uploadRoot + "/KeHoachSX_Export_" + current_time.getTime() + ".xlsx"); // file de export
			// tao file copy cua template
			Files.copy(FileExport, FileCopy);
			FileInputStream is_filecopy = new FileInputStream(FileCopy);

			XSSFWorkbook workbook = new XSSFWorkbook(is_filecopy);
			XSSFSheet sheet = workbook.getSheetAt(0);

			// tao font de cho vao style

			XSSFFont font_Calibri = workbook.createFont();
			font_Calibri.setFontName("Calibri");
			font_Calibri.setFontHeightInPoints((short) 9.5);

			XSSFFont font_Calibri_bold = workbook.createFont();
			font_Calibri_bold.setFontName("Calibri");
			font_Calibri_bold.setFontHeightInPoints((short) 10);
			font_Calibri_bold.setBold(true);

			XSSFFont font_Calibri_Italic = workbook.createFont();
			font_Calibri_Italic.setFontName("Calibri");
			font_Calibri_Italic.setFontHeightInPoints((short) 9.5);
			font_Calibri_Italic.setItalic(true);

			// Style cua Ha

			XSSFCellStyle cellStyle_Ngay = workbook.createCellStyle();
			cellStyle_Ngay.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Ngay.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Ngay.setFont(font_Calibri_bold);
			cellStyle_Ngay.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
			cellStyle_Ngay.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
			cellStyle_Ngay.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Ngay.setBorderTop(BorderStyle.THIN);
//			cellStyle_Ngay.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_Ngay.setBorderLeft(BorderStyle.THIN);
//			cellStyle_Ngay.setBorderRight(BorderStyle.THIN);

			// style danh sach tren
			XSSFCellStyle cellStyle_STT = workbook.createCellStyle();
			cellStyle_STT.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_STT.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_STT.setFont(font_Calibri);
			cellStyle_STT.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
			cellStyle_STT.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.index);
			cellStyle_STT.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_STT.setBorderTop(BorderStyle.THIN);
			cellStyle_STT.setBorderBottom(BorderStyle.DOTTED);
			cellStyle_STT.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_STT.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Ma = workbook.createCellStyle();
			cellStyle_Ma.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_Ma.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Ma.setFont(font_Calibri_bold);
			cellStyle_Ma.setWrapText(true);
			cellStyle_Ma.setFillForegroundColor(IndexedColors.CORAL.index);
			cellStyle_Ma.setFillBackgroundColor(IndexedColors.CORAL.index);
			cellStyle_Ma.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Ma.setBorderTop(BorderStyle.THIN);
			cellStyle_Ma.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_Ma.setBorderLeft(BorderStyle.THIN);
			cellStyle_Ma.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Mau = workbook.createCellStyle();
			cellStyle_Mau.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_Mau.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Mau.setFont(font_Calibri);
			cellStyle_Mau.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Mau.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Mau.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Mau.setBorderTop(BorderStyle.THIN);
			cellStyle_Mau.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_Mau.setBorderLeft(BorderStyle.THIN);
			cellStyle_Mau.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_SL = workbook.createCellStyle();
			cellStyle_SL.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_SL.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_SL.setFont(font_Calibri);
			cellStyle_SL.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_SL.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_SL.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_SL.setBorderTop(BorderStyle.THIN);
			cellStyle_SL.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_SL.setBorderLeft(BorderStyle.THIN);
			cellStyle_SL.setBorderRight(BorderStyle.DOTTED);
			cellStyle_SL.setDataFormat(3);

			XSSFCellStyle cellStyle_SL_center_value0 = workbook.createCellStyle();
			cellStyle_SL_center_value0.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_SL_center_value0.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_SL_center_value0.setFont(font_Calibri);
			cellStyle_SL_center_value0.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_SL_center_value0.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_SL_center_value0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_SL_center_value0.setBorderTop(BorderStyle.THIN);
			cellStyle_SL_center_value0.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_SL_center_value0.setBorderLeft(BorderStyle.THIN);
			cellStyle_SL_center_value0.setBorderRight(BorderStyle.DOTTED);
			cellStyle_SL_center_value0.setDataFormat(3);

			XSSFCellStyle cellStyle_Tong_Right = workbook.createCellStyle();
			cellStyle_Tong_Right.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Tong_Right.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Tong_Right.setFont(font_Calibri_bold);
			cellStyle_Tong_Right.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Right.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Right.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Tong_Right.setBorderTop(BorderStyle.THIN);
			cellStyle_Tong_Right.setBorderBottom(BorderStyle.DOTTED);
			cellStyle_Tong_Right.setBorderLeft(BorderStyle.THIN);
			cellStyle_Tong_Right.setBorderRight(BorderStyle.MEDIUM);
			cellStyle_Tong_Right.setDataFormat(3);

			XSSFCellStyle cellStyle_Tong_Bottom_STT = workbook.createCellStyle();
			cellStyle_Tong_Bottom_STT.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Tong_Bottom_STT.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Tong_Bottom_STT.setFont(font_Calibri_bold);
			cellStyle_Tong_Bottom_STT.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_STT.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_STT.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Tong_Bottom_STT.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_STT.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_STT.setBorderLeft(BorderStyle.MEDIUM);
//			cellStyle_Tong_Bottom_STT.setBorderRight(BorderStyle.MEDIUM);

			XSSFCellStyle cellStyle_Tong_Bottom_Text = workbook.createCellStyle();
			cellStyle_Tong_Bottom_Text.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Tong_Bottom_Text.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Tong_Bottom_Text.setFont(font_Calibri_bold);
			cellStyle_Tong_Bottom_Text.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_Text.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_Text.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Tong_Bottom_Text.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_Text.setBorderBottom(BorderStyle.MEDIUM);
//			cellStyle_Tong_Bottom_Text.setBorderLeft(BorderStyle.THIN);
			cellStyle_Tong_Bottom_Text.setBorderRight(BorderStyle.MEDIUM);

			XSSFCellStyle cellStyle_Tong_Bottom_SL = workbook.createCellStyle();
			cellStyle_Tong_Bottom_SL.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Tong_Bottom_SL.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Tong_Bottom_SL.setFont(font_Calibri_bold);
			cellStyle_Tong_Bottom_SL.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_SL.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_SL.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Tong_Bottom_SL.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_SL.setBorderBottom(BorderStyle.MEDIUM);
//			cellStyle_Tong_Bottom_SL.setBorderLeft(BorderStyle.THIN);
			cellStyle_Tong_Bottom_SL.setBorderRight(BorderStyle.DOTTED);
			cellStyle_Tong_Bottom_SL.setDataFormat(3);

			XSSFCellStyle cellStyle_Tong_Bottom_Tong = workbook.createCellStyle();
			cellStyle_Tong_Bottom_Tong.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Tong_Bottom_Tong.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Tong_Bottom_Tong.setFont(font_Calibri_bold);
			cellStyle_Tong_Bottom_Tong.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_Tong.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Tong_Bottom_Tong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Tong_Bottom_Tong.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_Tong.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_Tong.setBorderLeft(BorderStyle.THIN);
			cellStyle_Tong_Bottom_Tong.setBorderRight(BorderStyle.MEDIUM);
			cellStyle_Tong_Bottom_Tong.setDataFormat(3);

			// style danh sach duoi
			XSSFCellStyle cellStyle_Text_Title = workbook.createCellStyle();
			cellStyle_Text_Title.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Title.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Title.setFont(font_Calibri_bold);
			cellStyle_Text_Title.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_Title.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_Title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Title.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Title.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Title.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_Title.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Title_Number = workbook.createCellStyle();
			cellStyle_Text_Title_Number.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Title_Number.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Title_Number.setFont(font_Calibri_bold);
			cellStyle_Text_Title_Number.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_Title_Number.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_Title_Number.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Title_Number.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Title_Number.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Title_Number.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Title_Number.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Title_Tong = workbook.createCellStyle();
			cellStyle_Text_Title_Tong.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Title_Tong.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Title_Tong.setFont(font_Calibri_bold);
			cellStyle_Text_Title_Tong.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Title_Tong.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Title_Tong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Title_Tong.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Title_Tong.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Title_Tong.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Title_Tong.setBorderRight(BorderStyle.MEDIUM);

			XSSFCellStyle cellStyle_Text_Color = workbook.createCellStyle();
			cellStyle_Text_Color.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_Text_Color.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Color.setFont(font_Calibri);
			cellStyle_Text_Color.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_Color.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_Color.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Text_Color.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Color.setBorderBottom(BorderStyle.DOTTED);
			cellStyle_Text_Color.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_Color.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_SL = workbook.createCellStyle();
			cellStyle_Text_SL.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Text_SL.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_SL.setFont(font_Calibri);
			cellStyle_Text_SL.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_SL.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_SL.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Text_SL.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_SL.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_Text_SL.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_SL.setBorderRight(BorderStyle.DOTTED);
			cellStyle_Text_SL.setDataFormat(3);

			XSSFCellStyle cellStyle_Text_SL_center_value0 = workbook.createCellStyle();
			cellStyle_Text_SL_center_value0.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_SL_center_value0.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_SL_center_value0.setFont(font_Calibri);
			cellStyle_Text_SL_center_value0.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_SL_center_value0.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			cellStyle_Text_SL_center_value0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Text_SL_center_value0.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_SL_center_value0.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_Text_SL_center_value0.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_SL_center_value0.setBorderRight(BorderStyle.DOTTED);
			cellStyle_Text_SL_center_value0.setDataFormat(3);

			XSSFCellStyle cellStyle_Text_SL_tong = workbook.createCellStyle();
			cellStyle_Text_SL_tong.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Text_SL_tong.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_SL_tong.setFont(font_Calibri);
			cellStyle_Text_SL_tong.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_SL_tong.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_SL_tong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Text_SL_tong.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_SL_tong.setBorderBottom(BorderStyle.DOTTED);
//			cellStyle_Text_SL_tong.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_SL_tong.setBorderRight(BorderStyle.MEDIUM);
			cellStyle_Text_SL_tong.setDataFormat(3);

			XSSFCellStyle cellStyle_Text_Tong = workbook.createCellStyle();
			cellStyle_Text_Tong.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Tong.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Tong.setFont(font_Calibri_bold);
			cellStyle_Text_Tong.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Tong.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Tong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Tong.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Tong.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle_Text_Tong.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_Tong.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Tong_SL = workbook.createCellStyle();
			cellStyle_Text_Tong_SL.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Text_Tong_SL.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Tong_SL.setFont(font_Calibri_bold);
			cellStyle_Text_Tong_SL.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Tong_SL.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Tong_SL.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Tong_SL.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Tong_SL.setBorderBottom(BorderStyle.MEDIUM);
//			cellStyle_Text_Tong_SL.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle_Text_Tong_SL.setBorderRight(BorderStyle.DOTTED);
			cellStyle_Text_Tong_SL.setDataFormat(3);

			XSSFCellStyle cellStyle_Text_Tong_tong = workbook.createCellStyle();
			cellStyle_Text_Tong_tong.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Text_Tong_tong.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Tong_tong.setFont(font_Calibri_bold);
			cellStyle_Text_Tong_tong.setFillForegroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Tong_tong.setFillBackgroundColor(IndexedColors.YELLOW.index);
			cellStyle_Text_Tong_tong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Tong_tong.setBorderTop(BorderStyle.MEDIUM);
			cellStyle_Text_Tong_tong.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle_Text_Tong_tong.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Tong_tong.setBorderRight(BorderStyle.MEDIUM);
			cellStyle_Text_Tong_tong.setDataFormat(3);

			//
			XSSFCellStyle cellStyle_Text_Medium_all_center = workbook.createCellStyle();
			cellStyle_Text_Medium_all_center.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_all_center.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_all_center.setFont(font_Calibri);
			cellStyle_Text_Medium_all_center.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_all_center.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_all_center.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_all_center.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_all_center.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Medium_all_center.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_all_center.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_all_left = workbook.createCellStyle();
			cellStyle_Text_Medium_all_left.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_Text_Medium_all_left.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_all_left.setFont(font_Calibri);
			cellStyle_Text_Medium_all_left.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_all_left.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_all_left.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_all_left.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_all_left.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Medium_all_left.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_all_left.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_all_right = workbook.createCellStyle();
			cellStyle_Text_Medium_all_right.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle_Text_Medium_all_right.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_all_right.setFont(font_Calibri);
			cellStyle_Text_Medium_all_right.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_all_right.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_all_right.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_all_right.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_all_right.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Medium_all_right.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_all_right.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_noBottom = workbook.createCellStyle();
			cellStyle_Text_Medium_noBottom.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_noBottom.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_noBottom.setFont(font_Calibri);
			cellStyle_Text_Medium_noBottom.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noBottom.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noBottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_noBottom.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_noBottom.setBorderBottom(BorderStyle.DOTTED);
			cellStyle_Text_Medium_noBottom.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_noBottom.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_noLeft = workbook.createCellStyle();
			cellStyle_Text_Medium_noLeft.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_noLeft.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_noLeft.setFont(font_Calibri);
			cellStyle_Text_Medium_noLeft.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noLeft.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noLeft.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_noLeft.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_noLeft.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Medium_noLeft.setBorderLeft(BorderStyle.DOTTED);
			cellStyle_Text_Medium_noLeft.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_noRight = workbook.createCellStyle();
			cellStyle_Text_Medium_noRight.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_noRight.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_noRight.setFont(font_Calibri);
			cellStyle_Text_Medium_noRight.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noRight.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_noRight.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_noRight.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Medium_noRight.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_noRight.setBorderRight(BorderStyle.DOTTED);

			XSSFCellStyle cellStyle_Text_Medium_noTop = workbook.createCellStyle();
			cellStyle_Text_Medium_noTop.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_noTop.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_noTop.setFont(font_Calibri);
			cellStyle_Text_Medium_noTop.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noTop.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_noTop.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle_Text_Medium_noTop.setBorderTop(BorderStyle.DOTTED);
			cellStyle_Text_Medium_noTop.setBorderBottom(BorderStyle.THIN);
			cellStyle_Text_Medium_noTop.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_noTop.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_bottom = workbook.createCellStyle();
			cellStyle_Text_Medium_bottom.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_bottom.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_bottom.setFont(font_Calibri);
			cellStyle_Text_Medium_bottom.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_bottom.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_bottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Text_Medium_bottom.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_bottom.setBorderBottom(BorderStyle.THIN);
//			cellStyle_Text_Medium_bottom.setBorderLeft(BorderStyle.THIN);
//			cellStyle_Text_Medium_bottom.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle cellStyle_Text_Medium_bottom_right = workbook.createCellStyle();
			cellStyle_Text_Medium_bottom_right.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_Text_Medium_bottom_right.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_Text_Medium_bottom_right.setFont(font_Calibri);
			cellStyle_Text_Medium_bottom_right.setFillForegroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_bottom_right.setFillBackgroundColor(IndexedColors.WHITE.index);
			cellStyle_Text_Medium_bottom_right.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			cellStyle_Text_Medium_bottom_right.setBorderTop(BorderStyle.THIN);
			cellStyle_Text_Medium_bottom_right.setBorderBottom(BorderStyle.THIN);
//			cellStyle_Text_Medium_bottom_right.setBorderLeft(BorderStyle.THIN);
			cellStyle_Text_Medium_bottom_right.setBorderRight(BorderStyle.THIN);

			try {
				//////// GHI BANG DANH SACH MAU CO THEO SAN PHAM, PO, SHIPDATE
				Integer colNumber = 0;

				// Lay ten don hang, khach hang,
				String donHang = pcontract.getContractcode() == null ? "" : pcontract.getContractcode();
				String khachHang = pcontract.getBuyercode() == null ? "" : pcontract.getBuyercode();

				// Ghi ten don hang, khach hang
				Row row_2 = sheet.getRow(1);
				Cell cell_C2 = row_2.getCell(ColumnExcel.C);
				cell_C2.setCellValue(maHang);
				Cell cell_P2 = row_2.getCell(ColumnExcel.P);
				cell_P2.setCellValue(khachHang);

				Row row_3 = sheet.getRow(2);
				Cell cell_C3 = row_3.getCell(ColumnExcel.C);
				cell_C3.setCellValue(donHang);

				// Lay danh sach co san pham
				Map<Integer, List<String>> map_size = new HashMap<Integer, List<String>>();
				for(PContractProductSKUBinding binding : pcontractProductSKUBinding_list) {
					String coSP = binding.getCoSanPham();
//					Long sizeId = binding.getSizeId();
					Integer sort_size = binding.getSort_size();

					List<String> values = new ArrayList<String>();
					values.add(coSP);

					if(!map_size.containsKey(sort_size)) {
						map_size.put(sort_size, values);
					}
				}

				// sort ds map co san pham
				List<Integer> keys = new ArrayList<>(map_size.keySet());
//				List<List<String>> values = new ArrayList<>(map_size.values());
				Collections.sort(keys);
//				System.out.println(keys.size());
//				System.out.println(values.size());
				// chi co 20 cot nen lay 20 keys
//				List<Integer> newKeys = new ArrayList<Integer>();
//				for(Integer i = 0; i <= 20; i++) {
//					newKeys.add(keys.get(i));
//				}

				// Ghi danh sach co san pham
				Row row_4 = sheet.getRow(3);
				colNumber = ColumnExcel.C;
				for(Integer key : keys) {
					if(colNumber <= ColumnExcel.V) {
						Cell cell = row_4.getCell(colNumber);
						List<String> listString = map_size.get(key);
						listString.add(colNumber.toString()); // them col number vao ds co san pham de tien sau nay ghi tong tin vao file excel
						map_size.put(key, listString);
						cell.setCellValue(listString.get(0));
						colNumber++;
					}else {
						List<String> listString = map_size.get(key);
						listString.add(colNumber.toString()); // them col number vao ds co san pham de tien sau nay ghi tong tin vao file excel
						map_size.put(key, listString);
						colNumber++;
					}
				}
				colNumber=0;

				// ghi du lieu vao cac row
				int rowNumber = 4; // starting row
				int stt = 1; // cot STT
				String maSanPhamPONgayGiao_current = "";
				String mauSanPham_current = "";
				for(PContractProductSKUBinding binding : pcontractProductSKUBinding_list) {
					String maSanPhamPONgayGiao = binding.getMaSanPhamPONgayGiao();
					String mauSanPham = binding.getMauSanPham();
					String pONgayGiao = binding.getPONgayGiao();
					String po_CodeExtra = binding.getPo_CodeExtra();
					if(po_CodeExtra != null && !po_CodeExtra.trim().equals("")) {
						pONgayGiao = "Mã:"+ po_CodeExtra + pONgayGiao;
					}else {
						pONgayGiao = binding.getPONgayGiao2();
					}

					if(!maSanPhamPONgayGiao.equals(maSanPhamPONgayGiao_current)) {
						rowNumber++;
						mauSanPham_current = "";
						maSanPhamPONgayGiao_current = maSanPhamPONgayGiao;

						// them dong ma san pham moi
						Row row_ma = sheet.createRow(rowNumber);
						Cell cell_A_ma = row_ma.createCell(ColumnExcel.A);
//						cell_A_ma.setCellValue("");
						cell_A_ma.setCellStyle(cellStyle_STT);
						Cell cell_B_ma = row_ma.createCell(ColumnExcel.B);
//						cell_B_ma.setCellValue("Mã:"+maSanPhamPONgayGiao);
						cell_B_ma.setCellValue(pONgayGiao);
						cell_B_ma.setCellStyle(cellStyle_Ma);
						for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
							Cell cell_SL_ma = row_ma.createCell(i);
//							cell_SL_ma.setCellValue("");
							cell_SL_ma.setCellStyle(cellStyle_SL);
						}
						Cell cell_W_ma = row_ma.createCell(ColumnExcel.W);
//						cell_W_ma.setCellValue("");
						cell_W_ma.setCellStyle(cellStyle_Tong_Right);

						// tang rowNumber de them dong thong tin mau co
						rowNumber++;
						Row row = sheet.createRow(rowNumber);
						Cell cell_A = row.createCell(ColumnExcel.A);
						cell_A.setCellValue(stt);
						cell_A.setCellStyle(cellStyle_STT);
						Cell cell_B = row.createCell(ColumnExcel.B);
						cell_B.setCellValue(mauSanPham);
						cell_B.setCellStyle(cellStyle_Mau);
						for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
							Cell cell_SL = row.createCell(i);
							cell_SL.setCellValue("-");
//							cell_SL.setCellStyle(cellStyle_SL);
							cell_SL.setCellStyle(cellStyle_SL_center_value0);
						}
						Cell cell_W = row.createCell(ColumnExcel.W);
//						cell_W.setCellValue("");
						cell_W.setCellStyle(cellStyle_Tong_Right);
						cell_W.setCellFormula("SUM(C" + (rowNumber+1) + ":V" + (rowNumber+1) + ")");
						mauSanPham_current = mauSanPham;

						// set thong tin cho cot SL cua co tuong ung
						List<String> listString = map_size.get(binding.getSort_size());
//						String coSP = listString.get(0);
						colNumber = Integer.valueOf(listString.get(1));
						Cell cell_SL = row.createCell(colNumber);
						cell_SL.setCellValue(binding.getPquantity_total());
						cell_SL.setCellStyle(cellStyle_SL);
						stt++;
					}else {
						if(binding.getMauSanPham().equals(mauSanPham_current)) {
							// sua dong thong tin hien
							Row row = sheet.getRow(rowNumber);
							List<String> listString = map_size.get(binding.getSort_size());
//							String coSP = listString.get(0);
//							System.out.println(listString);
							colNumber = Integer.valueOf(listString.get(1));
							if(colNumber <= ColumnExcel.V) {
								Cell cell_SL = row.getCell(colNumber);
								cell_SL.setCellValue(binding.getPquantity_total());
								cell_SL.setCellStyle(cellStyle_SL);
							}
						}else {
							// them dong thong tin mau co moi
							mauSanPham_current = mauSanPham;
							rowNumber++;
							Row row = sheet.createRow(rowNumber);
							Cell cell_A = row.createCell(ColumnExcel.A);
							cell_A.setCellValue(stt);
							cell_A.setCellStyle(cellStyle_STT);
							Cell cell_B = row.createCell(ColumnExcel.B);
							cell_B.setCellValue(mauSanPham);
							cell_B.setCellStyle(cellStyle_Mau);
							for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
								Cell cell_SL = row.createCell(i);
								cell_SL.setCellValue("-");
//								cell_SL.setCellStyle(cellStyle_SL);
								cell_SL.setCellStyle(cellStyle_SL_center_value0);
							}
							Cell cell_W = row.createCell(ColumnExcel.W);
//							cell_W.setCellValue("");
							cell_W.setCellStyle(cellStyle_Tong_Right);
							cell_W.setCellFormula("SUM(C" + (rowNumber+1) + ":V" + (rowNumber+1) + ")");

							// set thong tin cho cot SL cua co tuong ung
							List<String> listString = map_size.get(binding.getSort_size());
//							String coSP = listString.get(0);
							colNumber = Integer.valueOf(listString.get(1));
							if(colNumber <= ColumnExcel.V) {
								Cell cell_SL = row.createCell(colNumber);
								cell_SL.setCellValue(binding.getPquantity_total());
								cell_SL.setCellStyle(cellStyle_SL);
							}
							stt++;
						}
					}
				}

				// Them dong tinh tong duoi
				rowNumber++;
				Row row_tong_duoi = sheet.createRow(rowNumber);
				Cell cell_A = row_tong_duoi.createCell(ColumnExcel.A);
//				cell_A.setCellValue("");
				cell_A.setCellStyle(cellStyle_Tong_Bottom_STT);
				Cell cell_B = row_tong_duoi.createCell(ColumnExcel.B);
				cell_B.setCellValue("Tổng");
				cell_B.setCellStyle(cellStyle_Tong_Bottom_Text);
				for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
					Cell cell_tong_SL = row_tong_duoi.createCell(i);
//					cell_tong_SL.setCellValue("");
					switch(i) {
						case 2:
							cell_tong_SL.setCellFormula("SUM(C" + 6 + ":C" + rowNumber + ")");
							break;
						case 3:
							cell_tong_SL.setCellFormula("SUM(D" + 6 + ":D" + rowNumber + ")");
							break;
						case 4:
							cell_tong_SL.setCellFormula("SUM(E" + 6 + ":E" + rowNumber + ")");
							break;
						case 5:
							cell_tong_SL.setCellFormula("SUM(F" + 6 + ":F" + rowNumber + ")");
							break;
						case 6:
							cell_tong_SL.setCellFormula("SUM(G" + 6 + ":G" + rowNumber + ")");
							break;
						case 7:
							cell_tong_SL.setCellFormula("SUM(H" + 6 + ":H" + rowNumber + ")");
							break;
						case 8:
							cell_tong_SL.setCellFormula("SUM(I" + 6 + ":I" + rowNumber + ")");
							break;
						case 9:
							cell_tong_SL.setCellFormula("SUM(J" + 6 + ":J" + rowNumber + ")");
							break;
						case 10:
							cell_tong_SL.setCellFormula("SUM(K" + 6 + ":K" + rowNumber + ")");
							break;
						case 11:
							cell_tong_SL.setCellFormula("SUM(L" + 6 + ":L" + rowNumber + ")");
							break;
						case 12:
							cell_tong_SL.setCellFormula("SUM(M" + 6 + ":M" + rowNumber + ")");
							break;
						case 13:
							cell_tong_SL.setCellFormula("SUM(N" + 6 + ":N" + rowNumber + ")");
							break;
						case 14:
							cell_tong_SL.setCellFormula("SUM(O" + 6 + ":O" + rowNumber + ")");
							break;
						case 15:
							cell_tong_SL.setCellFormula("SUM(P" + 6 + ":P" + rowNumber + ")");
							break;
						case 16:
							cell_tong_SL.setCellFormula("SUM(Q" + 6 + ":Q" + rowNumber + ")");
							break;
						case 17:
							cell_tong_SL.setCellFormula("SUM(R" + 6 + ":R" + rowNumber + ")");
							break;
						case 18:
							cell_tong_SL.setCellFormula("SUM(S" + 6 + ":S" + rowNumber + ")");
							break;
						case 19:
							cell_tong_SL.setCellFormula("SUM(T" + 6 + ":T" + rowNumber + ")");
							break;
						case 20:
							cell_tong_SL.setCellFormula("SUM(U" + 6 + ":U" + rowNumber + ")");
							break;
						case 21:
							cell_tong_SL.setCellFormula("SUM(V" + 6 + ":V" + rowNumber + ")");
							break;
					}
					cell_tong_SL.setCellStyle(cellStyle_Tong_Bottom_SL);
				}
				Cell cell_W = row_tong_duoi.createCell(ColumnExcel.W);
				cell_W.setCellFormula("SUM(C" + (rowNumber+1) + ":V" + (rowNumber+1) + ")");
				cell_W.setCellStyle(cellStyle_Tong_Bottom_Tong);

				//////// GHI BANG DANH SACH MAU CO THEO MAU
				// dong title danh sach thu 2
				rowNumber = rowNumber+2;
				Row row = sheet.createRow(rowNumber);
				cell_B = row.createCell(ColumnExcel.B);
				cell_B.setCellValue("MÀU CHÍNH");
				cell_B.setCellStyle(cellStyle_Text_Title);

				colNumber = 1;
				for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(colNumber);
					cell.setCellStyle(cellStyle_Text_Title_Number);
					colNumber++;
				}

				cell_W = row.createCell(ColumnExcel.W);
				cell_W.setCellValue("SL");
				cell_W.setCellStyle(cellStyle_Text_Title_Tong);

				// them ca dong mau co vao danh sach thu 2
//				rowNumber++;
				Integer danhSach2StartAt = rowNumber;
				String MauMaSanPham_current = "";
				for(PContractProductSKUBinding binding : pcontractProductSKUBinding_list_2) {
					String mauMaSanPham = binding.getMauMaSanPham();
					String mauSanPham = binding.getMauSanPham();
					String po_CodeExtra = binding.getPo_CodeExtra();
					if(po_CodeExtra != null && !po_CodeExtra.trim().equals("")) {
						mauSanPham = mauSanPham + "-" + po_CodeExtra;
					}

					if(!mauMaSanPham.equals(MauMaSanPham_current)) {
						rowNumber++;
						MauMaSanPham_current = mauMaSanPham;

						// them dong ma san pham moi
						row = sheet.createRow(rowNumber);
						cell_B = row.createCell(ColumnExcel.B);
//						cell_B.setCellValue(mauMaSanPham);
						cell_B.setCellValue(mauSanPham);
						cell_B.setCellStyle(cellStyle_Text_Color);
						for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
							Cell cell_SL = row.createCell(i);
							cell_SL.setCellValue("-");
//							cell_SL.setCellStyle(cellStyle_Text_SL_center_value0);
						}
						cell_W = row.createCell(ColumnExcel.W);
//						cell_W.setCellValue("");
						cell_W.setCellStyle(cellStyle_Text_SL_tong);

						// tang rowNumber de them dong thong tin mau co
						cell_B = row.createCell(ColumnExcel.B);
//						cell_B.setCellValue(mauMaSanPham);
						cell_B.setCellValue(mauSanPham);
						cell_B.setCellStyle(cellStyle_Text_Color);
						for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
							Cell cell_SL = row.createCell(i);
							cell_SL.setCellValue("-");
//							cell_SL.setCellStyle(cellStyle_Text_SL_center_value0);
						}
						cell_W = row.createCell(ColumnExcel.W);
//						cell_W.setCellValue("");
						cell_W.setCellStyle(cellStyle_Tong_Right);
						cell_W.setCellFormula("SUM(C" + (rowNumber+1) + ":V" + (rowNumber+1) + ")");

						// set thong tin cho cot SL cua co tuong ung
						List<String> listString = map_size.get(binding.getSort_size());
//						String coSP = listString.get(0);
						colNumber = Integer.valueOf(listString.get(1));
						Cell cell_SL = row.createCell(colNumber);
						cell_SL.setCellValue(binding.getPquantity_total());
//						cell_SL.setCellStyle(cellStyle_Text_SL);
//						cell_SL.setCellValue(binding.getPquantity_total().toString());
					}else {
						// sua dong thong tin hien
						row = sheet.getRow(rowNumber);
						List<String> listString = map_size.get(binding.getSort_size());
//						String coSP = listString.get(0);
//						System.out.println(listString);
						colNumber = Integer.valueOf(listString.get(1));
						if(colNumber <= ColumnExcel.V) {
							Cell cell_SL = row.getCell(colNumber);
//							String cellValue = cell_SL.getStringCellValue();
							DataFormatter formatter = new DataFormatter();
							String cellValue = formatter.formatCellValue(cell_SL);
							int cellCurrentValue = 0;
//							cellCurrentValue = (int) cell_SL.getNumericCellValue();
							if(cellValue != null && !cellValue.equals("") && !cellValue.equals("-")) {
								cellCurrentValue = Integer.valueOf(cellValue);
							}
							int addValue = binding.getPquantity_total() == null ? 0 : binding.getPquantity_total();
							cell_SL.setCellValue(cellCurrentValue + addValue);
//							cell_SL.setCellStyle(cellStyle_Text_SL);
						}
					}
				}

				// set format
//				System.out.println(danhSach2StartAt);
//				System.out.println(rowNumber);
				for(Integer i=danhSach2StartAt+1;i<=rowNumber;i++) {
					row = sheet.getRow(i);
					for(Integer j=ColumnExcel.C;j<=ColumnExcel.V;j++) {
						Cell cell_SL = row.getCell(j);
						DataFormatter formatter = new DataFormatter();
						String cellValue = formatter.formatCellValue(cell_SL);
						if(cellValue != null && !cellValue.equals("") && !cellValue.equals("-")) {
							cell_SL.setCellStyle(cellStyle_Text_SL);
						}else {
							cell_SL.setCellStyle(cellStyle_Text_SL_center_value0);
						}
					}
				}

				// dong tong danh sach 2
				rowNumber = rowNumber+1;
				row = sheet.createRow(rowNumber);
				cell_B = row.createCell(ColumnExcel.B);
				cell_B.setCellValue("Tổng");
				cell_B.setCellStyle(cellStyle_Text_Tong);

				for(Integer i = ColumnExcel.C; i <= ColumnExcel.V; i++) {
					Cell cell_tong_SL = row.createCell(i);
//					cell_tong_SL.setCellValue("");
					switch(i) {
						case 2:
							cell_tong_SL.setCellFormula("SUM(C" + (danhSach2StartAt+2) + ":C" + rowNumber + ")");
							break;
						case 3:
							cell_tong_SL.setCellFormula("SUM(D" + (danhSach2StartAt+2) + ":D" + rowNumber + ")");
							break;
						case 4:
							cell_tong_SL.setCellFormula("SUM(E" + (danhSach2StartAt+2) + ":E" + rowNumber + ")");
							break;
						case 5:
							cell_tong_SL.setCellFormula("SUM(F" + (danhSach2StartAt+2) + ":F" + rowNumber + ")");
							break;
						case 6:
							cell_tong_SL.setCellFormula("SUM(G" + (danhSach2StartAt+2) + ":G" + rowNumber + ")");
							break;
						case 7:
							cell_tong_SL.setCellFormula("SUM(H" + (danhSach2StartAt+2) + ":H" + rowNumber + ")");
							break;
						case 8:
							cell_tong_SL.setCellFormula("SUM(I" + (danhSach2StartAt+2) + ":I" + rowNumber + ")");
							break;
						case 9:
							cell_tong_SL.setCellFormula("SUM(J" + (danhSach2StartAt+2) + ":J" + rowNumber + ")");
							break;
						case 10:
							cell_tong_SL.setCellFormula("SUM(K" + (danhSach2StartAt+2) + ":K" + rowNumber + ")");
							break;
						case 11:
							cell_tong_SL.setCellFormula("SUM(L" + (danhSach2StartAt+2) + ":L" + rowNumber + ")");
							break;
						case 12:
							cell_tong_SL.setCellFormula("SUM(M" + (danhSach2StartAt+2) + ":M" + rowNumber + ")");
							break;
						case 13:
							cell_tong_SL.setCellFormula("SUM(N" + (danhSach2StartAt+2) + ":N" + rowNumber + ")");
							break;
						case 14:
							cell_tong_SL.setCellFormula("SUM(O" + (danhSach2StartAt+2) + ":O" + rowNumber + ")");
							break;
						case 15:
							cell_tong_SL.setCellFormula("SUM(P" + (danhSach2StartAt+2) + ":P" + rowNumber + ")");
							break;
						case 16:
							cell_tong_SL.setCellFormula("SUM(Q" + (danhSach2StartAt+2) + ":Q" + rowNumber + ")");
							break;
						case 17:
							cell_tong_SL.setCellFormula("SUM(R" + (danhSach2StartAt+2) + ":R" + rowNumber + ")");
							break;
						case 18:
							cell_tong_SL.setCellFormula("SUM(S" + (danhSach2StartAt+2) + ":S" + rowNumber + ")");
							break;
						case 19:
							cell_tong_SL.setCellFormula("SUM(T" + (danhSach2StartAt+2) + ":T" + rowNumber + ")");
							break;
						case 20:
							cell_tong_SL.setCellFormula("SUM(U" + (danhSach2StartAt+2) + ":U" + rowNumber + ")");
							break;
						case 21:
							cell_tong_SL.setCellFormula("SUM(V" + (danhSach2StartAt+2) + ":V" + rowNumber + ")");
							break;
					}
					cell_tong_SL.setCellStyle(cellStyle_Text_Tong_SL);
				}
				cell_W = row.createCell(ColumnExcel.W);
				cell_W.setCellFormula("SUM(C" + (rowNumber+1) + ":V" + (rowNumber+1) + ")");
				cell_W.setCellStyle(cellStyle_Text_Tong_tong);

				// TO SX
				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					cell.setCellStyle(cellStyle_Text_Medium_all_center);
				}

				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,2,5));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,6,9));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,10,13));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,14,16));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,17,19));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,20,22));

				cell_B = row.getCell(ColumnExcel.B);
				cell_B.setCellValue("Tổ SX");
				Cell cell_C = row.getCell(ColumnExcel.C);
				cell_C.setCellValue("Màu");
//				Cell cell_G = row.getCell(ColumnExcel.G);
//				Cell cell_K = row.getCell(ColumnExcel.K);
				Cell cell_O = row.getCell(ColumnExcel.O);
				cell_O.setCellValue("Bắt đầu");
				Cell cell_R = row.getCell(ColumnExcel.R);
				cell_R.setCellValue("Kết thúc");
				Cell cell_U = row.getCell(ColumnExcel.U);
				cell_U.setCellValue("Ghi chú");

				//
				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					cell.setCellStyle(cellStyle_Text_Medium_noBottom);
				}
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,2,5));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,6,9));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,10,13));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,14,16));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,17,19));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,20,22));

				//
				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					cell.setCellStyle(cellStyle_Text_Medium_noTop);
				}
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,2,5));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,6,9));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,10,13));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,14,16));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,17,19));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,20,22));

				// MUC KHOAN
				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
						cell.setCellValue("Mức khoán");
						cell.setCellStyle(cellStyle_Text_Medium_all_center);
					}else if(i == ColumnExcel.C) {
						cell.setCellValue("Ngày " + (i - 1));
						cell.setCellStyle(cellStyle_Text_Medium_noBottom);
					}else if(i == ColumnExcel.D) {
//						cell.setCellValue("Ngày " + (i - 1));
						cell.setCellStyle(cellStyle_Text_Medium_noBottom);
					}else if(i > ColumnExcel.D && i < ColumnExcel.X) {
						cell.setCellValue("Ngày " + (i - 2));
						cell.setCellStyle(cellStyle_Text_Medium_noBottom);
					}
				}
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,2,3));

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
//						cell.setCellValue("Mức khoán");
						cell.setCellStyle(cellStyle_Text_Medium_noBottom);
					}else if(i == ColumnExcel.C) {
						cell.setCellValue("Vào chuyền");
						cell.setCellStyle(cellStyle_Text_Medium_noTop);
					}else if(i > ColumnExcel.C && i < ColumnExcel.X) {
//						cell.setCellValue("Ngày " + (i - 1));
						cell.setCellStyle(cellStyle_Text_Medium_noTop);
					}
				}
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,2,3));

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
//						cell.setCellValue("Mức khoán");
						cell.setCellStyle(cellStyle_Text_Medium_noTop);
					}else if(i > ColumnExcel.B && i < ColumnExcel.X) {
//						cell.setCellValue("Ngày " + (i - 1));
						cell.setCellStyle(cellStyle_Text_Medium_all_center);
					}
				}

				// KY NHAN
				rowNumber=rowNumber+2;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.J) {
						cell.setCellValue("Ngày");
					}else if(i > ColumnExcel.J && i < ColumnExcel.O) {
						cell.setCellStyle(cellStyle_Ngay);
					}
				}
				sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,10,13));

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
						cell.setCellValue("Nơi nhận");
//						cell.setCellStyle(cellStyle_KyNhan);
					}else if(i == ColumnExcel.H) {
						cell.setCellValue("Lập biểu");
					}else if(i == ColumnExcel.N) {
						cell.setCellValue("Duyệt");
					}
				}

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
						cell.setCellValue("P.GĐ");
//						cell.setCellStyle(cellStyle_KyNhan);
					}else if(i == ColumnExcel.E) {
						cell.setCellValue("Tổ cắt");
					}
				}

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
						cell.setCellValue("Phòng KT");
//						cell.setCellStyle(cellStyle_KyNhan);
					}else if(i == ColumnExcel.E) {
						cell.setCellValue("Kho");
					}
				}

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
						cell.setCellValue("KTX");
//						cell.setCellStyle(cellStyle_KyNhan);
					}else if(i == ColumnExcel.E) {
						cell.setCellValue("H.Thiện");
					}
				}

				rowNumber=rowNumber+1;
				row = sheet.createRow(rowNumber);
				for(Integer i=1; i<ColumnExcel.X; i++) {
					Cell cell = row.createCell(i);
					if(i == ColumnExcel.B) {
						cell.setCellValue("KCS");
					}else if(i == ColumnExcel.E) {
						cell.setCellValue("Tổ may như trên");
					}else if(i == ColumnExcel.H) {
						// lap bieu
						cell.setCellValue(user.getFullname());
					}else if(i == ColumnExcel.M) {
						// duyet
						Long user_orgid_link = user.getOrgid_link();
						if(user_orgid_link != null) {
							Org user_org = orgService.findOne(user_orgid_link);
							String contactPerson = user_org.getContactperson();
							if(contactPerson != null) {
								cell.setCellValue(contactPerson);
							}
						}
					}
				}

				// tra file ve dang byte[]
				OutputStream outputstream = new FileOutputStream(file);
				workbook.write(outputstream);
				workbook.close();

				outputstream.close();

				InputStream file_download = new FileInputStream(file);
				response.data = IOUtils.toByteArray(file_download);
				file_download.close();

				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			} catch (Exception e) {
				e.printStackTrace();
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage(e.getMessage());
			} finally {
				workbook.close();
				FileCopy.delete();
				file.delete();
			}

			// response
//			response.data = result;
//			response.data = new ArrayList<PContract_PO>();

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<porder_report_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<porder_report_response>(response, HttpStatus.OK);
		}
	}
}
