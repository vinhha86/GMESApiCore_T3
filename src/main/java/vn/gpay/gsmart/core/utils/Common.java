package vn.gpay.gsmart.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.holiday.Holiday;
import vn.gpay.gsmart.core.holiday.IHolidayService;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.pcontract.IPContractService;
import vn.gpay.gsmart.core.pcontract.PContract;
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontractattributevalue.IPContractProductAtrributeValueService;
import vn.gpay.gsmart.core.pcontractbomcolor.IPContractBOMColorService;
import vn.gpay.gsmart.core.pcontractbomcolor.PContractBOMColor;
import vn.gpay.gsmart.core.pcontractbomsku.IPContractBOMSKUService;
import vn.gpay.gsmart.core.pcontractbomsku.PContractBOMSKU;
import vn.gpay.gsmart.core.pcontractconfigamount.ConfigAmount;
import vn.gpay.gsmart.core.pcontractconfigamount.IConfigAmountService;
import vn.gpay.gsmart.core.pcontractproductbom.IPContractProductBomService;
import vn.gpay.gsmart.core.pcontractproductbom.PContractProductBom;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKUService;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.porder.IPOrder_Service;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_Service;
import vn.gpay.gsmart.core.porder_grant.POrderGrant;
import vn.gpay.gsmart.core.porder_req.IPOrder_Req_Service;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.sku.ISKU_AttributeValue_Service;
import vn.gpay.gsmart.core.stocking_uniquecode.IStocking_UniqueCode_Service;
import vn.gpay.gsmart.core.stocking_uniquecode.Stocking_UniqueCode;
import vn.gpay.gsmart.core.task.ITask_Service;
import vn.gpay.gsmart.core.task.Task;
import vn.gpay.gsmart.core.task_checklist.ITask_CheckList_Service;
import vn.gpay.gsmart.core.task_checklist.Task_CheckList;
import vn.gpay.gsmart.core.task_flow.ITask_Flow_Service;
import vn.gpay.gsmart.core.task_flow.Task_Flow;
import vn.gpay.gsmart.core.task_grant.ITask_Grant_Service;
import vn.gpay.gsmart.core.task_grant.Task_Grant;
import vn.gpay.gsmart.core.task_object.ITask_Object_Service;
import vn.gpay.gsmart.core.task_object.Task_Object;
import vn.gpay.gsmart.core.tasktype.ITaskType_Service;
import vn.gpay.gsmart.core.tasktype.TaskType;
import vn.gpay.gsmart.core.tasktype_checklist.ITaskType_CheckList_Service;
import vn.gpay.gsmart.core.tasktype_checklist.TaskType_CheckList;

@Service
public class Common  {
	

	@Autowired IPContractBOMSKUService pcontractBOMSKUService;
	@Autowired IPContractService pcontractService;
	@Autowired ISKU_AttributeValue_Service skuavService;
	@Autowired IPContractBOMColorService bomcolorService;
	@Autowired IPContractProductAtrributeValueService ppavService;
	@Autowired IPContractProductSKUService ppskuService;
	@Autowired IPContractProductBomService ppbomService;
	@Autowired IHolidayService holidayService;
	@Autowired IConfigAmountService cfamountService;
	@Autowired IPContract_POService poService;
	@Autowired IPOrder_Req_Service reqService;
	@Autowired IOrgService orgService;
	
	@Autowired ITask_Service taskService;
	@Autowired ITask_CheckList_Service checklistService;
	@Autowired ITaskType_Service tasktypeService;
	@Autowired ITask_Grant_Service taskgrantService;
	@Autowired ITaskType_CheckList_Service typechecklistService;
	@Autowired ITask_Object_Service taskobjectService;
	@Autowired ITask_Flow_Service flowService;
	@Autowired IPOrder_Service porderService;
	@Autowired IPOrderGrant_Service grantService;

	@Autowired IStocking_UniqueCode_Service stockingService;
	
	
	@Autowired IProductService productService;
	
//	public int Tinh_LaVai(CutPlan_Size size_catdu, CutPlan_Size size_sodo) {
//		
//	}
	
