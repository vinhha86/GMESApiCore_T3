package vn.gpay.gsmart.core.api.product;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.gpay.gsmart.core.attribute.Attribute;
import vn.gpay.gsmart.core.attribute.IAttributeService;
import vn.gpay.gsmart.core.attributevalue.Attributevalue;
import vn.gpay.gsmart.core.attributevalue.IAttributeValueService;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.product.IProductBomService;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.product.ProductBOM;
import vn.gpay.gsmart.core.product.ProductBinding;
import vn.gpay.gsmart.core.product.ProductImg;
import vn.gpay.gsmart.core.productattributevalue.IProductAttributeService;
import vn.gpay.gsmart.core.productattributevalue.ProductAttributeValue;
import vn.gpay.gsmart.core.productattributevalue.ProductAttributeValueBinding;
import vn.gpay.gsmart.core.productpairing.IProductPairingService;
import vn.gpay.gsmart.core.productpairing.ProductPairing;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.sku.ISKU_AttributeValue_Service;
import vn.gpay.gsmart.core.sku.ISKU_Service;
import vn.gpay.gsmart.core.sku.SKU;
import vn.gpay.gsmart.core.sku.SKU_Attribute_Value;
import vn.gpay.gsmart.core.utils.AtributeFixValues;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.ProductType;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/product")
public class ProductAPI {
	@Autowired
	IProductService productService;
	@Autowired
	IAttributeService attrService;
	@Autowired
	IProductAttributeService pavService;
	@Autowired
	ISKU_AttributeValue_Service skuattService;
	@Autowired
	ISKU_Service skuService;
	@Autowired
	IProductBomService productbomservice;
	@Autowired
	Common commonService;
	@Autowired
	IProductPairingService productPairingService;
	@Autowired
	IAttributeValueService attributeValueService;
	@Autowired
	IPContract_POService poService;

