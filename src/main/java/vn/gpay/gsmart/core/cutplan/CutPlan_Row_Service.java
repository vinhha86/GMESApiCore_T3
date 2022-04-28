package vn.gpay.gsmart.core.cutplan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.cutplan_processing.CutplanProcessing;
import vn.gpay.gsmart.core.cutplan_processing.ICutplanProcessingService;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKURepository;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKUService;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.porder.POrder;
import vn.gpay.gsmart.core.porder_bom_color.IPOrderBomColor_Service;
import vn.gpay.gsmart.core.porder_bom_color.PorderBomColor;
import vn.gpay.gsmart.core.porder_bom_product.IPOrderBomProduct_Service;
import vn.gpay.gsmart.core.porder_bom_product.POrderBomProduct;
import vn.gpay.gsmart.core.porder_bom_sku.IPOrderBOMSKU_LoaiPhoi_Service;
import vn.gpay.gsmart.core.porder_bom_sku.IPOrderBOMSKU_Service;
import vn.gpay.gsmart.core.porder_bom_sku.POrderBOMSKU;
import vn.gpay.gsmart.core.porder_bom_sku.porder_bom_sku_loaiphoi;
import vn.gpay.gsmart.core.utils.AtributeFixValues;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.CutPlanRowType;
import vn.gpay.gsmart.core.utils.CutPlan_TypePhoiMau;
import vn.gpay.gsmart.core.utils.POrderBomType;

@Service
public class CutPlan_Row_Service extends AbstractService<CutPlan_Row> implements ICutPlan_Row_Service {
	@Autowired
	CutPlan_Row_Repository repo;
	@Autowired
	IPContractProductSKURepository sku_repo;
	@Autowired
	IPOrderBOMSKU_Service porderbomskuService;
	@Autowired
	ICutPlan_Size_Service cutplan_size_Service;
	@Autowired
	ICutPlan_Row_Service cutrow_Service;
	@Autowired
	IPOrderBomProduct_Service porderbomproductService;
	@Autowired
	IPOrderBomColor_Service porderbomcolorService;
	@Autowired
	ICutplanProcessingService cutplanprocessingService;
	@Autowired
	IPOrderBOMSKU_LoaiPhoi_Service skubom_loaiphoi_Service;
	@Autowired
	IPContractProductSKUService pskuservice;
	
	@Override
	protected JpaRepository<CutPlan_Row, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	@Override
	public List<CutPlan_Row> getby_color(Long porderid_link, Long material_skuid_link, Long colorid_link,
			Long orgrootid_link) {
		// TODO Auto-generated method stub
		Long attributeid_link = AtributeFixValues.ATTR_COLOR;
		return repo.getby_sku_and_porder(porderid_link, material_skuid_link, orgrootid_link, colorid_link,
				attributeid_link);
	}

	@Override
	public List<CutPlan_Row> getby_porder_matsku_productsku(Long porderid_link, Long material_skuid_link,
			Long product_skuid_link, Integer type, String name) {
		// TODO Auto-generated method stub
		return repo.getby_matsku_and_porder_and_productsku(material_skuid_link, porderid_link, product_skuid_link, type,
				name);
	}
	