	public Long CreateTask(Long orgrootid_link, Long orgid_link, Long userid_link, long tasktypeid_link, List<Task_Object> list_object, Long userinchargeid_link) {
		TaskType tasktype = tasktypeService.findOne(tasktypeid_link);
		
		Org org = orgService.findOne(orgid_link);
		String taskname = tasktype.getName() + ": "+org.getCode();
		
		String description = getDescriptoin_bytype(tasktypeid_link, list_object);
		if(userinchargeid_link == null) {
			List<Task_Grant> grants = taskgrantService.getby_tasktype_and_org(tasktypeid_link, orgid_link);
			if(grants.size()>0)
				userinchargeid_link = grants.get(0).getUserid_link();
		}
		
		
		Task task = new Task();
		task.setDatecreated(new Date());
		task.setDescription(description);
		task.setDuedate(Date_Add_with_holiday(new Date(), tasktype.getDuration()/24, orgrootid_link));
		task.setDuration(tasktype.getDuration()/24);
		task.setId(null);
		task.setName(taskname);
		task.setOrgrootid_link(orgrootid_link);
		task.setPercentdone(0);
		task.setStatusid_link(0);
		task.setTasktypeid_link(tasktypeid_link);
		task.setUsercreatedid_link(userid_link);
		task.setUserinchargeid_link(userinchargeid_link);
		
		task = taskService.save(task);
		
		//Tao subtask
		List<TaskType_CheckList> list_sub = typechecklistService.getby_tasktype(tasktypeid_link);
		for(TaskType_CheckList sub: list_sub) {
			Task_CheckList subtask = new Task_CheckList();
			subtask.setDone(false);
			subtask.setId(null);
			subtask.setDatecreated(new Date());
			subtask.setDescription(sub.getName());
			subtask.setOrgrootid_link(orgrootid_link);
			subtask.setTaskid_link(task.getId());
			subtask.setUsercreatedid_link(userid_link);
			subtask.setTasktype_checklist_id_link(sub.getId());
			
			checklistService.save(subtask);
		}
		
		//Tao Task_Object
		for(Task_Object object: list_object) {
			Task_Object obj = new Task_Object();
			obj.setId(null);
			obj.setTaskid_link(task.getId());
			obj.setObjectid_link(object.getObjectid_link());
			obj.setOrgrootid_link(object.getOrgrootid_link());
			obj.setTaskobjecttypeid_link(object.getTaskobjecttypeid_link());
			
			taskobjectService.save(obj);
		}
		
		//Tao Flow
		Task_Flow flow = new Task_Flow();
		flow.setDatecreated(new Date());
		flow.setDescription("Tạo việc");
		flow.setFlowstatusid_link(3);
		flow.setFromuserid_link(userid_link);
		flow.setId(null);
		flow.setOrgrootid_link(orgrootid_link);
		flow.setTaskid_link(task.getId());
		flow.setTaskstatusid_link(0);
		flow.setTouserid_link(userinchargeid_link);
		flowService.save(flow);
		
		return task.getId();
	}
	
