package vn.gpay.gsmart.core.api.pcontract_po;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.gpay.gsmart.core.api.balance.PContract_PO_Response;
import vn.gpay.gsmart.core.api.pcontract.PContract_getbysearch_request;
import vn.gpay.gsmart.core.api.stockin.StockinByIDRequest;
import vn.gpay.gsmart.core.attribute.Attribute;
import vn.gpay.gsmart.core.attribute.IAttributeService;
import vn.gpay.gsmart.core.attributevalue.Attributevalue;
import vn.gpay.gsmart.core.attributevalue.IAttributeValueService;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.category.IShipModeService;
import vn.gpay.gsmart.core.category.ShipMode;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.packingtype.IPackingTypeService;
import vn.gpay.gsmart.core.packingtype.PackingType;
import vn.gpay.gsmart.core.pcontract_bom2_npl_poline.IPContract_bom2_npl_poline_Service;
import vn.gpay.gsmart.core.pcontract_bom2_npl_poline.PContract_bom2_npl_poline;
import vn.gpay.gsmart.core.pcontract_bom2_npl_poline_sku.IPContract_bom2_npl_poline_sku_Service;
import vn.gpay.gsmart.core.pcontract_bom2_npl_poline_sku.PContract_bom2_npl_poline_sku;
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.IPContract_PO_NoLink_Service;
import vn.gpay.gsmart.core.pcontract_po.PContractPO_Shipping;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontract_po.PContract_POBinding;
import vn.gpay.gsmart.core.pcontract_po.PContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO_NoLink;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO_Progress_Binding;
import vn.gpay.gsmart.core.pcontract_po_productivity.IPContract_PO_Productivity_Service;
import vn.gpay.gsmart.core.pcontract_po_productivity.PContract_PO_Productivity;
import vn.gpay.gsmart.core.pcontract_po_shipping.IPContract_PO_ShippingService;
import vn.gpay.gsmart.core.pcontract_po_shipping.PContract_PO_Shipping;
import vn.gpay.gsmart.core.pcontract_price.IPContract_Price_DService;
import vn.gpay.gsmart.core.pcontract_price.IPContract_Price_D_SKUService;
import vn.gpay.gsmart.core.pcontract_price.IPContract_Price_Service;
import vn.gpay.gsmart.core.pcontract_price.PContract_Price;
import vn.gpay.gsmart.core.pcontract_price.PContract_PriceFOB_Data;
import vn.gpay.gsmart.core.pcontract_price.PContract_Price_D;
import vn.gpay.gsmart.core.pcontract_price.PContract_Price_D_SKU;
import vn.gpay.gsmart.core.pcontractproduct.IPContractProductService;
import vn.gpay.gsmart.core.pcontractproduct.PContractProduct;
import vn.gpay.gsmart.core.pcontractproductpairing.IPContractProductPairingService;
import vn.gpay.gsmart.core.pcontractproductpairing.PContractProductPairing;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKUService;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.porder.IPOrder_Service;
import vn.gpay.gsmart.core.porder.POrder;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_SKUService;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_Service;
import vn.gpay.gsmart.core.porder_grant.POrderGrant;
import vn.gpay.gsmart.core.porder_grant.POrderGrant_SKU;
import vn.gpay.gsmart.core.porder_req.IPOrder_Req_Service;
import vn.gpay.gsmart.core.porder_req.POrder_Req;
import vn.gpay.gsmart.core.porderprocessing.IPOrderProcessing_Service;
import vn.gpay.gsmart.core.porderprocessing.POrderProcessing;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.productattributevalue.IProductAttributeService;
import vn.gpay.gsmart.core.productattributevalue.ProductAttributeValue;
import vn.gpay.gsmart.core.productpairing.IProductPairingService;
import vn.gpay.gsmart.core.productpairing.ProductPairing;
import vn.gpay.gsmart.core.productpairing.ProductPairingService;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.sizeset.ISizeSetService;
import vn.gpay.gsmart.core.sku.ISKU_AttributeValue_Service;
import vn.gpay.gsmart.core.sku.ISKU_Service;
import vn.gpay.gsmart.core.sku.SKU;
import vn.gpay.gsmart.core.sku.SKU_Attribute_Value;
import vn.gpay.gsmart.core.task_object.ITask_Object_Service;
import vn.gpay.gsmart.core.utils.AtributeFixValues;
import vn.gpay.gsmart.core.utils.ColumnPO;
import vn.gpay.gsmart.core.utils.ColumnTemplate;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.GPAYDateFormat;
import vn.gpay.gsmart.core.utils.OrgType;
import vn.gpay.gsmart.core.utils.POStatus;
import vn.gpay.gsmart.core.utils.POType;
import vn.gpay.gsmart.core.utils.POrderReqStatus;
import vn.gpay.gsmart.core.utils.POrderStatus;
import vn.gpay.gsmart.core.utils.ProductType;
import vn.gpay.gsmart.core.utils.ResponseMessage;
import vn.gpay.gsmart.core.warehouse.IWarehouseService;

@RestController
@RequestMapping("/api/v1/pcontract_po")
public class PContract_POAPI {
	@Autowired IWarehouseService warehouseService;
	@Autowired
	IAttributeService attrService;
	@Autowired
	IPContract_POService pcontract_POService;
	@Autowired
	IPContract_PO_NoLink_Service pcontract_PO_NoLink_Service;
	@Autowired
	IProductAttributeService pavService;
	@Autowired
	IPContract_Price_Service pcontractpriceService;
	@Autowired
	IPContract_Price_DService pcontractpriceDService;
	@Autowired
	IPContract_Price_D_SKUService pcontractpriceDSkuService;
	@Autowired
	IPOrder_Service porderService;
	@Autowired
	IPOrder_Req_Service porder_req_Service;
	@Autowired
	ISKU_AttributeValue_Service skuattService;
	@Autowired
	ISKU_Service skuService;
	@Autowired
	IPContract_PO_ShippingService poshippingService;
	@Autowired
	IPOrderProcessing_Service processService;
	@Autowired
	Common commonService;
	@Autowired
	ITask_Object_Service taskobjectService;
	@Autowired
	IProductPairingService productpairService;
	@Autowired
	IPOrder_Req_Service reqService;
	@Autowired
	IProductService productService;
	@Autowired
	IShipModeService shipmodeService;
	@Autowired
	IPContractProductService pcontractproductService;
	@Autowired
	IPContractProductPairingService pcontractpairService;
	@Autowired
	IPContract_Price_Service priceService;
	@Autowired
	ISizeSetService sizesetService;
	@Autowired
	IPContract_PO_Productivity_Service productivityService;
	@Autowired
	IPContract_Price_DService pricedetailService;
	@Autowired
	IOrgService orgService;
	@Autowired
	IGpayUserOrgService userOrgService;
	@Autowired
	IPackingTypeService packingService;
	@Autowired
	IAttributeValueService attributevalueService;
	@Autowired
	IPContractProductSKUService ppskuService;
	@Autowired
	IPOrderGrant_Service grantService;
	@Autowired
	IPOrderGrant_SKUService grantskuService;
	@Autowired
	IPContract_bom2_npl_poline_Service bomnplService;
	@Autowired
	IPContract_bom2_npl_poline_sku_Service bomnplskuService;
	@Autowired
	IProductPairingService pairService;
	@Autowired
	IPContractProductService pcontractProductService;
	@Autowired
	IPOrderGrant_Service porderGrantService;
	@Autowired
	IPOrderGrant_SKUService porderGrantSkuService;

	@RequestMapping(value = "/upload_template", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadTemplate(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, @RequestParam("pcontractid_link") long pcontractid_link) {
		ResponseBase response = new ResponseBase();

		Date current_time = new Date();
		String name = "";
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			String FolderPath = "upload/pcontract_po";

			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);

			File uploadRootDir = new File(uploadRootPath);
			// Tạo thư mục gốc upload nếu nó không tồn tại.
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}

			name = file.getOriginalFilename();
			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = current_time.getTime() + "." + extend;
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);

				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				// doc file upload
				XSSFWorkbook workbook = new XSSFWorkbook(serverFile);
				XSSFSheet sheet = workbook.getSheetAt(0);

				// Kiem tra header
				int rowNum = 1;
				int colNum = 1;

				String mes_err = "";
				Row row = sheet.getRow(rowNum);
				try {
					String STT = "";
					STT = commonService.getStringValue(row.getCell(ColumnTemplate.STT));
					STT = STT.equals("0") ? "" : STT;
					while (!STT.equals("")) {
						// Kiểm tra sản phẩm có chưa thì sinh id sản phẩm

						long productid_link = 0;
						colNum = ColumnTemplate.Style + 1;
						String product_code = commonService.getStringValue(row.getCell(ColumnTemplate.Style));

						if (product_code == "")
							break;

						colNum = ColumnTemplate.amount_po + 1;
						String s_product_quantity = commonService.getStringValue(row.getCell(ColumnTemplate.amount_po));
						s_product_quantity = s_product_quantity.replace(",", "");
						Float product_quantity = s_product_quantity == "" ? 0 : Float.parseFloat(s_product_quantity);

						colNum = ColumnTemplate.Style_Set + 1;
						String product_set_code = commonService.getStringValue(row.getCell(ColumnTemplate.Style_Set));
						product_set_code = product_set_code.equals("0") ? "" : product_set_code;

						colNum = ColumnTemplate.amount_style + 1;
						String s_amount = commonService.getStringValue(row.getCell(ColumnTemplate.amount_style));
						s_amount = s_amount.replace(",", "");
						int amount = (int) row.getCell(ColumnTemplate.amount_style).getNumericCellValue() == 0 ? 1
								: (int) row.getCell(ColumnTemplate.amount_style).getNumericCellValue();

						int po_quantity = product_quantity.intValue() / amount;

						colNum = ColumnTemplate.Style_name + 1;
						String stylename = commonService.getStringValue(row.getCell(ColumnTemplate.Style_name));

						List<Product> products = productService.getone_by_code(orgrootid_link, product_code, (long) 0,
								ProductType.SKU_TYPE_COMPLETEPRODUCT);
						if (products.size() == 0) {
							Product p = new Product();
							p.setBuyercode(product_code);
							p.setBuyername(stylename);
							p.setId(null);
							p.setOrgrootid_link(orgrootid_link);
							p.setStatus(1);
							p.setUsercreateid_link(user.getId());
							p.setTimecreate(current_time);
							p.setProducttypeid_link(ProductType.SKU_TYPE_COMPLETEPRODUCT);
							p = productService.save(p);

							productid_link = p.getId();

							// Sinh thuoc tinh mac dinh cho san pham
							List<Attribute> lstAttr = attrService.getList_attribute_forproduct(
									ProductType.SKU_TYPE_COMPLETEPRODUCT, user.getRootorgid_link());
							for (Attribute attribute : lstAttr) {
								ProductAttributeValue pav = new ProductAttributeValue();
								long value = 0;

								if (attribute.getId() == AtributeFixValues.ATTR_COLOR) {
									value = AtributeFixValues.value_color_all;
								} else if (attribute.getId() == AtributeFixValues.ATTR_SIZE) {
									value = AtributeFixValues.value_size_all;
								} else if (attribute.getId() == AtributeFixValues.ATTR_SIZEWIDTH) {
									value = AtributeFixValues.value_sizewidth_all;
								}

								pav.setId((long) 0);
								pav.setProductid_link(productid_link);
								pav.setAttributeid_link(attribute.getId());
								pav.setAttributevalueid_link(value);
								pav.setOrgrootid_link(user.getRootorgid_link());
								pavService.save(pav);
							}

							// Sinh SKU cho mau all va co all
							long skuid_link = 0;

							SKU sku = new SKU();
							sku.setId(skuid_link);
							sku.setCode(genCodeSKU(p));
							sku.setName(p.getBuyername());
							sku.setProductid_link(productid_link);
							sku.setOrgrootid_link(user.getRootorgid_link());
							sku.setSkutypeid_link(ProductType.SKU_TYPE_COMPLETEPRODUCT);

							sku = skuService.save(sku);
							skuid_link = sku.getId();

							// Them vao bang sku_attribute_value
							SKU_Attribute_Value savMau = new SKU_Attribute_Value();
							savMau.setId((long) 0);
							savMau.setAttributevalueid_link(AtributeFixValues.value_color_all);
							savMau.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
							savMau.setOrgrootid_link(user.getRootorgid_link());
							savMau.setSkuid_link(skuid_link);
							savMau.setUsercreateid_link(user.getId());
							savMau.setTimecreate(new Date());

							skuattService.save(savMau);

							SKU_Attribute_Value savCo = new SKU_Attribute_Value();
							savCo.setId((long) 0);
							savCo.setAttributevalueid_link(AtributeFixValues.value_size_all);
							savCo.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
							savCo.setOrgrootid_link(user.getRootorgid_link());
							savCo.setSkuid_link(skuid_link);
							savCo.setUsercreateid_link(user.getId());
							savCo.setTimecreate(new Date());

							skuattService.save(savCo);

						} else {
							productid_link = products.get(0).getId();
						}

						// Kiem tra xem co phai PO cua hang bo hay khong
						long product_set_id_link = 0;
						if (!product_set_code.equals(null) && !product_set_code.equals("")) {
							List<Product> product_set = productService.getone_by_code(orgrootid_link, product_set_code,
									(long) 0, ProductType.SKU_TYPE_PRODUCT_PAIR);
							if (product_set.size() == 0) {
								Product set = new Product();
								set.setId(null);
								set.setBuyercode(product_set_code);
								set.setBuyername(amount + "-" + product_set_code);
								set.setDescription(amount + "-" + product_set_code);
								set.setOrgrootid_link(orgrootid_link);
								set.setStatus(1);
								set.setUsercreateid_link(user.getId());
								set.setTimecreate(current_time);
								set.setProducttypeid_link(ProductType.SKU_TYPE_PRODUCT_PAIR);
								set = productService.save(set);

								product_set_id_link = set.getId();
							} else {
								product_set_id_link = product_set.get(0).getId();
							}
						}

						// kiem tra trong bang productpair co chua thi them bo vao
						if (product_set_id_link > 0) {
							ProductPairing pair = productpairService.getproduct_pairing_bykey(productid_link,
									product_set_id_link);
							if (pair == null) {
								ProductPairing newpair = new ProductPairing();
								newpair.setAmount(amount);
								newpair.setId(null);
								newpair.setOrgrootid_link(orgrootid_link);
								newpair.setProductid_link(productid_link);
								newpair.setProductpairid_link(product_set_id_link);
								productpairService.save(newpair);

							}
						}

						// Them san pham vao trong pcontract
						List<PContractProduct> list_product = pcontractproductService
								.get_by_product_and_pcontract(orgrootid_link, productid_link, pcontractid_link);
						if (list_product.size() == 0) {
							PContractProduct product = new PContractProduct();
							product.setIs_breakdown_done(false);
							product.setIsbom2done(false);
							product.setIsbomdone(false);
							product.setOrgrootid_link(orgrootid_link);
							product.setPcontractid_link(pcontractid_link);
							product.setProductid_link(productid_link);
							product.setId(null);
							pcontractproductService.save(product);
						}

						// them bo vao trong pcontract
						if (product_set_id_link > 0) {
							List<PContractProductPairing> list_pair = pcontractpairService
									.getdetail_bypcontract_and_productpair(orgrootid_link, pcontractid_link,
											product_set_id_link);
							if (list_pair.size() == 0) {
								PContractProductPairing pair = new PContractProductPairing();
								pair.setId(null);
								pair.setOrgrootid_link(orgrootid_link);
								pair.setPcontractid_link(pcontractid_link);
								pair.setProductpairid_link(product_set_id_link);
								pcontractpairService.save(pair);
							}
						}

						long shipmodeid_link = 0;
						colNum = ColumnTemplate.shipmode + 1;
						String shipmode_name = row.getCell(ColumnTemplate.shipmode).getStringCellValue();
						List<ShipMode> shipmode = shipmodeService.getbyname(shipmode_name);
						if (shipmode.size() > 0) {
							shipmodeid_link = shipmode.get(0).getId();
						}

						// Kiem tra chao gia da ton tai hay chua
						colNum = ColumnTemplate.PO + 1;
						String PO_No = commonService.getStringValue(row.getCell(ColumnTemplate.PO));
						if (PO_No == "" || PO_No.equals("0")) {
							PO_No = "TBD";
						}

						colNum = ColumnTemplate.shipdate + 1;
						Date ShipDate = null;

						try {
							String s_shipdate = commonService.getStringValue(row.getCell(ColumnTemplate.shipdate));
							if (s_shipdate.contains("/")) {
								String[] s_date = s_shipdate.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									ShipDate = new SimpleDateFormat("dd/MM/yyyy").parse(s_shipdate);
								} else {
									mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
								}

							} else {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnTemplate.shipdate))) {
									ShipDate = row.getCell(ColumnTemplate.shipdate).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnTemplate.shipdate))) {
								ShipDate = row.getCell(ColumnTemplate.shipdate).getDateCellValue();
							}
						}

						if (ShipDate == null) {
							throw new Exception();
						}

						long po_productid_link = product_set_id_link > 0 ? product_set_id_link : productid_link;
						long pcontractpo_id_link = 0;

