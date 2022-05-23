package vn.gpay.gsmart.core.api.Upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.gpay.gsmart.core.attribute.Attribute;
import vn.gpay.gsmart.core.attribute.IAttributeService;
import vn.gpay.gsmart.core.attributevalue.Attributevalue;
import vn.gpay.gsmart.core.attributevalue.IAttributeValueService;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.category.ILaborLevelService;
import vn.gpay.gsmart.core.category.IShipModeService;
import vn.gpay.gsmart.core.category.LaborLevel;
import vn.gpay.gsmart.core.category.ShipMode;
import vn.gpay.gsmart.core.devices_type.Devices_Type;
import vn.gpay.gsmart.core.devices_type.IDevices_TypeService;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.packingtype.IPackingTypeService;
import vn.gpay.gsmart.core.packingtype.PackingType;
import vn.gpay.gsmart.core.pcontract.IPContractService;
import vn.gpay.gsmart.core.pcontract.PContract;
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontract_po_productivity.IPContract_PO_Productivity_Service;
import vn.gpay.gsmart.core.pcontract_po_productivity.PContract_PO_Productivity;
import vn.gpay.gsmart.core.pcontract_price.IPContract_Price_DService;
import vn.gpay.gsmart.core.pcontract_price.IPContract_Price_Service;
import vn.gpay.gsmart.core.pcontract_price.PContract_Price;
import vn.gpay.gsmart.core.pcontract_price.PContract_Price_D;
import vn.gpay.gsmart.core.pcontractproduct.IPContractProductService;
import vn.gpay.gsmart.core.pcontractproduct.PContractProduct;
import vn.gpay.gsmart.core.pcontractproductpairing.IPContractProductPairingService;
import vn.gpay.gsmart.core.pcontractproductpairing.PContractProductPairing;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKUService;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.personel.Personel;
import vn.gpay.gsmart.core.porder.IPOrder_Service;
import vn.gpay.gsmart.core.porder_req.IPOrder_Req_Service;
import vn.gpay.gsmart.core.porder_req.POrder_Req;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.product_balance.IProductBalanceService;
import vn.gpay.gsmart.core.product_balance.ProductBalance;
import vn.gpay.gsmart.core.product_balance_process.IProductBalanceProcessService;
import vn.gpay.gsmart.core.product_balance_process.ProductBalanceProcess;
import vn.gpay.gsmart.core.product_sewingcost.IProductSewingCostService;
import vn.gpay.gsmart.core.product_sewingcost.ProductSewingCost;
import vn.gpay.gsmart.core.productattributevalue.IProductAttributeService;
import vn.gpay.gsmart.core.productattributevalue.ProductAttributeValue;
import vn.gpay.gsmart.core.productpairing.IProductPairingService;
import vn.gpay.gsmart.core.productpairing.ProductPairing;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.sizeset.ISizeSetService;
import vn.gpay.gsmart.core.sizeset.SizeSet;
import vn.gpay.gsmart.core.sizesetattributevalue.ISizeSetAttributeService;
import vn.gpay.gsmart.core.sizesetattributevalue.SizeSetAttributeValue;
import vn.gpay.gsmart.core.sku.ISKU_AttributeValue_Service;
import vn.gpay.gsmart.core.sku.ISKU_Service;
import vn.gpay.gsmart.core.sku.SKU;
import vn.gpay.gsmart.core.sku.SKU_Attribute_Value;
import vn.gpay.gsmart.core.utils.*;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadAPI {
	@Autowired Common commonService;
	@Autowired IProductService productService;
	@Autowired IAttributeService attrService;
	@Autowired IProductAttributeService pavService;
	@Autowired ISKU_Service skuService;
	@Autowired ISKU_AttributeValue_Service skuattService;
	@Autowired IProductPairingService productpairService;
	@Autowired IPContract_Price_DService pricedetailService;
	@Autowired IPContractProductService pcontractproductService;
	@Autowired IPContractProductPairingService pcontractpairService;
	@Autowired IShipModeService shipmodeService;
	@Autowired IOrgService orgService;
	@Autowired IPContract_POService pcontract_POService;
	@Autowired IPContract_Price_Service priceService;
	@Autowired IPContract_PO_Productivity_Service productivityService;
	@Autowired ISizeSetService sizesetService;
	@Autowired IPOrder_Req_Service reqService;
	@Autowired IPOrder_Service porderService;
	@Autowired IPContractService pcontractService;
	@Autowired IPackingTypeService packingService;
	@Autowired IAttributeValueService attributevalueService;
	@Autowired IPContractProductSKUService ppskuService;
	@Autowired ISizeSetAttributeService sizeset_att_Service;
	@Autowired IProductSewingCostService productSewingCostService;
	@Autowired IDevices_TypeService devicesTypeService;
	@Autowired ILaborLevelService laborLevelService;
	@Autowired IProductBalanceService productBalanceService;
	@Autowired IProductBalanceProcessService productBalanceProcessService;

	@RequestMapping(value = "/personnelUploadProductSewingCost", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPersonnelProductSewingCost(HttpServletRequest request,
															  @RequestParam("file") MultipartFile file, 
															  @RequestParam("pcontractid_link") long pcontractid_link,
															  @RequestParam("productid_link") long productid_link) {
		ResponseBase response = new ResponseBase();
		String name = "";
		String mes_err = "";
		List<ProductSewingCost> productSewingCostList = new ArrayList<ProductSewingCost>();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long orgrootid_link = user.getRootorgid_link();
		Long orgid_link = user.getOrgid_link();
		Date current_time = new Date();
		try {
			String FolderPath = "upload/productsewingcost";
			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);
			File uploadRootDir = new File(uploadRootPath);
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

				int rowNum = 1;
				// int colNum = 1;

				Row row = sheet.getRow(rowNum);
				try {
					String Stt = "";
					Stt = commonService.getStringValue(row.getCell(ColumnProductSewingCost.STT)).trim(); // null or "0" return ""
//					Stt = Stt.equals("0") ? "" : Stt;
					while (!Stt.equals("")) {
						Integer ThoiGian = 0;
						Float DonGia = 0f;
						Integer SoLuong = 0;
						Long ThietBi = 0l;
						Long BacTho = 0l;
						// Lay du lieu tu o Excel
						String tenCongDoan = commonService.getStringValue(row.getCell(ColumnProductSewingCost.TenCongDoan)).trim();
						if (tenCongDoan.equals("")) {
							mes_err = "Tên công đoạn ở dòng thứ " + (rowNum + 1) + "không hợp lệ";
							break;
						}
						String maCongDoan = commonService.getStringValue(row.getCell(ColumnProductSewingCost.MaCongDoan)).trim();
						if (maCongDoan.equals("")) {
							maCongDoan = tenCongDoan;
						}
						String cumCongDoan = commonService.getStringValue(row.getCell(ColumnProductSewingCost.CumCongDoan)).trim();
						String thietBi = commonService.getStringValue(row.getCell(ColumnProductSewingCost.ThietBi)).trim();
						if (!thietBi.equals("")) {
							List<Devices_Type> a = devicesTypeService.loadDevicesTypeByName(thietBi); // Tim Id thiet bi theo ten
							if (a.size() == 0) {
								Devices_Type newDeviceType = new Devices_Type();
								newDeviceType.setName(thietBi);
								ThietBi = (devicesTypeService.save(newDeviceType)).getId();
							} else {
								ThietBi = a.get(0).getId();
							}
						}
						String bacTho = commonService.getStringValue(row.getCell(ColumnProductSewingCost.BacTho)).trim();
						if (!bacTho.equals("")) {
							List<LaborLevel> a = laborLevelService.findByName(bacTho);
							if (a.size() == 0) {
								LaborLevel newLaborLevel = new LaborLevel();
								newLaborLevel.setName(bacTho);
								BacTho = (laborLevelService.save(newLaborLevel)).getId();
							} else {
								BacTho = a.get(0).getId();
							}
						}
						String thoiGian = commonService.getStringValue(row.getCell(ColumnProductSewingCost.ThoiGian)).trim();
						if (!thoiGian.equals("")) {
							ThoiGian = Integer.parseInt(thoiGian);
						}
						String chuThich = commonService.getStringValue(row.getCell(ColumnProductSewingCost.ChuThich)).trim();
						String donGia = commonService.getStringValue(row.getCell(ColumnProductSewingCost.DonGia)).trim();
						if (!donGia.equals("")) {
							DonGia = Float.parseFloat(donGia);
						}
						String soLuong = commonService.getStringValue(row.getCell(ColumnProductSewingCost.SoLuong)).trim();
						if (!soLuong.equals("")) {
							SoLuong = Integer.parseInt(soLuong);
						}

						List<ProductSewingCost> productSewingCosts = productSewingCostService.findByProductPcontractName(productid_link, pcontractid_link, tenCongDoan);
						// Them moi Cong Doan
						if (productSewingCosts.size() == 0) {
							ProductSewingCost productSewingCost = new ProductSewingCost();
							productSewingCost.setName(tenCongDoan);
							productSewingCost.setCode(maCongDoan);
							productSewingCost.setOrgrootid_link(orgrootid_link);
							productSewingCost.setProductid_link(productid_link);
							productSewingCost.setPcontractid_link(pcontractid_link);
							productSewingCost.setOrgcreatedid_link(user.getOrgid_link());
							productSewingCost.setUsercreatedid_link(user.getId());
							if (DonGia != 0) {
								productSewingCost.setCost(DonGia);
							}
							if (SoLuong != 0) {
								productSewingCost.setAmount(SoLuong);
							}
							if (DonGia != 0 && SoLuong != 0) {
								productSewingCost.setTotalcost(DonGia * SoLuong);
							}
							productSewingCost.setDatecreated(current_time);
							if (ThietBi != 0) {
								productSewingCost.setDevicerequiredid_link(ThietBi);
							}
							if (BacTho != 0) {
								productSewingCost.setLaborrequiredid_link(BacTho);
							}
							if (ThoiGian != 0) {
								productSewingCost.setTimespent_standard(ThoiGian);
							}
							if (chuThich.equals("")) {
								productSewingCost.setTechcomment(chuThich);
							}

							if(cumCongDoan.equals("")) {
								productSewingCostService.save(productSewingCost);
							} else {
								List <ProductBalance> productBalances = productBalanceService.getByProduct(productid_link, pcontractid_link);
								Long id = 0l;
								boolean exist = false;
								for(ProductBalance a : productBalances) {
									if(a.getBalance_name().toLowerCase().trim().equals(cumCongDoan.toLowerCase().trim())) {
										exist = true;
										id = a.getId();
										break;
									}
								}
								ProductBalanceProcess productBalanceProcess = new ProductBalanceProcess();
								productBalanceProcess.setOrgrootid_link(orgrootid_link);
								productBalanceProcess.setProductsewingcostid_link(productSewingCostService.save(productSewingCost).getId());
								if(exist) {
									productBalanceProcess.setProductbalanceid_link(id);
								} else {
									ProductBalance productBalance = new ProductBalance();
									productBalance.setBalance_name(cumCongDoan);
									productBalance.setProductid_link(productid_link);
									productBalance.setPcontractid_link(pcontractid_link);
									productBalance.setOrgrootid_link(orgrootid_link);

									productBalanceProcess.setProductbalanceid_link(productBalanceService.save(productBalance).getId());
								}
								productBalanceProcessService.save(productBalanceProcess);
							}
						} else { // Update Cong Doan trong DB
							List <ProductSewingCost> productSewingCostsInBalance = productSewingCostService.findByProductPcontractNameInBalance(productid_link, pcontractid_link, tenCongDoan);
							if(productSewingCostsInBalance.size() != 0) {
							List <ProductBalanceProcess> productBalanceProcess = productBalanceProcessService.getByProductSewingcost(productSewingCostsInBalance.get(0).getId());
							productSewingCostsInBalance.get(0).setCode(maCongDoan);
							productSewingCostsInBalance.get(0).setDevicerequiredid_link(ThietBi);
							productSewingCostsInBalance.get(0).setLaborrequiredid_link(BacTho);
							productSewingCostsInBalance.get(0).setTimespent_standard(ThoiGian);
							productSewingCostsInBalance.get(0).setTechcomment(chuThich);
							productSewingCostsInBalance.get(0).setCost(DonGia);
							productSewingCostsInBalance.get(0).setAmount(SoLuong);
							if(DonGia != 0 && SoLuong != 0) {
								productSewingCostsInBalance.get(0).setTotalcost(DonGia * SoLuong);
							}
							productSewingCostService.save(productSewingCostsInBalance.get(0));
								if (cumCongDoan.equals("")) {
									productBalanceProcessService.deleteById(productBalanceProcess.get(0).getId());
								} else {
									// Moi don hang va san pham, cum cong doan khong the trung nhau
									List <ProductBalance> productBalances = productBalanceService.findByProductPcontractName(productid_link, pcontractid_link, cumCongDoan);
									if(productBalances.size() != 0) {
										productBalanceProcess.get(0).setProductbalanceid_link(productBalances.get(0).getId());
									} else {
										ProductBalance newProductBalance = new ProductBalance();
										newProductBalance.setOrgrootid_link(orgrootid_link);
										newProductBalance.setBalance_name(cumCongDoan);
										newProductBalance.setPcontractid_link(pcontractid_link);
										newProductBalance.setProductid_link(productid_link);
										productBalanceProcess.get(0).setProductbalanceid_link(productBalanceService.save(newProductBalance).getId());
									}
									productBalanceProcessService.save(productBalanceProcess.get(0));
								}
							} else {
								// Voi moi don hang va san pham, chi co 1 ten cong doan khong nam trong cum cong doan.
								List <ProductSewingCost> productSewingCostsOutBalance = productSewingCostService.findByProductPcontractNameOutBalance(productid_link, pcontractid_link, tenCongDoan);
								productSewingCostsOutBalance.get(0).setCode(maCongDoan);
								productSewingCostsOutBalance.get(0).setDevicerequiredid_link(ThietBi);
								productSewingCostsOutBalance.get(0).setLaborrequiredid_link(BacTho);
								productSewingCostsOutBalance.get(0).setTimespent_standard(ThoiGian);
								productSewingCostsOutBalance.get(0).setTechcomment(chuThich);
								productSewingCostsOutBalance.get(0).setCost(DonGia);
								productSewingCostsOutBalance.get(0).setAmount(SoLuong);
								if(DonGia != 0 && SoLuong != 0) {
									productSewingCostsOutBalance.get(0).setTotalcost(DonGia * SoLuong);
								}
								productSewingCostService.save(productSewingCostsOutBalance.get(0));
								if(!cumCongDoan.equals("")) {
									// Voi moi don hang va san pham, cum cong doan khong duoc trung
									List <ProductBalance> productBalances = productBalanceService.findByProductPcontractName(productid_link, pcontractid_link, cumCongDoan);
									ProductBalanceProcess newProductBalanceProcess = new ProductBalanceProcess();
									newProductBalanceProcess.setProductsewingcostid_link(productSewingCostsOutBalance.get(0).getId());
									if(productBalances.size() != 0) {
										newProductBalanceProcess.setProductbalanceid_link(productBalances.get(0).getId());
									} else {
										ProductBalance newProductBalance = new ProductBalance();
										newProductBalance.setOrgrootid_link(orgrootid_link);
										newProductBalance.setBalance_name(cumCongDoan);
										newProductBalance.setProductid_link(productid_link);
										newProductBalance.setPcontractid_link(pcontractid_link);

										newProductBalanceProcess.setProductbalanceid_link(productBalanceService.save(newProductBalance).getId());
									}
									productBalanceProcessService.save(newProductBalanceProcess);
								}
							}
						}
					rowNum++;
					row = sheet.getRow(rowNum);
					if (row == null)
						break;
					Stt = commonService.getStringValue(row.getCell(ColumnProductSewingCost.STT)).trim();
					Stt = Stt.equals("0") ? "" : Stt;
					}
				} catch(Exception e){
					e.printStackTrace();
					mes_err = "Có lỗi ở dòng " + (rowNum + 1) + " " + mes_err;
				} finally{
					workbook.close();
					serverFile.delete();
				}
				// neu co loi
				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			}
		} catch(Exception e){
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}

		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}
	//upload chao gia
	@RequestMapping(value = "/offers", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadTemplate(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, @RequestParam("pcontractid_link") long pcontractid_link) {
		ResponseBase response = new ResponseBase();

		Date current_time = new Date();
		String name = "";
		String mes_err = "";
		try {
			PContract pcontract = pcontractService.findOne(pcontractid_link);
			if (pcontract == null) {
				mes_err = "Đơn hàng không tồn tại trong hệ thống! Bạn hãy kiểm tra lại";
			}

			if (mes_err == "") {
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
					/*
					 * File excel mau co 2 row dau la danh cho Header
					 * Du lieu bat dau tu row thu 3 --> default rowNum = 2
					 */
					int rowNum = 2;
					int colNum = 1, col_phancach1 = ColumnTempNew.vendor_target + 1, col_phancach2 = 0;

					Row row = sheet.getRow(rowNum);
					Row rowheader = sheet.getRow(0);

					try {
						/*
						 * Cot STT chua thong tin cac dong thong tin can xu ly
						 * Cot STT co gia tri tu 1; gia tri 0 --> ko xu ly
						 * Doc cho den khi nao gap gia tri STT ="" thi moi thoi
						 */
						String STT = "";
						STT = commonService.getStringValue(row.getCell(ColumnTempNew.STT));
						STT = STT.equals("0") ? "" : STT;
						
						while (!STT.equals("")) {
							// Lay thong tin PO kiem tra xem PO da ton tai trong he thong hay chua
							// Neu la san pham don chiec thi kiem tra masp, ngay giao, vendor target
							// Neu la san pham bo thi kiem tra masp bo, ngay giao
//							if(rowNum == 14) {
//								String a = "";
//							}
//							else if(rowNum == 15) {
//								String a = "";
//							}
							// Kiem tra san pham co chua thi them san pham vao trong he thong
							long productid_link = 0;
							colNum = ColumnTempNew.MaSP + 1;
							String product_code = commonService.getStringValue(row.getCell(ColumnTempNew.MaSP)).trim();

							colNum = ColumnTempNew.TenSP + 1;
							String stylename = commonService.getStringValue(row.getCell(ColumnTempNew.TenSP)).trim();
							
							colNum = ColumnTempNew.MoTaSP + 1;
							String MoTaSP = commonService.getStringValue(row.getCell(ColumnTempNew.MoTaSP)).trim();

							if (product_code == "") {
								mes_err = "Mã Sản phẩm không được để trống";
								break;
							}

							/*
							 * Tim san pham (product) voi MaSP Buyer duoc cung cap. Neu chua co --> Tao moi san pham
							 */
							List<Product> products = productService.getone_by_code(orgrootid_link, product_code,
									(long) 0, ProductType.SKU_TYPE_COMPLETEPRODUCT);
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
								p.setDescription(MoTaSP);
								p = productService.save(p);

								productid_link = p.getId();

								// Sinh thuoc tinh mac dinh cho san pham
								List<Attribute> lstAttr = attrService.getList_attribute_forproduct(
										ProductType.SKU_TYPE_COMPLETEPRODUCT, user.getRootorgid_link());
								for (Attribute attribute : lstAttr) {
									ProductAttributeValue pav = new ProductAttributeValue();
									long value = 0;

									if (attribute.getId() == AtributeFixValues.ATTR_COLOR) {
										value = AtributeFixValues.value_color_all; //Mau
									} else if (attribute.getId() == AtributeFixValues.ATTR_SIZE) {
										value = AtributeFixValues.value_size_all; //Co
									} else if (attribute.getId() == AtributeFixValues.ATTR_SIZEWIDTH) {
										value = AtributeFixValues.value_sizewidth_all; //Co kho
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

							} 
							else {
								//San pham da duoc khai bao trong he thong
								Product p = products.get(0);
								p.setDescription(MoTaSP);
								p = productService.save(p);
								productid_link = p.getId();
							}

							// Kiem tra xem PO co phai la hang bo hay khong
							/*
							 * Neu cot 3 - Style set co thong tin thi day la hang bo
							 */
							long product_set_id_link = 0;

							//Ma hang bo
							colNum = ColumnTempNew.Style_Set + 1;
							String product_set_code = commonService.getStringValue(row.getCell(ColumnTempNew.Style_Set));
							product_set_code = product_set_code.equals("0") ? "" : product_set_code;

							//Ma vendor cua hang bo
							colNum = ColumnTempNew.Style_Set_Vendor + 1;
							String product_set_code_vendor = commonService.getStringValue(row.getCell(ColumnTempNew.Style_Set_Vendor));
							product_set_code_vendor = product_set_code_vendor.equals("0") ? "": product_set_code_vendor;

							//So luong san pham trong bo
							colNum = ColumnTempNew.amount_style + 1;
							String s_amount = commonService.getStringValue(row.getCell(ColumnTempNew.amount_style));
							s_amount = s_amount.replace(",", "");
							int amount = (int) row.getCell(ColumnTempNew.amount_style).getNumericCellValue() == 0 ? 1
									: (int) row.getCell(ColumnTempNew.amount_style).getNumericCellValue();

							/*
							 * Neu la san pham bo --> Kiem tra neu chua co thi them Bo vao bang Product
							 */
							if (!product_set_code.equals(null) && !product_set_code.equals("")) {
								List<Product> product_set = productService.getone_by_code(orgrootid_link,
										product_set_code, (long) 0, ProductType.SKU_TYPE_PRODUCT_PAIR);
								if (product_set.size() == 0) {
									Product set = new Product();
									set.setId(null);
									set.setBuyercode(product_set_code);
									set.setVendorcode(product_set_code_vendor);
									set.setBuyername("");
									set.setDescription("");
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

							// kiem tra trong bang pcontract_product_pairing co thong tin cua bo chua. Neu chua co --> Them vao
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

									Product set = productService.findOne(product_set_id_link);
									String name_old = set.getBuyername();
									name_old = name_old.equals("") ? "" : name_old + "; ";
									String name_new = name_old + amount + "-" + product_code;
									set.setBuyername(name_new);
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

							//Phuong thuc van chuyen (tim theo ten)
							long shipmodeid_link = 0;
							colNum = ColumnTempNew.shipmode + 1;
							String shipmode_name = commonService.getStringValue(row.getCell(ColumnTempNew.shipmode));
							List<ShipMode> shipmode = shipmodeService.getbyname(shipmode_name);
							if (shipmode.size() > 0) {
								shipmodeid_link = shipmode.get(0).getId();
							}

							/*
							 * Lay ngay giao hang dau tien trong danh sach dot giao hang
							 * Ngay giao hang phai theo dinh dang dd/MM/yyyy
							 */
							List<Date> list_ngaygiao = new ArrayList<Date>();
							List<Integer> list_soluong = new ArrayList<Integer>();

							Date ShipDate = null;
							colNum = ColumnTempNew.ns_target + 2;

							try {
								String s_shipdate = commonService.getStringValue(row.getCell(colNum));
								if (s_shipdate.contains("/")) {
									String[] s_date = s_shipdate.split("/");
									if (Integer.parseInt(s_date[1].toString()) < 13
											&& Integer.parseInt(s_date[0].toString()) < 32) {
										ShipDate = new SimpleDateFormat("dd/MM/yyyy").parse(s_shipdate);
									} else {
										mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
									}

								} else if (s_shipdate != "") {
									if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
										ShipDate = row.getCell(colNum).getDateCellValue();
									}
								}

							} catch (Exception e) {
								if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
									ShipDate = row.getCell(colNum).getDateCellValue();
								}
							}

							if (ShipDate != null) {
								list_ngaygiao.add(ShipDate);
							}

							//Ngay giao hang tiep theo
							colNum = col_phancach1 + 3;

							String s_po_quantity = commonService
									.getStringValue(row.getCell(ColumnTempNew.ns_target + 3));
							s_po_quantity = s_po_quantity.replace(",", "");
							Float f_po_quantity = s_po_quantity.equals("") ? 0 : Float.parseFloat(s_po_quantity);
//po_quantity luu so luong tong cua ca don hang							
							int po_quantity = f_po_quantity.intValue();
							if (po_quantity > 0)
								list_soluong.add(po_quantity);

							col_phancach2 = ColumnTempNew.ns_target + 4;
							colNum = col_phancach2;
							String s_header_phancach2 = commonService.getStringValue(rowheader.getCell(col_phancach2));

/*
 * Duyet cac dot giao hang de tao PO Line ke hoach
 * Chay den khi gap cot xxx thi dung
 * Phan nay chi de cong vao, tinh ra so luong tong cua PO
 * list_ngaygiao chua danh sach cac dot giao hang (PO Line ke hoach)
 */
							while (!s_header_phancach2.equals("xxx")) {

								Date shipdate2 = null;
								try {
									String s_shipdate2 = commonService.getStringValue(row.getCell(colNum));
									if (s_shipdate2.contains("/")) {
										String[] s_date = s_shipdate2.split("/");
										if (Integer.parseInt(s_date[1].toString()) < 13
												&& Integer.parseInt(s_date[0].toString()) < 32) {
											shipdate2 = new SimpleDateFormat("dd/MM/yyyy").parse(s_shipdate2);
										} else {
											mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
										}

									} else if (s_shipdate2 != "") {
										if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
											shipdate2 = row.getCell(colNum).getDateCellValue();
										}
									}

								} catch (Exception e) {
									if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
										shipdate2 = row.getCell(colNum).getDateCellValue();
									}
								}

								if (shipdate2 != null) {
									list_ngaygiao.add(shipdate2);
									// Lay so luong
									String s_sub_quantity = commonService.getStringValue(row.getCell(colNum + 1));
									s_sub_quantity = s_sub_quantity.replace(",", "");
									Float f_sub_quantity = s_sub_quantity.equals("") ?0:Float.parseFloat(s_sub_quantity);
									po_quantity += f_sub_quantity.intValue();

									list_soluong.add(f_sub_quantity.intValue() * amount);

									if (ShipDate == null)
										ShipDate = shipdate2;
									else if (ShipDate.before(shipdate2)) {
										ShipDate = shipdate2;
									}
								}

								colNum += 2;

								col_phancach2 += 2;
								s_header_phancach2 = commonService.getStringValue(rowheader.getCell(col_phancach2));
//End duyet PO Line
							}

							// Kiem tra chao gia da ton tai hay chua
							long pcontractpo_id_link = 0;
							PContract_PO po_tong = new PContract_PO();
							long po_productid_link = product_set_id_link > 0 ? product_set_id_link : productid_link;

							colNum = ColumnTempNew.fob + 1;
							String s_price_fob = commonService.getStringValue(row.getCell(ColumnTempNew.fob));
							s_price_fob = s_price_fob.replace(",", "");
							float price_fob = s_price_fob.equals("") ? 0 : Float.parseFloat(s_price_fob);

//							colNum = ColumnTempNew.amount_style + 1;
//							String s_amount_style = commonService.getStringValue(row.getCell(ColumnTempNew.amount_style));
//							s_amount_style = s_amount_style.replace(",", "");
//							Float amount_style = s_amount_style == "" ? 0 : Float.parseFloat(s_amount_style);

							//Gia muc tieu cua khach
							colNum = ColumnTempNew.vendor_target + 1;
							String s_vendor_target = commonService.getStringValue(row.getCell(ColumnTempNew.vendor_target));
							s_vendor_target = s_vendor_target.replace(",", "");
							float vendor_target = s_vendor_target == "" ? 0 : Float.parseFloat(s_vendor_target);
							
							//Nang suat muc tieu
							colNum = ColumnTempNew.ns_target + 1;
							String s_ns_target = commonService.getStringValue(row.getCell(ColumnTempNew.ns_target));
							s_ns_target = s_ns_target.replace(",", "");
							Float ns_target = s_ns_target == "" ? 0 : Float.parseFloat(s_ns_target);

							//Phan xuong chinh
							colNum = ColumnTempNew.org + 1;
							String s_org_code = commonService.getStringValue(row.getCell(ColumnTempNew.org));
							s_org_code = s_org_code.replace(",", "");
							Long orgid_link = null;
							List<Org> list_org = orgService.getbycode(s_org_code, orgrootid_link);
							if (list_org.size() > 0) {
								orgid_link = list_org.get(0).getId();
							}

							//So ngay san xuat = So tong / Nang suat
							int productiondays_ns = ns_target == 0 ? 0 : po_quantity / (ns_target.intValue());

							//Ngay NPL ve
							colNum = ColumnTempNew.matdate + 1;
							Date matdate = null;

							try {
								String s_matdate = commonService.getStringValue(row.getCell(ColumnTempNew.matdate));
								if (s_matdate.contains("/")) {
									String[] s_date = s_matdate.split("/");
									if (Integer.parseInt(s_date[1].toString()) < 13
											&& Integer.parseInt(s_date[0].toString()) < 32) {
										matdate = new SimpleDateFormat("dd/MM/yyyy").parse(s_matdate);
									} else {
										mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
									}

								} else {
									if (DateUtil.isCellDateFormatted(row.getCell(ColumnTempNew.matdate))) {
										matdate = row.getCell(ColumnTempNew.matdate).getDateCellValue();
									}
								}

							} catch (Exception e) {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnTempNew.matdate))) {
									matdate = row.getCell(ColumnTempNew.matdate).getDateCellValue();
								}
							}
							
							/*
							 * Ngay vao chuyen (production_date) = Ngay NPL ve + 7
							 * So ngay sx (production_day) = Ngay giao hang - Ngay vao chuyen - Ngay nghi theo lich
							 * So chuyen yeu cau (plan_linerequired) =  Nang suat ngay / So ngay sx
							 */
							Date production_date = null;
							int production_day = 0;
							float plan_linerequired = 0;
							if (matdate != null) {
								production_date = Common.Date_Add(matdate, 7);
								production_day = commonService.getDuration(production_date, ShipDate, orgrootid_link);
								plan_linerequired = (float) productiondays_ns / (float) production_day;
								if (plan_linerequired < 1)
									plan_linerequired = 1;

								DecimalFormat df = new DecimalFormat("#.##");
								String formatted = df.format(plan_linerequired);
								plan_linerequired = Float.parseFloat(formatted);
							}

							//So PO
							colNum = ColumnTempNew.PO + 1;
							String PO_No = commonService.getStringValue(row.getCell(ColumnTempNew.PO));
							if (PO_No == "" || PO_No.equals("0")) {
								PO_No = "TBD";
							}
							
							//Trang thai PO (Da duyet(0) / Chua duyet(-1))
							colNum = ColumnTempNew.status + 1;
							String s_status = commonService.getStringValue(row.getCell(ColumnTempNew.status));
							Integer status = POStatus.PO_STATUS_UNCONFIRM;
							if (s_status.toLowerCase().equals("y")) {
								status = POStatus.PO_STATUS_CONFIRMED;
							}

/*
 * Row du lieu phai co it nhat 1 thong tin giao hang trong danh sach cac dot giao hang
 * Neu khong cot ngay giao hang --> Chuyen
 * Neu co --> Them Chao gia (PO chinh) va PO Line ke hoach
 * Ket thuc o dong 10055
 */
							if (ShipDate != null) {
								List<PContract_PO> listpo = new ArrayList<PContract_PO>();
								if (product_set_id_link == 0) {
									listpo = pcontract_POService.check_exist_po(ShipDate, po_productid_link,
											shipmodeid_link, pcontractid_link, PO_No);
								} else {
									listpo = pcontract_POService.check_exist_po(ShipDate, po_productid_link,
											shipmodeid_link, pcontractid_link, "");
								}
//Start add/update PO tong								
								if (listpo.size() == 0) {
									po_tong.setId(null);
									po_tong.setCurrencyid_link((long) 1);
									po_tong.setDatecreated(current_time);
									po_tong.setIsauto_calculate(true);
									po_tong.setOrgrootid_link(orgrootid_link);
									po_tong.setPcontractid_link(pcontractid_link);
									po_tong.setProductid_link(po_productid_link);
									po_tong.setUsercreatedid_link(user.getId());
									po_tong.setDate_importdata(current_time);
									po_tong.setPo_typeid_link(POType.PO_CMP);
									po_tong.setSewtarget_percent((float) 20);
									po_tong.setParentpoid_link(null);

									if (status == POStatus.PO_STATUS_CONFIRMED) {
										po_tong.setOrgmerchandiseid_link(orgid_link);
									}

								} else {
									po_tong = listpo.get(0);
								}

								po_tong.setProductiondays_ns(productiondays_ns);
								po_tong.setShipmodeid_link(shipmodeid_link);
								po_tong.setShipdate(ShipDate);
								po_tong.setMatdate(matdate);
								po_tong.setProductiondate(production_date);
								po_tong.setProductiondays(production_day);
								po_tong.setStatus(status);
								po_tong.setPo_buyer(PO_No);
								po_tong.setPo_vendor(PO_No);
								po_tong.setPo_quantity(po_quantity);
								po_tong.setIs_tbd(PO_No == "TBD" ? true : false);
								po_tong.setCode(PO_No);
								po_tong.setProductid_link(po_productid_link);

								po_tong = pcontract_POService.save(po_tong);
								pcontractpo_id_link = po_tong.getId();
//End Update PO tong
								
//Start tinh nang suat, ngay sx, so chuyen y/c cua san pham con --> Ghi vao bang pcontract_po_productivity
								List<PContract_PO_Productivity> list_productivity = productivityService
										.getbypo_and_product(pcontractpo_id_link, productid_link);
								PContract_PO_Productivity po_productivity = new PContract_PO_Productivity();
								if (list_productivity.size() == 0) {
									po_productivity.setId(null);
									po_productivity.setOrgrootid_link(orgrootid_link);
									po_productivity.setPcontract_poid_link(pcontractpo_id_link);
									po_productivity.setProductid_link(productid_link);
								} else {
									po_productivity = list_productivity.get(0);
								}

								po_productivity.setAmount(po_quantity * amount);
								po_productivity.setPlan_linerequired(plan_linerequired);
								po_productivity.setPlan_productivity(ns_target.intValue());
								po_productivity.setProductiondays_ns(productiondays_ns);
								productivityService.save(po_productivity);
//End tinh nang suat, ngay sx, so chuyen y/c cua san pham con
								
/*
 * Xoa cac dai co di roi insert lai vao san pham con hoac san pham don
 * Khi upload file --> Xoa dai co di tinh toan lai chu khong update
 */
								List<PContract_Price> list_price = priceService.getPrice_by_product(pcontractpo_id_link,
										productid_link);
								for (PContract_Price price : list_price) {
									List<PContract_Price_D> list_price_d = pricedetailService
											.getPrice_D_ByPContractPrice(price.getId());
									for (PContract_Price_D price_d : list_price_d) {
										pricedetailService.delete(price_d);
									}
									priceService.delete(price);
								}
//End Xoa dai co
								
/*
 * Sinh cac dai co va gia CMP cho tung dai co vao trong san pham con
 * Chot hien tai Max chi co 6 giai co (Infant, Monthly, Toddle, Normal, Big, Plus)
 * Bang: pcontract_price va pcontract_price_d
 */
								// Sinh cac dai co khac all
								float total_price_amount = 0;
								float total_amount = 0;
								float total_price = 0;
								int count = 0;

								for (int i = col_phancach2 + 1; i <= col_phancach2 + 6; i++) {
									colNum = i + 1;
									Row row2 = sheet.getRow(1);
									String sizesetname = commonService.getStringValue(row2.getCell(i));
									
									//Gia cua dai co
									String s_price_sizeset = commonService.getStringValue(row.getCell(i));
									s_price_sizeset = s_price_sizeset.replace(",", "");
									Float price_sizeset = s_price_sizeset.equals("") ? 0: Float.parseFloat(s_price_sizeset);

									//So luong cua dai co
									String s_amount_sizeset = commonService.getStringValue(row.getCell(i + 7));
									s_amount_sizeset = s_amount_sizeset.replace(",", "");
									Float amount_sizeset = s_amount_sizeset.equals("") ? 0: Float.parseFloat(s_amount_sizeset);

									if (price_sizeset > 0 || amount_sizeset > 0) {
										count++;
										total_price_amount += price_sizeset * amount_sizeset;
										total_amount += amount_sizeset;
										total_price += price_sizeset;

										Long sizesetid_link = sizesetService.getbyname(sizesetname);

										PContract_Price price = new PContract_Price();
										price.setId(null);
										price.setIs_fix(true);
										price.setOrgrootid_link(orgrootid_link);
										price.setPcontract_poid_link(pcontractpo_id_link);
										price.setPcontractid_link(pcontractid_link);
										price.setProductid_link(productid_link);
										price.setSizesetid_link(sizesetid_link == null ? 0 : sizesetid_link);
										price.setDate_importdata(current_time);
										price.setPrice_cmp(price_sizeset);
										price.setTotalprice(price_sizeset);
										price.setQuantity(amount_sizeset.intValue());
										price = priceService.save(price);

										// Them detail
										PContract_Price_D price_sizeset_d = new PContract_Price_D();
										price_sizeset_d.setOrgrootid_link(orgrootid_link);
										price_sizeset_d.setFobpriceid_link((long) 1);
										price_sizeset_d.setPrice(price_sizeset);
										price_sizeset_d.setIsfob(false);
										price_sizeset_d.setId(null);
										price_sizeset_d.setSizesetid_link(sizesetid_link == null ? 0 : sizesetid_link);
										price_sizeset_d.setPcontract_poid_link(pcontractpo_id_link);
										price_sizeset_d.setPcontractid_link(pcontractid_link);
										price_sizeset_d.setPcontractpriceid_link(price.getId());
										price_sizeset_d.setProductid_link(productid_link);
										pricedetailService.save(price_sizeset_d);

									}
								}
//End Sinh dai co
								float price_cmp = 0;

								if (total_amount == 0) {
									price_cmp = count == 0 ? 0 : total_price / count;
								} else {
									price_cmp = total_price_amount / total_amount;
								}

								DecimalFormat df = new DecimalFormat("#.###");
								String formatted = df.format(price_cmp);
								price_cmp = Float.parseFloat(formatted);

// Sinh dai co all vao san pham con (san pham nao cung se co 1 dai co ALL de luu so luong va gia tong hop cua cac dai co con)
								PContract_Price price_all = new PContract_Price();
								price_all.setId(null);
								price_all.setIs_fix(true);
								price_all.setOrgrootid_link(orgrootid_link);
								price_all.setPcontract_poid_link(pcontractpo_id_link);
								price_all.setPcontractid_link(pcontractid_link);
								price_all.setProductid_link(productid_link);
								price_all.setSizesetid_link((long) 1);
								price_all.setDate_importdata(current_time);
								price_all.setPrice_cmp(price_cmp);
								price_all.setTotalprice(price_cmp + price_fob);
								price_all.setPrice_fob(price_fob);
								price_all.setPrice_vendortarget(vendor_target);
								price_all.setQuantity(po_quantity);
								price_all = priceService.save(price_all);

								// Them detail
								PContract_Price_D price_detail_all = new PContract_Price_D();
								price_detail_all.setOrgrootid_link(orgrootid_link);
								price_detail_all.setFobpriceid_link((long) 1);
								price_detail_all.setPrice(price_cmp);
								price_detail_all.setIsfob(false);
								price_detail_all.setId(null);
								price_detail_all.setSizesetid_link((long) 1);
								price_detail_all.setPcontract_poid_link(pcontractpo_id_link);
								price_detail_all.setPcontractid_link(pcontractid_link);
								price_detail_all.setPcontractpriceid_link(price_all.getId());
								price_detail_all.setProductid_link(productid_link);
								pricedetailService.save(price_detail_all);

/*
 * kiem tra xem co phai san pham bo khong thi cap nhat cac dai co vao trong sản phẩm bộ
 */
								if (product_set_id_link > 0) {
									// Kiem tra ns cua san pham bo
									List<PContract_PO_Productivity> list_productivity_set = productivityService
											.getbypo_and_product(pcontractpo_id_link, product_set_id_link);
									PContract_PO_Productivity po_productivity_set = new PContract_PO_Productivity();
									if (list_productivity_set.size() == 0) {
										po_productivity_set.setId(null);
										po_productivity_set.setOrgrootid_link(orgrootid_link);
										po_productivity_set.setPcontract_poid_link(pcontractpo_id_link);
										po_productivity_set.setProductid_link(product_set_id_link);
									} else {
										po_productivity_set = list_productivity_set.get(0);
									}

									po_productivity_set.setAmount(po_quantity);
									po_productivity_set.setPlan_linerequired(plan_linerequired);
									po_productivity_set.setPlan_productivity(ns_target.intValue());
									po_productivity_set.setProductiondays_ns(productiondays_ns);
									productivityService.save(po_productivity_set);

									// Xoa cac dai co di roi insert lai vao san pham con hoac san pham bo
									List<PContract_Price> list_price_set = priceService
											.getPrice_by_product(pcontractpo_id_link, product_set_id_link);
									for (PContract_Price price : list_price_set) {
										List<PContract_Price_D> list_price_d = pricedetailService
												.getPrice_D_ByPContractPrice(price.getId());
										for (PContract_Price_D price_d : list_price_d) {
											pricedetailService.delete(price_d);
										}
										priceService.delete(price);
									}

									// them gia va so luong cac dai co khac all vao trong bo
									List<Long> list_sizeset = new ArrayList<Long>();
									list_sizeset.add((long) 1);
									for (int i = col_phancach2 + 1; i <= col_phancach2 + 6; i++) {
										Row row2 = sheet.getRow(1);
										String sizesetname = commonService.getStringValue(row2.getCell(i));
										Long sizesetid_link = sizesetService.getbyname(sizesetname);
										list_sizeset.add(sizesetid_link);
									}

									for (Long sizeid : list_sizeset) {
										// Lay gia cua cac san pham con cua dai co do
										// Lay ds cac san pham con cua san pham bo
										List<ProductPairing> list_pair = productpairService
												.getproduct_pairing_detail_bycontract(orgrootid_link, pcontractid_link,
														product_set_id_link);
										Float price_set = (float) 0;
										int amount_set = 0;
										for (ProductPairing pair : list_pair) {
											List<PContract_Price> list_price_chil = priceService
													.getPrice_by_product_and_sizeset(pcontractpo_id_link,
															pair.getProductid_link(), sizeid);
											if (list_price_chil.size() > 0) {
												amount_set = list_price_chil.get(0).getQuantity();
												price_set += list_price_chil.get(0).getPrice_cmp();
											}
										}

										if (amount_set > 0 || price_set > 0) {
											// them gia va so luong vao trong san pham bo
											PContract_Price price_sizeset = new PContract_Price();
											price_sizeset.setId(null);
											price_sizeset.setIs_fix(true);
											price_sizeset.setOrgrootid_link(orgrootid_link);
											price_sizeset.setPcontract_poid_link(pcontractpo_id_link);
											price_sizeset.setPcontractid_link(pcontractid_link);
											price_sizeset.setProductid_link(product_set_id_link);
											price_sizeset.setSizesetid_link(sizeid == null ? 0 : sizeid);
											price_sizeset.setDate_importdata(current_time);
											price_sizeset.setPrice_cmp(price_set);
											price_sizeset.setTotalprice(price_set);
											price_sizeset.setQuantity(amount_set);
											price_sizeset = priceService.save(price_sizeset);

											// Them detail
											PContract_Price_D price_sizeset_d = new PContract_Price_D();
											price_sizeset_d.setOrgrootid_link(orgrootid_link);
											price_sizeset_d.setFobpriceid_link((long) 1);
											price_sizeset_d.setPrice(price_set);
											price_sizeset_d.setIsfob(false);
											price_sizeset_d.setId(null);
											price_sizeset_d.setSizesetid_link(sizeid == null ? 0 : sizeid);
											price_sizeset_d.setPcontract_poid_link(pcontractpo_id_link);
											price_sizeset_d.setPcontractid_link(pcontractid_link);
											price_sizeset_d.setPcontractpriceid_link(price_sizeset.getId());
											price_sizeset_d.setProductid_link(productid_link);
											pricedetailService.save(price_sizeset_d);
										}
									}
								}
//End Cap nhat dai co va gia cho san pham bo
								
/*
 * Sinh cac PO Line giao hang ke hoach (PO Line con)
 */
								for (int i = 0; i < list_ngaygiao.size(); i++) {
									Date ngaygiao = list_ngaygiao.get(i);
									int soluong = list_soluong.get(i);

									List<PContract_PO> list_line_gh = new ArrayList<PContract_PO>();
									list_line_gh = pcontract_POService.check_exist_line(ngaygiao, po_productid_link,
											pcontractid_link, pcontractpo_id_link);
									PContract_PO po_line = new PContract_PO();

									Date production_date_line = null;
									int production_day_line = 0;
									float plan_linerequired_line = 0;
									int productiondays_ns_line = ns_target == 0 ? 0 : soluong / ns_target.intValue();
									if (i == 0) {
										if (matdate != null) {
											production_date_line = Common.Date_Add(matdate, 7);
											production_day_line = commonService.getDuration(production_date_line,
													ngaygiao, orgrootid_link);
											plan_linerequired_line = (float) productiondays_ns_line
													/ (float) production_day_line;
											if (plan_linerequired_line < 1)
												plan_linerequired_line = 1;

											DecimalFormat df_line = new DecimalFormat("#.##");
											String formatted_line = df_line.format(plan_linerequired_line);
											plan_linerequired_line = Float.parseFloat(formatted_line);
										}
									} else {
										// từ đợt thứ 2 thì ngày bắt đầu = ngày giao hàng đợt trước đó - 2
										production_date_line = commonService
												.Date_Add_with_holiday(list_ngaygiao.get(i - 1), -2, orgrootid_link);
										production_day_line = commonService.getDuration(production_date_line, ngaygiao,
												orgrootid_link);
										plan_linerequired_line = (float) productiondays_ns_line
												/ (float) production_day_line;
										if (plan_linerequired_line < 1)
											plan_linerequired_line = 1;

										DecimalFormat df_line = new DecimalFormat("#.##");
										String formatted_line = df_line.format(plan_linerequired_line);
										plan_linerequired_line = Float.parseFloat(formatted_line);
									}
									int total = 0;
									if (list_line_gh.size() == 0) {
										po_line.setCurrencyid_link((long) 1);
										po_line.setDatecreated(current_time);
										po_line.setIsauto_calculate(true);
										po_line.setOrgrootid_link(orgrootid_link);
										po_line.setPcontractid_link(pcontractid_link);
										po_line.setProductid_link(po_productid_link);
										po_line.setUsercreatedid_link(user.getId());
										po_line.setDate_importdata(current_time);
										po_line.setPo_typeid_link(POType.PO_LINE_PLAN);
										po_line.setSewtarget_percent((float) 20);
										po_line.setParentpoid_link(pcontractpo_id_link);

										if (status == POStatus.PO_STATUS_CONFIRMED) {
											po_line.setOrgmerchandiseid_link(orgid_link);
										}
										po_line.setPo_quantity(soluong);
									} else {
										po_line = list_line_gh.get(0);
										total = po_line.getPo_quantity();
									}
									po_line.setProductiondays_ns(productiondays_ns_line);
									po_line.setShipmodeid_link(shipmodeid_link);
									po_line.setShipdate(ngaygiao);
									po_line.setMatdate(matdate);
									po_line.setProductiondate(production_date_line);
									po_line.setProductiondays(production_day_line);
									po_line.setStatus(po_tong.getStatus());
									po_line.setPo_buyer(PO_No);
									po_line.setPo_vendor(PO_No);
//									po_line.setPo_quantity(soluong);
									po_line.setIs_tbd(PO_No == "TBD" ? true : false);
									po_line.setCode(PO_No);
									po_line.setProductid_link(po_productid_link);

									po_line = pcontract_POService.save(po_line);

									Long pcontract_po_line_id = po_line.getId();

/*
 * Kiem tra porder_req ton tai chua thi them vao
 * Hien o Lenh chua phan chuyen hoac yeu cau xep ke hoach
 */
									if (orgid_link != null) {
										List<POrder_Req> list_req = reqService.getByOrg_PO_Product(pcontract_po_line_id,
												productid_link, orgid_link);
										if (list_req.size() == 0) {
											total += soluong;
											POrder_Req porder_req = new POrder_Req();
											porder_req.setAmount_inset(amount);
											porder_req.setGranttoorgid_link(orgid_link);
											porder_req.setId(null);
											porder_req.setIs_calculate(true);
											porder_req.setOrgrootid_link(orgrootid_link);
											porder_req.setPcontract_poid_link(pcontract_po_line_id);
											porder_req.setPcontractid_link(pcontractid_link);
											porder_req.setProductid_link(productid_link);
											porder_req.setStatus(status == POStatus.PO_STATUS_CONFIRMED
													? POrderReqStatus.STATUS_POCONFFIRMED
													: POrderReqStatus.STATUS_FREE);
											porder_req.setTotalorder(soluong);
											porder_req = reqService.save(porder_req);

											if (po_tong.getStatus() == POStatus.PO_STATUS_CONFIRMED)
												porderService.createPOrder(porder_req, user);
										} else {
											POrder_Req porder_req = list_req.get(0);
//											total = total - porder_req.getTotalorder() + soluong;
											porder_req.setTotalorder(total);
											reqService.save(porder_req);
										}
									}
//End Kiem tra porder_req ton tai chua thi them vao
									
//									if (product_set_code.equals("")) {
//										po_line.setPo_quantity(total);
//										pcontract_POService.save(po_line);
//									}

									// Kiem tra ns cua san pham trong line
									List<PContract_PO_Productivity> list_productivity_line = productivityService
											.getbypo_and_product(pcontract_po_line_id, productid_link);
									PContract_PO_Productivity po_productivity_line = new PContract_PO_Productivity();
									if (list_productivity_line.size() == 0) {
										po_productivity_line.setId(null);
										po_productivity_line.setOrgrootid_link(orgrootid_link);
										po_productivity_line.setPcontract_poid_link(pcontract_po_line_id);
										po_productivity_line.setProductid_link(productid_link);
									} else {
										po_productivity_line = list_productivity_line.get(0);
									}

									po_productivity_line.setAmount(po_line.getPo_quantity() * amount);
									po_productivity_line.setPlan_linerequired(plan_linerequired_line);
									po_productivity_line.setPlan_productivity(ns_target.intValue());
									po_productivity_line.setProductiondays_ns(productiondays_ns_line);
									productivityService.save(po_productivity_line);

									if (product_set_id_link > 0) {
										// Kiem tra ns cua san pham trong line
										List<PContract_PO_Productivity> list_productivity_line_set = productivityService
												.getbypo_and_product(pcontract_po_line_id, product_set_id_link);
										PContract_PO_Productivity po_productivity_line_set = new PContract_PO_Productivity();
										if (list_productivity_line_set.size() == 0) {
											po_productivity_line_set.setId(null);
											po_productivity_line_set.setOrgrootid_link(orgrootid_link);
											po_productivity_line_set.setPcontract_poid_link(pcontract_po_line_id);
											po_productivity_line_set.setProductid_link(product_set_id_link);
										} else {
											po_productivity_line_set = list_productivity_line_set.get(0);
										}

										po_productivity_line_set.setAmount(soluong);
										po_productivity_line_set.setPlan_linerequired(plan_linerequired_line);
										po_productivity_line_set.setPlan_productivity(ns_target.intValue());
										po_productivity_line_set.setProductiondays_ns(productiondays_ns_line);
										productivityService.save(po_productivity_line_set);
									}
//End sinh PO Line ke hoach										
								}
//End 1 Row								
							}

							// Chuyen sang row tiep theo neu con du lieu thi xu ly tiep khong thi dung lai
							rowNum++;
							row = sheet.getRow(rowNum);
							if (row == null)
								break;

							STT = commonService.getStringValue(row.getCell(ColumnTempNew.STT));
							STT = STT.equals("0") ? "" : STT;
						}
					} catch (Exception e) {
						mes_err = "Có lỗi ở dòng " + (rowNum + 1) + " và cột " + colNum + ": " + mes_err;
					} finally {
						workbook.close();
						serverFile.delete(); //Xoa file luon sau khi xu ly xong
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
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage(mes_err);
			}

		} catch (Exception e) {
			e.printStackTrace();
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

	//po chi tiet (FOB)
	@RequestMapping(value = "/upload_po_fob", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPO_FOB(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, @RequestParam("parentid_link") long parentid_link,
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
				String mes_err = "";
				Row row = sheet.getRow(rowNum);
				try {
					String PO = "";
					PO = commonService.getStringValue(row.getCell(ColumnPO_FOB.PO));
					while (!PO.equals("")) {

						colNum = ColumnPO_FOB.Line;
						String Line = commonService.getStringValue(row.getCell(ColumnPO_FOB.Line));
						Line = Line.equals("0") ? "" : Line;

						colNum = ColumnPO_FOB.shipdate;
						Date ShipDate = null;
						try {
							String s_shipdate = commonService.getStringValue(row.getCell(ColumnPO_FOB.shipdate));
							if (s_shipdate.contains("/")) {
								String[] s_date = s_shipdate.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									ShipDate = new SimpleDateFormat("dd/MM/yyyy").parse(s_shipdate);
								} else {
									mes_err = "Định dạng ngày không đúng dd/MM/yyyy! ";
								}

							} else {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPO_FOB.shipdate))) {
									ShipDate = row.getCell(ColumnPO_FOB.shipdate).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPO_FOB.shipdate))) {
								ShipDate = row.getCell(ColumnPO_FOB.shipdate).getDateCellValue();
							}
						}

						if (ShipDate == null) {
							throw new Exception();
						}

						colNum = ColumnPO_FOB.shipmode;
						String Shipmode = row.getCell(ColumnPO_FOB.shipmode).getStringCellValue();
						Shipmode = Shipmode.equals("0") ? "" : Shipmode;

						colNum = ColumnPO_FOB.packing_method;
						String PackingMethod = row.getCell(ColumnPO_FOB.packing_method).getStringCellValue();
						PackingMethod = PackingMethod.equals("0") ? "" : PackingMethod;

						colNum = ColumnPO_FOB.color_name;
						String ColorName = commonService.getStringValue(row.getCell(ColumnPO_FOB.color_name));
						ColorName = ColorName.equals("0") ? "" : ColorName;

						colNum = ColumnPO_FOB.color_code;
						String ColorCode = commonService.getStringValue(row.getCell(ColumnPO_FOB.color_code));
						ColorCode = ColorCode.equals("0") ? "" : ColorCode;

						colNum = ColumnPO_FOB.style;
						String Style = commonService.getStringValue(row.getCell(ColumnPO_FOB.style));
						Style = Style.equals("0") ? "" : Style;

						colNum = ColumnPO_FOB.size_set;
						String SizeSet = commonService.getStringValue(row.getCell(ColumnPO_FOB.size_set));
						SizeSet = SizeSet.equals("0") ? "" : SizeSet;
						
						colNum = ColumnPO_FOB.ratio;
						String Ratio = commonService.getStringValue(row.getCell(ColumnPO_FOB.ratio));
						Ratio = Ratio.equals("0") ? "" : Ratio;
						
						colNum = ColumnPO_FOB.po_quantity;
						String s_PO_quantity = commonService.getStringValue(row.getCell(ColumnPO_FOB.po_quantity));
						s_PO_quantity = s_PO_quantity.equals("0") ? "" : s_PO_quantity;
						s_PO_quantity = s_PO_quantity.replace(",", "");
						Integer PO_quantity = Integer.parseInt(s_PO_quantity);


						// Kiem tra size-set
						Long sizesetid_link = null;

						if (!SizeSet.equals("")) {
							List<SizeSet> list_sizeset = sizesetService.getSizeSetByName(SizeSet);
							if (list_sizeset.size() == 0) {
								SizeSet sizeset = new SizeSet();
								sizeset.setComment("");
								sizeset.setId(null);
								sizeset.setName(SizeSet);
								sizeset.setOrgrootid_link(orgrootid_link);
								sizeset.setSortvalue(0);
								sizeset.setTimecreate(new Date());
								sizeset.setUsercreatedid_link(user.getId());

								sizeset = sizesetService.save(sizeset);
								sizesetid_link = sizeset.getId();
							} else {
								sizesetid_link = list_sizeset.get(0).getId();
							}
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
						if (!PO.equals("TBD")) {

							Long pcontractpoid_link = null;

							// Kiem tra xem PO con da ton tai hay chua
							String s_po = Line.equals("") ? PO : PO + "-" + Line;
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

								po_new = pcontract_POService.save(po_new);

								pcontractpoid_link = po_new.getId();
							} else {
								pcontractpoid_link = list_po.get(0).getId();
								PContract_PO po = list_po.get(0);
								po.setStatus(POStatus.PO_STATUS_CONFIRMED);
								po.setComment(Style);
								po = pcontract_POService.save(po);
								
								pcontractpoid_link = po.getId();
							}

							Long colorid_link = null;
							List<Attributevalue> listAttributevalue = attributevalueService
									.getByValue(ColorName + "(" + ColorCode + ")", AtributeFixValues.ATTR_COLOR);
							if (listAttributevalue.size() == 0) {
								Attributevalue av = new Attributevalue();
								av.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
								av.setId(null);
								av.setIsdefault(false);
								av.setOrgrootid_link(orgrootid_link);
								av.setSortvalue(attributevalueService.getMaxSortValue(AtributeFixValues.ATTR_COLOR));
								av.setTimecreate(new Date());
								av.setUsercreateid_link(user.getId());
								av.setValue(ColorName + "(" + ColorCode + ")");

								av = attributevalueService.save(av);
								colorid_link = av.getId();
							} else {
								colorid_link = listAttributevalue.get(0).getId();
							}

							// lấy từng cỡ trong dải cỡ
							colNum = ColumnPO_FOB.size;
							String list_size = commonService.getStringValue(row.getCell(ColumnPO_FOB.size));
							list_size = list_size.equals("0") ? "" : list_size;
							String[] str_size = list_size.split("-");
							
							colNum = ColumnPO_FOB.ratio;
							String[] list_ratio = Ratio.split("-");
							
							int total_size_ratio = 0;
							for(String ratio: list_ratio) {
								int i_ratio = Integer.parseInt(ratio);
								total_size_ratio += i_ratio;
							}

							for (int i=0; i<str_size.length; i++) {
								String size_name = str_size[i];
								Long sizeid_link = null;
								List<Attributevalue> listsize = attributevalueService.getByValue(size_name,
										AtributeFixValues.ATTR_SIZE);
								if (listsize.size() == 0) {
									Attributevalue av = new Attributevalue();
									av.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
									av.setId(null);
									av.setIsdefault(false);
									av.setOrgrootid_link(orgrootid_link);
									av.setSortvalue(attributevalueService.getMaxSortValue(AtributeFixValues.ATTR_SIZE));
									av.setTimecreate(new Date());
									av.setUsercreateid_link(user.getId());
									av.setValue(size_name);

									av = attributevalueService.save(av);
									sizeid_link = av.getId();
								} else {
									sizeid_link = listsize.get(0).getId();
								}
								
								//kiem tra co da co trong dai co chua thi them vao
								List<SizeSetAttributeValue> list_sizeset_att = sizeset_att_Service.getOne_bysizeset_and_value(sizesetid_link, AtributeFixValues.ATTR_SIZE, sizeid_link);
								if(list_sizeset_att.size() == 0) {
									SizeSetAttributeValue sizeset_att = new SizeSetAttributeValue();
									sizeset_att.setAttributeid_link(AtributeFixValues.ATTR_SIZE);
									sizeset_att.setAttributevalueid_link(sizeid_link);
									sizeset_att.setId(null);
									sizeset_att.setOrgrootid_link(orgrootid_link);
									sizeset_att.setSizesetid_link(sizesetid_link);
									sizeset_att_Service.save(sizeset_att);
								}
								
								int size_ratio = Integer.parseInt(list_ratio[i]);
								int quantity_size = (int)Math.ceil(PO_quantity*size_ratio/total_size_ratio);
								int quantity_size_plus = commonService
										.Calculate_pquantity_production(quantity_size);
								
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
										savMau.setId(null);
										savMau.setAttributevalueid_link(colorid_link);
										savMau.setAttributeid_link(AtributeFixValues.ATTR_COLOR);
										savMau.setOrgrootid_link(orgrootid_link);
										savMau.setSkuid_link(skuid_link);
										savMau.setUsercreateid_link(user.getId());
										savMau.setTimecreate(new Date());

										skuattService.save(savMau);

										SKU_Attribute_Value savCo = new SKU_Attribute_Value();
										savCo.setId(null);
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
									
									List<PContractProductSKU> ppskus = ppskuService.getlistsku_bysku_and_product_PO(
											skuid_link, pcontractpoid_link, parent.getProductid_link());
									
									if (ppskus.size() == 0) {
										PContractProductSKU ppsku = new PContractProductSKU();
										ppsku.setId(null);
										ppsku.setOrgrootid_link(orgrootid_link);
										ppsku.setPcontract_poid_link(pcontractpoid_link);
										ppsku.setPcontractid_link(pcontractid_link);
										ppsku.setPquantity_porder(quantity_size);
										ppsku.setPquantity_production(quantity_size_plus);
										ppsku.setPquantity_total(quantity_size_plus);
										ppsku.setProductid_link(parent.getProductid_link());
										ppsku.setSkuid_link(skuid_link);
										ppskuService.save(ppsku);
									} else {
										PContractProductSKU ppsku = ppskus.get(0);
										ppsku.setPquantity_porder(quantity_size);
										ppsku.setPquantity_production(quantity_size_plus);
										ppsku.setPquantity_total(quantity_size_plus);
										ppskuService.save(ppsku);
									}
								}
								else {
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
													quantity_size * productPairing.getAmount());
											ppsku.setPquantity_production(quantity_size_plus * productPairing.getAmount());
											ppsku.setPquantity_total(quantity_size_plus * productPairing.getAmount());
											ppsku.setProductid_link(productPairing.getProductid_link());
											ppsku.setSkuid_link(skuid_link);
											ppskuService.save(ppsku);
										} else {
											PContractProductSKU ppsku = ppskus.get(0);
											ppsku.setPquantity_porder(
													quantity_size * productPairing.getAmount());
											ppsku.setPquantity_production(quantity_size_plus * productPairing.getAmount());
											ppsku.setPquantity_total(quantity_size_plus * productPairing.getAmount());
											ppskuService.save(ppsku);
										}
									}
								}
							}
						}
						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						PO = commonService.getStringValue(row.getCell(ColumnPO_FOB.PO));
						PO = PO.equals("0") ? "" : PO;
					}
				} catch (Exception e) {
					mes_err += "Có lỗi ở dòng " + (rowNum + 1) + " và cột " + (colNum + 1);
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

}