	public String DateToString(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public String getDescriptoin_bytype(Long tasktypeid_link, List<Task_Object> list_object) {
		String name = "";
		int typeid = tasktypeid_link.intValue();
		switch (typeid) {
		case 0:
			Long pcontract_poid_link = null;
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
						
			PContract_PO po = poService.findOne(pcontract_poid_link);
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		case 1:
			Long pcontractid_link = null, productid_link = null;
			pcontract_poid_link = null;
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHang) {
					pcontractid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.SanPham) {
					productid_link = object.getObjectid_link();
					break;
				}
			}
			
			PContract pcontract = pcontractService.findOne(pcontractid_link);
			Product product = productService.findOne(productid_link);
			
			po = poService.findOne(pcontract_poid_link);
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(Mã HĐ: " + pcontract.getContractcode()+")-(Sản phẩm: "+ product.getBuyercode() +")-(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		case 2:
			pcontractid_link = null;
			pcontract_poid_link = null;
			productid_link = null;
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.SanPham) {
					productid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHang) {
					pcontractid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
			
			pcontract = pcontractService.findOne(pcontractid_link);
			po = poService.findOne(pcontract_poid_link);
			product = productService.findOne(productid_link);
			
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(Mã HĐ: " + pcontract.getContractcode()+")-(Sản phẩm: "+ product.getBuyercode() +")-(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		case 3:
			pcontractid_link = null;
			pcontract_poid_link = null;
			productid_link = null;
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHang) {
					pcontractid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.SanPham) {
					productid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
			
			pcontract = pcontractService.findOne(pcontractid_link);
			po = poService.findOne(pcontract_poid_link);
			product = productService.findOne(productid_link);
			
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(Mã HĐ: " + pcontract.getContractcode()+")-(Sản phẩm: "+ product.getBuyercode() +")-(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		case 4:
			pcontractid_link = null;
			pcontract_poid_link = null;
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHang) {
					pcontractid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
			
			pcontract = pcontractService.findOne(pcontractid_link);
			po = poService.findOne(pcontract_poid_link);
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(Mã HĐ: " + pcontract.getContractcode()+")-(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		case 5:
			pcontractid_link = null;
			pcontract_poid_link = null;
			productid_link = null;
//			Long porderid_link = null;
			
//			for(Task_Object object : list_object) {
//				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.LenhSanXuat) {
//					porderid_link = object.getObjectid_link();
//					break;
//				}
//			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.SanPham) {
					productid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHang) {
					pcontractid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
			
			pcontract = pcontractService.findOne(pcontractid_link);
			po = poService.findOne(pcontract_poid_link);
			product = productService.findOne(productid_link);
//			POrder porder = porderService.findOne(porderid_link);
			
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(Mã HĐ: " + pcontract.getContractcode()+")-(Sản phẩm: "+ product.getBuyercode() +")-(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		case 6:
			pcontractid_link = null;
			pcontract_poid_link = null;
			productid_link = null;
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.SanPham) {
					productid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHang) {
					pcontractid_link = object.getObjectid_link();
					break;
				}
			}
			
			for(Task_Object object : list_object) {
				if(object.getTaskobjecttypeid_link().intValue() == TaskObjectType_Name.DonHangPO) {
					pcontract_poid_link = object.getObjectid_link();
					break;
				}
			}
			
			pcontract = pcontractService.findOne(pcontractid_link);
			po = poService.findOne(pcontract_poid_link);
			product = productService.findOne(productid_link);
			
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			name = "(Mã HĐ: " + pcontract.getContractcode()+")-(Sản phẩm: "+ product.getBuyercode() +")-(PO Buyer: "+po.getPo_buyer()+ ")-(PO Vendor: "+ po.getPo_vendor() + ")-(Giao hàng: "+ dateFormat.format(po.getShipdate())
			+ ")-(SL: " + FormatNumber(po.getPo_quantity().intValue()) + ")";
			return name;
		default:
			return "";
		}
	}
	
	public List<PContractBOMSKU> getBOMSKU_PContract_Product(long pcontractid_link, long productid_link, List<PContractProductSKU> listsku){
		List<PContractBOMSKU> listbomsku = new ArrayList<PContractBOMSKU>();
		
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long orgrootid_link = user.getRootorgid_link();
				
		// Lay tu bom sku
		for (PContractProductSKU sku : listsku) {	
			List<PContractBOMSKU> lsSKUBOM = pcontractBOMSKUService.getMaterials_BySKUId(sku.getSkuid_link());
			listbomsku.addAll(lsSKUBOM);
		}
		
		
		//Lay tu bom color
		long colorid_link = 0;
		
		List<PContractBOMColor> list_color_bom = bomcolorService.getall_byproduct(pcontractid_link, productid_link);
		for(PContractBOMColor bom_color : list_color_bom ) {
			colorid_link = bom_color.getColorid_link();
			//Lay cac sku co mau trong san pham
			List<Long> list_sku_color = ppskuService.getsku_bycolor(pcontractid_link, productid_link,colorid_link);
			
			for (Long skuid_link : list_sku_color) {	
				PContractBOMSKU skubom = new PContractBOMSKU();
				skubom.setAmount(bom_color.getAmount());
				skubom.setId(null);
				skubom.setMaterialid_link(bom_color.getMaterialid_link());
				skubom.setPcontractid_link(pcontractid_link);
				skubom.setProductid_link(productid_link);
				skubom.setSkuid_link(skuid_link);
				skubom.setOrgrootid_link(orgrootid_link);
				skubom.setLost_ratio((float)0);
				
				listbomsku.add(skubom);
			}
		}
		
		//Lay tu bom product (Chung ca san pham)
		List<PContractProductBom> listproductbom = ppbomService.get_pcontract_productBOMbyid(productid_link, pcontractid_link);
		
		List<PContractProductSKU> list_sku = ppskuService.getlistsku_byproduct_and_pcontract(orgrootid_link, productid_link, pcontractid_link);
		
		for(PContractProductBom ppbom : listproductbom) {
			for(PContractProductSKU ppsku : list_sku) {
				PContractBOMSKU skubom = new PContractBOMSKU();
				skubom.setAmount(ppbom.getAmount());
				skubom.setId(null);
				skubom.setMaterialid_link(ppbom.getMaterialid_link());
				skubom.setPcontractid_link(pcontractid_link);
				skubom.setProductid_link(productid_link);
				skubom.setSkuid_link(ppsku.getSkuid_link());
				skubom.setOrgrootid_link(orgrootid_link);
				skubom.setLost_ratio((float)0);
				
				listbomsku.add(skubom);
			}
		}
		
		
		return listbomsku;
	}

	
	public String getFolderPath(int producttypeid_link) {
		String Path = AtributeFixValues.folder_upload+"/";
		if(10 <= producttypeid_link && 20 > producttypeid_link) {
			Path += "product";
		}
		else if (20 <= producttypeid_link && 30 > producttypeid_link) {
			if (producttypeid_link == 20) {
				Path += "Material"; //Vai lot
			}
			else if (producttypeid_link == 21) {
				Path += "Sub_Material"; //Vai lot
			}
			else if (producttypeid_link == 22) {
				Path += "Mix_Material"; // Vai phoi
			}
			else if (producttypeid_link == 23) {
				Path += "Mex"; // Vai phoi
			}
		}
		else if (30 <= producttypeid_link && 40 > producttypeid_link) {
			Path += "sewingtrim";
		}
		else if (40 <= producttypeid_link && 50 > producttypeid_link) {
			Path += "packingtrim";
		}
		else if (50 <= producttypeid_link && 60 > producttypeid_link) {
			Path += "thread";
		}
		
		return Path;
	}
	
	public String getInvoiceNumber() {
		String invoice_number = "";
		Stocking_UniqueCode stocking = stockingService.getby_type(4);
		String prefix = stocking.getStocking_prefix();
		Integer max = stocking.getStocking_max() + 1;
		String STT = max.toString();
		
		while(STT.toString().length() < 5) {
			STT = "0"+STT;
		}
		invoice_number = prefix + "_" + STT;
		return invoice_number;
	}
	
	public String get_BikeNUmber() {
		String bike_number = "";
		Stocking_UniqueCode stocking = stockingService.getby_type(5);
		String prefix = stocking.getStocking_prefix();
		Integer max = stocking.getStocking_max() + 1;
		String STT = max.toString();
		
		while(STT.toString().length() < 7) {
			STT = "0"+STT;
		}
		bike_number = prefix + "-" + STT;
		return bike_number;
	}
	
	public String getStockout_order_code() {
		String stockout_order_code = "";
		Stocking_UniqueCode stocking = stockingService.getby_type(3);
		String prefix = stocking.getStocking_prefix();
		Integer max = stocking.getStocking_max() + 1;
		String STT = max.toString();
		
		while(STT.toString().length() < 5) {
			STT = "0"+STT;
		}
		stockout_order_code = prefix + "_" + STT;
		return stockout_order_code;
	}
	
	public static void copyFile(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	public boolean CompareDate(Calendar a, Calendar b) {
		if(a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR) &&
				a.get(Calendar.MONTH) == b.get(Calendar.MONTH) &&
				a.get(Calendar.YEAR) == b.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}
	
	public boolean CompareDate(Date a, Date b) {
		Calendar c_a = Calendar.getInstance();
		c_a.setTime(a);
		Calendar c_b = Calendar.getInstance();
		c_b.setTime(b);
		if(c_a.get(Calendar.DAY_OF_YEAR) == c_b.get(Calendar.DAY_OF_YEAR) &&
			c_a.get(Calendar.MONTH) == c_b.get(Calendar.MONTH) &&
			c_a.get(Calendar.YEAR) == c_b.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}
	
	public Date ReCalculate(Long porder_grant_id_link, Long orgrootid_link) {
		POrderGrant grant = grantService.findOne(porder_grant_id_link);
		int amount = grant.getGrantamount();
		int productivity = grant.getProductivity();
		int duration = getDuration_byProductivity(amount, productivity);
		Date startdate = getBeginOfDate(grant.getStart_date_plan());
		Date dateend = Date_Add_with_holiday(startdate, duration - 1, orgrootid_link);
		
		
		grant.setDuration(duration);
		grant.setFinish_date_plan(dateend);
		grantService.save(grant);
		return dateend;
	}

	public int getDuration(Date startdate, Date enddate, long orgrootid_link) {
		if(startdate.after(enddate)) return 1;
		
		int duration = 0;		
		
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		
		start.setTime(startdate);
		end.setTime(enddate);
		List<Integer> list_year = new ArrayList<Integer>();
		list_year.add(start.get(Calendar.YEAR));
		list_year.add(end.get(Calendar.YEAR));
		
		List<Holiday> list_holiday = holidayService.getby_many_year(orgrootid_link, list_year);
		
		while(start.before(end)) {
			if(start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				boolean check = false;
				for(Holiday holiday : list_holiday) {
					Calendar day = Calendar.getInstance();
					day.setTime(holiday.getDay());
					if(CompareDate(start, day)) {
						check = true;
						break;
					}
				}
				
				if(!check) {
					duration++;
				}
			}
			
			start.add(Calendar.DAY_OF_WEEK , 1);
		}
		
//		if(!check_dayoff(end, orgrootid_link)) {
//			duration++;
//		}
		
		return duration;
	}
	
	public int getDuration_byProductivity(int total, int productivity) {
		int duration = ((int)Math.ceil(total/productivity) + (total % productivity == 0 ? 0 : 1));
		return duration;
	}
	
	public int getProductivity(int total, int duration) {
		if(duration ==0 ) return total;
		
		int ret = ((int)Math.ceil(total/duration) + (total % duration == 0 ? 0 : 1));
		return ret;
	}
	
	public boolean check_dayoff(Calendar _date, long orgrootid_link) {
		if(_date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return true;
		else {
			int year = _date.get(Calendar.YEAR);
			List<Holiday> list_holiday = holidayService.getby_year(orgrootid_link, year);
			for(Holiday holiday : list_holiday) {
				Calendar c_holiday = Calendar.getInstance();
				c_holiday.setTime(holiday.getDay());
				if(CompareDate(_date, c_holiday)) {
					return true;
				}
//				if(_date.getTime().compareTo(holiday.getDay()) == 0) {
//					return true;
//				}
			}
		}
		return false;
	}
	public static Date Date_Add(Date date, int amount) {
		Calendar _date = Calendar.getInstance();
		if(amount != 0) {
			_date.setTime(date);
			_date.add(Calendar.DATE, amount);
			_date.set(Calendar.HOUR_OF_DAY, 0);
			_date.set(Calendar.MINUTE, 0);
			_date.set(Calendar.SECOND, 0);
		}
		
		return _date.getTime();
	}
	public static int Date_Compare(Date date1, Date date2) {
	    Calendar calendar1 = Calendar.getInstance();

	    calendar1.setTime(date1);
	    calendar1.set(Calendar.HOUR_OF_DAY, 0);
	    calendar1.set(Calendar.MINUTE, 0);
	    calendar1.set(Calendar.SECOND, 0);
	    calendar1.set(Calendar.MILLISECOND, 0);
	    
	    Calendar calendar2 = Calendar.getInstance();

	    calendar2.setTime(date2);
	    calendar2.set(Calendar.HOUR_OF_DAY, 0);
	    calendar2.set(Calendar.MINUTE, 0);
	    calendar2.set(Calendar.SECOND, 0);
	    calendar2.set(Calendar.MILLISECOND, 0);

	    return calendar1.compareTo(calendar2);
	}
	public static String Date_ToString(Date date1, String format){
		DateFormat dateFormat = new SimpleDateFormat(format);  
		return dateFormat.format(date1);  
	}
	public Date Date_Add_with_holiday(Date date, int amount, long orgrootid_link) {
		Calendar _date = Calendar.getInstance();
		_date.setTime(date);
		if(amount == 1) {
			_date.add(Calendar.DATE, 1);
			while(check_dayoff(_date, orgrootid_link)) {
				_date.add(Calendar.DATE, 1);
			}
		}
		else {
			while(amount > 0) {
				_date.add(Calendar.DATE, 1);
				if(!check_dayoff(_date, orgrootid_link)) {
					amount--;
				}
			}
			
			while(amount < 0) {
				_date.add(Calendar.DATE, -1);
				if(!check_dayoff(_date, orgrootid_link)) {
					amount++;
				}
			}
		}
		
		_date.set(Calendar.HOUR_OF_DAY, 0);
		_date.set(Calendar.MINUTE, 0);
		_date.set(Calendar.SECOND, 0);
		_date.add(Calendar.DAY_OF_WEEK, 1);
		_date.add(Calendar.MINUTE, -1);
		
		return _date.getTime();
	}
	
	public Date getEndOfDate(Date date) {
		Calendar start = Calendar.getInstance();
		start.setTime(date);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		
		start.add(Calendar.DAY_OF_WEEK, 1);
		start.add(Calendar.MINUTE, -1);
		Date date_ret = start.getTime();
		return date_ret;
	}
	
	public Date getBeginOfDate(Date date) {
		Calendar start = Calendar.getInstance();
		start.setTime(date);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		Date date_ret = start.getTime();
		return date_ret;
	}
	
	public Date getPrevious(Date date) {
		Calendar start = Calendar.getInstance();
		start.setTime(date);
		start.add(Calendar.MINUTE, -1);
		Date date_ret = start.getTime();
		return date_ret;
	}
	
	public List<Date> getList_SunDay_byYear(int year){
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(year, 1, 1, 0,0,0);
		end.set(year+1, 12, 31,0,0,0);
		List<Date> list = new ArrayList<Date>();
		while(start.before(end)) {
			if(start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				list.add(start.getTime());
			start.add(Calendar.DATE, 1);
		}
		
		return list;
	}
	
	public List<Date> getList_SunDay_between_time(Date startDate, Date endDate){
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		
		List<Date> list = new ArrayList<Date>();
		while(start.before(end)) {
			if(start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				list.add(start.getTime());
			start.add(Calendar.DATE, 1);
		}
		
		return list;
	}
	
	public int Calculate_pquantity_production(int amount) {
		int pquantity_production = 0;
		ConfigAmount list_cfg = cfamountService.getby_amount(amount);
		if(list_cfg!=null) {
			// type = 0: add, 1: percent
			if(list_cfg.getType() == 0) {
				int plus = Math.abs(list_cfg.getAmount_plus()) == list_cfg.getAmount_plus() ? 0 : 1;
				pquantity_production = amount + (int)Math.abs(list_cfg.getAmount_plus()) + plus;
			}
			else {
				float a = amount*list_cfg.getAmount_plus()/100;
				int b = (int)Math.abs(a);
				int amount_plus =  a - (float)b < 0.5 ? 0 : 1;
				pquantity_production = amount + (int)Math.abs(a) + amount_plus;
			}
		}
		
		return pquantity_production;
	}
	
	public String FormatNumber(int a) {
		 DecimalFormat myFormatter = new DecimalFormat("#,###");
		 return myFormatter.format(a);
	}
	
	public String getString_currency(Long id) {
		switch (id.intValue()) {
		case 1:
			return "$";

		default:
			return "$";
		}
	}
	
	public String getState(int status) {
		switch (status) {
		case 0:
			return "ChuaLam";
		case 1:
			return "DangLam";
		case 2:
			return "DaXong";
		case -1:
			return "TuChoi";
		default:
			return "";
		}
	}
	
	public String getCls_header(Long typeid_link) {
		int typeid = typeid_link.intValue();
		switch (typeid) {
		case -1:
			return "task-header";
		case 0:
			return "task-header-porderreq";
		case 1:
			return "task-header-podetail";
		case 2:
			return "task-header-bom";
		case 3:
			return "task-header-bom2";
		case 4:
			return "task-header-pordercreate";
		case 5:
			return "task-header-granttoline";
		default:
			return "task-header-blue";
		}
	}
	
	public String getCls_bystatus(int status) {
		switch (status) {
		case 0:
			return "ChuaLam";
		case 1:
			return "DangLam";
		case 2:
			return "DaXong";
		case -1:
			return "TuChoi";
		default :
			return "ChuaLam";
		}
	}
	public int gettype_npl_byname(String name) {
		int type = 0;
		switch (name) {
		case "SEWING":
			type = 30;
			break;
		case "PACKING":
			type = 40;
			break;
		case "TICKET":
			type = 60;
			break;
		case "FABRIC":
			type = 20;
			break;
		case "THREAD":
			type = 50;
			break;
		}
		return type;
	}
	
	public String gettypename_npl_by_id(int typeid) {
		String type = "Other";
		switch (typeid) {
		case 30:
			type = "SEWING";
			break;
		case 40:
			type = "PACKING";
			break;
		case 60:
			type = "TICKET";
			break;
		case 20:
			type = "FABRIC";
			break;
		case 50:
			type = "THREAD";
			break;
		}
		return type;
	}
	
	public String getStringValue(Cell cell) {
		if(cell == null) return "";
		try {
			Double value = cell.getNumericCellValue();
			int i_value = value.intValue();
			if(value == i_value) {
				return (i_value+"").equals("0") ? "" : i_value+"";
			}
			return (value+"").equals("0") ? "" : value+"";
		}
		catch (Exception e) {
			
		}
		
		try {
			int value = (int)cell.getNumericCellValue();
			return (value+"").equals("0") ? "" : value+"";
		}
		catch (Exception e) {
			
		}
		
		try {
			return cell.getStringCellValue();
		}
		catch (Exception e) {
			return "";
		}
	}
	
	
	public String GetStockinCode() {
		String code = "";
		Stocking_UniqueCode stocking = stockingService.getby_type(1);
		String prefix = stocking.getStocking_prefix();
		Integer max = stocking.getStocking_max() + 1;
		String STT = max.toString();
		
		while(STT.toString().length() < 5) {
			STT = "0"+STT;
		}
		code = prefix + "_" + STT;
		return code;
	}
	
	public String GetStockoutCode() {
		String code = "";
		Stocking_UniqueCode stocking = stockingService.getby_type(2);
		String prefix = stocking.getStocking_prefix();
		Integer max = stocking.getStocking_max() + 1;
		String STT = max.toString();
		
		while(STT.toString().length() < 5) {
			STT = "0"+STT;
		}
		code = prefix + "_" + STT;
		return code;
	}
	
	public String GetSessionCode() {
		String code = "";
		Stocking_UniqueCode stocking = stockingService.getby_type(4);
		String prefix = stocking.getStocking_prefix();
		Integer max = stocking.getStocking_max() + 1;
		String STT = max.toString();
		
		while(STT.toString().length() < 5) {
			STT = "0"+STT;
		}
		code = prefix + "_" + STT;
		return code;
	}
	
	public static Double convertCmToYds(Double cm) {
		return (double)0.0109361*cm;
	}
	public static Double convertMetToYds(Double met) {
		return (double)1.09361*met;
	}
	public static Double convertCmToMet(Double cm) {
		return (double)0.01*cm;
	}
}