//						colNum = ColumnTemplate.cmp + 1;
//						String s_price_cmp = commonService.getStringValue(row.getCell(ColumnTemplate.cmp));
//						s_price_cmp = s_price_cmp.replace(",", "");
//						float price_cmp = s_price_cmp.equals("") ? 0 : Float.parseFloat(s_price_cmp);

						colNum = ColumnTemplate.fob + 1;
						String s_price_fob = commonService.getStringValue(row.getCell(ColumnTemplate.fob));
						s_price_fob = s_price_fob.replace(",", "");
						float price_fob = s_price_fob.equals("") ? 0 : Float.parseFloat(s_price_fob);

						colNum = ColumnTemplate.vendor_target + 1;
						String s_vendor_target = commonService
								.getStringValue(row.getCell(ColumnTemplate.vendor_target));
						s_vendor_target = s_vendor_target.replace(",", "");
						float vendor_target = s_vendor_target == "" ? 0 : Float.parseFloat(s_vendor_target);

						colNum = ColumnTemplate.org + 1;
						String s_org_code = commonService.getStringValue(row.getCell(ColumnTemplate.org));
						s_org_code = s_org_code.replace(",", "");
						Long orgid_link = null;
						List<Org> list_org = orgService.getbycode(s_org_code, orgrootid_link);
						if (list_org.size() > 0) {
							orgid_link = list_org.get(0).getId();
						}

						colNum = ColumnTemplate.matdate + 1;
						Date matdate = null;

						try {
							String s_matdate = commonService.getStringValue(row.getCell(ColumnTemplate.matdate));
							if (s_matdate.contains("/")) {
								String[] s_date = s_matdate.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									matdate = new SimpleDateFormat("dd/MM/yyyy").parse(s_matdate);
								} else {
									mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
								}

							} else {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnTemplate.matdate))) {
									matdate = row.getCell(ColumnTemplate.matdate).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnTemplate.matdate))) {
								matdate = row.getCell(ColumnTemplate.matdate).getDateCellValue();
							}
						}

						if (matdate == null) {
							throw new Exception();
						}

						Date production_date = Common.Date_Add(matdate, 7);
						int production_day = commonService.getDuration(production_date, ShipDate, orgrootid_link);

						List<PContract_PO> listpo = new ArrayList<PContract_PO>();
						if (product_set_id_link == 0) {
							listpo = pcontract_POService.check_exist_po(ShipDate, po_productid_link, shipmodeid_link,
									pcontractid_link, PO_No);
						} else {
							listpo = pcontract_POService.check_exist_po(ShipDate, po_productid_link, shipmodeid_link,
									pcontractid_link, "");
						}

						if (listpo.size() > 0) {
							pcontractpo_id_link = listpo.get(0).getId();
							PContract_PO po = listpo.get(0);
							po.setProductiondate(production_date);
							po.setProductiondays(production_day);
							po.setMatdate(matdate);
							pcontract_POService.save(po);
						}

						if (pcontractpo_id_link == 0) {
							Float price_cmp = null;
							Float price_cmp_total = (float) 0;
							int count = 0;

							PContract_PO po_new = new PContract_PO();
							po_new.setId(null);
							po_new.setCode(PO_No);
							po_new.setCurrencyid_link((long) 1);
							po_new.setDatecreated(current_time);
							po_new.setIs_tbd(PO_No == "TBD" ? true : false);
							po_new.setIsauto_calculate(true);
							po_new.setOrgrootid_link(orgrootid_link);
							po_new.setPcontractid_link(pcontractid_link);
							po_new.setPo_buyer(PO_No);
							po_new.setPo_vendor(PO_No);
							po_new.setPo_quantity(po_quantity);
							po_new.setProductid_link(po_productid_link);
							po_new.setShipdate(ShipDate);
							po_new.setShipmodeid_link(shipmodeid_link);
							po_new.setStatus(POStatus.PO_STATUS_PROBLEM);
							po_new.setUsercreatedid_link(user.getId());
							po_new.setDate_importdata(current_time);
							po_new.setProductiondate(production_date);
							po_new.setProductiondays(production_day);
							po_new.setMatdate(matdate);

							po_new = pcontract_POService.save(po_new);
							pcontractpo_id_link = po_new.getId();

							// kiem tra porder_req ton tai chua thi them vao
							if (orgid_link != null) {
								List<POrder_Req> list_req = reqService.getByOrg_PO_Product(pcontractpo_id_link,
										productid_link, orgid_link);
								if (list_req.size() == 0) {
									POrder_Req porder_req = new POrder_Req();
									porder_req.setAmount_inset(amount);
									porder_req.setGranttoorgid_link(orgid_link);
									porder_req.setId(null);
									porder_req.setIs_calculate(true);
									porder_req.setOrgrootid_link(orgrootid_link);
									porder_req.setPcontract_poid_link(pcontractpo_id_link);
									porder_req.setPcontractid_link(pcontractid_link);
									porder_req.setProductid_link(productid_link);
									porder_req.setStatus(POrderReqStatus.STATUS_FREE);
									porder_req.setTotalorder(po_quantity);
									reqService.save(porder_req);
								}

							}

							for (int i = ColumnTemplate.infant; i <= ColumnTemplate.plus; i++) {
								colNum = i + 1;
								Row row_header = sheet.getRow(0);
								String sizesetname = commonService.getStringValue(row_header.getCell(i));
								String s_amount_sizeset = commonService.getStringValue(row.getCell(i));
								s_amount_sizeset = s_amount_sizeset.replace(",", "");
								Float amount_sizeset = s_amount_sizeset.equals("") ? 0
										: Float.parseFloat(s_amount_sizeset);

								Long sizesetid_link = sizesetService.getbyname(sizesetname);
								if (amount_sizeset > 0) {
									count++;
									price_cmp_total += amount_sizeset;
									PContract_Price price = new PContract_Price();
									price.setId(null);
									price.setIs_fix(false);
									price.setOrgrootid_link(orgrootid_link);
									price.setPcontract_poid_link(po_new.getId());
									price.setPcontractid_link(pcontractid_link);
									price.setProductid_link(productid_link);
									price.setSizesetid_link(sizesetid_link == null ? 0 : sizesetid_link);
									price.setDate_importdata(current_time);
									price.setPrice_cmp(amount_sizeset);
									price.setTotalprice(amount_sizeset);
									price = priceService.save(price);

									// Them detail cho dai co
									PContract_Price_D price_detail_sizeset = new PContract_Price_D();
									price_detail_sizeset.setOrgrootid_link(orgrootid_link);
									price_detail_sizeset.setFobpriceid_link((long) 1);
									price_detail_sizeset.setPrice(amount_sizeset);
									price_detail_sizeset.setIsfob(false);
									price_detail_sizeset.setDatecreated(current_time);
									price_detail_sizeset.setId(null);
									price_detail_sizeset.setSizesetid_link((long) 1);
									price_detail_sizeset.setPcontract_poid_link(pcontractpo_id_link);
									price_detail_sizeset.setPcontractid_link(pcontractid_link);
									price_detail_sizeset.setPcontractpriceid_link(price.getId());
									price_detail_sizeset.setProductid_link(productid_link);
									pricedetailService.save(price_detail_sizeset);
								}
							}

							// Tinh gia cmp = trung binh gia cua cac dai co
							if (count == 0)
								price_cmp = (float) 0;
							else {
								price_cmp = price_cmp_total / count;
								DecimalFormat df = new DecimalFormat("#.###");
								String formatted = df.format(price_cmp);
								price_cmp = Float.parseFloat(formatted);
							}

							// Them cho san pham con
							PContract_Price price_all = new PContract_Price();
							price_all.setId(null);
							price_all.setIs_fix(true);
							price_all.setOrgrootid_link(orgrootid_link);
							price_all.setPcontract_poid_link(pcontractpo_id_link);
							price_all.setPcontractid_link(pcontractid_link);
							price_all.setPrice_vendortarget(vendor_target);
							price_all.setPrice_cmp(price_cmp);
							price_all.setPrice_fob(price_fob);
							price_all.setTotalprice(price_cmp + price_fob);
							price_all.setProductid_link(productid_link);
							price_all.setQuantity(product_quantity.intValue());
							price_all.setSizesetid_link(sizesetService.getbyname("ALL"));
							price_all.setDate_importdata(current_time);
							price_all = priceService.save(price_all);

							// Them detail cho dai co All
							PContract_Price_D price_detail_all = new PContract_Price_D();
							price_detail_all.setOrgrootid_link(orgrootid_link);
							price_detail_all.setFobpriceid_link((long) 1);
							price_detail_all.setPrice(price_cmp);
							price_detail_all.setIsfob(false);
							price_detail_all.setDatecreated(current_time);
							price_detail_all.setId(null);
							price_detail_all.setSizesetid_link((long) 1);
							price_detail_all.setPcontract_poid_link(pcontractpo_id_link);
							price_detail_all.setPcontractid_link(pcontractid_link);
							price_detail_all.setPcontractpriceid_link(price_all.getId());
							price_detail_all.setProductid_link(productid_link);
							pricedetailService.save(price_detail_all);

							// Them all cho bo
							if (product_set_id_link > 0) {
								PContract_Price price_all_set = new PContract_Price();
								price_all_set.setId(null);
								price_all_set.setIs_fix(true);
								price_all_set.setOrgrootid_link(orgrootid_link);
								price_all_set.setPcontract_poid_link(po_new.getId());
								price_all_set.setPcontractid_link(pcontractid_link);
								price_all_set.setPrice_vendortarget(vendor_target);
								price_all_set.setPrice_cmp(price_cmp);
								price_all_set.setPrice_fob(price_fob);
								price_all_set.setTotalprice(price_fob + price_cmp);
								price_all_set.setProductid_link(product_set_id_link);
								price_all_set.setQuantity(po_quantity);
								price_all_set.setSizesetid_link(sizesetService.getbyname("ALL"));
								price_all_set.setDate_importdata(current_time);
								price_all_set = priceService.save(price_all_set);

								// Them detai price cho dai co All
								PContract_Price_D price_detail = new PContract_Price_D();
								price_detail.setOrgrootid_link(orgrootid_link);
								price_detail.setFobpriceid_link((long) 1);
								price_detail.setPrice(price_cmp);
								price_detail.setIsfob(false);
								price_detail.setDatecreated(current_time);
								price_detail.setId(null);
								price_detail.setSizesetid_link((long) 1);
								price_detail.setPcontract_poid_link(pcontractpo_id_link);
								price_detail.setPcontractid_link(pcontractid_link);
								price_detail.setPcontractpriceid_link(price_all_set.getId());
								price_detail.setProductid_link(product_set_id_link);
								pricedetailService.save(price_detail);

								//
								for (int i = ColumnTemplate.infant; i <= ColumnTemplate.plus; i++) {
									colNum = i + 1;
									Row row_header = sheet.getRow(0);
									String sizesetname = commonService.getStringValue(row_header.getCell(i));
									String s_amount_sizeset = commonService.getStringValue(row.getCell(i));
									s_amount_sizeset = s_amount_sizeset.replace(",", "");
									Float amount_sizeset = s_amount_sizeset.equals("") ? 0
											: Float.parseFloat(s_amount_sizeset);
									Long sizesetid_link = sizesetService.getbyname(sizesetname);

									if (amount_sizeset > 0) {
										PContract_Price price = new PContract_Price();
										price.setId(null);
										price.setIs_fix(false);
										price.setOrgrootid_link(orgrootid_link);
										price.setPcontract_poid_link(po_new.getId());
										price.setPcontractid_link(pcontractid_link);
										price.setProductid_link(product_set_id_link);
										price.setSizesetid_link(sizesetid_link == null ? 0 : sizesetid_link);
										price.setDate_importdata(current_time);
										price.setPrice_cmp(amount_sizeset);
										price.setTotalprice(amount_sizeset);
										price = priceService.save(price);

										// Them detail
										PContract_Price_D price_sizeset = new PContract_Price_D();
										price_sizeset.setOrgrootid_link(orgrootid_link);
										price_sizeset.setFobpriceid_link((long) 1);
										price_sizeset.setPrice(amount_sizeset);
										price_sizeset.setIsfob(false);
										price_sizeset.setId(null);
										price_sizeset.setSizesetid_link(sizesetid_link == null ? 0 : sizesetid_link);
										price_sizeset.setPcontract_poid_link(pcontractpo_id_link);
										price_sizeset.setPcontractid_link(pcontractid_link);
										price_sizeset.setPcontractpriceid_link(price.getId());
										price_sizeset.setProductid_link(product_set_id_link);
										pricedetailService.save(price_sizeset);
									}
								}
							}

						}
						// truong hop hang bo po da co roi
						else {
							Float price_cmp = null;
							Float price_cmp_total = (float) 0;
							int count = 0;

							// neu la hang bo thi them khong thi thoi trung coi nhu bo qua khong xu ly
							if (product_set_id_link > 0) {
								// Kiem tra dai co san pham con da co hay chua
								List<PContract_Price> list_price = priceService.getPrice_by_product(pcontractpo_id_link,
										productid_link);

								//
								for (int i = ColumnTemplate.infant; i <= ColumnTemplate.plus; i++) {
									colNum = i + 1;
									Row row_header = sheet.getRow(0);
//									String sizesetname = row_header.getCell(i).getStringCellValue();
//									int amount_sizeset = (int) row.getCell(i).getNumericCellValue();

									String sizesetname = commonService.getStringValue(row_header.getCell(i));
									String s_amount_sizeset = commonService.getStringValue(row.getCell(i));
									s_amount_sizeset = s_amount_sizeset.replace(",", "");
									Float amount_sizeset = s_amount_sizeset.equals("") ? 0
											: Float.parseFloat(s_amount_sizeset);
									Long sizesetid_link = sizesetService.getbyname(sizesetname);

									if (amount_sizeset > 0) {
										price_cmp_total += amount_sizeset;
										count++;
										// kiem tra xem dai co co du lieu chua!
										if (list_price.size() == 0) {
											PContract_Price price = new PContract_Price();
											price.setId(null);
											price.setIs_fix(false);
											price.setOrgrootid_link(orgrootid_link);
											price.setPcontract_poid_link(pcontractpo_id_link);
											price.setPcontractid_link(pcontractid_link);
											price.setProductid_link(productid_link);
											price.setSizesetid_link(sizesetid_link);
											price.setDate_importdata(current_time);
											price.setPrice_cmp(amount_sizeset);
											price.setTotalprice(amount_sizeset);
											price = priceService.save(price);

											// Them detail
											PContract_Price_D price_detail = new PContract_Price_D();
											price_detail.setOrgrootid_link(orgrootid_link);
											price_detail.setFobpriceid_link((long) 1);
											price_detail.setPrice(amount_sizeset);
											price_detail.setIsfob(false);
											price_detail.setId(null);
											price_detail.setSizesetid_link(sizesetid_link);
											price_detail.setPcontract_poid_link(pcontractpo_id_link);
											price_detail.setPcontractid_link(pcontractid_link);
											price_detail.setPcontractpriceid_link(price.getId());
											price_detail.setProductid_link(productid_link);
											pricedetailService.save(price_detail);

//											PContract_Price _price_set = new PContract_Price();
//											_price_set.setId(null);
//											_price_set.setIs_fix(false);
//											_price_set.setOrgrootid_link(orgrootid_link);
//											_price_set.setPcontract_poid_link(pcontractpo_id_link);
//											_price_set.setPcontractid_link(pcontractid_link);
//											_price_set.setQuantity(amount_sizeset);
//											_price_set.setSizesetid_link(sizesetid_link);
//											_price_set.setDate_importdata(current_time);
//											_price_set.setProductid_link(product_set_id_link);
//											_price_set.setPrice_cmp(price_cmp);
//											_price_set = priceService.save(_price_set);
//											
//											//Them detail
//											PContract_Price_D price_detail_set = new PContract_Price_D();
//											price_detail_set.setOrgrootid_link(orgrootid_link);
//											price_detail_set.setFobpriceid_link((long)1);
//											price_detail_set.setPrice(price_cmp);
//											price_detail_set.setIsfob(false);
//											price_detail_set.setId(null);
//											price_detail_set.setSizesetid_link(sizesetid_link);
//											price_detail_set.setPcontract_poid_link(pcontractpo_id_link);
//											price_detail_set.setPcontractid_link(pcontractid_link);
//											price_detail_set.setPcontractpriceid_link(_price_set.getId());
//											price_detail_set.setProductid_link(productid_link);
//											pricedetailService.save(price_detail_set);
										} else {
											List<PContract_Price> list_price_old = new ArrayList<PContract_Price>(
													list_price);
											list_price_old.removeIf(c -> !c.getSizesetid_link().equals(sizesetid_link));
											if (list_price_old.size() == 0) {
												PContract_Price price = new PContract_Price();
												price.setId(null);
												price.setIs_fix(false);
												price.setOrgrootid_link(orgrootid_link);
												price.setPcontract_poid_link(pcontractpo_id_link);
												price.setPcontractid_link(pcontractid_link);
												price.setProductid_link(productid_link);
												price.setSizesetid_link(sizesetid_link);
												price.setDate_importdata(current_time);
												price.setPrice_cmp(amount_sizeset);
												price.setTotalprice(price_cmp);
												price = priceService.save(price);

												// Them detail
												PContract_Price_D price_detail = new PContract_Price_D();
												price_detail.setOrgrootid_link(orgrootid_link);
												price_detail.setFobpriceid_link((long) 1);
												price_detail.setPrice(amount_sizeset);
												price_detail.setIsfob(false);
												price_detail.setId(null);
												price_detail.setSizesetid_link(sizesetid_link);
												price_detail.setPcontract_poid_link(pcontractpo_id_link);
												price_detail.setPcontractid_link(pcontractid_link);
												price_detail.setPcontractpriceid_link(price.getId());
												price_detail.setProductid_link(productid_link);
												pricedetailService.save(price_detail);

												PContract_Price price_set = new PContract_Price();
												price_set.setId(null);
												price_set.setIs_fix(false);
												price_set.setOrgrootid_link(orgrootid_link);
												price_set.setPcontract_poid_link(pcontractpo_id_link);
												price_set.setPcontractid_link(pcontractid_link);
												price_set.setProductid_link(product_set_id_link);
												price_set.setSizesetid_link(sizesetid_link);
												price_set.setDate_importdata(current_time);
												price_set.setPrice_cmp(price_cmp);
												price_set.setTotalprice(price_cmp);
												price_set = priceService.save(price_set);

												// Them detail
												PContract_Price_D price_detail_set = new PContract_Price_D();
												price_detail_set.setOrgrootid_link(orgrootid_link);
												price_detail_set.setFobpriceid_link((long) 1);
												price_detail_set.setPrice(price_cmp);
												price_detail_set.setIsfob(false);
												price_detail_set.setId(null);
												price_detail_set.setSizesetid_link(sizesetid_link);
												price_detail_set.setPcontract_poid_link(pcontractpo_id_link);
												price_detail_set.setPcontractid_link(pcontractid_link);
												price_detail_set.setPcontractpriceid_link(price_set.getId());
												price_detail_set.setProductid_link(productid_link);
												pricedetailService.save(price_detail_set);

											}
										}
									}
								}
								if (count == 0)
									price_cmp = (float) 0;
								else {
									price_cmp = price_cmp_total / count;
									DecimalFormat df = new DecimalFormat("#.###");
									String formatted = df.format(price_cmp);
									price_cmp = Float.parseFloat(formatted);
								}

								// them dai co vao san pham con
								if (list_price.size() == 0) {
									PContract_Price price_all_set = new PContract_Price();
									price_all_set.setId(null);
									price_all_set.setIs_fix(false);
									price_all_set.setOrgrootid_link(orgrootid_link);
									price_all_set.setPcontract_poid_link(pcontractpo_id_link);
									price_all_set.setPcontractid_link(pcontractid_link);
									price_all_set.setPrice_vendortarget(vendor_target);
									price_all_set.setPrice_cmp(price_cmp);
									price_all_set.setPrice_fob(price_fob);
									price_all_set.setTotalprice(price_cmp + price_fob);
									price_all_set.setProductid_link(productid_link);
									price_all_set.setQuantity(product_quantity.intValue());
									price_all_set.setSizesetid_link(sizesetService.getbyname("ALL"));
									price_all_set.setDate_importdata(current_time);
									price_all_set = priceService.save(price_all_set);

									// Them detail
									PContract_Price_D price_detail = new PContract_Price_D();
									price_detail.setOrgrootid_link(orgrootid_link);
									price_detail.setFobpriceid_link((long) 1);
									price_detail.setPrice(price_cmp);
									price_detail.setIsfob(false);
									price_detail.setId(null);
									price_detail.setSizesetid_link(sizesetService.getbyname("ALL"));
									price_detail.setPcontract_poid_link(pcontractpo_id_link);
									price_detail.setPcontractid_link(pcontractid_link);
									price_detail.setPcontractpriceid_link(price_all_set.getId());
									price_all_set.setProductid_link(productid_link);
									pricedetailService.save(price_detail);
								}

								// Cap nhat gia len san pham cha
								List<PContract_Price> list_price_set = priceService
										.getPrice_by_product(pcontractpo_id_link, product_set_id_link);
								if (list_price_set.size() > 0) {
									PContract_Price price_parent = list_price_set.get(0);
									price_parent.setPrice_cmp(price_parent.getPrice_cmp() + price_cmp);
								}
							} else {
								if (orgid_link != null) {
									List<POrder_Req> list_req = reqService.getByOrg_PO_Product(pcontractpo_id_link,
											productid_link, orgid_link);
									if (list_req.size() == 0) {
										POrder_Req porder_req = new POrder_Req();
										porder_req.setAmount_inset(amount);
										porder_req.setGranttoorgid_link(orgid_link);
										porder_req.setId(null);
										porder_req.setIs_calculate(true);
										porder_req.setOrgrootid_link(orgrootid_link);
										porder_req.setPcontract_poid_link(pcontractpo_id_link);
										porder_req.setPcontractid_link(pcontractid_link);
										porder_req.setProductid_link(productid_link);
										porder_req.setStatus(POrderReqStatus.STATUS_FREE);
										porder_req.setTotalorder(po_quantity);
										reqService.save(porder_req);
									}

								}
								// Hàng đơn chiếc thì cập nhật dải cỡ
								for (int i = ColumnTemplate.infant; i <= ColumnTemplate.plus; i++) {
									colNum = i + 1;
									Row row_header = sheet.getRow(0);
									String s_amount_sizeset = commonService.getStringValue(row.getCell(i));
									s_amount_sizeset = s_amount_sizeset.replace(",", "");
									Float amount_sizeset = s_amount_sizeset.equals("") ? 0
											: Float.parseFloat(s_amount_sizeset);
//									int amount_sizeset = (int) row.getCell(i).getNumericCellValue();
									if (amount_sizeset > 0) {
										price_cmp_total += amount_sizeset;
										count++;
										String sizesetname = commonService.getStringValue(row_header.getCell(i));
										Long sizesetid_link = sizesetService.getbyname(sizesetname);
										List<PContract_Price> list_price = priceService.getPrice_by_product_and_sizeset(
												pcontractpo_id_link, productid_link, sizesetid_link);
										for (PContract_Price pContract_Price : list_price) {
											priceService.delete(pContract_Price);
										}

										PContract_Price price = new PContract_Price();
										price.setDate_importdata(current_time);
										price.setId(null);
										price.setIs_fix(false);
										price.setOrgrootid_link(orgrootid_link);
										price.setPcontract_poid_link(pcontractpo_id_link);
										price.setPcontractid_link(pcontractid_link);
										price.setProductid_link(productid_link);
										price.setSizesetid_link(sizesetid_link);
										price.setPrice_cmp(amount_sizeset);
										price.setTotalprice(amount_sizeset);
										priceService.save(price);

										PContract_Price_D price_detail = new PContract_Price_D();
										price_detail.setOrgrootid_link(orgrootid_link);
										price_detail.setFobpriceid_link((long) 1);
										price_detail.setPrice(amount_sizeset);
										price_detail.setIsfob(false);
										price_detail.setId(null);
										price_detail.setSizesetid_link(sizesetid_link);
										price_detail.setPcontract_poid_link(pcontractpo_id_link);
										price_detail.setPcontractid_link(pcontractid_link);
										price_detail.setPcontractpriceid_link(price.getId());
										price_detail.setProductid_link(productid_link);
										pricedetailService.save(price_detail);
									}
								}

								// tinh gia cmp theo trung binh cua cac dai co
								if (count == 0)
									price_cmp = (float) 0;
								else {
									price_cmp = price_cmp_total / count;
									DecimalFormat df = new DecimalFormat("#.###");
									String formatted = df.format(price_cmp);
									price_cmp = Float.parseFloat(formatted);
								}

								// cap nhat lai gia cmp cua san pham ( dai co all)
								List<PContract_Price> prices = priceService
										.getPrice_by_product_and_sizeset(pcontractpo_id_link, productid_link, (long) 1);
								if (prices.size() > 0) {
									PContract_Price price = prices.get(0);
									price.setPrice_cmp(price_cmp);
									price.setTotalprice(price_cmp);
									priceService.save(price);

									List<PContract_Price_D> price_details = pricedetailService
											.getPrice_D_ByFobPriceAndPContractPrice(price.getId(), (long) 1);
									if (price_details.size() > 0) {
										PContract_Price_D price_detail = price_details.get(0);
										price_detail.setPrice(price_cmp);
										pricedetailService.save(price_detail);
									} else {
										PContract_Price_D price_detail = new PContract_Price_D();
										price_detail.setOrgrootid_link(orgrootid_link);
										price_detail.setFobpriceid_link((long) 1);
										price_detail.setPrice(price_cmp);
										price_detail.setIsfob(false);
										price_detail.setId(null);
										price_detail.setSizesetid_link((long) 1);
										price_detail.setPcontract_poid_link(pcontractpo_id_link);
										price_detail.setPcontractid_link(pcontractid_link);
										price_detail.setPcontractpriceid_link(price.getId());
										price_detail.setProductid_link(productid_link);
										pricedetailService.save(price_detail);
									}
								}

							}
						}

						// Tu sinh PO con neu po da xac nhan
						colNum = ColumnTemplate.status + 1;
						String s_status = commonService.getStringValue(row.getCell(ColumnTemplate.status));
						if (!s_status.equals("")) {
							PContract_PO po = pcontract_POService.findOne(pcontractpo_id_link);
							po.setStatus(POStatus.PO_STATUS_CONFIRMED);
							po.setOrgmerchandiseid_link(orgid_link);
							pcontract_POService.save(po);
//							
//							Long pcontractpo_chil_id_link = null;
//							
//							List<PContract_PO> list_po_chil = pcontract_POService.get_by_parentid(pcontractpo_id_link);
//							if(list_po_chil.size() == 0) {
//								PContract_PO po_chil = new PContract_PO();
//								po_chil.setCode(PO_No);
//								po_chil.setDate_importdata(current_time);
//								po_chil.setId(null);
//								po_chil.setIs_tbd(false);
//								po_chil.setMatdate(matdate);
//								po_chil.setOrgmerchandiseid_link(orgid_link);
//								po_chil.setOrgrootid_link(orgrootid_link);
//								po_chil.setParentpoid_link(pcontractpo_id_link);
//								po_chil.setPcontractid_link(pcontractid_link);
//								po_chil.setPo_buyer(PO_No);
//								po_chil.setPo_vendor(PO_No);
//								po_chil.setPo_quantity(po.getPo_quantity());
//								po_chil.setProductid_link(po.getProductid_link());
//								po_chil.setProductiondate(production_date);
//								po_chil.setProductiondays(production_day);
//								po_chil.setShipdate(ShipDate);
//								po_chil.setShipmodeid_link(shipmodeid_link);
//								po_chil.setStatus(POStatus.PO_STATUS_CONFIRMED);
//								po_chil = pcontract_POService.save(po_chil);
//								
//								pcontractpo_chil_id_link = po_chil.getId();
//							}
//							else {
//								pcontractpo_chil_id_link = list_po_chil.get(0).getId();
//							}
//							
//							//Kiem tra porder_req cua po chil co chua
//							List<POrder_Req> list_req_chil = reqService.getByOrg_PO_Product(pcontractpo_chil_id_link, po.getProductid_link(), orgid_link);
//							if(list_req_chil.size() == 0) {
//								POrder_Req porder_req = new POrder_Req();
//								porder_req.setAmount_inset(amount);
//								porder_req.setGranttoorgid_link(orgid_link);
//								porder_req.setId(null);
//								porder_req.setIs_calculate(true);
//								porder_req.setOrgrootid_link(orgrootid_link);
//								porder_req.setPcontract_poid_link(pcontractpo_chil_id_link);
//								porder_req.setPcontractid_link(pcontractid_link);
//								porder_req.setProductid_link(productid_link);
//								porder_req.setStatus(POrderReqStatus.STATUS_FREE);
//								porder_req.setTotalorder(po_quantity);
//								reqService.save(porder_req);
//							}

							// Lay danh sach cac porder_req cua po
							List<POrder_Req> list_req = reqService.getByContractAndPO(pcontractid_link,
									pcontractpo_id_link);
							for (POrder_Req req : list_req) {
								porderService.createPOrder(req, user);
							}
						}

						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						STT = commonService.getStringValue(row.getCell(ColumnTemplate.STT));
						STT = STT.equals("0") ? "" : STT;
					}
				} catch (Exception e) {
					mes_err = "Có lỗi ở dòng " + (rowNum + 1) + " và cột " + colNum;
				} finally {
					workbook.close();
					serverFile.delete();
				}

				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Có lỗi trong quá trình upload! Bạn hãy thử lại");
			}
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/upload_po", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPO(HttpServletRequest request, @RequestParam("file") MultipartFile file,
			@RequestParam("parentid_link") long parentid_link,
			@RequestParam("pcontractid_link") long pcontractid_link) {
		ResponseBase response = new ResponseBase();

		Date current_time = new Date();
		String name = "";
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			PContract_PO parent = pcontract_POService.findOne(parentid_link);
			String FolderPath = "upload/pcontract_po";

			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);

			File uploadRootDir = new File(uploadRootPath);
			// Tạo thư mục gốc upload nếu nó không tồn tại.
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}

			name = file.getOriginalFilename();
			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = current_time.getTime() + "." + extend;
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);

				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				// doc file upload
				XSSFWorkbook workbook = new XSSFWorkbook(serverFile);
				XSSFSheet sheet = workbook.getSheetAt(0);

				// Kiem tra header
				int rowNum = 1;
				int colNum = 0;
				int amount_po = 0;
				String mes_err = "";
				String mes_err_addition = "";
				Row rowheader = sheet.getRow(0);
				Row row = sheet.getRow(rowNum);
				try {
					// kiểm tra file có column Mã phụ hay không
					String stringJColumn = commonService.getStringValue(rowheader.getCell(ColumnPO.CodeExtra));
					Boolean isColumnCodeExtra = stringJColumn.trim().equals("Mã phụ") ? true : false;
					
					//
					String STT = "";
					STT = commonService.getStringValue(row.getCell(ColumnPO.STT));
					STT = STT.equals("0") ? "" : STT;
					while (!STT.equals("")) {
//						String a  = commonService.getStringValue(row.getCell(ColumnPO.STT));
						colNum = ColumnPO.PO;
						String PO_No = commonService.getStringValue(row.getCell(ColumnPO.PO)).trim();

						colNum = ColumnPO.Line;
						String Line = commonService.getStringValue(row.getCell(ColumnPO.Line)).trim();
						Line = Line.equals("0") ? "" : Line;

						colNum = ColumnPO.Shipdate;
						Date ShipDate = null;
						try {
							String s_shipdate = commonService.getStringValue(row.getCell(ColumnPO.Shipdate));
							if (s_shipdate.contains("/")) {
								String[] s_date = s_shipdate.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									ShipDate = new SimpleDateFormat("dd/MM/yyyy").parse(s_shipdate);
								} else {
									mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
								}

							} else {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPO.Shipdate))) {
									ShipDate = row.getCell(ColumnPO.Shipdate).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPO.Shipdate))) {
								ShipDate = row.getCell(ColumnPO.Shipdate).getDateCellValue();
							}
						}

						if (ShipDate == null) {
							mes_err_addition = "Ngày giao hàng không được để trống";
							throw new Exception();
						}

						colNum = ColumnPO.Shipmode;
						String Shipmode = commonService.getStringValue(row.getCell(ColumnPO.Shipmode)).trim();
						Shipmode = Shipmode.equals("0") ? "" : Shipmode;

						colNum = ColumnPO.PackingMethod;
						String PackingMethod = commonService.getStringValue(row.getCell(ColumnPO.PackingMethod)).trim();
						
						colNum = ColumnPO.DC;
						String DC = commonService.getStringValue(row.getCell(ColumnPO.DC)).trim();
						
						colNum = ColumnPO.Factory;
						String Factory = commonService.getStringValue(row.getCell(ColumnPO.Factory)).trim();
						
						Factory = Factory.equals("0") ? "" : Factory;
						Long factoryId = null;
						if(!Factory.equals("")){ 
							// kiem tra phan xuong co trong db hay khong 
							// kiem tra theo ten hoac ma
							List<Org> orgList = orgService.getOrgByNameOrCode(Factory, OrgType.ORG_TYPE_XUONGSX);
							if(orgList.size() == 0) {
								// -> khong co bao loi
								mes_err_addition = "Phân xưởng " + Factory + " không tồn tại trong hệ thống";
								throw new Exception();
							}else {
								factoryId = orgList.get(0).getId();
							}
						}else {
							// lấy factoryId theo user
							Long userOrgId = user.getOrgid_link();
							Org userOrg = orgService.findOne(userOrgId);
							Integer userOrgType = userOrg.getOrgtypeid_link();
							if(userOrgType == OrgType.ORG_TYPE_XUONGSX) {
								// lay id neu la xuong sx
								factoryId = userOrg.getId();
							}else {
								// ko lay id
								factoryId = null;
							}
						}

						colNum = ColumnPO.Colorname;
						String ColorName = commonService.getStringValue(row.getCell(ColumnPO.Colorname)).replace("\n", "").replace("\r", "").trim();
						ColorName = ColorName.equals("0") ? "" : ColorName;
						
						if (ColorName == null || ColorName.trim().equals("")) {
							mes_err_addition = "Tên màu không được để trống";
							throw new Exception();
						}


						colNum = ColumnPO.Colorcode;
						String ColorCode = commonService.getStringValue(row.getCell(ColumnPO.Colorcode)).trim();
						ColorCode = ColorCode.equals("0") ? "" : ColorCode;
						
						if (ColorName.equals(ColorCode)) {
							mes_err_addition = "Tên màu không được trùng với mã màu";
							throw new Exception();
						}

						colNum = ColumnPO.Style;
						String Style = commonService.getStringValue(row.getCell(ColumnPO.Style)).trim();
						Style = Style.equals("0") ? "" : Style;
						
						colNum = ColumnPO.CodeExtra;
						String CodeExtra = "";
						if(isColumnCodeExtra) {
							CodeExtra = commonService.getStringValue(row.getCell(ColumnPO.CodeExtra)).trim();
							CodeExtra = CodeExtra.equals("0") ? "" : CodeExtra;
						}

						// Kiem tra Shipmode da ton tai hay chua
						Long shipmodeid_link = null;
						if (!Shipmode.equals("")) {
							List<ShipMode> shipmodes = shipmodeService.getbyname(Shipmode);
							if (shipmodes.size() == 0) {
								ShipMode shipmode_new = new ShipMode();
								shipmode_new.setId(null);
								shipmode_new.setName(Shipmode);
								shipmode_new = shipmodeService.save(shipmode_new);

								shipmodeid_link = shipmode_new.getId();
							} else {
								shipmodeid_link = shipmodes.get(0).getId();
							}
						}

						// Kiem tra PackingMethod
						Long packingmethodid_link = null;
						if (!PackingMethod.equals("")) {
							List<PackingType> list_packing = packingService.getbyname(PackingMethod, orgrootid_link);
							if (list_packing.size() == 0) {
								PackingType pk = new PackingType();
								pk.setCode(PackingMethod);
								pk.setId(null);
								pk.setName(PackingMethod);
								pk.setOrgrootid_link(orgrootid_link);
								pk = packingService.save(pk);

								packingmethodid_link = pk.getId();
							} else {
								packingmethodid_link = list_packing.get(0).getId();
							}
						}

						// Kiem tra PO co dung voi PO dang chon de them moi khong
						if (!PO_No.equals("TBD")) {

							amount_po = 0;
							Long pcontractpoid_link = null;
							// Kiem tra so PO va so PO cha
//							List<PContract_PO> list_po_parent = pcontract_POService.check_exist_PONo(PO_No, pcontractid_link);
//							if(list_po_parent.size() > 0) {

							// Kiem tra xem PO con da ton tai hay chua
							String s_po = Line.equals("") ? PO_No : PO_No + "-" + Line;
							List<PContract_PO> list_po = pcontract_POService.check_exist_po_children(s_po, ShipDate,
									shipmodeid_link, pcontractid_link, parentid_link);

							if (list_po.size() == 0) {

								PContract_PO po_new = new PContract_PO();
								po_new.setId(null);
								po_new.setPo_buyer(s_po);
								po_new.setShipdate(ShipDate);
								po_new.setShipmodeid_link(shipmodeid_link);
								po_new.setPcontractid_link(pcontractid_link);
								po_new.setParentpoid_link(parentid_link);
								po_new.setOrgrootid_link(orgrootid_link);
								po_new.setPackingnotice(packingmethodid_link + "");
								po_new.setProductid_link(parent.getProductid_link());
								po_new.setPlan_productivity(parent.getPlan_productivity());
								po_new.setPlan_linerequired(parent.getPlan_linerequired());
								po_new.setStatus(POStatus.PO_STATUS_CONFIRMED);
								po_new.setPo_typeid_link(POType.PO_LINE_CONFIRMED);
								po_new.setComment(Style);
								po_new.setOrggrantid_link(factoryId);
								po_new.setDc(DC);
								if(isColumnCodeExtra) {
									po_new.setCode_extra(CodeExtra);
								}
								po_new = pcontract_POService.save(po_new);

								pcontractpoid_link = po_new.getId();
							} else {
								pcontractpoid_link = list_po.get(0).getId();
								PContract_PO po = list_po.get(0);
								po.setStatus(POStatus.PO_STATUS_CONFIRMED);
								po.setComment(Style);
								po.setOrggrantid_link(factoryId);
//									amount_po = po.getPo_quantity();
								po.setDc(DC);
								if(isColumnCodeExtra) {
									String po_CodeExtra = po.getCode_extra();
									if(po_CodeExtra == null || po_CodeExtra.equals("")) {
										po.setCode_extra(CodeExtra);
									}
								}
								po = pcontract_POService.save(po);
							}

							Long colorid_link = null;
							
							String colorNameString = "";
							if(ColorCode == null || ColorCode.trim().equals("")) {
								colorNameString = ColorName;
							}else {
								colorNameString = ColorName
										+ "(" + ColorCode + ")";
							}
							
							List<Attributevalue> listAttributevalue = attributevalueService
									.getByValue(colorNameString, AtributeFixValues.ATTR_COLOR);
							if (listAttributevalue.size() == 0) {
								Attributevalue av = new Attributevalue();
								av.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
								av.setId(null);
								av.setIsdefault(false);
								av.setOrgrootid_link(orgrootid_link);
								av.setSortvalue(attributevalueService.getMaxSortValue(AtributeFixValues.ATTR_COLOR));
								av.setTimecreate(new Date());
								av.setUsercreateid_link(user.getId());
								av.setValue(colorNameString);

								av = attributevalueService.save(av);
								colorid_link = av.getId();
							} else {
								colorid_link = listAttributevalue.get(0).getId();
							}
							
							int columnsize = ColumnPO.Colorcode;
							if(isColumnCodeExtra) {
								columnsize = ColumnPO.Colorcode + 2;
							}else {
								columnsize = ColumnPO.Colorcode + 1;
							}

							
							String s_sizename = commonService.getStringValue(rowheader.getCell(columnsize));
							s_sizename = s_sizename.equals("0") ? "" : s_sizename;
							while (!s_sizename.equals("")) {
								colNum = columnsize;
								// han che viec de qua nhieu cot co thi khong xu ly nua
								if (colNum == 50)
									break;

								Long sizeid_link = null;
								String sizename = commonService.getStringValue(rowheader.getCell(columnsize));
								List<Attributevalue> list_size = attributevalueService.getByValue(sizename,
										AtributeFixValues.ATTR_SIZE);
								if (list_size.size() == 0) {
									Attributevalue av = new Attributevalue();
									av.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
									av.setId(null);
									av.setIsdefault(false);
									av.setOrgrootid_link(orgrootid_link);
									av.setSortvalue(attributevalueService.getMaxSortValue(AtributeFixValues.ATTR_SIZE));
									av.setTimecreate(new Date());
									av.setUsercreateid_link(user.getId());
									av.setValue(sizename);

									av = attributevalueService.save(av);
									sizeid_link = av.getId();
								} else {
									sizeid_link = list_size.get(0).getId();
								}
								String s_amount = commonService.getStringValue(row.getCell(columnsize)).equals("") ? "0"
										: commonService.getStringValue(row.getCell(columnsize));
								s_amount = s_amount.replace(",", "");
								Double amount = Double.parseDouble(s_amount);
								if (amount > 0) {
									amount_po += amount;

									Product product = productService.findOne(parent.getProductid_link());
									if (product.getProducttypeid_link() != 5) {
										Long skuid_link = skuattService.getsku_byproduct_and_valuemau_valueco(
												parent.getProductid_link(), colorid_link, sizeid_link);

										if (skuid_link == 0) {

											SKU sku = new SKU();
											sku.setCode(genCodeSKU(product));
											sku.setId(null);
											sku.setUnitid_link(product.getUnitid_link());
											sku.setName(genCodeSKU(product));
											sku.setOrgrootid_link(orgrootid_link);
											sku.setProductid_link(parent.getProductid_link());
											sku.setSkutypeid_link(10);
											sku = skuService.save(sku);

											skuid_link = sku.getId();

											// Them vao bang sku_attribute_value
											SKU_Attribute_Value savMau = new SKU_Attribute_Value();
											savMau.setId((long) 0);
											savMau.setAttributevalueid_link(colorid_link);
											savMau.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
											savMau.setOrgrootid_link(orgrootid_link);
											savMau.setSkuid_link(skuid_link);
											savMau.setUsercreateid_link(user.getId());
											savMau.setTimecreate(new Date());

											skuattService.save(savMau);

											SKU_Attribute_Value savCo = new SKU_Attribute_Value();
											savCo.setId((long) 0);
											savCo.setAttributevalueid_link(sizeid_link);
											savCo.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
											savCo.setOrgrootid_link(orgrootid_link);
											savCo.setSkuid_link(skuid_link);
											savCo.setUsercreateid_link(user.getId());
											savCo.setTimecreate(new Date());

											skuattService.save(savCo);

											// Them vao trong product_attribute_value
											ProductAttributeValue pav_mau = new ProductAttributeValue();
											pav_mau.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
											pav_mau.setAttributevalueid_link(colorid_link);
											pav_mau.setId(null);
											pav_mau.setIsDefault(false);
											pav_mau.setOrgrootid_link(orgrootid_link);
											pav_mau.setProductid_link(parent.getProductid_link());
											pavService.save(pav_mau);

											ProductAttributeValue pav_co = new ProductAttributeValue();
											pav_co.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
											pav_co.setAttributevalueid_link(sizeid_link);
											pav_co.setId(null);
											pav_co.setIsDefault(false);
											pav_co.setOrgrootid_link(orgrootid_link);
											pav_co.setProductid_link(parent.getProductid_link());
											pavService.save(pav_co);
										}
										int amount_plus = commonService
												.Calculate_pquantity_production(amount.intValue());
										List<PContractProductSKU> ppskus = ppskuService.getlistsku_bysku_and_product_PO(
												skuid_link, pcontractpoid_link, parent.getProductid_link());
										if (ppskus.size() == 0) {
											PContractProductSKU ppsku = new PContractProductSKU();
											ppsku.setId(null);
											ppsku.setOrgrootid_link(orgrootid_link);
											ppsku.setPcontract_poid_link(pcontractpoid_link);
											ppsku.setPcontractid_link(pcontractid_link);
											ppsku.setPquantity_porder(amount.intValue()); // sl đơn
											ppsku.setPquantity_production(amount_plus); // sl sx
											ppsku.setPquantity_total(amount_plus); // sl tong
											ppsku.setProductid_link(parent.getProductid_link());
											ppsku.setSkuid_link(skuid_link);
											ppskuService.save(ppsku);
										} else {
											PContractProductSKU ppsku = ppskus.get(0);
											ppsku.setPquantity_porder(amount.intValue()); // sl đơn
											ppsku.setPquantity_production(amount_plus); // sl sx
											ppsku.setPquantity_total(amount_plus); // sl tong
											ppskuService.save(ppsku);
										}
										
									} else {
										List<ProductPairing> list_pair = productpairService
												.getproduct_pairing_detail_bycontract(orgrootid_link, pcontractid_link,
														product.getId());
										for (ProductPairing productPairing : list_pair) {
											Product product_children = productService
													.findOne(productPairing.getProductid_link());

											Long skuid_link = skuattService.getsku_byproduct_and_valuemau_valueco(
													productPairing.getProductid_link(), colorid_link, sizeid_link);

											if (skuid_link == 0 || skuid_link == null) {

												SKU sku = new SKU();
												sku.setCode(genCodeSKU(product_children));
												sku.setId(null);
												sku.setUnitid_link(product.getUnitid_link());
												sku.setName(genCodeSKU(product_children));
												sku.setOrgrootid_link(orgrootid_link);
												sku.setProductid_link(productPairing.getProductid_link());
												sku.setSkutypeid_link(10);
												sku = skuService.save(sku);

												skuid_link = sku.getId();

												// Them vao bang sku_attribute_value
												SKU_Attribute_Value savMau = new SKU_Attribute_Value();
												savMau.setId((long) 0);
												savMau.setAttributevalueid_link(colorid_link);
												savMau.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
												savMau.setOrgrootid_link(orgrootid_link);
												savMau.setSkuid_link(skuid_link);
												savMau.setUsercreateid_link(user.getId());
												savMau.setTimecreate(new Date());

												skuattService.save(savMau);

												SKU_Attribute_Value savCo = new SKU_Attribute_Value();
												savCo.setId((long) 0);
												savCo.setAttributevalueid_link(sizeid_link);
												savCo.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
												savCo.setOrgrootid_link(orgrootid_link);
												savCo.setSkuid_link(skuid_link);
												savCo.setUsercreateid_link(user.getId());
												savCo.setTimecreate(new Date());

												skuattService.save(savCo);

												// Them vao trong product_attribute_value
												ProductAttributeValue pav_mau = new ProductAttributeValue();
												pav_mau.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
												pav_mau.setAttributevalueid_link(colorid_link);
												pav_mau.setId(null);
												pav_mau.setIsDefault(false);
												pav_mau.setOrgrootid_link(orgrootid_link);
												pav_mau.setProductid_link(productPairing.getProductid_link());
												pavService.save(pav_mau);

												ProductAttributeValue pav_co = new ProductAttributeValue();
												pav_co.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
												pav_co.setAttributevalueid_link(sizeid_link);
												pav_co.setId(null);
												pav_co.setIsDefault(false);
												pav_co.setOrgrootid_link(orgrootid_link);
												pav_co.setProductid_link(productPairing.getProductid_link());
												pavService.save(pav_co);
											}

											int amount_plus = commonService.Calculate_pquantity_production(
													amount.intValue() * productPairing.getAmount());
											List<PContractProductSKU> ppskus = ppskuService
													.getlistsku_bysku_and_product_PO(skuid_link, pcontractpoid_link,
															productPairing.getProductid_link());
											if (ppskus.size() == 0) {
												PContractProductSKU ppsku = new PContractProductSKU();
												ppsku.setId(null);
												ppsku.setOrgrootid_link(orgrootid_link);
												ppsku.setPcontract_poid_link(pcontractpoid_link);
												ppsku.setPcontractid_link(pcontractid_link);
												ppsku.setPquantity_porder(
														amount.intValue() * productPairing.getAmount());
												ppsku.setPquantity_production(amount_plus);
												ppsku.setPquantity_total(amount_plus);
												ppsku.setProductid_link(productPairing.getProductid_link());
												ppsku.setSkuid_link(skuid_link);
												ppskuService.save(ppsku);
											} else {
												PContractProductSKU ppsku = ppskus.get(0);
												ppsku.setPquantity_porder(
														amount.intValue() * productPairing.getAmount());
												ppsku.setPquantity_production(amount_plus);
												ppsku.setPquantity_total(amount_plus);
												ppskuService.save(ppsku);
											}
										}
									}
								}

								columnsize++;

								s_sizename = commonService.getStringValue(rowheader.getCell(columnsize));
								s_sizename = s_sizename.equals("0") ? "" : s_sizename;
							}
							// Cap nhat lai so tong cua po
							PContract_PO po = pcontract_POService.findOne(pcontractpoid_link);
							po.setPo_quantity(amount_po);
							po = pcontract_POService.save(po);

							// Cap nhat lai so luong trong pcontract_productivity
						}
						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						STT = commonService.getStringValue(row.getCell(ColumnTemplate.STT));
						STT = STT.equals("0") ? "" : STT;
					}
					
					// set lai sl cho po line sau khi upload
					parent = pcontract_POService.findOne(parentid_link);
					Product product = productService.findOne(parent.getProductid_link());
					Integer producttypeid_link = product.getProducttypeid_link();
					// ds po line thuc te
					List<PContract_PO> listPContractPO_thucte = pcontract_POService
							.get_by_parent_and_type_and_MauSP(parent.getId(), POType.PO_LINE_CONFIRMED, null);
					for(PContract_PO pcontract_po : listPContractPO_thucte) {
						if(producttypeid_link.equals(5)) { // sp bo -> lay ds sp con
							List<ProductPairing> dsSpCon = productpairService.getby_product(product.getId());
							if(dsSpCon.size() > 0) {
								ProductPairing productPairing = dsSpCon.get(0);
								Integer amount = productPairing.getAmount() == null ? 0 : productPairing.getAmount(); // sl sp trong 1 bo -> tinh tong sl roi chia cho amount -> sl bo
								List<PContractProductSKU> pcontractProductSKU_list_spCOn = ppskuService.getbypo_and_product(
										pcontract_po.getId(), productPairing.getProductid_link()
										);
								Integer totalSL = 0;
								for(PContractProductSKU pcontractProductSKU : pcontractProductSKU_list_spCOn) {
									totalSL += pcontractProductSKU.getPquantity_porder() == null ? 0 : pcontractProductSKU.getPquantity_porder();
								}
								
								if(amount > 0) {
									pcontract_po.setPo_quantity(totalSL / amount);
									pcontract_POService.save(pcontract_po);
								}
								
							}
						}else {
							List<PContractProductSKU> PContractProductSKU_list = ppskuService.getbypo_and_product(
									pcontract_po.getId(), pcontract_po.getProductid_link()
									);
							Integer totalSL = 0;
							for(PContractProductSKU pcontractProductSKU : PContractProductSKU_list) {
								totalSL += pcontractProductSKU.getPquantity_porder() == null ? 0 : pcontractProductSKU.getPquantity_porder();
							}
							pcontract_po.setPo_quantity(totalSL);
							pcontract_POService.save(pcontract_po);
						}
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					mes_err += "Có lỗi ở dòng " + (rowNum + 1) + " và cột " + (colNum + 1);
					if(!mes_err_addition.equals("")) {
						mes_err+=". " + mes_err_addition;
					}
				} finally {
					workbook.close();
					serverFile.delete();
				}

				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Có lỗi trong quá trình upload! Bạn hãy thử lại");
			}
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	private String genCodeSKU(Product product) {
		List<SKU> lstSKU = skuService.getlist_byProduct(product.getId());
		if (lstSKU.size() == 0) {
			return product.getBuyercode().trim() + "_" + "1";
		}
		String old_code = lstSKU.get(0).getCode().trim();
		String[] obj = old_code.split("_");
		int a = Integer.parseInt(obj[obj.length - 1]);
		String code = product.getBuyercode() + "_" + (a + 1);
		return code;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<PContract_pocreate_response> PContractPOCreate(@RequestBody PContract_pocreate_request entity,
			HttpServletRequest request) {
		PContract_pocreate_response response = new PContract_pocreate_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long usercreatedid_link = user.getId();
			long pcontractid_link = entity.pcontractid_link;

			PContract_PO pcontract_po = entity.data;
			if (pcontract_po.getId() == null) {
				pcontract_po.setOrgrootid_link(orgrootid_link);
				pcontract_po.setUsercreatedid_link(usercreatedid_link);
				pcontract_po.setDatecreated(new Date());
				pcontract_po.setPcontractid_link(pcontractid_link);
				pcontract_po.setStatus(POStatus.PO_STATUS_UNCONFIRM);
			}
//			else {
//				PContract_PO pcontract_po_old = pcontract_POService.findOne(pcontract_po.getId());
//				pcontract_po.setOrgrootid_link(pcontract_po_old.getOrgrootid_link());
//				pcontract_po.setUsercreatedid_link(pcontract_po_old.getUsercreatedid_link());
//				pcontract_po.setDatecreated(pcontract_po_old.getDatecreated());
//			}
			pcontract_po = pcontract_POService.save(pcontract_po);

			long pcontract_poid_link = pcontract_po.getId();
			// Update Price cua PO hien tai
			updatePriceList(usercreatedid_link, orgrootid_link, pcontractid_link, pcontract_poid_link,
					entity.data.getPcontract_price());

			// Update List Price cua cac PO co Shipdate sau PO hien tai va cua cung
			// PContract va Product
			List<PContract_PO> listpo_latershipdate = pcontract_POService.getPO_LaterShipdate(orgrootid_link,
					pcontractid_link, pcontract_po.getProductid_link(), pcontract_po.getShipdate());
			for (PContract_PO thePO_Latershipdate : listpo_latershipdate) {
				thePO_Latershipdate.setSewtarget_percent(pcontract_po.getSewtarget_percent());
				thePO_Latershipdate.setExchangerate(pcontract_po.getExchangerate());
				pcontract_POService.save(thePO_Latershipdate);
				updatePriceList(usercreatedid_link, orgrootid_link, pcontractid_link, thePO_Latershipdate.getId(),
						entity.data.getPcontract_price());
			}

			// Cap nhat productivity
			List<PContract_PO_Productivity> list_productivity = entity.data.getPcontract_po_productivity();
			for (PContract_PO_Productivity pContract_PO_Productivity : list_productivity) {
				if (pContract_PO_Productivity.getId() == null) {
					pContract_PO_Productivity.setOrgrootid_link(orgrootid_link);
				}
				pContract_PO_Productivity.setPcontract_poid_link(pcontract_poid_link);
				productivityService.save(pContract_PO_Productivity);
			}

			// Update POrder_Req
			List<POrder_Req> lst_porders = entity.po_orders;
//			String po_code = pcontract_po.getPo_vendor().length() > 0?pcontract_po.getPo_vendor():pcontract_po.getPo_buyer();
			for (POrder_Req porder : lst_porders) {
				POrder_Req porder_req = new POrder_Req();
				if (null == porder.getId() || 0 == porder.getId()) {
					// Them moi POrder_req

					porder_req.setPcontractid_link(pcontractid_link);
					porder_req.setPcontract_poid_link(pcontract_poid_link);

					porder_req.setTotalorder(porder.getTotalorder());
					porder_req.setGranttoorgid_link(porder.getGranttoorgid_link());
					porder_req.setAmount_inset(porder.getAmount_inset());

					porder_req.setOrgrootid_link(orgrootid_link);
					porder_req.setProductid_link(porder.getProductid_link());
					porder_req.setOrderdate(new Date());
					porder_req.setUsercreatedid_link(user.getId());
					porder_req.setStatus(POrderReqStatus.STATUS_FREE);
					porder_req.setTimecreated(new Date());
					porder_req.setIs_calculate(porder.getIs_calculate());

					// Them ngay yeu cau phan xuong phai hoan thanh xep ke hoach (2 ngay sau ngay
					// hien tai)
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					c.add(Calendar.DATE, 2);
					porder_req.setPlandate_required(c.getTime());

					// Save to DB
					porder_req_Service.savePOrder_Req(porder_req);

					// Create taskboard
//					long orgid_link = porder.getGranttoorgid_link();
//					long tasktypeid_link = 0;
//					List<Task_Object> list_object = new ArrayList<Task_Object>();
//
//					Task_Object object_pcontract = new Task_Object();
//					object_pcontract.setId(null);
//					object_pcontract.setObjectid_link(pcontractid_link);
//					object_pcontract.setOrgrootid_link(orgrootid_link);
//					object_pcontract.setTaskobjecttypeid_link((long) TaskObjectType_Name.DonHang);
//					list_object.add(object_pcontract);
//
//					Task_Object object_pcontractpo = new Task_Object();
//					object_pcontractpo.setId(null);
//					object_pcontractpo.setObjectid_link(pcontract_poid_link);
//					object_pcontractpo.setOrgrootid_link(orgrootid_link);
//					object_pcontractpo.setTaskobjecttypeid_link((long) TaskObjectType_Name.DonHangPO);
//					list_object.add(object_pcontractpo);
//
//					Task_Object object_porder_req = new Task_Object();
//					object_porder_req.setId(null);
//					object_porder_req.setObjectid_link(porder_req_id_link);
//					object_porder_req.setOrgrootid_link(orgrootid_link);
//					object_porder_req.setTaskobjecttypeid_link((long) TaskObjectType_Name.YeuCauSanXuat);
//					list_object.add(object_porder_req);
//
//					commonService.CreateTask(orgrootid_link, orgid_link, usercreatedid_link, tasktypeid_link,
//							list_object, null);
				} else {
					porder_req = porder_req_Service.findOne(porder.getId());
					porder_req.setTotalorder(porder.getTotalorder());
					porder_req.setIs_calculate(porder.getIs_calculate());
					// Save to DB
					porder_req_Service.savePOrder_Req(porder_req);
				}

				// Tao lenh cho Phan xuong neu chao gia được chốt
				Long parentid_link = pcontract_po.getParentpoid_link() == null ? 0 : pcontract_po.getParentpoid_link();
				if (pcontract_po.getStatus() == POStatus.PO_STATUS_CONFIRMED && parentid_link == 0) {
					porderService.createPOrder(porder_req, user);
				}

			}

			// Response to Client
			response.id = pcontract_po.getId();
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

			return new ResponseEntity<PContract_pocreate_response>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_pocreate_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	private void updatePriceList(Long usercreatedid_link, Long orgrootid_link, Long pcontractid_link,
			Long pcontract_poid_link, List<PContract_Price> list_price_new) {
		// Xoa list price cu của PO

		// Xoa PContract_Price_D_SKU truoc vi lay pcontract_poid_link theo
		// PContract_Price_D
		List<PContract_Price_D_SKU> list_price_d_sku = pcontractpriceDSkuService
				.getPrice_D_SKU_ByPO(pcontract_poid_link);
		for (PContract_Price_D_SKU price_d_sku : list_price_d_sku) {
			pcontractpriceDSkuService.delete(price_d_sku);
		}

		List<PContract_Price> list_price = pcontractpriceService.getPrice_ByPO(pcontract_poid_link);
		for (PContract_Price price : list_price) {
			pcontractpriceService.delete(price);
		}
		List<PContract_Price_D> list_price_d = pcontractpriceDService.getPrice_D_ByPO(pcontract_poid_link);
		for (PContract_Price_D price_d : list_price_d) {
			pcontractpriceDService.delete(price_d);
		}

		// them Price list moi
		for (PContract_Price price : list_price_new) {
			PContract_Price newPrice = new PContract_Price();
//			newPrice.setId(null);
			newPrice.setPcontract_poid_link(pcontract_poid_link);
			newPrice.setPcontractid_link(pcontractid_link);
			newPrice.setOrgrootid_link(orgrootid_link);

			newPrice.setProductid_link(price.getProductid_link());
			newPrice.setSizesetid_link(price.getSizesetid_link());
			newPrice.setPrice_cmp(price.getPrice_cmp());
			newPrice.setPrice_fob(price.getPrice_fob());
			newPrice.setPrice_sewingcost(price.getPrice_sewingcost());
			newPrice.setPrice_sewingtarget(price.getPrice_sewingtarget());
			newPrice.setPrice_vendortarget(price.getPrice_vendortarget());
			newPrice.setTotalprice(price.getTotalprice());
			newPrice.setSalaryfund(price.getSalaryfund());
			newPrice.setQuantity(price.getQuantity());
			newPrice.setIs_fix(price.getIs_fix());

			for (PContract_Price_D price_d : price.getPcontract_price_d()) {
				PContract_Price_D newPrice_D = new PContract_Price_D();
				newPrice_D.setPcontract_poid_link(pcontract_poid_link);
				newPrice_D.setPcontractid_link(pcontractid_link);
				newPrice_D.setOrgrootid_link(orgrootid_link);

				newPrice_D.setProductid_link(price_d.getProductid_link());
				newPrice_D.setPrice(price_d.getPrice());
				newPrice_D.setCurrencyid_link(price_d.getCurrencyid_link());
				newPrice_D.setExchangerate(price_d.getExchangerate());
				newPrice_D.setCost(price_d.getCost());
				newPrice_D.setIsfob(price_d.getIsfob());
				newPrice_D.setFobpriceid_link(price_d.getFobpriceid_link());
				newPrice_D.setSizesetid_link(price_d.getSizesetid_link());
				newPrice_D.setQuota(price_d.getQuota());
				newPrice_D.setUnitprice(price_d.getUnitprice());
				newPrice_D.setUnitid_link(price_d.getUnitid_link());
				newPrice_D.setUsercreatedid_link(usercreatedid_link);
				newPrice_D.setDatecreated(new Date());

				newPrice_D.setLost_ratio(price_d.getLost_ratio());
				newPrice_D.setMaterialid_link(price_d.getMaterialid_link());
				newPrice_D.setProviderid_link(price_d.getProviderid_link());

				for (PContract_Price_D_SKU price_d_sku : price_d.getPcontract_price_d_sku()) {
					PContract_Price_D_SKU newPrice_D_SKU = new PContract_Price_D_SKU();

//					newPrice_D_SKU.setPcontractprice_d_id_link(pcontractprice_d_id_link);
					newPrice_D_SKU.setMaterialid_link(price_d_sku.getMaterialid_link());
					newPrice_D_SKU.setAmount(price_d_sku.getAmount());
					newPrice_D_SKU.setUnitprice(price_d_sku.getUnitprice());
					newPrice_D_SKU.setTotalprice(price_d_sku.getTotalprice());

					newPrice_D.getPcontract_price_d_sku().add(newPrice_D_SKU);
				}

				newPrice.getPcontract_price_d().add(newPrice_D);
			}
			pcontractpriceService.save(newPrice);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<PContract_pocreate_response> PContractUpdate(@RequestBody PContract_pocreate_request entity,
			HttpServletRequest request) {
		PContract_pocreate_response response = new PContract_pocreate_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long usercreatedid_link = user.getId();

			PContract_PO pcontract_po = entity.data;
			if (pcontract_po.getId() == null || pcontract_po.getId() == 0) {
				pcontract_po.setUsercreatedid_link(usercreatedid_link);
				pcontract_po.setDatecreated(new Date());
				pcontract_po.setOrgrootid_link(orgrootid_link);
			}

			pcontract_po = pcontract_POService.save(pcontract_po);
			
			//Update so tong cua PO cha
			update_quantity_parentpo(pcontract_po.getParentpoid_link());

			// Cap nhat productivity
			List<PContract_PO_Productivity> list_productivity = entity.data.getPcontract_po_productivity();
			for (PContract_PO_Productivity pContract_PO_Productivity : list_productivity) {
				if (pContract_PO_Productivity.getId() == null) {
					pContract_PO_Productivity.setOrgrootid_link(orgrootid_link);
				}
				pContract_PO_Productivity.setPcontract_poid_link(pcontract_po.getId());
				productivityService.save(pContract_PO_Productivity);
			}

			// Update POrder_Req
//			int total = 0;
			List<POrder_Req> lst_porders = entity.po_orders;
//			String po_code = pcontract_po.getPo_vendor().length() > 0?pcontract_po.getPo_vendor():pcontract_po.getPo_buyer();
			if (null != lst_porders)
				for (POrder_Req porder : lst_porders) {
//				total += porder.getTotalorder();
					POrder_Req porder_req = new POrder_Req();
					if (null == porder.getId() || 0 == porder.getId()) {
						// Them moi POrder
						porder_req.setPcontractid_link(pcontract_po.getPcontractid_link());
						porder_req.setPcontract_poid_link(pcontract_po.getId());

						porder_req.setTotalorder(porder.getTotalorder());
						porder_req.setGranttoorgid_link(porder.getGranttoorgid_link());
						porder_req.setAmount_inset(porder.getAmount_inset());

						porder_req.setOrgrootid_link(orgrootid_link);
						porder_req.setProductid_link(porder.getProductid_link());
						porder_req.setOrderdate(new Date());
						porder_req.setUsercreatedid_link(usercreatedid_link);
						porder_req.setStatus(POrderReqStatus.STATUS_FREE);
						porder_req.setTimecreated(new Date());

						// Save to DB
						porder_req_Service.savePOrder_Req(porder_req);
					} else {
						porder_req = porder_req_Service.findOne(porder.getId());
						porder_req.setTotalorder(porder.getTotalorder());
						// Save to DB
						porder_req_Service.savePOrder_Req(porder_req);
					}

					// Tao lenh cho Phan xuong neu chao gia được chốt
//				Long parentid_link = pcontract_po.getParentpoid_link() == null ? 0 : pcontract_po.getParentpoid_link();
					if (pcontract_po.getStatus() == POStatus.PO_STATUS_CONFIRMED) {
						porderService.createPOrder(porder_req, user);
					}
				}
//			pcontract_po.setPo_quantity(total);
//			pcontract_po = pcontract_POService.save(pcontract_po);

			// Response to Client
			response.id = pcontract_po.getId();
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

			return new ResponseEntity<PContract_pocreate_response>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_pocreate_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/update_merchandiser", method = RequestMethod.POST)
	public ResponseEntity<PContract_pocreate_response> update_merchandiser(@RequestBody PContract_pocreate_request entity,
			HttpServletRequest request) {
		PContract_pocreate_response response = new PContract_pocreate_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			PContract_PO pcontract_po_req = entity.data;
			PContract_PO pcontract_po = pcontract_POService.findOne(pcontract_po_req.getId());
			pcontract_po.setMerchandiserid_link(pcontract_po_req.getMerchandiserid_link());
			pcontract_POService.save(pcontract_po);

			// Response to Client
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

			return new ResponseEntity<PContract_pocreate_response>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_pocreate_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	private void update_quantity_parentpo(Long parentpoid_link) {
		if (null != parentpoid_link) {
			List<PContract_PO> ls_po_child = pcontract_POService.get_by_parent_and_type(parentpoid_link, POType.PO_LINE_PLAN);
			if (ls_po_child.size() > 0) {
				int total_quantity = 0;
				for(PContract_PO thePO_Child: ls_po_child) {
					total_quantity += null == thePO_Child.getPo_quantity()?0:thePO_Child.getPo_quantity();
				}
				PContract_PO thePO_Parent = pcontract_POService.findOne(parentpoid_link);
				thePO_Parent.setPo_quantity(total_quantity);
				for(PContract_PO_Productivity theProductivity: thePO_Parent.getPcontract_po_productivity()) {
					if (theProductivity.getProductid_link().equals(thePO_Parent.getProductid_link())) {
						theProductivity.setAmount(total_quantity);
					}
				}
				pcontract_POService.save(thePO_Parent);
			}
		}
	}

	@RequestMapping(value = "/getbycontractproduct", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getPOByContractProduct(
			@RequestBody PContract_getbycontractproduct_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			long orgid_link = user.getOrgid_link();

			List<PContract_PO> pcontract = pcontract_POService.getPOByContractProduct(orgrootid_link,
					entity.pcontractid_link, entity.productid_link, user.getId(), orgid_link, entity.potype);
			response.data = pcontract;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getby_product_and_type", method = RequestMethod.POST)
	public ResponseEntity<getpo_by_product_response> getByProductAndType(@RequestBody getpo_by_product_request entity,
			HttpServletRequest request) {
		getpo_by_product_response response = new getpo_by_product_response();
		try {
			Long pcontractid_link = entity.pcontractid_link;
			Long productid_link = entity.productid_link;
//			Long material_skuid_link = entity.material_skuid_link;

			List<Integer> type = new ArrayList<Integer>();
			type.add(POType.PO_LINE_CONFIRMED);

			response.data = pcontract_POService.getby_pcontract_and_type_andproduct(pcontractid_link, type,
					productid_link);
//			response.poline = bomnplService.getby_product_and_npl(productid_link, pcontractid_link, material_skuid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getpo_by_product_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getpo_by_product_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getby_shipping", method = RequestMethod.POST)
	public ResponseEntity<getby_shipping_response> getby_shipping(@RequestBody getby_shipping_request entity,
			HttpServletRequest request) {
		getby_shipping_response response = new getby_shipping_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgid_link = user.getOrgid_link();
			Long productid_link = entity.productid_link;
			Long orgrootid_link = user.getRootorgid_link();
			Long colorid_link = entity.colorid_link;
			Long sizesetid_link = entity.sizesetid_link;
			Org userOrg = orgService.findOne(orgid_link);

			List<Long> list_org = new ArrayList<Long>();
			if (orgid_link != 0 && orgid_link != 1) {
				for (GpayUserOrg userorg : userOrgService.getall_byuser_andtype(user.getId(),
						OrgType.ORG_TYPE_FACTORY)) {
					list_org.add(userorg.getOrgid_link());
				}
				// Them chinh don vi cua user
				if (!list_org.contains(orgid_link))
					list_org.add(orgid_link);
			}
			
			List<PContractPO_Shipping> pcontractPOShippingList = pcontract_POService.get_po_shipping(orgrootid_link, list_org, productid_link, colorid_link,
					sizesetid_link);

			response.data = new ArrayList<PContractPO_Shipping>();
			for(PContractPO_Shipping pcontractPOShipping : pcontractPOShippingList) {
				Long orgid = pcontractPOShipping.getOrggrantid_link();
				// Không lấy vào danh sách nếu là DHA id:2 và NV id:7 và Apparel Tech Vietnam id:322 và Daehan Global id:12
//				if(orgid != null && (orgid.equals(2L) || orgid.equals(7L) || orgid.equals(322L) || orgid.equals(12L))){
//					continue;
//				}
				
				if(userOrg.getOrgtypeid_link() == OrgType.ORG_TYPE_XUONGSX) {
					// neu la xuong san xuat, lay cac poline co orggrantid_link
					if(pcontractPOShipping.getOrggrantid_link() == null) {
						response.data.add(pcontractPOShipping);
					}else
					if(pcontractPOShipping.getOrggrantid_link().equals(userOrg.getId())) {
						response.data.add(pcontractPOShipping);
					}
				}else {
					// con lai thi lay het
					response.data.add(pcontractPOShipping);
				}
			}
//			response.data = pcontractPOShippingList;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_shipping_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getby_shipping_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/quick_update_line", method = RequestMethod.POST)
	public ResponseEntity<quickupdate_line_response> getPOByContractProduct(
			@RequestBody quickupdate_line_request entity, HttpServletRequest request) {
		quickupdate_line_response response = new quickupdate_line_response();
		try {
			pcontract_POService.save(entity.data);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<quickupdate_line_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<quickupdate_line_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getbycontract_type", method = RequestMethod.POST)
	public ResponseEntity<getby_pcontract_and_type_response> getPOByContractAndType(
			@RequestBody getby_pcontract_and_type_request entity, HttpServletRequest request) {
		getby_pcontract_and_type_response response = new getby_pcontract_and_type_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			List<Integer> type = new ArrayList<Integer>();
			Long pcontractid_link = entity.pcontractid_link;
			String[] list_type = entity.potype.split(",");
			for (String id : list_type) {
				type.add(Integer.parseInt(id));
			}

			List<PContract_PO> pcontract = pcontract_POService.getby_pcontract_and_type(pcontractid_link, type);
			HashMap<Long, String> hash_FOBWorklist = new HashMap<Long, String>();
			for (PContract_PO po : pcontract) {
				List<ProductPairing> p = pairService.getproduct_pairing_detail_bycontract(orgrootid_link,
						po.getPcontractid_link(), po.getProductid_link());
				int total = 1;
				if (p.size() > 0) {
					total = 0;
					for (ProductPairing pair : p) {
						total += pair.getAmount();
					}
				}
				po.setTotalpair(total);
				po.setPo_quantity_sp(po.getPo_quantity() * total);
				
				//Lay danh sach NPL FOB (Nha may tu cung cap bao gom ca In/Theu/Giat)
				String myfob_work = hash_FOBWorklist.get(po.getParentpoid_link());
				if (null != myfob_work) {
					po.setFob_worklist(myfob_work);
				} else {
					List<PContract_PriceFOB_Data> fob_worklist = pcontractpriceDService.getPrice_FOB(pcontractid_link, po.getParentpoid_link());
					String sfob_work = "";
					for(PContract_PriceFOB_Data fobwork: fob_worklist) {
						if (sfob_work.length()>0) sfob_work += "; ";
						sfob_work += fobwork.getFob_price() + "(" + fobwork.getProvider_name() + ")";
					}
					po.setFob_worklist(sfob_work);
					hash_FOBWorklist.put(po.getParentpoid_link(), sfob_work);
				}
			}
			response.data = pcontract;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_pcontract_and_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getby_pcontract_and_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getDsPhanXuongSX_byPcontractId", method = RequestMethod.POST)
	public ResponseEntity<getby_pcontract_and_type_response> getDsPhanXuongSX_byPcontractId(
			@RequestBody getby_pcontract_and_type_request entity, HttpServletRequest request) {
		getby_pcontract_and_type_response response = new getby_pcontract_and_type_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			List<Integer> type = new ArrayList<Integer>();
			Long pcontractid_link = entity.pcontractid_link;
			String[] list_type = entity.potype.split(",");
			for (String id : list_type) {
				type.add(Integer.parseInt(id));
			}

			List<PContract_PO> pcontract = pcontract_POService.getby_pcontract_and_type(pcontractid_link, type);
			List<Long> phanXuongIds = new ArrayList<Long>();
			for (PContract_PO po : pcontract) {
				List<Long> factories_Id = po.getFactories_Id();
				for(Long id : factories_Id) {
					if(!phanXuongIds.contains(id)) {
						phanXuongIds.add(id);
					}
				}
			}
			response.phanXuongIds = phanXuongIds;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_pcontract_and_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getby_pcontract_and_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/cancel_po_offer", method = RequestMethod.POST)
	public ResponseEntity<cancel_po_offer_response> Cancel_PO_Offer(@RequestBody cancel_po_offer_request entity,
			HttpServletRequest request) {
		cancel_po_offer_response response = new cancel_po_offer_response();
		try {
			Long pcontract_poid_link = entity.pcontract_poid_link;
			// Lay ds nhung line giao hang cua chao gia
			List<PContract_PO> list_line = pcontract_POService.get_by_parent_and_type(pcontract_poid_link,
					POType.PO_LINE_PLAN);
			for (PContract_PO line : list_line) {
				// Lay nhung porder
				Long pcontractid_link = line.getPcontractid_link();
				List<POrder> list_porder = porderService.getByContractAndPO(pcontractid_link, line.getId());

				for (POrder porder : list_porder) {
					// lay nhung grant cua lenh
					List<POrderGrant> list_grant = grantService.getbyporder_andpo(porder.getId(), pcontract_poid_link);
					for (POrderGrant grant : list_grant) {
						grantService.delete(grant);
					}

					// xoa lenh chua phan chuyen
					porderService.delete(porder);

					// Cap nhat lai trang thai cua porder-req
					if (porder.getPorderreqid_link() != null) {
						POrder_Req req = porder_req_Service.findOne(porder.getPorderreqid_link());
						req.setStatus(POrderReqStatus.STATUS_FREE);
						porder_req_Service.save(req);
					}

				}

				// Cap nhat trang thai cua line
				line.setStatus(POStatus.PO_STATUS_UNCONFIRM);
				pcontract_POService.save(line);
			}

			// Xoa SKU cua po
			List<PContractProductSKU> list_sku = ppskuService.getlistsku_bypo(pcontract_poid_link);
			for (PContractProductSKU sku : list_sku) {
				ppskuService.delete(sku);
			}

			// Cap nhat trang thai cua chao gia
			PContract_PO offer = pcontract_POService.findOne(pcontract_poid_link);
			offer.setStatus(POStatus.PO_STATUS_UNCONFIRM);
			pcontract_POService.save(offer);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<cancel_po_offer_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<cancel_po_offer_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getall_bycode", method = RequestMethod.POST)
	public ResponseEntity<getall_poline_by_code_response> GetAllByCode(
			@RequestBody getall_poline_by_code_request entity, HttpServletRequest request) {
		getall_poline_by_code_response response = new getall_poline_by_code_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			long orgrootid_link = user.getRootorgid_link();
			long orgid_link = user.getOrgid_link();
			String po_code = entity.po_buyer;

			List<Long> list_org = new ArrayList<Long>();
			if (orgid_link != 0 && orgid_link != 1) {
				for (GpayUserOrg userorg : userOrgService.getall_byuser_andtype(user.getId(),
						OrgType.ORG_TYPE_FACTORY)) {
					list_org.add(userorg.getOrgid_link());
				}
				// Them chinh don vi cua user
				if (!list_org.contains(orgid_link))
					list_org.add(orgid_link);
			}

			response.data = pcontract_POService.getBySearch_andType(po_code, list_org, POType.PO_LINE_CONFIRMED);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getall_poline_by_code_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getall_poline_by_code_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getall_sku_byline", method = RequestMethod.POST)
	public ResponseEntity<get_sku_by_line_response> GetAllSKUByLine(@RequestBody get_sku_by_line_request entity,
			HttpServletRequest request) {
		get_sku_by_line_response response = new get_sku_by_line_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			PContract_PO po_line = pcontract_POService.findOne(entity.pcontract_poid_link);
			Long pcontractid_link = po_line.getPcontractid_link();

			Product product = productService.findOne(po_line.getProductid_link());
			List<Long> list_product = new ArrayList<Long>();

			if (product.getProducttypeid_link() == ProductType.SKU_TYPE_PRODUCT_PAIR) {
				Long productpairid_link = product.getId();
				List<ProductPairing> list_pair = productpairService.getproduct_pairing_detail_bycontract(orgrootid_link,
						pcontractid_link, productpairid_link);
				for (ProductPairing pair : list_pair) {
					Boolean isContain = Arrays.asList(list_product).contains(pair.getProductid_link());
					if(!isContain)
						list_product.add(pair.getProductid_link());
				}
			} else {
				Boolean isContain = Arrays.asList(list_product).contains(product.getId());
					if(!isContain)
				list_product.add(product.getId());
			}

			List<PContractProductSKU> list_sku = new ArrayList<PContractProductSKU>();
			for (Long productid_link : list_product) {
				list_sku.addAll(ppskuService.getbypo_and_product(po_line.getId(), productid_link));
			}

			//Tinh so ton kho thanh pham
			if (entity.isshow_available !=0) {
				for(PContractProductSKU sku: list_sku) {
					sku.setPquantity_onhand_end(warehouseService.getSumBy_Sku(sku.getSkuid_link()));
				}
			}
			response.data = list_sku;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<get_sku_by_line_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<get_sku_by_line_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/get_productiondate", method = RequestMethod.POST)
	public ResponseEntity<get_productiondate_response> getProductionDate(@RequestBody get_productiondate_request entity,
			HttpServletRequest request) {
		get_productiondate_response response = new get_productiondate_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			Date date = entity.date_material;
			int amount = entity.amount_day + 1;

			Date production_date = commonService.Date_Add_with_holiday(date, amount, orgrootid_link);
			response.productiondate = commonService.getBeginOfDate(production_date);
			response.duration = commonService.getDuration(response.productiondate, entity.shipdate, orgrootid_link);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<get_productiondate_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<get_productiondate_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getleafonly_bycontract", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getPOLeafOnly_ByPContract(
			@RequestBody PContract_getbycontractproduct_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			long orgrootid_link = user.getRootorgid_link();

			List<PContract_PO> pcontract = pcontract_POService.getPOLeafOnlyByContract(entity.pcontractid_link,
					entity.productid_link);
			if (entity.pcontractpo_id_link > 0) {
				pcontract.removeIf(c -> c.getId() != entity.pcontractpo_id_link);
			}
			response.data = pcontract;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/delete_listline", method = RequestMethod.POST)
	public ResponseEntity<delete_listpo_line_response> DeleteListPOLine(@RequestBody delete_listpo_line_request entity,
			HttpServletRequest request) {
		delete_listpo_line_response response = new delete_listpo_line_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();
			for (PContract_PO thePO : entity.listid) {
				Long pcontractid_link = thePO.getPcontractid_link();

				List<POrder> list_porder = porderService.getByContractAndPO(thePO.getPcontractid_link(), thePO.getId());
				// chi lay nhung lenh da keo vao bieu do
//				List<POrder> list_porder_grant = list_porder.stream()
//						.filter(item -> null != item.getStatus() && item.getStatus() > POrderStatus.PORDER_STATUS_FREE)
//						.collect(Collectors.toList());
//				if (list_porder_grant.size() > 0) {
//					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
//					response.setMessage(
//							"Hiện vẫn đang có Lệnh SX của đơn hàng đã phân chuyền! Cần hủy phân chuyền Lệnh SX trước khi xóa Line: "
//									+ thePO.getPo_buyer());
//					return new ResponseEntity<delete_listpo_line_response>(response, HttpStatus.BAD_REQUEST);
//				}

				// Kiem tra danh sach po cua chao gia neu khong co thi cho xoa
				List<PContract_PO> list_po = pcontract_POService.get_by_parentid(thePO.getId());
				if (list_po.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage("Bạn không thể xóa chào giá đã phát sinh PO hoặc line giao hàng!");
					return new ResponseEntity<delete_listpo_line_response>(response, HttpStatus.BAD_REQUEST);
				}

				// Delete POrder_Req
				for (POrder_Req thePOrder_Req : porder_req_Service.getByPO(thePO.getId())) {
					porder_req_Service.delete(thePOrder_Req);
				}

				// Delete Shipping
				for (PContract_PO_Shipping theShipping : poshippingService.getByPOID(thePO.getId())) {
					poshippingService.delete(theShipping);
				}

				// Delete porder
//				List<POrder> list_porder_free = list_porder.stream()
//						.filter(item -> null != item.getStatus() && item.getStatus() > POrderStatus.PORDER_STATUS_FREE)
//						.collect(Collectors.toList());
				for (POrder porder : list_porder) {
					porderService.delete(porder);
				}

				// Delete sku
				Product product = productService.findOne(thePO.getProductid_link());
				if (product.getProducttypeid_link() == ProductType.SKU_TYPE_COMPLETEPRODUCT) {
					List<PContractProductSKU> listsku = ppskuService.getbypo_and_product(thePO.getId(),
							thePO.getProductid_link());
					for (PContractProductSKU pContractProductSKU : listsku) {
						ppskuService.delete(pContractProductSKU);
					}
				} else {
					List<ProductPairing> list_pair = pairService.getproduct_pairing_detail_bycontract(orgrootid_link,
							pcontractid_link, thePO.getProductid_link());
					for (ProductPairing pair : list_pair) {
						List<PContractProductSKU> listsku = ppskuService.getbypo_and_product(thePO.getId(),
								pair.getProductid_link());
						for (PContractProductSKU pContractProductSKU : listsku) {
							ppskuService.delete(pContractProductSKU);
						}
					}
				}

				// Delete PO Prices
				for (PContract_Price thePrice : thePO.getPcontract_price()) {
					for (PContract_Price_D thePrice_D : thePrice.getPcontract_price_d()) {
						pcontractpriceDService.delete(thePrice_D);
					}
					pcontractpriceService.delete(thePrice);
				}

				// xoa producttivity
				List<PContract_PO_Productivity> list_productivity = productivityService.getbypo(thePO.getId());
				for (PContract_PO_Productivity productivity : list_productivity) {
					productivityService.delete(productivity);
				}

				// Delete PO
				pcontract_POService.delete(thePO);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<delete_listpo_line_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<delete_listpo_line_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getpo_offer_accept", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getPOAccept(
			@RequestBody getoffer_accept_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgid_link = user.getOrgid_link();
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

			List<PContract_PO> pcontract = pcontract_POService.getPO_Offer_Accept_ByPContract_AndOrg(
					entity.pcontractid_link, entity.productid_link == null ? 0 : entity.productid_link, list_org);
			
			// so sanh po_quantity voi po_line thuc te
			for(PContract_PO pcontract_PO : pcontract) {
				Long pcontract_poid_link = pcontract_PO.getId();
				List<PContract_PO> listPContractPO = pcontract_POService
						.get_by_parent_and_type_and_MauSP(pcontract_poid_link, POType.PO_LINE_CONFIRMED, null);
				Integer po_quantity_total = 0;
				for(PContract_PO pcontract_PO_child : listPContractPO) {
					Integer po_quantity_child = pcontract_PO_child.getPo_quantity() == null ? 0 : pcontract_PO_child.getPo_quantity();
					po_quantity_total += po_quantity_child;
				}
				pcontract_PO.setPo_quantity_total(po_quantity_total);
				Integer po_quantity = pcontract_PO.getPo_quantity() == null ? 0 : pcontract_PO.getPo_quantity();
				pcontract_PO.setPo_quantity_difference((po_quantity_total - po_quantity));
			}
			
//			if(orgs.size() > 0 ) {
//				for(PContract_PO parent: pcontract) {
////				      new Thread("" + parent.getId()){
////				          public void run(){
//				        	List<PContract_PO> list_remove = new ArrayList<PContract_PO>(parent.getSub_po());
//							for(String code : orgs) {
//								list_remove.removeIf(c-> c.getFactories().contains(code));
//							}
//							
//							for(PContract_PO po_remove: list_remove) {
//								parent.getSub_po().remove(po_remove);
//							}
////				        	
////				          }
////				        }.start();
//				}
//			}

			response.data = pcontract;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/accept", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> Accept(@RequestBody PContractPO_accept_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			long orgrootid_link = user.getRootorgid_link();
//			long orgid_link = entity.orgid_link;
//			long userid_link = user.getId();

			PContract_PO po = pcontract_POService.findOne(entity.pcontract_poid_link);
//			if(po.getPo_quantity() != po.getAmount_org()) {
//				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
//				response.setMessage("Số lượng PO không trùng với số lượng phân về cho các xưởng!");
//			    return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
//			}
			po.setOrgmerchandiseid_link(entity.orgid_link);
			po.setMerchandiserid_link(entity.userid_link);
			po.setStatus(POStatus.PO_STATUS_CONFIRMED);

			pcontract_POService.save(po);

			// Lay danh sach cac line cua po tong va chuyen ve trang thai xác nhận
			List<PContract_PO> list_line_gh = pcontract_POService.get_by_parent_and_type(entity.pcontract_poid_link,
					POType.PO_LINE_PLAN);

			for (PContract_PO line : list_line_gh) {
				line.setOrgmerchandiseid_link(entity.orgid_link);
				line.setMerchandiserid_link(entity.userid_link);
				line.setStatus(POStatus.PO_STATUS_CONFIRMED);

				pcontract_POService.save(line);

				// Kiem tra xem porder_req da keo vao uom thu hay chua? Neu keo roi thi cap nhat
				// lai trang thai cua lenh
				List<POrder_Req> list_req = porder_req_Service.getByPO(line.getId());
				for (POrder_Req req : list_req) {
					// Req co porder la da duoc keo vao chuyen roi
					List<POrder> list_porder = porderService.getByPOrder_Req(line.getId(), req.getId());
					if (list_porder.size() > 0) {
						POrder porder = list_porder.get(0);
						if (porder.getStatus() == POrderStatus.PORDER_STATUS_UNCONFIRM) {
							porder.setStatus(POrderStatus.PORDER_STATUS_GRANTED);
						}

						// Cap nhat porder_grant status ve 1
						List<POrderGrant> list_grant = grantService.getByOrderId(porder.getId());
						for (POrderGrant grant : list_grant) {
							if (grant.getStatus() == -1) {
								grant.setStatus(1);
								grantService.save(grant);
							}
						}
					}
					// req chua co porder thi sinh porder cho req
					else {
						porderService.createPOrder(req, user);
					}
				}
			}

			// Sinh Cong viec
//			long pcontractid_link = po.getPcontractid_link();
//			long pcontract_poid_link = po.getId();
//			long productid_link = po.getProductid_link();
//
//			long userinchargeid_link = entity.userid_link;
//			// Kiem tra san pham co phai la san pham bo hay ko. Neu la san pham bo thi phai
//			// tao task cho tung san pham con
//			List<ProductPairing> listpair = productpairService.getproduct_pairing_detail_bycontract(orgrootid_link,
//					pcontractid_link, productid_link);
//			if (listpair.size() == 0) {
//				List<Task_Object> list_object = new ArrayList<Task_Object>();
//
//				Task_Object object_pcontract = new Task_Object();
//				object_pcontract.setId(null);
//				object_pcontract.setObjectid_link(pcontractid_link);
//				object_pcontract.setOrgrootid_link(orgrootid_link);
//				object_pcontract.setTaskobjecttypeid_link((long) TaskObjectType_Name.DonHang);
//				list_object.add(object_pcontract);
//
//				Task_Object object_pcontractpo = new Task_Object();
//				object_pcontractpo.setId(null);
//				object_pcontractpo.setObjectid_link(pcontract_poid_link);
//				object_pcontractpo.setOrgrootid_link(orgrootid_link);
//				object_pcontractpo.setTaskobjecttypeid_link((long) TaskObjectType_Name.DonHangPO);
//				list_object.add(object_pcontractpo);
//
//				Task_Object object_product = new Task_Object();
//				object_product.setId(null);
//				object_product.setObjectid_link(productid_link);
//				object_product.setOrgrootid_link(orgrootid_link);
//				object_product.setTaskobjecttypeid_link((long) TaskObjectType_Name.SanPham);
//				list_object.add(object_product);
//
//				long tasktypeid_link_chitiet = 1; // chi tiet don hang
//				commonService.CreateTask(orgrootid_link, orgid_link, userid_link, tasktypeid_link_chitiet, list_object,
//						userinchargeid_link);
//
//				long tasktypeid_link_haiquan = 2; // dinh muc hai quan
//				commonService.CreateTask(orgrootid_link, orgid_link, userid_link, tasktypeid_link_haiquan, list_object,
//						userinchargeid_link);
//
//				long tasktypeid_link_candoi = 3; // dinh muc can doi
//				commonService.CreateTask(orgrootid_link, orgid_link, userid_link, tasktypeid_link_candoi, list_object,
//						userinchargeid_link);
//			} else {
//				for (ProductPairing pair : listpair) {
//					List<Task_Object> list_object = new ArrayList<Task_Object>();
//
//					Task_Object object_pcontract = new Task_Object();
//					object_pcontract.setId(null);
//					object_pcontract.setObjectid_link(pcontractid_link);
//					object_pcontract.setOrgrootid_link(orgrootid_link);
//					object_pcontract.setTaskobjecttypeid_link((long) TaskObjectType_Name.DonHang);
//					list_object.add(object_pcontract);
//
//					Task_Object object_pcontractpo = new Task_Object();
//					object_pcontractpo.setId(null);
//					object_pcontractpo.setObjectid_link(pcontract_poid_link);
//					object_pcontractpo.setOrgrootid_link(orgrootid_link);
//					object_pcontractpo.setTaskobjecttypeid_link((long) TaskObjectType_Name.DonHangPO);
//					list_object.add(object_pcontractpo);
//
//					Task_Object object_product = new Task_Object();
//					object_product.setId(null);
//					object_product.setObjectid_link(pair.getProductid_link());
//					object_product.setOrgrootid_link(orgrootid_link);
//					object_product.setTaskobjecttypeid_link((long) TaskObjectType_Name.SanPham);
//					list_object.add(object_product);
//
//					long tasktypeid_link_chitiet = 1; // chi tiet don hang
//					commonService.CreateTask(orgrootid_link, orgid_link, userid_link, tasktypeid_link_chitiet,
//							list_object, userinchargeid_link);
//
//					long tasktypeid_link_haiquan = 2; // dinh muc hai quan
//					commonService.CreateTask(orgrootid_link, orgid_link, userid_link, tasktypeid_link_haiquan,
//							list_object, userinchargeid_link);
//
//					long tasktypeid_link_candoi = 3; // dinh muc can doi
//					commonService.CreateTask(orgrootid_link, orgid_link, userid_link, tasktypeid_link_candoi,
//							list_object, userinchargeid_link);
//				}
//			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage("Có lỗi trong quá trình xác nhận! Bạn vui lòng thử lại.");
			return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getbycontract", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getPOByContract(
			@RequestBody PContract_getbycontractproduct_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			List<PContract_PO> pcontract = pcontract_POService.getPOByContract(orgrootid_link, entity.pcontractid_link);
			response.data = pcontract;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getone", method = RequestMethod.POST)
	public ResponseEntity<PContract_pogetone_response> PContractGetOne(@RequestBody PContract_pogetone_request entity,
			HttpServletRequest request) {
		PContract_pogetone_response response = new PContract_pogetone_response();
		try {

			response.data = pcontract_POService.findOne(entity.id);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_pogetone_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_pogetone_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/check_cancel", method = RequestMethod.POST)
	public ResponseEntity<check_cancel_po_response> CheckCancel(@RequestBody cancel_po_request entity,
			HttpServletRequest request) {
		check_cancel_po_response response = new check_cancel_po_response();
		try {
			String mes = "";
			Long pcontract_poid_link = entity.pcontract_poid_link;

			List<POrderProcessing> list = processService.getby_pcontratpo(pcontract_poid_link);
			if (list.size() > 0) {
				mes = "PO đã tồn tại lệnh sản xuất đã vào chuyển! Bạn có chắc chắn muốn hủy PO?";
			}

			response.mes = mes;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<check_cancel_po_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<check_cancel_po_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/cancel_po", method = RequestMethod.POST)
	public ResponseEntity<cancel_po_response> Cancel(@RequestBody cancel_po_request entity,
			HttpServletRequest request) {
		cancel_po_response response = new cancel_po_response();
		try {
			Long pcontract_poid_link = entity.pcontract_poid_link;

			// Cap nhat trang thai PO
			PContract_PO po = pcontract_POService.findOne(pcontract_poid_link);
			po.setStatus(POStatus.PO_STATUS_CANCEL);
			pcontract_POService.save(po);

			// Cap nhat trang thai lenh
			Long pcontractid_link = po.getPcontractid_link();
			List<POrder> list_porder = porderService.getByContractAndPO(pcontractid_link, pcontract_poid_link);
			for (POrder pOrder : list_porder) {
				pOrder.setStatus(POrderStatus.PORDER_STATUS_CANCEL);
				porderService.save(pOrder);
			}

			// Cap nhat trang thai yeu cau xep KH
			List<POrder_Req> list_req = porder_req_Service.getByContractAndPO(pcontractid_link, pcontract_poid_link);
			for (POrder_Req pOrder_Req : list_req) {
				pOrder_Req.setStatus(POrderReqStatus.STATUS_CANCEL);
				porder_req_Service.save(pOrder_Req);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<cancel_po_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<cancel_po_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> PContract_PODelete(@RequestBody PContract_podelete_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {

			PContract_PO thePO = pcontract_POService.findOne(entity.id);
			if (null != thePO) {
//				 Check if having POrder? refuse deleting if have
				List<POrder> list_porder = porderService.getByContractAndPO(thePO.getPcontractid_link(), thePO.getId());
				// chi lay nhung lenh da keo vao bieu do
				List<POrder> list_porder_grant = list_porder.stream()
						.filter(item -> null != item.getStatus() && item.getStatus() > POrderStatus.PORDER_STATUS_FREE)
						.collect(Collectors.toList());
				if (list_porder_grant.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(
							"Hiện vẫn đang có Lệnh SX của đơn hàng đã phân chuyền! Cần hủy phân chuyền Lệnh SX trước khi xóa đơn hàng");
					return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
				}

				// Kiem tra danh sach po cua chao gia neu khong co thi cho xoa
				List<PContract_PO> list_po = pcontract_POService.get_by_parentid(thePO.getId());
				if (list_po.size() > 0) {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage("Bạn không thể xóa chào giá đã phát sinh PO hoặc line giao hàng!");
					return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
				}

				// Delete POrder_Req
				for (POrder_Req thePOrder_Req : porder_req_Service.getByPO(thePO.getId())) {
					porder_req_Service.delete(thePOrder_Req);
				}

				// Delete Shipping
				for (PContract_PO_Shipping theShipping : poshippingService.getByPOID(thePO.getId())) {
					poshippingService.delete(theShipping);
				}

				// Delete porder
				List<POrder> list_porder_free = list_porder.stream()
						.filter(item -> null != item.getStatus() && item.getStatus() == POrderStatus.PORDER_STATUS_FREE)
						.collect(Collectors.toList());
				for (POrder porder : list_porder_free) {
					porderService.delete(porder);
				}

				// Delete sku
				List<PContractProductSKU> listsku = ppskuService.getbypo_and_product(thePO.getId(),
						thePO.getProductid_link());
				for (PContractProductSKU pContractProductSKU : listsku) {
					ppskuService.delete(pContractProductSKU);
				}

				// Delete PO Prices
				for (PContract_Price thePrice : thePO.getPcontract_price()) {
					for (PContract_Price_D thePrice_D : thePrice.getPcontract_price_d()) {
						pcontractpriceDService.delete(thePrice_D);
					}
					pcontractpriceService.delete(thePrice);
				}

				// xoa producttivity
				List<PContract_PO_Productivity> list_productivity = productivityService.getbypo(thePO.getId());
				for (PContract_PO_Productivity productivity : list_productivity) {
					productivityService.delete(productivity);
				}

				// Delete PO
				pcontract_POService.delete(thePO);
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Đơn hàng không tồn tại");
				return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
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

	@RequestMapping(value = "/getByContractAndProductBuyerCodeAndPOBuyer", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getByContractAndProductBuyerCodeAndPOBuyer(
			@RequestBody PContract_getbycontractproductbuyercodepobuyer_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			List<Org> listorg = new ArrayList<Org>();
			for (GpayUserOrg userorg : userOrgService.getall_byuser_andtype(user.getId(), OrgType.ORG_TYPE_FACTORY)) {
				listorg.add(orgService.findOne(userorg.getOrgid_link()));
			}

			Long orgid_link = user.getOrgid_link();
			Org userOrg = null;
			if (orgid_link != 0 && orgid_link != 1 && orgid_link != null) {
				userOrg = orgService.findOne(orgid_link);
			}

			List<PContract_PO> pcontractpoList = pcontract_POService
					.getPcontractPoByPContractAndPOBuyer(entity.pcontractid_link, entity.po_buyer, entity.buyercode);
			response.data = new ArrayList<PContract_PO>();

//			List<PContract_PO> pcontractpoList = pcontract_POService.getPOByContract(user.getRootorgid_link(), entity.pcontractid_link);
//			response.data = pcontractpoList;
			// chỉ lấy pcontract_po con
			for (PContract_PO pcontractpo : pcontractpoList) {
				if (userOrg != null) {
					boolean flag = true;
					List<POrder_Req> porderReqList = porder_req_Service.getByPO(pcontractpo.getId());
					for (POrder_Req porderReq : porderReqList) {
						Long granttoorgid_link = porderReq.getGranttoorgid_link();

						// nếu user được xem nhiều org (GpayUserOrg)
						if (listorg.size() > 0) {
							if (!flag)
								break;
							for (Org org : listorg) {
								if (user.getOrgid_link() == granttoorgid_link || org.getId() == granttoorgid_link) {
									flag = false;
									break;
								}
							}
						} else {
							if (user.getOrgid_link() == granttoorgid_link) {
								flag = false;
								break;
							}
						}
					}

					if (flag)
						continue;
				}

				// Chi lay cac PO Line
				if (pcontractpo.getParentpoid_link() != null) {
					response.data.add(pcontractpo);
				}
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getForMarketTypeChart", method = RequestMethod.POST)
	public ResponseEntity<PContract_PO_getForMarketTypeChart_response> getForMarketTypeChart(
			HttpServletRequest request) {
		PContract_PO_getForMarketTypeChart_response response = new PContract_PO_getForMarketTypeChart_response();
		try {
			response.data = pcontract_POService.getForMarketTypeChart();

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_PO_getForMarketTypeChart_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_PO_getForMarketTypeChart_response>(response, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/getPOLine_Confirm_ByMonthYear", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbymonthyear_response> getPOLine_Confirm_ByMonthYear(
			@RequestBody PContract_getbysearch_request entity, HttpServletRequest request) {
		PContract_getbymonthyear_response response = new PContract_getbymonthyear_response();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date firstDay = sdf.parse(entity.firstDayOfMonth_shipDate);
			Date lastDay = sdf.parse(entity.lastDayOfMonth_shipDate);
			List<PContract_PO_NoLink> listPContractPO = pcontract_PO_NoLink_Service
					.get_by_month_year(firstDay,lastDay, POType.PO_LINE_CONFIRMED);

			// Update danh sach to chuyen duoc giao sx cho PO Line
//			for (PContract_PO_NoLink thePoline : listPContractPO) {
//				//
////				Long pcontractid_link = thePoline.getPcontractid_link();
////				String pcontract_code =  pcontract
//				//
//				thePoline.setProductionlines(grantskuService.getProductionLines(thePoline.getId()));
//
//				// set phuong thuc dong goi
//				String packingnotice = thePoline.getPackingnotice();
//				if(packingnotice == null || packingnotice.equals("null") || packingnotice.equals("")) {
//					thePoline.setPhuongThucDongGoi("");
//				}else {
//					String phuongThucDongGoi = "";
//					String[] listStr = packingnotice.split(";");
//					for(Integer i=0; i<listStr.length;i++) {
//						if(!listStr[i].equals("")) {
//							Long packingTypeId = Long.parseLong(listStr[i]);
//							PackingType packingType = packingService.findOne(packingTypeId);
//							String packingTypeCode = packingType.getCode();
////							String packingTypeName = packingType.getName();
//							if(phuongThucDongGoi.equals("")) {
//								phuongThucDongGoi+= packingTypeCode;
//							}else {
//								phuongThucDongGoi+= "; " + packingTypeCode;
//							}
//						}
//					}
//					thePoline.setPhuongThucDongGoi(phuongThucDongGoi);
//				}
//			}

			response.data = listPContractPO;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbymonthyear_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbymonthyear_response>(response, HttpStatus.OK);
		}
	}


	@RequestMapping(value = "/getPOLine_Confirm", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getPOLine_Confirm(
			@RequestBody PContract_PO_getByPorder_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
			Long pcontract_poid_link = entity.pcontract_poid_link;
			Long mausanphamid_link = entity.mausanphamid_link == null ? null : entity.mausanphamid_link;
			if(mausanphamid_link != null) {
				if(mausanphamid_link.equals((long)0)) {
					mausanphamid_link = null;
				}
			}

			List<PContract_PO> listPContractPO = pcontract_POService
					.get_by_parent_and_type_and_MauSP(pcontract_poid_link, POType.PO_LINE_CONFIRMED, mausanphamid_link);
			
			// Update danh sach to chuyen duoc giao sx cho PO Line
			for (PContract_PO thePoline : listPContractPO) {
				thePoline.setProductionlines(grantskuService.getProductionLines(thePoline.getId()));
				
				// set phuong thuc dong goi
				String packingnotice = thePoline.getPackingnotice();
				if(packingnotice == null || packingnotice.equals("null") || packingnotice.equals("")) {
					thePoline.setPhuongThucDongGoi("");
				}else {
					String phuongThucDongGoi = "";
					String[] listStr = packingnotice.split(";");
					for(Integer i=0; i<listStr.length;i++) {
						if(!listStr[i].equals("")) {
							Long packingTypeId = Long.parseLong(listStr[i]);
							PackingType packingType = packingService.findOne(packingTypeId);
							String packingTypeCode = packingType.getCode();
//							String packingTypeName = packingType.getName();
							if(phuongThucDongGoi.equals("")) {
								phuongThucDongGoi+= packingTypeCode;
							}else {
								phuongThucDongGoi+= "; " + packingTypeCode;
							}
						}
					}
					thePoline.setPhuongThucDongGoi(phuongThucDongGoi);
				}
			}

			response.data = listPContractPO;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getForPOrderListPContractPO", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getForPOrderListPContractPO(
			@RequestBody PContract_PO_getByPorder_request entity, HttpServletRequest request) {
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
//			Long porderid_link = entity.porderid_link;
			Long pcontract_poid_link = entity.pcontract_poid_link;
//			POrder porder = porderService.findOne(porderid_link);
			PContract_PO pcontractPo = pcontract_POService.findOne(pcontract_poid_link);
			if (pcontractPo.getParentpoid_link() != null) {
				pcontract_poid_link = pcontractPo.getParentpoid_link();
			}

			List<PContract_PO> listPContractPO = pcontract_POService.get_by_parentid(pcontract_poid_link);
			response.data = listPContractPO;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getOffers_byOrg", method = RequestMethod.POST)
	public ResponseEntity<getOffes_byOrg_response> getOffers_byOrg(@RequestBody getOffers_byOrg_request entity,
			HttpServletRequest request) {
		getOffes_byOrg_response response = new getOffes_byOrg_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgid_link = user.getOrgid_link();
			List<Long> list_org = new ArrayList<Long>();

			if (orgid_link != 0 && orgid_link != 1) {
				for (GpayUserOrg userorg : userOrgService.getall_byuser_andtype(user.getId(),
						OrgType.ORG_TYPE_FACTORY)) {
					list_org.add(userorg.getOrgid_link());
				}
				// Them chinh don vi cua user
				list_org.add(orgid_link);
			} else {
				List<Org> listorg = orgService.findOrgByType(user.getRootorgid_link(), orgid_link,
						OrgType.ORG_TYPE_FACTORY);
				for (Org org : listorg) {
					list_org.add(org.getId());
				}
			}

			List<PContract_PO> list = new ArrayList<PContract_PO>();
			list = pcontract_POService.getall_offers_by_org(list_org);
			response.data = list;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			ResponseEntity<getOffes_byOrg_response> a = new ResponseEntity<getOffes_byOrg_response>(response,
					HttpStatus.OK);
			return a;
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getOffes_byOrg_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/get_poline_by_offer", method = RequestMethod.POST)
	public ResponseEntity<get_poline_by_po_response> GetLineByOffer(@RequestBody get_poline_by_po entity,
			HttpServletRequest request) {
		get_poline_by_po_response response = new get_poline_by_po_response();
		try {
			Long parentid_link = entity.pcontract_poid_link;
			response.data = pcontract_POService.get_by_parent_and_type(parentid_link, POType.PO_LINE_PLAN);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			ResponseEntity<get_poline_by_po_response> a = new ResponseEntity<get_poline_by_po_response>(response,
					HttpStatus.OK);
			return a;
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<get_poline_by_po_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete_plan_porder", method = RequestMethod.POST)
	public ResponseEntity<delete_plan_porder_response> DeletePlanPOrder(@RequestBody delete_porder_plan_request entity,
			HttpServletRequest request) {
		delete_plan_porder_response response = new delete_plan_porder_response();
		try {
			Long pcontract_poid_link = entity.pcontract_poid_link;

			// Lay nhung line giao hang cua po
			List<PContract_PO> list_line = pcontract_POService.get_by_parent_and_type(pcontract_poid_link,
					POType.PO_LINE_PLAN);

			for (PContract_PO line : list_line) {
				// lay nhung porder_req cua line
				List<POrder_Req> list_req = porder_req_Service.getByPO(line.getId());

				for (POrder_Req req : list_req) {
					// Lay nhung porder
					List<POrder> list_porder = porderService.getByPOrder_Req(pcontract_poid_link, req.getId());
					// Lay cac lenh uom thu
					for (POrder porder : list_porder) {
						List<POrderGrant> list_grant = grantService.getByOrderId(porder.getId());

						for (POrderGrant grant : list_grant) {
							grantService.delete(grant);
						}

						porderService.delete(porder);
					}

					porder_req_Service.delete(req);
				}
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			ResponseEntity<delete_plan_porder_response> a = new ResponseEntity<delete_plan_porder_response>(response,
					HttpStatus.OK);
			return a;
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<delete_plan_porder_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/copyline", method = RequestMethod.POST)
	public ResponseEntity<copy_poline_response> CopyLine(@RequestBody copy_poline_request entity,
			HttpServletRequest request) {
		copy_poline_response response = new copy_poline_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();

			PContract_PO po_old = pcontract_POService.findOne(entity.pcontract_poid_Link);

			PContract_PO po_new = new PContract_PO();
			po_new.setCode(po_old.getCode());
			po_new.setCurrencyid_link(po_old.getCurrencyid_link());
			po_new.setDatecreated(new Date());
			po_new.setEtm_from(po_old.getEtm_from());
			po_new.setEtm_to(po_old.getEtm_to());
			po_new.setExchangerate(po_old.getExchangerate());
			po_new.setId(null);
			po_new.setIs_tbd(po_old.getIs_tbd());
			po_new.setIsauto_calculate(po_old.getIsauto_calculate());
			po_new.setMatdate(po_old.getMatdate());
			po_new.setOrgrootid_link(orgrootid_link);
			po_new.setPackingnotice(po_old.getPackingnotice());
			po_new.setParentpoid_link(po_old.getParentpoid_link());
			po_new.setPcontractid_link(po_old.getPcontractid_link());
			po_new.setPlan_linerequired(po_old.getPlan_linerequired());
			po_new.setPlan_productivity(po_old.getPlan_productivity());
			po_new.setPo_buyer(po_old.getPo_buyer() + " copy");
			po_new.setPo_quantity(po_old.getPo_quantity());
			po_new.setPo_typeid_link(po_old.getPo_typeid_link());
			po_new.setPo_vendor(po_old.getPo_vendor() + " copy");
			po_new.setPortfromid_link(po_old.getPortfromid_link());
			po_new.setPorttoid_link(po_old.getPorttoid_link());
			po_new.setPrice_add(po_old.getPrice_add());
			po_new.setPrice_cmp(po_old.getPrice_cmp());
			po_new.setPrice_commission(po_old.getPrice_commission());
			po_new.setPrice_fob(po_old.getPrice_fob());
			po_new.setPrice_sweingfact(po_old.getPrice_sweingfact());
			po_new.setPrice_sweingtarget(po_old.getPrice_sweingtarget());
			po_new.setProductid_link(po_old.getProductid_link());
			po_new.setProductiondate(po_old.getProductiondate());
			po_new.setProductiondays(po_old.getProductiondays());
			po_new.setProductiondays_ns(po_old.getProductiondays_ns());
			po_new.setProductionlines(po_old.getProductionlines());
			po_new.setQcorgid_link(po_old.getQcorgid_link());
			po_new.setQcorgname(po_old.getQcorgname());
			po_new.setSalaryfund(po_old.getSalaryfund());
			po_new.setSewtarget_percent(po_old.getSewtarget_percent());
			po_new.setShipdate(po_old.getShipdate());
			po_new.setShipmodeid_link(po_old.getShipmodeid_link());
			po_new.setStatus(po_old.getStatus());
			po_new.setUnitid_link(po_old.getUnitid_link());
			po_new.setUsercreatedid_link(user.getId());

			po_new = pcontract_POService.save(po_new);

			// copy ns_production
			List<PContract_PO_Productivity> list_productivity = po_old.getPcontract_po_productivity();
			for (PContract_PO_Productivity po_productivity : list_productivity) {
				PContract_PO_Productivity po_productivity_new = new PContract_PO_Productivity();
				po_productivity_new.setAmount(po_productivity.getAmount());
				po_productivity_new.setId(null);
				po_productivity_new.setOrgrootid_link(orgrootid_link);
				po_productivity_new.setPcontract_poid_link(po_new.getId());
				po_productivity_new.setPlan_linerequired(po_productivity.getPlan_linerequired());
				po_productivity_new.setPlan_productivity(po_productivity.getPlan_productivity());
				po_productivity_new.setProductid_link(po_productivity.getProductid_link());
				po_productivity_new.setProductiondays_ns(po_productivity.getProductiondays_ns());

				productivityService.save(po_productivity_new);
			}

			// copy porder_req
			List<POrder_Req> list_req = porder_req_Service.getByContractAndPO(po_old.getPcontractid_link(),
					po_old.getId());
			for (POrder_Req req : list_req) {
				POrder_Req req_new = new POrder_Req();
				req_new.setAmount_inset(req.getAmount_inset());
				req_new.setGranttoorgid_link(req.getGranttoorgid_link());
				req_new.setId(null);
				req_new.setIs_calculate(req.getIs_calculate());
				req_new.setOrdercode(req.getOrdercode());
				req_new.setOrderdate(req.getOrderdate());
				req_new.setOrgrootid_link(orgrootid_link);
				req_new.setPcontract_poid_link(po_new.getId());
				req_new.setPcontractid_link(req.getPcontractid_link());
				req_new.setPlandate_required(req.getPlandate_required());
				req_new.setProductid_link(req.getProductid_link());
				req_new.setSizesetid_link(req.getSizesetid_link());
				req_new.setStatus(req.getStatus());
				req_new.setTimecreated(new Date());
				req_new.setTotalorder(req.getTotalorder());
				req_new.setUsercreatedid_link(user.getId());

				porder_req_Service.save(req_new);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			ResponseEntity<copy_poline_response> a = new ResponseEntity<copy_poline_response>(response, HttpStatus.OK);
			return a;
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<copy_poline_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getpo_havetoship", method = RequestMethod.POST)
	public ResponseEntity<getpo_nolink_by_product_response> getPO_HavetoShip (
			@RequestBody getpo_havetoship_request entity, HttpServletRequest request) 
	{
		getpo_nolink_by_product_response response = new getpo_nolink_by_product_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			List<PContract_PO_NoLink> pcontract = pcontract_PO_NoLink_Service.getPO_HavetoShip(orgrootid_link, 
					entity.shipdate_from, entity.shipdate_to, entity.orgbuyerid_link);
			for (PContract_PO_NoLink po : pcontract) {
				List<ProductPairing> p = pairService.getproduct_pairing_detail_bycontract(orgrootid_link,
						po.getPcontractid_link(), po.getProductid_link());
				int total = 1;
				if (p.size() > 0) {
					total = 0;
					for (ProductPairing pair : p) {
						total += pair.getAmount();
					}
				}
				po.setTotalpair(total);
				po.setPo_quantity_sp(po.getPo_quantity() * total);
			}
			response.data = pcontract;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getpo_nolink_by_product_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getpo_nolink_by_product_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getByStockin", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getByStockin (
			@RequestBody StockinByIDRequest entity, HttpServletRequest request) 
	{
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			Long id = entity.id;
			List<PContract_PO> pcontract_PO_list = pcontract_POService.getByStockin(id);
			for (PContract_PO thePoline : pcontract_PO_list) {
				thePoline.setProductionlines(grantskuService.getProductionLines(thePoline.getId()));
				
				// set phuong thuc dong goi
				String packingnotice = thePoline.getPackingnotice();
				if(packingnotice == null || packingnotice.equals("null") || packingnotice.equals("")) {
					thePoline.setPhuongThucDongGoi("");
				}else {
					String phuongThucDongGoi = "";
					String[] listStr = packingnotice.split(";");
					for(Integer i=0; i<listStr.length;i++) {
						if(!listStr[i].equals("")) {
							Long packingTypeId = Long.parseLong(listStr[i]);
							PackingType packingType = packingService.findOne(packingTypeId);
							String packingTypeCode = packingType.getCode();
//										String packingTypeName = packingType.getName();
							if(phuongThucDongGoi.equals("")) {
								phuongThucDongGoi+= packingTypeCode;
							}else {
								phuongThucDongGoi+= "; " + packingTypeCode;
							}
						}
					}
					thePoline.setPhuongThucDongGoi(phuongThucDongGoi);
				}
			}
			response.data = pcontract_PO_list;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getBySearch_POLine_Stockin", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getBySearch_POLine_Stockin (
			@RequestBody getall_poline_by_code_request entity, HttpServletRequest request) 
	{
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			long orgrootid_link = user.getRootorgid_link();

			String po_buyer = entity.po_buyer == null ? "" : entity.po_buyer.trim();
			String productbuyercode = entity.productbuyercode == null ? "" : entity.productbuyercode.trim();
			String pcontractcode = entity.pcontractcode == null ? "" : entity.pcontractcode.trim();
			Date shipdateFrom = entity.shipdateFrom == null ? entity.shipdateFrom : GPAYDateFormat.atStartOfDay(entity.shipdateFrom);
			Date shipdateTo = entity.shipdateTo == null ? entity.shipdateTo : GPAYDateFormat.atEndOfDay(entity.shipdateTo);
			Long stockinid_link = entity.stockinid_link;
			
			// tìm danh sách id các poline đã có trong stockin
			List<Long> poLineIdList = pcontract_POService.getPoLineIdByStockin(stockinid_link);
			if(poLineIdList.size() == 0) {
				poLineIdList = null;
			}
			
			// tìm danh sách các poline ko có trong poLineIdList
			List<PContract_PO> pcontract_PO_list = pcontract_POService.getBySearch_POLine_Stockin(
					po_buyer, productbuyercode, pcontractcode, shipdateFrom, shipdateTo, poLineIdList
					);
			
			// Update danh sach to chuyen duoc giao sx cho PO Line
			for (PContract_PO thePoline : pcontract_PO_list) {
				thePoline.setProductionlines(grantskuService.getProductionLines(thePoline.getId()));
				
				// set phuong thuc dong goi
				String packingnotice = thePoline.getPackingnotice();
				if(packingnotice == null || packingnotice.equals("null") || packingnotice.equals("")) {
					thePoline.setPhuongThucDongGoi("");
				}else {
					String phuongThucDongGoi = "";
					String[] listStr = packingnotice.split(";");
					for(Integer i=0; i<listStr.length;i++) {
						if(!listStr[i].equals("")) {
							Long packingTypeId = Long.parseLong(listStr[i]);
							PackingType packingType = packingService.findOne(packingTypeId);
							String packingTypeCode = packingType.getCode();
//										String packingTypeName = packingType.getName();
							if(phuongThucDongGoi.equals("")) {
								phuongThucDongGoi+= packingTypeCode;
							}else {
								phuongThucDongGoi+= "; " + packingTypeCode;
							}
						}
					}
					thePoline.setPhuongThucDongGoi(phuongThucDongGoi);
				}
			}
			
			response.data = pcontract_PO_list;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	//
	
	@RequestMapping(value = "/getByStockout", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getByStockout (
			@RequestBody StockinByIDRequest entity, HttpServletRequest request) 
	{
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			Long id = entity.id;
			List<PContract_PO> pcontract_PO_list = pcontract_POService.getByStockout(id);
			for (PContract_PO thePoline : pcontract_PO_list) {
				thePoline.setProductionlines(grantskuService.getProductionLines(thePoline.getId()));
				
				// set phuong thuc dong goi
				String packingnotice = thePoline.getPackingnotice();
				if(packingnotice == null || packingnotice.equals("null") || packingnotice.equals("")) {
					thePoline.setPhuongThucDongGoi("");
				}else {
					String phuongThucDongGoi = "";
					String[] listStr = packingnotice.split(";");
					for(Integer i=0; i<listStr.length;i++) {
						if(!listStr[i].equals("")) {
							Long packingTypeId = Long.parseLong(listStr[i]);
							PackingType packingType = packingService.findOne(packingTypeId);
							String packingTypeCode = packingType.getCode();
//										String packingTypeName = packingType.getName();
							if(phuongThucDongGoi.equals("")) {
								phuongThucDongGoi+= packingTypeCode;
							}else {
								phuongThucDongGoi+= "; " + packingTypeCode;
							}
						}
					}
					thePoline.setPhuongThucDongGoi(phuongThucDongGoi);
				}
			}
			response.data = pcontract_PO_list;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getBySearch_POLine_Stockout", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getBySearch_POLine_Stockout (
			@RequestBody getall_poline_by_code_request entity, HttpServletRequest request) 
	{
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			long orgrootid_link = user.getRootorgid_link();

			String po_buyer = entity.po_buyer == null ? "" : entity.po_buyer.trim();
			String productbuyercode = entity.productbuyercode == null ? "" : entity.productbuyercode.trim();
			String pcontractcode = entity.pcontractcode == null ? "" : entity.pcontractcode.trim();
			Date shipdateFrom = entity.shipdateFrom == null ? entity.shipdateFrom : GPAYDateFormat.atStartOfDay(entity.shipdateFrom);
			Date shipdateTo = entity.shipdateTo == null ? entity.shipdateTo : GPAYDateFormat.atEndOfDay(entity.shipdateTo);
			Long stockoutid_link = entity.stockoutid_link;
			
			// tìm danh sách id các poline đã có trong stockin
			List<Long> poLineIdList = pcontract_POService.getPoLineIdByStockout(stockoutid_link);
			if(poLineIdList.size() == 0) {
				poLineIdList = null;
			}
			
			// tìm danh sách các poline ko có trong poLineIdList
			List<PContract_PO> pcontract_PO_list = pcontract_POService.getBySearch_POLine_Stockout(
					po_buyer, productbuyercode, pcontractcode, shipdateFrom, shipdateTo, poLineIdList
					);
			// Update danh sach to chuyen duoc giao sx cho PO Line
			for (PContract_PO thePoline : pcontract_PO_list) {
				thePoline.setProductionlines(grantskuService.getProductionLines(thePoline.getId()));
				
				// set phuong thuc dong goi
				String packingnotice = thePoline.getPackingnotice();
				if(packingnotice == null || packingnotice.equals("null") || packingnotice.equals("")) {
					thePoline.setPhuongThucDongGoi("");
				}else {
					String phuongThucDongGoi = "";
					String[] listStr = packingnotice.split(";");
					for(Integer i=0; i<listStr.length;i++) {
						if(!listStr[i].equals("")) {
							Long packingTypeId = Long.parseLong(listStr[i]);
							PackingType packingType = packingService.findOne(packingTypeId);
							String packingTypeCode = packingType.getCode();
//										String packingTypeName = packingType.getName();
							if(phuongThucDongGoi.equals("")) {
								phuongThucDongGoi+= packingTypeCode;
							}else {
								phuongThucDongGoi+= "; " + packingTypeCode;
							}
						}
					}
					thePoline.setPhuongThucDongGoi(phuongThucDongGoi);
				}
			}
			
			response.data = pcontract_PO_list;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getPolineByPcontractProduct", method = RequestMethod.POST)
	public ResponseEntity<PContract_getbycontractproduct_response> getPolineByPcontractProduct (
			@RequestBody getall_poline_by_code_request entity, HttpServletRequest request) 
	{
		PContract_getbycontractproduct_response response = new PContract_getbycontractproduct_response();
		try {
//			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			long orgrootid_link = user.getRootorgid_link();
			
			Long pcontract_product_id = entity.pcontract_product_id;
			Long pcontractid_link = entity.pcontractid_link;
			Boolean isSpBo = entity.isSpBo;
			
//			System.out.println("pcontractid_link " + pcontractid_link);
//			System.out.println("pcontract_product_id req " + pcontract_product_id);
			response.data = new ArrayList<PContract_PO>();
			if(isSpBo) {
				// sp bo va sp don trong bo
				// kiem tra xem la sp bo hay sp don
				Long product_id = pcontract_product_id;
				Product product_req = productService.findOne(product_id);
				if(product_req.getProducttypeid_link() == ProductType.SKU_TYPE_PRODUCT_PAIR) {
					// bo -> tim po line sp bo
					List<PContract_PO> poLineList = pcontract_POService.getPoLineByPcontract(pcontractid_link, product_id);
					response.data = poLineList;
				}else if(product_req.getProducttypeid_link() == ProductType.SKU_TYPE_COMPLETEPRODUCT) {
					// don -> tim po line sp bo
					List<ProductPairing> productPairingList = productpairService.getproduct_byproduct_inpair(product_id);
					for(ProductPairing productPairing : productPairingList) {
						Long productpairid_link = productPairing.getProductpairid_link();
						List<PContract_PO> poLineList = pcontract_POService.getPoLineByPcontract(pcontractid_link, productpairid_link);
						response.data.addAll(poLineList);
					}
				}
			}else {
				// sp don
				PContractProduct pcontractProduct = pcontractProductService.findOne(pcontract_product_id);
				Long pcontractid = pcontractProduct.getPcontractid_link();
				Long productId = pcontractProduct.getProductid_link();
				
				List<PContract_PO> poLineList = pcontract_POService.getPoLineByPcontract(pcontractid, productId);
				response.data = poLineList;
			}
			

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_getbycontractproduct_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/deselectPContractBom2Npl", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> deselectPContractBom2Npl (
			@RequestBody deselectPContractBom2Npl_request entity, HttpServletRequest request) 
	{
		ChartProductShipDateResponse response = new ChartProductShipDateResponse();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			Long orgid_link = user.getOrgid_link();

			Long material_skuid_link = entity.material_skuid_link;
			Long pcontractid_link = entity.pcontractid_link;
			Long productid_link = entity.productid_link;
			List<Long> pcontract_poid_link_list = entity.pcontract_poid_link_list;
			
			// tìm xem nếu là sp đơn trong bộ thì đổi thành id sp bộ
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
			
			for(Long pcontract_poid_link : pcontract_poid_link_list) {
				PContract_PO po = pcontract_POService.findOne(pcontract_poid_link);
				for(Long productid : productidList) {
					if(productid.equals(po.getProductid_link())) {
						List<PContract_bom2_npl_poline> pcontract_bom2_npl_poline_list = bomnplService.getby_Pcontract_Product_Material_skuid_link_PcontractPo(
								pcontractid_link, productid, material_skuid_link, pcontract_poid_link);
						for(PContract_bom2_npl_poline pcontract_bom2_npl_poline : pcontract_bom2_npl_poline_list) {
							List<PContract_bom2_npl_poline_sku> pcontract_bom2_npl_poline_sku_list = bomnplskuService.getby_Pcontract_Product_Material_skuid_link_PcontractPo(
									orgrootid_link, pcontractid_link, productid, material_skuid_link, pcontract_poid_link);
							for(PContract_bom2_npl_poline_sku pcontract_bom2_npl_poline_sku : pcontract_bom2_npl_poline_sku_list) {
								bomnplskuService.delete(pcontract_bom2_npl_poline_sku);
							}
							bomnplService.delete(pcontract_bom2_npl_poline);
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
	
	@RequestMapping(value = "/selectAllPContractBom2Npl", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> selectAllPContractBom2Npl (
			@RequestBody deselectPContractBom2Npl_request entity, HttpServletRequest request) 
	{
		ChartProductShipDateResponse response = new ChartProductShipDateResponse();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			Long orgid_link = user.getOrgid_link();

			Long material_skuid_link = entity.material_skuid_link;
			Long pcontractid_link = entity.pcontractid_link;
			Long productid_link = entity.productid_link;
			List<Long> pcontract_poid_link_list = entity.pcontract_poid_link_list;
			
			// tìm xem nếu là sp đơn trong bộ thì đổi thành id sp bộ
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
			
			for(Long pcontract_poid_link : pcontract_poid_link_list) {
				PContract_PO po = pcontract_POService.findOne(pcontract_poid_link);
				for(Long productid : productidList) {
					if(productid.equals(po.getProductid_link())) {
						List<PContract_bom2_npl_poline> pcontract_bom2_npl_poline_list = bomnplService.getby_Pcontract_Product_Material_skuid_link_PcontractPo(
								pcontractid_link, productid, material_skuid_link, pcontract_poid_link);
						if(pcontract_bom2_npl_poline_list.size() == 0) {
							PContract_bom2_npl_poline newPContract_bom2_npl_poline = new PContract_bom2_npl_poline();
							newPContract_bom2_npl_poline.setId(null);
							newPContract_bom2_npl_poline.setPcontractid_link(pcontractid_link);
							newPContract_bom2_npl_poline.setPcontract_poid_link(pcontract_poid_link);
							newPContract_bom2_npl_poline.setNpl_skuid_link(material_skuid_link);
							bomnplService.save(newPContract_bom2_npl_poline);
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
	
	@RequestMapping(value = "/getForDashboardMer_ProgressStore", method = RequestMethod.POST)
	public ResponseEntity<PContract_PO_Progress_Binding_response> getForDashboardMer_ProgressStore (
			@RequestBody ChartRequest entity, HttpServletRequest request) 
	{
		PContract_PO_Progress_Binding_response response = new PContract_PO_Progress_Binding_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			Long orgid_link = user.getOrgid_link();
			
			Long pcontract_poid_link = entity.pcontract_poid_link;
			PContract_PO poLine = pcontract_POService.findOne(pcontract_poid_link);
			
//			List<POrderGrant> porderGrantList = porderGrantService.getby_pcontract_po_id(pcontract_poid_link);
			List<POrderGrant_SKU> porderGrantSKUList = porderGrantSkuService.getby_pcontract_po_id(pcontract_poid_link);
			Map<Long, PContract_PO_Progress_Binding> map = new HashMap<Long, PContract_PO_Progress_Binding>();
			for(POrderGrant_SKU porderGranSKU : porderGrantSKUList) {
				Long porderGrantId = porderGranSKU.getPordergrantid_link();
				POrderGrant porderGrant = porderGrantService.findOne(porderGrantId);
				Long granttoorgid_link = porderGrant.getGranttoorgid_link();
				if(map.containsKey(granttoorgid_link)) {
					PContract_PO_Progress_Binding pcontract_PO_Progress_Binding = map.get(granttoorgid_link);
					Integer total = pcontract_PO_Progress_Binding.getTotal();
					total += porderGranSKU.getGrantamount() == null ? 0 : porderGranSKU.getGrantamount();
					Integer sumDone = 0 ;
					Integer sumNotDone = total - sumDone;
					pcontract_PO_Progress_Binding.setTotal(total);
					pcontract_PO_Progress_Binding.setSumDone(sumDone);
					pcontract_PO_Progress_Binding.setSumNotDone(sumNotDone);
					map.put(granttoorgid_link, pcontract_PO_Progress_Binding);
					
				}else {
					PContract_PO_Progress_Binding newPContract_PO_Progress_Binding = new PContract_PO_Progress_Binding();
					Org org = orgService.findOne(granttoorgid_link);
					newPContract_PO_Progress_Binding.setOrgid_link(org.getId());
					newPContract_PO_Progress_Binding.setOrgName(org.getName());
					newPContract_PO_Progress_Binding.setOrgCode(org.getCode());
					
					Integer total = porderGranSKU.getGrantamount() == null ? 0 : porderGranSKU.getGrantamount();
					Integer sumDone = 0 ;
					Integer sumNotDone = total - sumDone;
					
					newPContract_PO_Progress_Binding.setTotal(total);
					newPContract_PO_Progress_Binding.setSumDone(sumDone);
					newPContract_PO_Progress_Binding.setSumNotDone(sumNotDone);
					map.put(granttoorgid_link, newPContract_PO_Progress_Binding);
				}
			}
			
			List<PContract_PO_Progress_Binding> result = new ArrayList<PContract_PO_Progress_Binding>(map.values());
			Comparator<PContract_PO_Progress_Binding> compareByCode = (PContract_PO_Progress_Binding a1, PContract_PO_Progress_Binding a2) 
					-> a1.getOrgCode().compareTo( a2.getOrgCode());
			Collections.sort(result, compareByCode);
			
			response.data = result;
//			System.out.println("result " + response.data.size());

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<PContract_PO_Progress_Binding_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<PContract_PO_Progress_Binding_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getForDashboardMer", method = RequestMethod.POST)
	public ResponseEntity<getpo_by_product_response> getForDashboardMer (
			@RequestBody ChartRequest entity, HttpServletRequest request) 
	{
		getpo_by_product_response response = new getpo_by_product_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			Long orgid_link = user.getOrgid_link();
			
			if(orgid_link.equals((long) 1)) { // của tổng công ty -> null -> lấy hết
				orgid_link = null;
			}
			
			List<Long> productIdList = entity.productIdList;
			Integer status = entity.status;
			String contract_code = entity.contract_code == null ? "" : entity.contract_code.trim();
			String product_code = entity.product_code == null ? "" : entity.product_code.trim();
			String po_code = entity.po_code == null ? "" : entity.po_code.trim();
			Long buyer = entity.buyer;
			Long vendor = entity.vendor;
			
			List<PContract_PO> data = new ArrayList<PContract_PO>();
			data = getDsPoLineCanGiaoTheoNgay(orgid_link, status, contract_code, product_code, po_code, buyer, vendor);
			
			response.data = data;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getpo_by_product_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getpo_by_product_response>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getProductShipDateChart", method = RequestMethod.POST)
	public ResponseEntity<ChartProductShipDateResponse> getProductShipDateChart (
			@RequestBody ChartRequest entity, HttpServletRequest request) 
	{
		ChartProductShipDateResponse response = new ChartProductShipDateResponse();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();
			Long orgid_link = user.getOrgid_link();
			
			if(orgid_link.equals((long) 1)) { // của tổng công ty -> null -> lấy hết
				orgid_link = null;
			}
			
			String contract_code = entity.contract_code == null ? "" : entity.contract_code.trim();
			String product_code = entity.product_code == null ? "" : entity.product_code.trim();
			String po_code = entity.po_code == null ? "" : entity.po_code.trim();
			Long buyer = entity.buyer;
			Long vendor = entity.vendor;
			
			List<PContract_POBinding> data = new ArrayList<PContract_POBinding>();
			// giao trong 3 ngay : 1
			PContract_POBinding maHangCanGiao3ngay = getSLMaHangCanGiaoTheoNgay(orgid_link, 1, contract_code, product_code, po_code, buyer, vendor);
			data.add(maHangCanGiao3ngay);
			// giao trong 5 ngay : 2
			PContract_POBinding maHangCanGiao5ngay = getSLMaHangCanGiaoTheoNgay(orgid_link, 2, contract_code, product_code, po_code, buyer, vendor);
			data.add(maHangCanGiao5ngay);
			// giao trong 10 ngay : 3
			PContract_POBinding maHangCanGiao10ngay = getSLMaHangCanGiaoTheoNgay(orgid_link, 3, contract_code, product_code, po_code, buyer, vendor);
			data.add(maHangCanGiao10ngay);
			// cham giao hang : 0
			PContract_POBinding maHangChamGiaoHang = getSLMaHangCanGiaoTheoNgay(orgid_link, 0, contract_code, product_code, po_code, buyer, vendor);
			data.add(maHangChamGiaoHang);
			
			// chậm giao hàng (đã quá ngày giao hàng nhưng vẫn chưa giao đủ)
			// gửi lên thông tin 
			// mã đơn hàng: contract_code
			// mã sp: product_code
			// mã po: po_code
			// buyer, vendor
			
			
			response.data = data;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ChartProductShipDateResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ChartProductShipDateResponse>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	private PContract_POBinding getSLMaHangCanGiaoTheoNgay (Long orgid_link, Integer status,
			String contract_code, String product_code, String po_code, Long buyer, Long vendor) {
		Integer dayFrom = null;
		Integer dayTo = null;
		String statusName = "";
		switch(status) {
			case 0:
				statusName = "Chậm giao hàng"; // chậm 10 ngày
				dayFrom = -20;
				dayTo = -1;
				break;
			case 1:
				statusName = "3 ngày";
				dayFrom = 0;
				dayTo = 3;
				break;
			case 2:
				statusName = "5 ngày";
				dayFrom = 4;
				dayTo = 5;
				break;
			case 3:
				statusName = "10 ngày";
				dayFrom = 6;
				dayTo = 10;
				break;
		}
		
		Date today = new Date();
		Date dateFrom = null;
		Date dateTo = null;
		Calendar calendar = Calendar.getInstance();
		if(dayFrom != null) {
			calendar.setTime(today);
			calendar.add(Calendar.DATE, dayFrom);
			dateFrom = calendar.getTime();
		}
		if(dayTo != null) {
			calendar = Calendar.getInstance();
			calendar.setTime(today);
			calendar.add(Calendar.DATE, dayTo);
			dateTo = calendar.getTime();
		}
		
		List<Long> maHangCanGiao = pcontract_POService.getMaHangCanGiao(orgid_link, dateFrom, dateTo, contract_code, product_code, po_code, buyer, vendor);
		
		List<Long> maHangCanGiaoChuaHoanThanh = new ArrayList<Long>();
		
		for(Long maHangId : maHangCanGiao) {
			Boolean chuaHoanThanh = false;
			Integer statusDaGiaoHang = POStatus.PO_STATUS_DELIVERED;
			// tim danh sach cac poline cua maHang nay, loop qua ds poline, neu co poline co status < statusDaGiaoHang -> chua giao -> chuaHoanThanh -> add
			List<PContract_PO> dsPoline = new ArrayList<PContract_PO>();
			if(status != 0) {
				dsPoline = pcontract_POService.getPOLineChuaGiao(
						orgid_link, dateFrom, dateTo, contract_code, maHangId, po_code, buyer, vendor, statusDaGiaoHang);
			}else {
//				dsPoline = pcontract_POService.getPOLineChuaGiao_Cham(
//						orgid_link, today, contract_code, maHangId, po_code, buyer, vendor, statusDaGiaoHang);
				dsPoline = pcontract_POService.getPOLineChuaGiao(
						orgid_link, dateFrom, dateTo, contract_code, maHangId, po_code, buyer, vendor, statusDaGiaoHang);
			}
			
			if(dsPoline.size() > 0) {
				chuaHoanThanh = true;
			}
			if(chuaHoanThanh) {
				maHangCanGiaoChuaHoanThanh.add(maHangId);
			}
		}
		
		Integer sum = maHangCanGiaoChuaHoanThanh.size();
		List<String> tenMaHangCanGiao = new ArrayList<String>();
		for(Long maHangId : maHangCanGiaoChuaHoanThanh) {
			Product product = productService.findOne(maHangId);
			String buyercode = product.getBuyercode() == null ? "" : product.getBuyercode();
			if(!buyercode.equals("")) {
				tenMaHangCanGiao.add(buyercode);
			}
		}
		Collections.sort(tenMaHangCanGiao);
		
		PContract_POBinding pcontract_POBinding = new PContract_POBinding();
		pcontract_POBinding.setStatus(status);
		pcontract_POBinding.setSum(sum.longValue());
		pcontract_POBinding.setStatusName(statusName);
		pcontract_POBinding.setProductIdList(maHangCanGiaoChuaHoanThanh);
		pcontract_POBinding.setProductBuyerCodeList(tenMaHangCanGiao);
		return pcontract_POBinding;
	}
	
	private List<PContract_PO> getDsPoLineCanGiaoTheoNgay (Long orgid_link, Integer status,
			String contract_code, String product_code, String po_code, Long buyer, Long vendor) {
		Integer dayFrom = null;
		Integer dayTo = null;
		switch(status) {
			case 0:
				dayFrom = -20; // chậm 10 ngày
				dayTo = -1;
				break;
			case 1:
				dayFrom = 0;
				dayTo = 3;
				break;
			case 2:
				dayFrom = 4;
				dayTo = 5;
				break;
			case 3:
				dayFrom = 6;
				dayTo = 10;
				break;
		}
		
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, dayFrom);
		Date dateFrom = calendar.getTime();
		calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, dayTo);
		Date dateTo = calendar.getTime();
		
		List<PContract_PO> poLineList = new ArrayList<PContract_PO>();
		poLineList = pcontract_POService.getDsPoLineCanGiao(orgid_link, dateFrom, dateTo, contract_code, product_code, po_code, buyer, vendor);
		
		return poLineList;
	}
}