	@RequestMapping(value = "/filter", method = RequestMethod.POST)
	public ResponseEntity<Product_filter_response> Product_Filter(HttpServletRequest request,
			@RequestBody Product_filter_request entity) {
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Product_filter_response response = new Product_filter_response();
		try {
			List<Product> lst_product = productService.filter(user.getRootorgid_link(), entity.product_type,
					entity.code, entity.partnercode, entity.attributes, entity.productid_link,
					entity.orgcustomerid_link);
			List<ProductBinding> list = new ArrayList<ProductBinding>();
			for (Product product : lst_product) {
				ProductBinding bind = new ProductBinding();
				bind.setBuyercode(product.getBuyercode());
				bind.setId(product.getId());
				bind.setPartnercode(product.getPartnercode());
				bind.setName(product.getName());
				bind.setProduct_type(product.getProducttypeid_link());

				String FolderPath = commonService.getFolderPath(product.getProducttypeid_link());
				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				bind.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));

				list.add(bind);
			}
			response.data = list;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_filter_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_filter_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall_product_single", method = RequestMethod.POST)
	public ResponseEntity<GetProductSingle_response> GetProductSingle(HttpServletRequest request,
			@RequestBody GetProductSingle_request entity) {
		GetProductSingle_response response = new GetProductSingle_response();
		try {
			String code = entity.buyercode;
			Boolean is_pair = entity.is_pair;
			int type = ProductType.SKU_TYPE_COMPLETEPRODUCT;
			if (is_pair)
				type = ProductType.SKU_TYPE_PRODUCT_PAIR;

			response.data = productService.getByBuyerCodeAndType(code, type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<GetProductSingle_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<GetProductSingle_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete_img", method = RequestMethod.POST)
	public ResponseEntity<delete_image_response> DeleteImg(HttpServletRequest request,
			@RequestBody delete_image_request entity) {
		delete_image_response response = new delete_image_response();
		try {
			Product product = productService.findOne(entity.productid_link);
			String uploadRootPath = request.getServletContext().getRealPath("");
			File uploadRootDir = new File(uploadRootPath);
			String FolderPath = AtributeFixValues.folder_upload + "/product";
			String img_path = uploadRootDir.getParent() + "/" + FolderPath;
			switch (entity.img) {
			case 1:

				img_path += "/" + product.getImgurl1();
				product.setImgurl1(null);
				break;
			case 2:
				img_path += "/" + product.getImgurl1();
				product.setImgurl2(null);
				break;
			case 3:
				img_path += "/" + product.getImgurl1();
				product.setImgurl3(null);
				break;
			case 4:
				img_path += "/" + product.getImgurl1();
				product.setImgurl4(null);
				break;
			case 5:
				img_path += "/" + product.getImgurl1();
				product.setImgurl5(null);
				break;
			default:
				break;
			}
			File img = new File(img_path);
			img.delete();
			productService.save(product);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<delete_image_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<delete_image_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getDefaultAttr", method = RequestMethod.POST)
	public ResponseEntity<Product_DefaultAttr_response> getDefaultAttr(HttpServletRequest request,
			@RequestBody Product_DefaultAttr_request entity) {
		Product_DefaultAttr_response response = new Product_DefaultAttr_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			List<Attribute> lstAttr = attrService.getList_attribute_forproduct(entity.producttypeid_link,
					user.getRootorgid_link());
//			List<ProductAttributeValue> lstAttrVal = new ArrayList<ProductAttributeValue>();
//			for (Attribute attribute : lstAttr) {
//				ProductAttributeValue pav = new ProductAttributeValue();
//				pav.setId(attribute.getId());
//				pav.setIsDefault(true);
//				pav.setAttributeid_link(attribute.getId());
//				pav.setAttributevalueid_link((long) 0);
//				pav.setOrgrootid_link(user.getRootorgid_link());
//				
//				pav.setAttribute(attribute);
//				lstAttrVal.add(pav);
//			}
			response.data = lstAttr;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_DefaultAttr_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_DefaultAttr_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getall", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_GetAll(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Page<Product> pProduct = productService.getall_by_orgrootid_paging(user.getRootorgid_link(), entity);
			List<Product> lstproduct = pProduct.getContent();
			List<ProductBinding> lstdata = new ArrayList<>();
			String FolderPath = "";

			for (Product product : lstproduct) {
				ProductBinding pb = new ProductBinding();

				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setDesignerName(product.getDesignerName());
				pb.setInfo(product.getDescription());

				FolderPath = commonService.getFolderPath(product.getProducttypeid_link());
				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}

			response.pagedata = lstdata;
			response.totalCount = pProduct.getTotalElements();
			response.data = productService.getall_by_orgrootid(user.getRootorgid_link(), entity.product_type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall_products", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_GetAll_Products(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			Page<Product> pProduct = productService.getall_products(user.getRootorgid_link(), entity);
//			List<Product> lstproduct = pProduct.getContent();

			List<Product> list = productService.getAllProduct(user.getRootorgid_link(), entity.code, entity.name);

			PageRequest page = PageRequest.of(entity.page - 1, entity.limit);
			int start = (int) page.getOffset();
			int end = (start + page.getPageSize()) > list.size() ? list.size() : (start + page.getPageSize());
			Page<Product> pageToReturn = new PageImpl<Product>(list.subList(start, end), page, list.size());
			List<Product> lstproduct = pageToReturn.getContent();

			List<ProductBinding> lstdata = new ArrayList<>();
			String FolderPath = AtributeFixValues.folder_upload + "/product";

			for (Product product : lstproduct) {
				ProductBinding pb = new ProductBinding();

				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setBuyercode(product.getBuyercode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setDesignerName(product.getDesignerName());
				pb.setInfo(product.getDescription());

				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}

			response.pagedata = lstdata;
//			response.totalCount = pProduct.getTotalElements();
			response.totalCount = list.size();
			response.data = productService.getall_by_orgrootid(user.getRootorgid_link(), entity.product_type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall_mainmaterials", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_GetAll_MainMaterials(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			Page<Product> pProduct = productService.getall_mainmaterials(user.getRootorgid_link(), entity);
//			List<Product> lstproduct = productService.getall_materials(user.getRootorgid_link(), entity);

			List<Product> list = productService.getall_materials(user.getRootorgid_link(), entity);

			PageRequest page = PageRequest.of(entity.page - 1, entity.limit);
			int start = (int) page.getOffset();
			int end = (start + page.getPageSize()) > list.size() ? list.size() : (start + page.getPageSize());
			Page<Product> pageToReturn = new PageImpl<Product>(list.subList(start, end), page, list.size());
			List<Product> lstproduct = pageToReturn.getContent();

			List<ProductBinding> lstdata = new ArrayList<>();
			String FolderPath = AtributeFixValues.folder_upload + "/material";

			for (Product product : lstproduct) {
				ProductBinding pb = new ProductBinding();

				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setDesignerName(product.getDesignerName());
				pb.setBuyercode(product.getBuyercode());

				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}

			response.pagedata = lstdata;
			response.totalCount = list.size();
			response.data = productService.getall_by_orgrootid(user.getRootorgid_link(), entity.product_type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall_sewingtrim", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_GetAll_SewingTrim(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Page<Product> pProduct = productService.getall_sewingtrim(user.getRootorgid_link(), entity);
			List<Product> lstproduct = pProduct.getContent();
			List<ProductBinding> lstdata = new ArrayList<>();
			String FolderPath = AtributeFixValues.folder_upload + "upload/sewingtrim";

			for (Product product : lstproduct) {
				ProductBinding pb = new ProductBinding();

				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setDesignerName(product.getDesignerName());
				pb.setBuyercode(product.getBuyercode());

				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}

			response.pagedata = lstdata;
			response.totalCount = pProduct.getTotalElements();
			response.data = productService.getall_by_orgrootid(user.getRootorgid_link(), entity.product_type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall_sewingthread", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_GetAll_SewingThread(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Page<Product> pProduct = productService.getall_sewingthread(user.getRootorgid_link(), entity);
			List<Product> lstproduct = pProduct.getContent();
			List<ProductBinding> lstdata = new ArrayList<>();
			String FolderPath = AtributeFixValues.folder_upload + "/sewingthread";

			for (Product product : lstproduct) {
				ProductBinding pb = new ProductBinding();

				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setDesignerName(product.getDesignerName());
				pb.setBuyercode(product.getBuyercode());

				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}

			response.pagedata = lstdata;
			response.totalCount = pProduct.getTotalElements();
			response.data = productService.getall_by_orgrootid(user.getRootorgid_link(), entity.product_type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall_packingtrim", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_GetAll_PackingTrim(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Page<Product> pProduct = productService.getall_packingtrim(user.getRootorgid_link(), entity);
			List<Product> lstproduct = pProduct.getContent();
			List<ProductBinding> lstdata = new ArrayList<>();
			String FolderPath = AtributeFixValues.folder_upload + "/packingtrim";

			for (Product product : lstproduct) {
				ProductBinding pb = new ProductBinding();

				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setDesignerName(product.getDesignerName());
				pb.setBuyercode(product.getBuyercode());

				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}

			response.pagedata = lstdata;
			response.totalCount = pProduct.getTotalElements();
			response.data = productService.getall_by_orgrootid(user.getRootorgid_link(), entity.product_type);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getproductbom", method = RequestMethod.POST)
	public ResponseEntity<ProductBOM_getbyid_response> GetProductBom(HttpServletRequest request,
			@RequestBody ProductBOM_getbyid_request entity) {
		ProductBOM_getbyid_response response = new ProductBOM_getbyid_response();
		try {
			response.data = productbomservice.getproductBOMbyid(entity.productid_link);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ProductBOM_getbyid_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ProductBOM_getbyid_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/createproductbom", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> CreateProductBom(HttpServletRequest request,
			@RequestBody ProductBOM_create_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for (Long npl : entity.listnpl) {
				ProductBOM productbom = new ProductBOM();
				productbom.setProductid_link(entity.productid_link);
				productbom.setMaterialid_link(npl);
				productbom.setUnitid_link((long) 0);
				productbom.setAmount((float) 0);
				productbom.setLost_ratio((float) 0);
				productbom.setDescription("");
				productbom.setCreateduserid_link(user.getId());
				productbom.setCreateddate(new Date());
				productbom.setOrgrootid_link(user.getRootorgid_link());

				productbomservice.save(productbom);
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

	@RequestMapping(value = "/updateproductbom", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UpdateProductBom(HttpServletRequest request,
			@RequestBody ProductBOM_update_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ProductBOM productbom = entity.data;
			if (productbom.getId() == null || productbom.getId() == 0) {
				productbom.setOrgrootid_link(user.getRootorgid_link());
				productbom.setCreateduserid_link(user.getId());
				productbom.setCreateddate(new Date());
			} else {
				ProductBOM productbom_old = productbomservice.findOne(productbom.getId());
				productbom.setOrgrootid_link(productbom_old.getOrgrootid_link());
				productbom.setCreateduserid_link(productbom_old.getCreateduserid_link());
				productbom.setCreateddate(productbom_old.getCreateddate());
			}
			productbomservice.save(productbom);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/deleteproductbom", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> DeleteProductBom(HttpServletRequest request,
			@RequestBody Product_deletebom_request entity) {
		ResponseBase response = new ResponseBase();
		try {
			productbomservice.deleteById(entity.id);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getmaterial_notinbom", method = RequestMethod.POST)
	public ResponseEntity<Product_getmaterial_notinbom_response> Product_Get_Material_NotinBom(
			HttpServletRequest request, @RequestBody Product_getmaterial_notinbom_request entity) {
		Product_getmaterial_notinbom_response response = new Product_getmaterial_notinbom_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();

			List<Product> data = productService.getList_material_notin_PContractProductBOM(orgrootid_link, entity.code,
					entity.name, entity.tenmaunpl, entity.productid_link, entity.product_type, entity.pcontractid_link);
			List<ProductBinding> lstdata = new ArrayList<ProductBinding>();

			for (Product product : data) {
				ProductBinding pb = new ProductBinding();
				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());

				String FolderPath = commonService.getFolderPath(product.getProducttypeid_link());
				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}
			response.data = lstdata;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getmaterial_notinbom_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getmaterial_notinbom_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getmaterial_notinpcontractproductbom", method = RequestMethod.POST)
	public ResponseEntity<Product_getmaterial_notinbom_response> Product_Get_Material_NotinPContractProductBom(
			HttpServletRequest request, @RequestBody Product_getall_npl_notin_pcontractproductbom_request entity) {
		Product_getmaterial_notinbom_response response = new Product_getmaterial_notinbom_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long orgrootid_link = user.getRootorgid_link();

			List<Product> data = productService.getList_material_notin_PContractProductBOM(orgrootid_link, entity.code,
					entity.name, entity.tenmaunpl, entity.productid_link, entity.product_type, entity.pcontractid_link);
			List<ProductBinding> lstdata = new ArrayList<ProductBinding>();

			for (Product product : data) {
				ProductBinding pb = new ProductBinding();
				pb.setId(product.getId());
				pb.setCode(product.getCode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());

				String FolderPath = commonService.getFolderPath(product.getProducttypeid_link());
				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));
				lstdata.add(pb);
			}
			response.data = lstdata;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getmaterial_notinbom_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getmaterial_notinbom_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getone", method = RequestMethod.POST)
	public ResponseEntity<Product_getOne_Response> Product_GetOne(HttpServletRequest request,
			@RequestBody Product_getOne_request entity) {
		Product_getOne_Response response = new Product_getOne_Response();
		try {
			Product product = productService.findOne(entity.id);
			response.data = product;

			ProductImg pimg = new ProductImg();
			String FolderPath = commonService.getFolderPath(product.getProducttypeid_link());

			String uploadRootPath = request.getServletContext().getRealPath("");
			File uploadRootDir = new File(uploadRootPath);

			pimg.img1 = getimg(response.data.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath);
			pimg.img2 = getimg(response.data.getImgurl2(), uploadRootDir.getParent() + "/" + FolderPath);
			pimg.img3 = getimg(response.data.getImgurl3(), uploadRootDir.getParent() + "/" + FolderPath);
			pimg.img4 = getimg(response.data.getImgurl4(), uploadRootDir.getParent() + "/" + FolderPath);
			pimg.img5 = getimg(response.data.getImgurl5(), uploadRootDir.getParent() + "/" + FolderPath);

			response.img = pimg;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getOne_Response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getOne_Response>(response, HttpStatus.OK);
		}
	}

	private byte[] getimg(String filename, String uploadRootPath) {
		String filePath = uploadRootPath + "/" + filename;
		Path path = Paths.get(filePath);
		byte[] data;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			data = null;
		}
		return data;
	}

	@RequestMapping(value = "/getattrvalue", method = RequestMethod.POST)
	public ResponseEntity<Product_getattvalue_response> Product_GetAttributeValue(HttpServletRequest request,
			@RequestBody Product_getattvalue_request entity) {
		Product_getattvalue_response response = new Product_getattvalue_response();
		response.data = new ArrayList<ProductAttributeValueBinding>();
		try {
			List<ProductAttributeValue> lst = pavService.getall_byProductId(entity.id);
			for (ProductAttributeValue productAttributeValue : lst) {
				ProductAttributeValueBinding obj = new ProductAttributeValueBinding();
				obj.setAttributeName(productAttributeValue.getAttributeName());
				obj.setAttributeid_link(productAttributeValue.getAttributeid_link());
				obj.setAttributeValueName("");
				obj.setIs_select(productAttributeValue.getIs_select());
				obj.setSortvalue(productAttributeValue.getAttributeSortValue());

				boolean isExist = false;

				for (ProductAttributeValueBinding binding : response.data) {
					if (binding.getAttributeid_link() == obj.getAttributeid_link()) {
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					response.data.add(obj);
				}
			}

			for (ProductAttributeValueBinding binding : response.data) {
				String name = "";
				String id = "";
				for (ProductAttributeValue productAttributeValue : lst) {
					if (productAttributeValue.getAttributeName() == binding.getAttributeName()) {
						String attName = productAttributeValue.getAttributeValueName();

						if (attName != "") {
							if (name == "") {
								name += productAttributeValue.getAttributeValueName();
								id += productAttributeValue.getAttributevalueid_link() == 0 ? ""
										: productAttributeValue.getAttributevalueid_link();
							} else {
								name += ", " + productAttributeValue.getAttributeValueName();
								id += "," + productAttributeValue.getAttributevalueid_link();
							}
						}
					}

					binding.setAttributeValueName(name);
					binding.setList_attributevalueid(id);
				}
			}

			for (ProductAttributeValueBinding binding : response.data) {
				String[] idarray = binding.getList_attributevalueid().split(",");
				List<String> idliststring = new ArrayList<>(Arrays.asList(idarray));
				List<Long> idlist = new ArrayList<Long>();
				for (String idnum : idliststring) {
					if (!idnum.equals("")) {
						idlist.add(Long.parseLong(idnum));
					}
				}

				List<Attributevalue> attributeVals = new ArrayList<>();
				for (Long idattributeval : idlist) {
					attributeVals.add(attributeValueService.findOne(idattributeval));
				}
				Comparator<Attributevalue> compareBySortValue = (Attributevalue a1, Attributevalue a2) -> a1
						.getSortvalue().compareTo(a2.getSortvalue());
				Collections.sort(attributeVals, compareBySortValue);

				binding.setAttributeValueName("");
				String name = "";
				for (Attributevalue attributeVal : attributeVals) {
					if (name.equals("")) {
						name += attributeVal.getValue();
					} else {
						name += ", " + attributeVal.getValue();
					}
				}
				binding.setAttributeValueName(name);
			}

			Comparator<ProductAttributeValueBinding> compareBySortValue = (ProductAttributeValueBinding a1,
					ProductAttributeValueBinding a2) -> a1.getSortvalue().compareTo(a2.getSortvalue());
			Collections.sort(response.data, compareBySortValue);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getattvalue_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getattvalue_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/createproduct", method = RequestMethod.POST)
	public ResponseEntity<Product_create_response> Product_Create(@RequestBody Product_create_request entity,
			HttpServletRequest request) {
		Product_create_response response = new Product_create_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Product product = entity.data;
			long orgrootid_link = user.getRootorgid_link();
			boolean isNew = false;

			if (product.getId() == null || product.getId() == 0) {
				isNew = true;
				product.setOrgrootid_link(user.getRootorgid_link());
				product.setUsercreateid_link(user.getId());
				product.setTimecreate(new Date());

				if (product.getVendorcode() == "" || product.getVendorcode() == null) {
					product.setVendorcode(product.getBuyercode());
				}

				if (product.getVendorname() == "" || product.getVendorname() == null) {
					product.setVendorname(product.getBuyername());
				}

			} else {
				Product product_old = productService.findOne(product.getId());
				product.setOrgrootid_link(product_old.getOrgrootid_link());
				product.setUsercreateid_link(product_old.getUsercreateid_link());
				product.setTimecreate(product_old.getTimecreate());
				product.setListPAvalue(product_old.getListPAvalue());
			}

			List<Product> pcheck = productService.getone_by_code(orgrootid_link, product.getBuyercode(),
					product.getId(), product.getProducttypeid_link());

			if (pcheck.size() > 0) {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Mã đã tồn tại trong hệ thống!");
			} else {
				product = productService.save(product);

				if (isNew) {
					// sau khi lưu sản phẩm xong thì tự động insert các thuộc tính của sản phẩm vào
					List<Attribute> lstAttr = attrService.getList_attribute_forproduct(product.getProducttypeid_link(),
							user.getRootorgid_link());
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
						pav.setProductid_link(product.getId());
						pav.setAttributeid_link(attribute.getId());
						pav.setAttributevalueid_link(value);
						pav.setOrgrootid_link(user.getRootorgid_link());
						pavService.save(pav);
					}

					// Sinh SKU cho mau all va co all
					long skuid_link = 0;

					SKU sku = new SKU();
					sku.setId(skuid_link);
					sku.setCode(genCodeSKU(product));
					sku.setName(product.getName());
					sku.setProductid_link(product.getId());
					sku.setOrgrootid_link(user.getRootorgid_link());
					sku.setSkutypeid_link(product.getProducttypeid_link());
					sku.setUnitid_link(product.getUnitid_link());

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

				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			}

			response.id = product.getId();
			response.product = product;
			return new ResponseEntity<Product_create_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_create_response>(response, HttpStatus.OK);
		}
	}

	private String genCodeSKU(Product product) {
		List<SKU> lstSKU = skuService.getlist_byProduct(product.getId());
		if (lstSKU.size() == 0) {
			return product.getBuyercode() + "_" + "1";
		}
		String old_code = lstSKU.get(0).getCode();
		String[] obj = old_code.split("_");
		int a = Integer.parseInt(obj[obj.length - 1]);
		return product.getBuyercode() + "_" + (a + 1);
	}

	@RequestMapping(value = "/update_productpair", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> ProductPair_Update(@RequestBody Product_update_productpair_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Product product = productService.findOne(entity.id);

			product.setBuyercode(entity.code);
			product.setVendorcode(entity.vendorCode);
			product.setName(entity.name);

			List<Product> pcheck = productService.getone_by_code(user.getRootorgid_link(), product.getBuyercode(),
					product.getId(), product.getProducttypeid_link());

			if (pcheck.size() > 0) {
				response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
				response.setMessage("Mã đã tồn tại trong hệ thống!");
			} else {
				product = productService.save(product);

				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			}
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getimg", method = RequestMethod.POST)
	public ResponseEntity<Product_viewimg_response> GetImage(HttpServletRequest request,
			@RequestBody Product_viewimg_request entity) {
		Product_viewimg_response response = new Product_viewimg_response();
		try {
			String FolderPath = commonService.getFolderPath(entity.product_type);

			Product product = productService.findOne(entity.id);
			String filename = product.getBuyercode().replace(" ", "").replace("/", "-") + "_" + entity.img + "."
					+ entity.ext;
			String uploadRootPath = request.getServletContext().getRealPath("");
			File uploadRootDir = new File(uploadRootPath);
			String filePath = uploadRootDir.getParent() + "/" + FolderPath + "/" + filename;
			Path path = Paths.get(filePath);
			byte[] data = Files.readAllBytes(path);
			response.data = data;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_viewimg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_viewimg_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/viewimg", method = RequestMethod.POST)
	public ResponseEntity<Product_viewimg_response> ViewImage(HttpServletRequest request,
			@RequestBody Product_viewimg_request entity) {
		Product_viewimg_response response = new Product_viewimg_response();
		try {
			String FolderPath = commonService.getFolderPath(entity.product_type);

			Product product = productService.findOne(entity.id);
			String filename = product.getBuyercode().replace(" ", "").replace("/", "-") + "_" + entity.img + "."
					+ entity.ext;
			String uploadRootPath = request.getServletContext().getRealPath("");
			File uploadRootDir = new File(uploadRootPath);
			String filePath = uploadRootDir.getParent() + "/" + FolderPath + "/" + filename;
			Path path = Paths.get(filePath);
			byte[] data = Files.readAllBytes(path);
			response.data = data;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_viewimg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_viewimg_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/updateimg", method = RequestMethod.POST)
	public ResponseEntity<Product_updateimg_response> Product_updteImg(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, @RequestParam("id") int id, @RequestParam("img") int img) {
		Product_updateimg_response response = new Product_updateimg_response();

		try {
			Product product = productService.findOne(id);
			String FolderPath = commonService.getFolderPath(product.getProducttypeid_link());

			String uploadRootPath = request.getServletContext().getRealPath("");
			File uploadRootDir = new File(uploadRootPath);
			File folder_upload = new File(uploadRootDir.getParent() + "/" + FolderPath);
			// Tạo thư mục gốc upload nếu nó không tồn tại.
			if (!folder_upload.exists()) {
				folder_upload.mkdirs();
			}

			String name = file.getOriginalFilename();
			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = product.getBuyercode().replace(" ", "").replace("/", "-") + "_" + img + "." + extend;
				File serverFile = new File(folder_upload.getAbsolutePath() + File.separator + name);

				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
			}

			switch (img) {
			case 1:
				product.setImgurl1(name);
				break;
			case 2:
				product.setImgurl2(name);
				break;
			case 3:
				product.setImgurl3(name);
				break;
			case 4:
				product.setImgurl4(name);
				break;
			case 5:
				product.setImgurl5(name);
				break;

			default:
				break;
			}
			productService.save(product);
			response.imgname = name;
			response.imgnumber = img;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_updateimg_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_updateimg_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> Prduct_Delete(@RequestBody Product_delete_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();

		try {
			// Xóa bảng product : update status = -1
			Product p = productService.findOne(entity.id);
			p.setStatus(-1);
			productService.save(p);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		}
	}

	// Chỉ lấy loại nguyên phụ liệu
	@RequestMapping(value = "/getall_materialtypes", method = RequestMethod.POST)
	public ResponseEntity<Product_type_response> getall_MaterialTypes(HttpServletRequest request) {
		Product_type_response response = new Product_type_response();
		try {

			response.data = productService.getall_ProductTypes(ProductType.SKU_TYPE_MATERIAL_MIN,
					ProductType.SKU_TYPE_MATERIAL_MAX);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Chỉ lấy loại nguyên phụ liệu chinhs
	@RequestMapping(value = "/getall_mainmaterialtypes", method = RequestMethod.POST)
	public ResponseEntity<Product_type_response> getall_MainMaterialTypes(HttpServletRequest request) {
		Product_type_response response = new Product_type_response();
		try {

			response.data = productService.getall_ProductTypes(ProductType.SKU_TYPE_MATERIAL_MIN,
					ProductType.SKU_TYPE_MATERIAL_MAX);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Chỉ lấy loại phu lieu
	@RequestMapping(value = "/getall_submaterialtypes", method = RequestMethod.POST)
	public ResponseEntity<Product_type_response> getall_SubMaterialTypes(HttpServletRequest request) {
		Product_type_response response = new Product_type_response();
		try {

			response.data = productService.getall_ProductTypes(ProductType.SKU_TYPE_SWEINGTRIM_MIN,
					ProductType.SKU_TYPE_PACKINGTRIM_MAX);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Lấy tất cả các loại Thành phẩm
	@RequestMapping(value = "/getall_completeproducttypes", method = RequestMethod.POST)
	public ResponseEntity<Product_type_response> getall_CompleteProductTypes(HttpServletRequest request) {
		Product_type_response response = new Product_type_response();
		try {

			response.data = productService.getall_ProductTypes(ProductType.SKU_TYPE_PRODUCT_MIN,
					ProductType.SKU_TYPE_PRODUCT_MAX);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Lấy tất cả các loại Thành phẩm và nguyên phụ liệu
	@RequestMapping(value = "/getall_types", method = RequestMethod.POST)
	public ResponseEntity<Product_type_response> getall_Types(HttpServletRequest request) {
		Product_type_response response = new Product_type_response();
		try {

			response.data = productService.getall_ProductTypes(ProductType.SKU_TYPE_PRODUCT_MIN,
					ProductType.SKU_TYPE_PACKINGTRIM_MAX);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Lấy tất cả không phải là sản phẩm
	@RequestMapping(value = "/getnotproduct", method = RequestMethod.POST)
	public ResponseEntity<Product_type_response> get_not_product(HttpServletRequest request) {
		Product_type_response response = new Product_type_response();
		try {

			response.data = productService.getall_ProductTypes(ProductType.SKU_TYPE_MATERIAL_MIN,
					ProductType.SKU_TYPE_SWEINGTHREAD_MAX);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_type_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_type_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/update_select_att", method = RequestMethod.POST)
	public ResponseEntity<get_description_response> UpdateAttribute(HttpServletRequest request,
			@RequestBody get_description_request entity) {
		get_description_response response = new get_description_response();
		try {
			long attributeid_link = entity.attributeid_link;
			long productid_link = entity.productid_link;

			List<ProductAttributeValue> pav = pavService.getList_byAttId(attributeid_link, productid_link);
			for (ProductAttributeValue productAttributeValue : pav) {
				productAttributeValue.setIs_select(entity.check);
				pavService.save(productAttributeValue);
			}

			Product product = productService.findOne(productid_link);
//			product.setDescription(entity.description);
			productService.save(product);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<get_description_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<get_description_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getby_po", method = RequestMethod.POST)
	public ResponseEntity<getby_po_response> GetByPO(HttpServletRequest request, @RequestBody getby_po_request entity) {
		getby_po_response response = new getby_po_response();
		try {
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			long orgrootid_link = user.getRootorgid_link();

			long pcontract_poid_link = entity.pcontract_poid_link;
			PContract_PO po = poService.findOne(pcontract_poid_link);
			long pcontractid_link = po.getPcontractid_link();

			Product product = productService.findOne(po.getProductid_link());
			List<Product> list_product = new ArrayList<Product>();

			if (product.getProducttypeid_link().equals(ProductType.SKU_TYPE_PRODUCT_PAIR)) {
				List<ProductPairing> list_pair = productPairingService
						.getproduct_pairing_detail_bycontract(orgrootid_link, pcontractid_link, product.getId());
				for (ProductPairing pair : list_pair) {
					Product p = productService.findOne(pair.getProductid_link());
					list_product.add(p);
				}
			} else {
				Product p = productService.findOne(product.getId());
				list_product.add(p);
			}

			response.data = list_product;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_po_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getby_po_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/get_by_pairid", method = RequestMethod.POST)
	public ResponseEntity<getlistproduct_bypairingid_response> getbypairid(
			@RequestBody getlistproduct_bypairingid_request entity, HttpServletRequest request) {
		getlistproduct_bypairingid_response response = new getlistproduct_bypairingid_response();
		try {
			response.data = new ArrayList<ProductBinding>();
			Product product = productService.findOne(entity.product_pairid_link);
			if (!entity.ishidden_pair || product.getProducttypeid_link() != 5) {
				int po_quantity = entity.po_quantity == null ? 0 : entity.po_quantity;

				ProductBinding pb = new ProductBinding();
				pb.setId(product.getId());
				pb.setCode(product.getBuyercode());
				pb.setName(product.getName());
				pb.setProduct_type(product.getProducttypeid_link());
				pb.setProduct_typeName(product.getProducttype_name());
				pb.setCoKho(product.getCoKho());
				pb.setThanhPhanVai(product.getThanhPhanVai());
				pb.setTenMauNPL(product.getTenMauNPL());
				pb.setInfo(product.getDescription());
				pb.setCode_amount(product.getBuyercode() + " (" + commonService.FormatNumber(po_quantity) + ")");

				pb.setPquantity(entity.po_quantity);

				String FolderPath = commonService.getFolderPath(product.getProducttypeid_link());
				String uploadRootPath = request.getServletContext().getRealPath("");
				File uploadRootDir = new File(uploadRootPath);
				pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));

				response.data.add(pb);
			}
			
			if(product.getProducttypeid_link() == 5) {
				Long pcontractid_link = entity.pcontractid_link;
				List<Product> list = new ArrayList<Product>();
				if (pcontractid_link == null)
					list = productService.getby_pairid(entity.product_pairid_link);
				else
					list = productService.getby_pairid_and_pcontract(entity.product_pairid_link, pcontractid_link);

				for (Product _product : list) {
					ProductPairing pairInfo = productPairingService.getproduct_pairing_bykey(_product.getId(),
							entity.product_pairid_link);

					ProductBinding pb = new ProductBinding();
					pb.setId(_product.getId());
					pb.setCode(_product.getBuyercode());
					pb.setName(_product.getName());
					pb.setProduct_type(_product.getProducttypeid_link());
					pb.setProduct_typeName(_product.getProducttype_name());
					pb.setCoKho(_product.getCoKho());
					pb.setThanhPhanVai(_product.getThanhPhanVai());
					pb.setTenMauNPL(_product.getTenMauNPL());
					pb.setPairamount(pairInfo.getAmount());
					pb.setInfo(pairInfo.getProductinfo());
					if (entity.po_quantity != null) {
						pb.setPquantity(pairInfo.getAmount() * entity.po_quantity);
						pb.setCode_amount(
								_product.getBuyercode() + " (" + commonService.FormatNumber(pb.getPquantity()) + ")");
					}
					String FolderPath = commonService.getFolderPath(_product.getProducttypeid_link());
					String uploadRootPath = request.getServletContext().getRealPath("");
					File uploadRootDir = new File(uploadRootPath);
					pb.setUrlimage(getimg(product.getImgurl1(), uploadRootDir.getParent() + "/" + FolderPath));

					response.data.add(pb);
				}
			}
			

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getlistproduct_bypairingid_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<getlistproduct_bypairingid_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getProductByExactBuyercode", method = RequestMethod.POST)
	public ResponseEntity<Product_getOne_Response> getProductByExactBuyercode(HttpServletRequest request,
			@RequestBody Product_getProductByBuyercode_request entity) {
		Product_getOne_Response response = new Product_getOne_Response();
		try {

			List<Product> list = productService.getProductByExactBuyercode(entity.buyercode);
			if (list.size() > 0) {
				response.data = list.get(0);
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage("Mã SP(buyer) không tồn tại");
				return new ResponseEntity<Product_getOne_Response>(response, HttpStatus.OK);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getOne_Response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getOne_Response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getByBuyerCodeAndType", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> getByBuyerCodeAndType(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		Product_getall_response response = new Product_getall_response();
		try {
			String buyercode = entity.buyercode;
			Integer producttypeid_link = entity.producttypeid_link;

			List<Product> list = productService.getByBuyerCodeAndType(buyercode, producttypeid_link);
			response.data = list;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/get_forStockinProductSearch", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> Product_Filter(HttpServletRequest request,
			@RequestBody Product_getall_request entity) {
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Product_getall_response response = new Product_getall_response();
		try {
			// tìm sản phẩm đơn theo mã sp, mã đơn hàng, mã po
			String productSearchString = entity.productSearchString;
			Integer producttypeid_link = 10;
			List<Integer> list_producttypeid_link = new ArrayList<Integer>();
			list_producttypeid_link.add(producttypeid_link);
			// producttypeid_link = 10
			List<Product> result = new ArrayList<Product>();
			if(productSearchString != null) {
				productSearchString = productSearchString.trim();
//				System.out.println(productSearchString);
				result = productService.getBy_Buyercode_Contract_PO(productSearchString, list_producttypeid_link);
				// sp bo -> lay sp don
				List<Product> product_list_spdon = productService.getBy_Buyercode_Contract_PO_Pairing(productSearchString, list_producttypeid_link);
				for(Product item : product_list_spdon) {
					Boolean isContain = false;
					for(Product product : result) {
						if(item.getId().equals(product.getId())) {
							isContain = true;
							break;
						}
					}
					if(!isContain) {
						result.add(item);
					}
				}
			}
			
			
			response.data = result;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getSpDonById", method = RequestMethod.POST)
	public ResponseEntity<Product_getall_response> getSpDonById(@RequestBody Product_getOne_request entity,
			HttpServletRequest request) {
		Product_getall_response response = new Product_getall_response();

		try {
			Long id = entity.id;
			Long pcontractid_link = entity.pcontractid_link;
			
			Product product = productService.findOne(id);
			response.data = new ArrayList<Product>();
			if(product.getProducttypeid_link() == ProductType.SKU_TYPE_PRODUCT_PAIR) {
				// tìm danh sách sản phẩm con của bộ
				List<Long> productIdList = productPairingService.getProductIdByProductPair(pcontractid_link, id);
				for(Long productId : productIdList) {
					response.data.add(productService.findOne(productId));
				}
			}else if(product.getProducttypeid_link() == ProductType.SKU_TYPE_COMPLETEPRODUCT){
				response.data.add(product);
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Product_getall_response>(response, HttpStatus.OK);
		}
	}
}