	/*
	 * Written By HungDaiBang
	 * Tính lại trung bình dinh_muc_cat cua cac loai ke hoach cat --> Dinh muc kt
	 */
	@Override
	public boolean sync_porderbom_kt(Long pcontractid_link,Long productid_link,Long material_skuid_link,Long colorid_link,
			Long userid_link, Long orgrootid_link) {
		try {
			List<CutPlan_Dinhmuc_KT> lsDinh_muc_cat = repo.getavg_dinhmuccat(pcontractid_link,productid_link,material_skuid_link,colorid_link);
			Double dinhmuc_kt_total = (double)0;
			//Dinh muc KT bang tong dinh muc cua cac ke hoach cat
			for(CutPlan_Dinhmuc_KT dm_cat:lsDinh_muc_cat) {
				dinhmuc_kt_total += null != dm_cat.getDinhmuc_kt()?dm_cat.getDinhmuc_kt():(double)0;
				
				//1. Lấy plan_row của dòng yêu cầu (row type = 2)
				List<CutPlan_Row> ls_planrow_yeucau = cutrow_Service.getplanrow_bykey(
						pcontractid_link, 
						productid_link, 
						material_skuid_link, 
						colorid_link, 
						dm_cat.getLoaiphoimau(), 
						CutPlanRowType.yeucau);
				if (ls_planrow_yeucau.size() > 0) {
					CutPlan_Row planrow_yeucau = ls_planrow_yeucau.get(0);
					planrow_yeucau.setDinh_muc_cat(dm_cat.getDinhmuc_kt().floatValue());
				}
			}
			
			//Xác định loại định mức dựa trên kế hoạch cắt (loại phối màu)
//			Integer typeDinhmuc = -1;
//			if (typephoimau == CutPlan_TypePhoiMau.SODO) typeDinhmuc = POrderBomType.Kythuat;
//			if (typephoimau == CutPlan_TypePhoiMau.VIEN) typeDinhmuc = POrderBomType.Vien;
			Integer typeDinhmuc = POrderBomType.Kythuat;
			
			//Lay don vi tinh cua dinh muc tu porder_bom_product
			Long bom_product_unit = null;
			List<POrderBomProduct> pContractProductBom = porderbomproductService
					.getby_pcontract_product_and_material(pcontractid_link, productid_link, material_skuid_link);
			if (pContractProductBom.size() > 0) {
				POrderBomProduct porderbom = pContractProductBom.get(0);
				bom_product_unit = porderbom.getUnitid_link();
			}
			
			// Lay danh sach sku cua san pham trong don hang
			List<Long> ls_productid = new ArrayList<Long>();
			ls_productid.add(productid_link);
			List<PContractProductSKU> list_sku = pskuservice.getsumsku_bypcontract(pcontractid_link, ls_productid);
			
			//Cập nhật định mức kỹ thuật cho tất cả các Product SKU vào bảng porder_bom_sku
			for(PContractProductSKU theSKU:list_sku) {
//				List<POrderBOMSKU> lsBOMSKU = porderbomskuService.getByProductSKU(pcontractid_link, productid_link, material_skuid_link, theSKU.getSkuid_link());
				List<POrderBOMSKU> lsBOMSKU = porderbomskuService.getby_pcontract_product_and_material_and_sku_and_type(
						pcontractid_link, productid_link, 
						material_skuid_link, theSKU.getSkuid_link(), typeDinhmuc);
				/* 
				 * Don vi tinh cua ban cat mac dinh la cm
				 * --> Qui doi ve don vi tinh cua dinh muc
				 * --> Neu dinh muc khong co DVT --> Mac dinh qui doi ve yds
				 */
				Double dinhmuc_kt_total_convert = null;
				if (null != bom_product_unit) {
					if (bom_product_unit.equals((long)1)) { //DVT Met
//						dinhmuc_kt_total_convert = Common.convertCmToMet(dinhmuc_kt_total);
						dinhmuc_kt_total_convert = dinhmuc_kt_total;
					} else { //Khac met qui het ve YDS
						dinhmuc_kt_total_convert = Common.convertMetToYds(dinhmuc_kt_total);
					}
				} else { //Khong co DVT qui ve YDS
					dinhmuc_kt_total_convert = Common.convertMetToYds(dinhmuc_kt_total);
				}
				
				if (lsBOMSKU.size() > 0) {
					//Update
					POrderBOMSKU theBOMSKU = lsBOMSKU.get(0);
					theBOMSKU.setAmount(dinhmuc_kt_total_convert.floatValue());
					porderbomskuService.save(theBOMSKU);
				} else {
					//Addnew
					POrderBOMSKU theBOMSKU = new POrderBOMSKU();
					theBOMSKU.setType(typeDinhmuc);
					theBOMSKU.setCreateduserid_link(userid_link);
					theBOMSKU.setCreateddate(new Date());
					theBOMSKU.setPcontractid_link(pcontractid_link);
					theBOMSKU.setProductid_link(productid_link);
					theBOMSKU.setSkuid_link(theSKU.getSkuid_link());
					theBOMSKU.setMaterialid_link(material_skuid_link);
					theBOMSKU.setAmount(dinhmuc_kt_total_convert.floatValue());
					porderbomskuService.save(theBOMSKU);
					
				}
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/*
	 * Written by HungDaiBang
	 */
	@Override
	public boolean sync_porder_bom_new(Long material_skuid_link, Long pcontractid_link, Long productid_link,
			Long colorid_link, Long userid_link, Long orgrootid_link, String loaiphoimau, Integer typephoimau) {
		/*
		 * 1. Duyệt qua các dòng Cutplan_row --> Lấy các dòng Cutplan_size tương ứng và tính ra tỷ lệ size cho từng product_sku
		 * 2. Lấy SL_Vai của dòng Cutplan_row nhân với tỷ lệ size để xác định số vải sẽ dùng cho từng product_sku trong sơ đồ
		 * 3. Lưu lại thông tin cộng dồn Material_SKU + Product_SKU + Số lượng + SL vải + ĐỊnh mức (= SL_vai/SL_SKU)
		 */
		try {
			List<CutPlan_Row> ls_planrow = getplanrow_bykey(
					pcontractid_link, 
					productid_link, 
					material_skuid_link, 
					colorid_link, 
					loaiphoimau, 
					CutPlanRowType.sodocat);
			List<CutPlan_Dinhmuc> lsDinhmuc = new ArrayList<CutPlan_Dinhmuc>();
			for(CutPlan_Row cutplan_row: ls_planrow) {
				List<CutPlan_Size> ls_plansize = cutplan_size_Service.getplansize_byplanrow(cutplan_row.getId());
				
				//Tính tổng số sản phẩm trong plan_row
				int total_sku_planrow = 0;
				for(CutPlan_Size plan_size:ls_plansize) {
					total_sku_planrow += plan_size.getAmount();
				}
				
				//Tính tỷ lệ size và Số lượng vải dùng cho từng SKU
				if (total_sku_planrow > 0) {
					for(CutPlan_Size plan_size:ls_plansize) {
						Float ty_le = (float) plan_size.getAmount()/(float) total_sku_planrow;
						Float material_amount = cutplan_row.getSl_vai()*ty_le;
						CutPlan_Dinhmuc theDinhmuc = new CutPlan_Dinhmuc();
						
						boolean isExisted = false;
						for (CutPlan_Dinhmuc myDinhmuc:lsDinhmuc) {
							if (myDinhmuc.getId().equals(plan_size.getProduct_skuid_link())) {
								theDinhmuc = myDinhmuc;
								isExisted = true;
								break;
							}
						}
						if (!isExisted) {
							theDinhmuc.setId(plan_size.getProduct_skuid_link());
							lsDinhmuc.add(theDinhmuc);
						}
						theDinhmuc.setMaterialskuid_link(material_skuid_link);
						theDinhmuc.setProductsku_amount(theDinhmuc.getProductsku_amount() + plan_size.getAmount());
						theDinhmuc.setMaterial_amount(theDinhmuc.getMaterial_amount() + material_amount);
						theDinhmuc.calculateBOM();//Tính lại BOM cho Product SKU
					}
				}
			}
			
			//Xác định loại định mức dựa trên kế hoạch cắt (loại phối màu)
			Integer typeDinhmuc = -1;
			if (typephoimau == CutPlan_TypePhoiMau.SODO) typeDinhmuc = POrderBomType.Kythuat;
			if (typephoimau == CutPlan_TypePhoiMau.VIEN) typeDinhmuc = POrderBomType.Vien;
			
			//Lay don vi tinh cua dinh muc tu porder_bom_product
			Long bom_product_unit = null;
			List<POrderBomProduct> pContractProductBom = porderbomproductService
					.getby_pcontract_product_and_material(pcontractid_link, productid_link, material_skuid_link);
			if (pContractProductBom.size() > 0) {
				POrderBomProduct porderbom = pContractProductBom.get(0);
				bom_product_unit = porderbom.getUnitid_link();
			}
			
			for (CutPlan_Dinhmuc theDinhmuc:lsDinhmuc) {
				List<POrderBOMSKU> lsBOMSKU = porderbomskuService.getby_pcontract_product_and_material_and_sku_and_type(
						pcontractid_link, productid_link, 
						theDinhmuc.getMaterialskuid_link(), theDinhmuc.getId(), typeDinhmuc);

				/* 
				 * Don vi tinh cua ban cat mac dinh la cm
				 * --> Qui doi ve don vi tinh cua dinh muc
				 * --> Neu dinh muc khong co DVT --> Mac dinh qui doi ve yds
				 */
//				if (null != bom_product_unit) {
//					if (bom_product_unit.equals((long)1)) { //DVT Met
//						theDinhmuc.setCutplan_bom(Common.convertCmToMet(theDinhmuc.getCutplan_bom()));
//					} else { //Khac met qui het ve YDS
//						theDinhmuc.setCutplan_bom(Common.convertCmToYds(theDinhmuc.getCutplan_bom()));
//					}
//				} else { //Khong co DVT qui ve YDS
//					theDinhmuc.setCutplan_bom(Common.convertCmToYds(theDinhmuc.getCutplan_bom()));
//				}
				
				if (lsBOMSKU.size() > 0) {
					//Update
					POrderBOMSKU theBOMSKU = lsBOMSKU.get(0);
					theBOMSKU.setAmount(theDinhmuc.getCutplan_bom());
					porderbomskuService.save(theBOMSKU);
				} else {
					//Addnew
					POrderBOMSKU theBOMSKU = new POrderBOMSKU();
					theBOMSKU.setType(typeDinhmuc);
					theBOMSKU.setCreateduserid_link(userid_link);
					theBOMSKU.setCreateddate(new Date());
					theBOMSKU.setPcontractid_link(pcontractid_link);
					theBOMSKU.setProductid_link(productid_link);
					theBOMSKU.setSkuid_link(theDinhmuc.getId());
					theBOMSKU.setMaterialid_link(theDinhmuc.getMaterialskuid_link());
					theBOMSKU.setAmount(theDinhmuc.getCutplan_bom());
					porderbomskuService.save(theBOMSKU);
					
					//Lấy thông tin từ định mức cân đối sang
//					if (lsBOMSKU_Candoi.size()>0) {
//						POrderBOMSKU theBOMSKU_Candoi = lsBOMSKU_Candoi.get(0);
//						theBOMSKU.setLost_ratio(theBOMSKU_Candoi.getLost_ratio());
//					}
					
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean sync_porder_bom(Long material_skuid_link, Long pcontractid_link, Long productid_link,
			Long colorid_link, Long userid_link, Long orgrootid_link) {
		// TODO Auto-generated method stub
//		Long porderid_link = porder.getId();
		List<String> list_loaiphoi = cutrow_Service.getAllLoaiPhoiMau(pcontractid_link, productid_link,
				material_skuid_link);
		for (String loaiphoi : list_loaiphoi) {
			int tongsl_sp = 0;
			float sl_vai = 0;

			// tinh tong so luong san pham trong tat ca cac so do
			List<CutPlan_Size> list_sodo = cutplan_size_Service.getby_sku_and_pcontract_product_and_color_loaiphoi(
					material_skuid_link, pcontractid_link, productid_link, orgrootid_link, colorid_link, loaiphoi);
			Map<Long, Float> map = new HashMap<Long, Float>();
			for (CutPlan_Size sodo : list_sodo) {
				CutPlan_Row cut_row = cutrow_Service.findOne(sodo.getCutplanrowid_link());
				if (cut_row.getType().equals(CutPlanRowType.sodocat)) {
					tongsl_sp += (sodo.getAmount() == null ? 0 : sodo.getAmount()) * cut_row.getLa_vai();
					if (map.get(sodo.getCutplanrowid_link()) == null) {
						float f_slvai = cut_row.getSl_vai();
						sl_vai += f_slvai;
						map.put(sodo.getCutplanrowid_link(), f_slvai);
					}

				}
			}

			List<Long> list_sku = sku_repo.getskuid_bycolorid_link(productid_link, pcontractid_link, colorid_link);

			for (Long product_skuid_link : list_sku) {
				float bom = 0;
				int sl_zise = 0;

				// tinh so luong cua tung co
				List<CutPlan_Size> listsize_sodo = cutplan_size_Service
						.getby_pcontract_product_matsku_productsku_loaiphoi(pcontractid_link, productid_link,
								material_skuid_link, product_skuid_link, CutPlanRowType.sodocat, "", loaiphoi);
				for (CutPlan_Size cut_size : listsize_sodo) {
					CutPlan_Row cut_row = cutrow_Service.findOne(cut_size.getCutplanrowid_link());

					if (cut_size.getProduct_skuid_link().equals(product_skuid_link))
						sl_zise += (cut_size.getAmount() == null ? 0 : cut_size.getAmount()) * cut_row.getLa_vai();

				}

				float bom_avg = (float) (sl_vai / (float) tongsl_sp);
//				float size_percent = ((float) sl_zise / (float) tongsl_sp);
				bom = bom_avg * 1;
//				if (bom > 0) {
//					String av = "";
//				}
				bom = (float) Math.ceil((bom * 10000)) / 10000;

				List<porder_bom_sku_loaiphoi> list_bomsku_loaiphoi = skubom_loaiphoi_Service
						.getbypcontract_product_material_sku_loaiphoi(pcontractid_link, productid_link,
								material_skuid_link, product_skuid_link, loaiphoi);

				// neu co dinh muc roi thi cap nhat chua co thi them vao
				if (list_bomsku_loaiphoi.size() > 0) {
					porder_bom_sku_loaiphoi bomsku = list_bomsku_loaiphoi.get(0);
					bomsku.setAmount(bom);
					skubom_loaiphoi_Service.save(bomsku);
				} else {
					porder_bom_sku_loaiphoi bomsku = new porder_bom_sku_loaiphoi();
					bomsku.setId(null);
					bomsku.setAmount(bom);
					bomsku.setMaterial_skuid_link(material_skuid_link);
					bomsku.setPcontractid_link(pcontractid_link);
					bomsku.setProductid_link(productid_link);
					bomsku.setSkuid_link(product_skuid_link);
					bomsku.setLoaiphoi(loaiphoi);

					skubom_loaiphoi_Service.save(bomsku);

				}

				List<porder_bom_sku_loaiphoi> list_bom_loaiphoi = skubom_loaiphoi_Service
						.getbypcontract_product_material_sku(pcontractid_link, productid_link, material_skuid_link,
								product_skuid_link);
				float bomall = 0;

				for (porder_bom_sku_loaiphoi bomsku_loaiphoi : list_bom_loaiphoi) {
					bomall += bomsku_loaiphoi.getAmount();
				}

				List<POrderBOMSKU> list_bomsku = porderbomskuService
						.getby_pcontract_product_and_material_and_sku_and_type(pcontractid_link, productid_link,
								material_skuid_link, product_skuid_link, POrderBomType.Kythuat);

				// neu co dinh muc roi thi cap nhat chua co thi them vao
				if (list_bomsku.size() > 0) {
					POrderBOMSKU bomsku = list_bomsku.get(0);
					bomsku.setAmount(bomall);
					porderbomskuService.save(bomsku);
				} else {
					POrderBOMSKU bomsku = new POrderBOMSKU();
					bomsku.setId(null);
					bomsku.setAmount(bom);
					bomsku.setCreateddate(new Date());
					bomsku.setCreateduserid_link(userid_link);
					bomsku.setMaterialid_link(material_skuid_link);
					bomsku.setOrgrootid_link(orgrootid_link);
					bomsku.setPcontractid_link(pcontractid_link);
					bomsku.setPorderid_link(null);
					bomsku.setProductid_link(productid_link);
					bomsku.setSkuid_link(product_skuid_link);
					bomsku.setType(POrderBomType.Kythuat);

					porderbomskuService.save(bomsku);

				}
			}
		}

		// xoa het dinh muc chung va chung mau cua nguyen lieu
		// update lai bang bom amount = 0
		List<POrderBomProduct> pContractProductBom = porderbomproductService
				.getby_pcontract_product_and_material(pcontractid_link, productid_link, material_skuid_link);
		if (pContractProductBom.size() > 0) {
			POrderBomProduct porderbom = pContractProductBom.get(0);
			porderbom.setAmount((float) 0);
			porderbomproductService.update(porderbom);
		}

		// update lai bang sku bom
		List<PorderBomColor> listcolor = porderbomcolorService.getby_pcontract_product_and_material_and_color(
				pcontractid_link, productid_link, material_skuid_link, colorid_link);

		for (PorderBomColor pColor : listcolor) {
			porderbomcolorService.delete(pColor);
		}

		return true;
	}

	@Override
	public List<CutPlan_Row> findByPOrder(Long porderid_link) {
		// TODO Auto-generated method stub
		return repo.findByPOrder(porderid_link);
	}

	@Override
	public boolean sync_porder_bom_from_cutprocesing(Long material_skuid_link, POrder porder, Long colorid_link,
			Long userid_link, Long orgrootid_link) {
		// TODO Auto-generated method stub
		Long productid_link = porder.getProductid_link();
		Long pcontractid_link = porder.getPcontractid_link();
		Long porderid_link = porder.getId();

		int tongsl_sp = 0;
		float sl_vai = 0;

		// tinh tong so luong san pham trong tat ca cac so do
		List<CutPlan_Size> list_sodo = cutplan_size_Service.getby_sku_and_porder_and_color(material_skuid_link,
				porderid_link, orgrootid_link, colorid_link);
		for (CutPlan_Size sodo : list_sodo) {
			List<CutplanProcessing> list_cut_processing = cutplanprocessingService
					.getby_cutplanrow(sodo.getCutplanrowid_link());
			if (list_cut_processing.size() > 0) {
				tongsl_sp += (sodo.getAmount() == null ? 0 : sodo.getAmount()) * list_cut_processing.get(0).getLa_vai();
				sl_vai += list_cut_processing.get(0).getAmountcut();
			}
//			CutPlan_Row cut_row = cutrow_Service.findOne(sodo.getCutplanrowid_link());
//			if(cut_row.getType() == CutPlanRowType.sodocat) {
//				tongsl_sp += (sodo.getAmount() == null ? 0 : sodo.getAmount()) * cut_row.getLa_vai();
//				sl_vai += cut_row.getSl_vai();
//			}
		}

		List<Long> list_sku = sku_repo.getskuid_bycolorid_link(productid_link, pcontractid_link, colorid_link);
		for (Long product_skuid_link : list_sku) {
			float bom = 0;
			int sl_zise = 0;

			// tinh so luong cua tung co
			List<CutPlan_Size> listsize_sodo = cutplan_size_Service.getby_porder_matsku_productsku(porderid_link,
					material_skuid_link, product_skuid_link, CutPlanRowType.sodocat, "");
			for (CutPlan_Size cut_size : listsize_sodo) {
				CutPlan_Row cut_row = cutrow_Service.findOne(cut_size.getCutplanrowid_link());

				if (cut_size.getProduct_skuid_link().equals(product_skuid_link))
					sl_zise += (cut_size.getAmount() == null ? 0 : cut_size.getAmount()) * cut_row.getLa_vai();

			}

			float bom_avg = (float) (sl_vai / (float) tongsl_sp);
			float size_percent = ((float) sl_zise / (float) tongsl_sp);
			bom = bom_avg * size_percent;
			bom = (float) Math.ceil((bom * 10000)) / 10000;

			List<POrderBOMSKU> list_bomsku = porderbomskuService.getby_porder_and_material_and_sku_and_type(
					porderid_link, material_skuid_link, product_skuid_link, POrderBomType.SanXuat);

			// neu co dinh muc roi thi cap nhat chua co thi them vao
			if (list_bomsku.size() > 0) {

				POrderBOMSKU bomsku = list_bomsku.get(0);
				bomsku.setAmount(bom);
				porderbomskuService.save(bomsku);
			} else {
				POrderBOMSKU bomsku = new POrderBOMSKU();
				bomsku.setId(null);
				bomsku.setAmount(bom);
				bomsku.setCreateddate(new Date());
				bomsku.setCreateduserid_link(userid_link);
				bomsku.setMaterialid_link(material_skuid_link);
				bomsku.setOrgrootid_link(orgrootid_link);
				bomsku.setPcontractid_link(pcontractid_link);
				bomsku.setPorderid_link(porderid_link);
				bomsku.setProductid_link(productid_link);
				bomsku.setSkuid_link(product_skuid_link);

				porderbomskuService.save(bomsku);

			}
		}

		// xoa het dinh muc chung va chung mau cua nguyen lieu
		// update lai bang bom amount = 0
		List<POrderBomProduct> pContractProductBom = porderbomproductService.getby_porder_and_material(porderid_link,
				material_skuid_link);
		if (pContractProductBom.size() > 0) {
			POrderBomProduct porderbom = pContractProductBom.get(0);
			porderbom.setAmount((float) 0);
			porderbomproductService.update(porderbom);
		}

		// update lai bang sku bom
		List<PorderBomColor> listcolor = porderbomcolorService.getby_porder_and_material_and_color(porderid_link,
				material_skuid_link, colorid_link);

		for (PorderBomColor pColor : listcolor) {
			porderbomcolorService.delete(pColor);
		}

		return true;
	}

	@Override
	public List<CutPlan_Row> getby_color(Long pcontractid_link, Long productid_link, Long material_skuid_link,
			Long colorid_link, Long orgrootid_link) {
		// TODO Auto-generated method stub
		Long attributeid_link = AtributeFixValues.ATTR_COLOR;
		return repo.getby_sku_and_pcontract_and_product(pcontractid_link, productid_link, material_skuid_link,
				orgrootid_link, colorid_link, attributeid_link);
	}

	@Override
	public List<String> getAllLoaiPhoiMau(Long pcontractid_link, Long productid_link, Long material_skuid_link) {
		return repo.GetAll_loaiphoimau(pcontractid_link, productid_link, material_skuid_link);
	}

	@Override
	public List<CutPlan_Row> getby_loaiphoi(Long pcontractid_link, Long productid_link, Long material_skuid_link,
			Long colorid_link, Long orgrootid_link, String loaiphoi) {
		Long attributeid_link = AtributeFixValues.ATTR_COLOR;
		return repo.getby_loaiphoi(pcontractid_link, productid_link, material_skuid_link, orgrootid_link, colorid_link,
				attributeid_link, loaiphoi);
	}

	//HungDaiBang
	@Override
	public List<CutPlan_Row> getplanrow_bykey(
			Long pcontractid_link,
			Long productid_link,
			Long material_skuid_link,
			Long colorid_link,
			String loaiphoimau,
			Integer type
			) {
		return repo.getplanrow_bykey(
				pcontractid_link, 
				productid_link, 
				material_skuid_link, 
				colorid_link, 
				loaiphoimau,
				type
				);
	}

}
