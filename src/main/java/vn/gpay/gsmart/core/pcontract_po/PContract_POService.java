package vn.gpay.gsmart.core.pcontract_po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.cutplan_processing.ICutplanProcessingService;
import vn.gpay.gsmart.core.packingtype.IPackingTypeRepository;
import vn.gpay.gsmart.core.packingtype.PackingType;
import vn.gpay.gsmart.core.pcontract_price.IPContract_Price_Repository;
import vn.gpay.gsmart.core.pcontractproductsku.IPContractProductSKURepository;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.porder_bom_sku.IPOrderBOMSKU_Service;
import vn.gpay.gsmart.core.porder_grant.IPOrderGrant_Service;
import vn.gpay.gsmart.core.porder_product_sku.IPOrder_Product_SKU_Service;
import vn.gpay.gsmart.core.porderprocessing.IPOrderProcessing_Service;
import vn.gpay.gsmart.core.porders_poline.IPOrder_POLine_Service;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.productpairing.IProductPairingService;
import vn.gpay.gsmart.core.productpairing.ProductPairing;
import vn.gpay.gsmart.core.sizesetattributevalue.ISizeSetAttributeRepository;
import vn.gpay.gsmart.core.sizesetattributevalue.SizeSetAttributeValue;
import vn.gpay.gsmart.core.sku.ISKU_AttValue_Repository;
import vn.gpay.gsmart.core.stockin.IStockInService;
import vn.gpay.gsmart.core.stockout.IStockOutService;
import vn.gpay.gsmart.core.utils.ProductType;

@Service
public class PContract_POService extends AbstractService<PContract_PO> implements IPContract_POService {
	@Autowired
	IPContract_PORepository repo;
	@Autowired
	IPContract_Price_Repository price_repo;
	@Autowired
	EntityManager em;
	@Autowired
	IPackingTypeRepository packing_repo;
	@Autowired
	IPOrder_POLine_Service porder_line_Service;
	@Autowired
	IPOrderGrant_Service porderGrantService;
	@Autowired
	IPOrderProcessing_Service pprocessRepository;
	@Autowired
	IStockOutService stockOutService;
	@Autowired
	IStockInService stockInService;
	@Autowired
	IPOrder_Product_SKU_Service porderskuService;
	@Autowired
	IPOrderBOMSKU_Service porderBOMSKU_Service;
	@Autowired
	ICutplanProcessingService cutplanProcessingService;
	@Autowired
	IProductService productService;
	@Autowired
	IProductPairingService pairService;
	@Autowired
	IPContractProductSKURepository pcontractsku_repo;
	@Autowired
	ISizeSetAttributeRepository sizesetatt_repo;
	@Autowired
	ISKU_AttValue_Repository sku_av_repo;

	@Override
	protected JpaRepository<PContract_PO, Long> getRepository() {
		return repo;
	}

	@Override
	public List<PContract_PO> getPOByContractProduct(Long orgrootid_link, Long pcontractid_link, Long productid_link,
			Long userid_link, Long orgid_link, Integer potype) {
		if (orgid_link == 1)
			userid_link = null;
		if (potype == 0)
			return repo.getPO_Chaogia(orgrootid_link, pcontractid_link, productid_link, userid_link);
		else
			return repo.getPO_Duyet(orgrootid_link, pcontractid_link, productid_link, userid_link);
	}

	@Override
	public List<PContract_PO> getPOByContractAndProduct(Long pcontractid_link, Long productid_link) {
		return repo.getPOByContractAndProduct(pcontractid_link, productid_link);
	}

//	@Override
//	//Chi lay cac PO o muc la
//	public List<PContract_PO> getPO_LeafOnly(Long orgrootid_link,
//			Long pcontractid_link,Long productid_link, Long userid_link, Long orgid_link){
//		try{
//			if(orgid_link == 1) userid_link = null;
//			List<PContract_PO> a = repo.getPOByContractProduct(orgrootid_link, pcontractid_link, productid_link, userid_link);
//			return a;
//		} catch(Exception ex){
//			ex.printStackTrace();
//			return null;
//		}
//	}

	@Override
	public List<PContract_PO> getPOLeafOnlyByContract(Long pcontractid_link, Long productid_link) {
		try {
			productid_link = productid_link == 0 ? null : productid_link;
			return repo.getPOLeafOnlyByContract(pcontractid_link, productid_link);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	@Override
	public List<PContract_PO> getPOByContract(Long orgrootid_link, Long pcontractid_link) {
		return repo.getPOByContract(orgrootid_link, pcontractid_link);
	}

	@Override
	public List<PContract_PO> getPO_LaterShipdate(Long orgrootid_link, Long pcontractid_link, Long productid_link,
			Date shipdate) {
		return repo.getPO_LaterShipdate(orgrootid_link, pcontractid_link, productid_link, shipdate);
	}

	@Override
	public List<PContract_PO> getPO_Offer_Accept_ByPContract(Long pcontractid_link, Long productid_link) {
		try {
			return repo.getPO_Offer_Accept_ByPContract(pcontractid_link, productid_link == 0 ? null : productid_link);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<PContract_PO> getPcontractPoByPContractAndPOBuyer(Long pcontractid_link, String po_buyer,
			String buyercode) {
		return repo.getPcontractPoByPContractAndPOBuyer(pcontractid_link, po_buyer, buyercode);
	}

	@Override
	public List<PContract_PO> getone_by_template_set(String PO_No, Date ShipDate, long productid_link,
			long shipmodeid_link, long pcontractid_link) {
		Long shipmode = shipmodeid_link == 0 ? null : shipmodeid_link;
		return repo.getone_by_template_set(PO_No, shipmode, productid_link, ShipDate, pcontractid_link);
	}

	@Override
	public List<PContract_PO> check_exist_po(Date ShipDate, long productid_link, long shipmodeid_link,
			long pcontractid_link, String po_buyer) {
		po_buyer = (po_buyer == "" || po_buyer.toUpperCase() == "TBD") ? null : po_buyer;
		List<PContract_PO> list_po = repo.getone_by_template(shipmodeid_link, productid_link, ShipDate,
				pcontractid_link, po_buyer);

		return list_po;
	}

	@Override
	public List<PContract_PO> get_by_parentid(Long pcontractpo_parentid_link) {
		return repo.getby_parentid_link(pcontractpo_parentid_link);
	}

	@Override
	public List<PContract_PO> check_exist_po_children(String PO_No, Date Shipdate, Long shipmodeid_link,
			Long pcontractid_link, Long parentid_link) {
		return repo.getone_po_upload(PO_No, shipmodeid_link, Shipdate, pcontractid_link, parentid_link);
	}

	@Override
	public List<PContract_PO> check_exist_PONo(String PO_No, Long pcontractid_link) {
		return repo.getone_po_byPO_no(PO_No, pcontractid_link);
	}

	@Override
	public List<PContract_PO> getBySearch(String po_code, List<Long> orgs) {
		List<PContract_PO> lst = new ArrayList<PContract_PO>();
//		if (products.size() > 0)
//			if (orgs.size() > 0)
//				lst = repo.getBySearch(po_code,orgs);
//			else
//				lst = repo.getBySearch_ProductOnly(po_code);
//		else 
		po_code = po_code == null ? "" : po_code;
		if (orgs.size() > 0)
			lst = repo.getBySearch_OrgOnly(po_code, orgs);
		else
			lst = repo.getBySearch_CodeOnly(po_code);

		return lst;
	}

	@Override
	public List<PContract_POBinding> getForMarketTypeChart() {

		List<PContract_POBinding> data = new ArrayList<PContract_POBinding>();
		List<Object[]> objects = repo.getForMarketTypeChart();

		for (Object[] row : objects) {
			Long sum = (Long) row[0];
			String name = (String) row[1] == null ? "Khác" : (String) row[1];
			PContract_POBinding temp = new PContract_POBinding();
			temp.setSum(sum);
			temp.setMarketName(name);
			data.add(temp);
		}

		return data;
	}

	@Override
	public List<PContract_PO> getPO_Offer_Accept_ByPContract_AndOrg(Long pcontractid_link, Long productid_link,
			List<Long> list_orgid_link) {
		productid_link = productid_link == 0 ? null : productid_link;
		if (list_orgid_link.size() == 0)
			return repo.getPO_Offer_Accept_ByPContract(pcontractid_link, productid_link);
		else
			return repo.getPO_Offer_Accept_ByPContract_AndOrg(pcontractid_link, productid_link, list_orgid_link);
	}

	@Override
	public List<PContract_PO> getby_porder(Long porderid_link) {
		return repo.getby_porder(porderid_link);
	}

	@Override
	public List<PContract_PO> check_exist_line(Date ShipDate, long productid_link, long pcontractid_link,
			long parentid_link) {
		return repo.getone_line_giaohang(productid_link, ShipDate, pcontractid_link, parentid_link);
	}

	@Override
	public List<PContract_PO> get_by_parent_and_type(Long pcontractpo_parentid_link, int po_typeid_link) {
		return repo.getby_parent_and_type(pcontractpo_parentid_link, po_typeid_link);
	}

	@Override
	public List<PContract_PO> getall_offers_by_org(List<Long> orgid_link) {
		return repo.getOffers_byOrg(orgid_link);
	}

	@Override
	public List<Long> getpcontract_BySearch(String po_code, List<Long> orgs) {
		po_code = po_code == null ? "" : po_code;
		orgs = orgs.size() == 0 ? null : orgs;

		return repo.getPContractBySearch_OrgOnly(po_code, orgs);
	}

	@Override
	public List<PContract_PO> getBySearch_andType(String po_code, List<Long> orgs, int po_type) {
		orgs = orgs.size() == 0 ? null : orgs;
		return repo.getBySearch_OrgAndType(po_code, orgs, po_type);
	}

	@Override
	public List<PContract_PO> getby_pcontract_and_type(Long pcontractid_link, List<Integer> type) {
		return repo.getby_pcontract_and_type(type, pcontractid_link);
	}

	@Override
	public List<PContractPO_Shipping> get_po_shipping(Long orgrootid_link, List<Long> orgs, Long productid_link,
			Long colorid_link, Long sizesetid_link) {
		orgs = orgs.size() == 0 ? null : orgs;
		List<SizeSetAttributeValue> list_av = sizesetatt_repo.getall_bySizeSetId(sizesetid_link);
		List<Long> listsku = new ArrayList<Long>();

		if (sizesetid_link != null && colorid_link != null) {
			for (SizeSetAttributeValue sizeset_av : list_av) {
				List<Long> skuid_links = sku_av_repo.getskuid_by_valueMau_and_valueCo(colorid_link,
						sizeset_av.getAttributevalueid_link(), productid_link);
				if (skuid_links.size() > 0)
					listsku.add(skuid_links.get(0));
			}
			if (listsku.size() == 0)
				listsku.add((long) -1);
		} else {
			if (sizesetid_link == null && colorid_link != null) {
				listsku = sku_av_repo.get_bycolorid_link(productid_link, colorid_link);
				if (listsku.size() == 0)
					listsku.add((long) -1);
			} else {
				listsku = null;
			}
		}

		List<PContract_PO> list_po = repo.getby_product_color_sizeset( productid_link, listsku);

		List<PContractPO_Shipping> list_shipping = new ArrayList<PContractPO_Shipping>();
		for (PContract_PO po : list_po) {
			// kiem tra xem co phai san pham bo hay ko
			// neu la bo thi lay tung san pham con
			if (po.getProduct_typeid_link() != ProductType.SKU_TYPE_PRODUCT_PAIR) {
				PContractPO_Shipping ship = new PContractPO_Shipping();
				ship.setCode(po.getCode());
				ship.setPcontract_poid_link(po.getId());
				ship.setPcontractid_link(po.getPcontractid_link());
				ship.setPo_buyer(po.getPo_buyer());
				ship.setPo_quantity(po.getPo_quantity());
				ship.setProductid_link(po.getProductid_link());
				ship.setShipdate(po.getShipdate());
				ship.setProductbuyercode(po.getProductbuyercode());
				ship.setPortFrom(po.getPortFrom());
				ship.setShipmode_name(po.getShipMode());
				ship.setIsmap(po.getIsmap());
				ship.setProductbuyercode_parent(po.getProductbuyercode());
				ship.setOrggrantid_link(po.getOrggrantid_link());

				ship.setTotalpair(1);

				if (!po.getPackingnotice().equals("") && !po.getPackingnotice().equals("null")
						&& !po.getPackingnotice().equals(null)) {
					String[] arr_id = po.getPackingnotice().split(";");
					List<Long> list_id = new ArrayList<Long>();
					for (String id : arr_id) {
						list_id.add(Long.parseLong(id));
					}
					List<PackingType> list_packing = packing_repo.getbylistid(orgrootid_link, list_id);
					String packing_method = "";
					for (PackingType packing : list_packing) {
						if (packing_method != "") {
							packing_method += ", " + packing.getCode();
						} else {
							packing_method = packing.getCode();
						}
					}
					ship.setPacking_method(packing_method);
				}

				list_shipping.add(ship);
			} else {
				List<ProductPairing> p = pairService.getproduct_pairing_detail_bycontract(orgrootid_link,
						po.getPcontractid_link(), po.getProductid_link());
				p.removeIf(c -> !c.getProductid_link().equals(productid_link));
				for (ProductPairing pair : p) {
					PContractPO_Shipping ship = new PContractPO_Shipping();
					ship.setCode(po.getCode());
					ship.setPcontract_poid_link(po.getId());
					ship.setPcontractid_link(po.getPcontractid_link());
					ship.setPo_buyer(po.getPo_buyer());
					ship.setProductid_link(pair.getProductid_link());
					ship.setShipdate(po.getShipdate());
					ship.setProductbuyercode(pair.getProductBuyerCode());
					ship.setPortFrom(po.getPortFrom());
					ship.setShipmode_name(po.getShipMode());
					ship.setOrggrantid_link(po.getOrggrantid_link());
					ship.setTotalpair(1);
					ship.setProductbuyercode_parent(po.getProductbuyercode() + " (Bộ " + p.size() + ")");

					ship.setPo_quantity(po.getPo_quantity() * pair.getAmount());

					if (!po.getPackingnotice().equals("") && !po.getPackingnotice().equals("null")
							&& !po.getPackingnotice().equals(null)) {
						String[] arr_id = po.getPackingnotice().split(";");
						List<Long> list_id = new ArrayList<Long>();
						for (String id : arr_id) {
							list_id.add(Long.parseLong(id));
						}
						List<PackingType> list_packing = packing_repo.getbylistid(orgrootid_link, list_id);
						String packing_method = "";
						for (PackingType packing : list_packing) {
							if (packing_method != "") {
								packing_method += ", " + packing.getCode();
							} else {
								packing_method = packing.getCode();
							}
						}
						ship.setPacking_method(packing_method);
					}

					boolean is_map = false;
					List<PContractProductSKU> list_sku = pcontractsku_repo.getsku_notmap_by_product(po.getId(),
							pair.getProductid_link());
					if (list_sku.size() == 0)
						is_map = true;
					ship.setIsmap(is_map);
					list_shipping.add(ship);
				}
			}

		}
		return list_shipping;
	}

	@Override
	public List<PContract_PO> getbycode_and_type_and_product(String po_no, int type, Long pcontractid_link,
			Long productid_link) {
		return repo.getbycode_and_type_and_product(pcontractid_link, po_no, type, productid_link);
	}

	@Override
	public List<PContract_PO> getpo_notin_list(List<String> list_po, int type, Long pcontractid_link) {
		return repo.getnotin_list_pono(pcontractid_link, list_po, type);
	}

	@Override
	public List<PContract_PO> getby_pcontract_and_type_andproduct(Long pcontractid_link, List<Integer> type,
			Long productid_link) {
		return repo.getby_pcontract_and_type_and_product(type, pcontractid_link, productid_link);
	}

	@Override
	public List<PContract_PO> getpo_byid(Long pcontractpoid_link) {
		return repo.getbyId(pcontractpoid_link);
	}

	@Override
	public List<PContract_PO> get_by_parent_and_type_and_MauSP(Long pcontractpo_parentid_link, int po_typeid_link,
			Long mausanphamid_link) {
		return repo.getby_parent_and_type_and_mausp(pcontractpo_parentid_link, mausanphamid_link, po_typeid_link);
	}
	
    @Override
    public List<PContract_PO> get_by_parent_and_type_and_MauSP_and_Shipdate(Long pcontractpo_parentid_link, int po_typeid_link,
            Long mausanphamid_link, Date shipdate_from, Date shipdate_to){
        return repo.getby_parent_and_type_and_mausp_and_shipdate(pcontractpo_parentid_link, mausanphamid_link, po_typeid_link, shipdate_from, shipdate_to);
    }
	
	@Override
	public Integer getSumPoQuantity_by_parent_and_type_and_mausp(Long pcontractpo_parentid_link, int po_typeid_link,
			Long mausanphamid_link) {
		return repo.getSumPoQuantity_by_parent_and_type_and_mausp(pcontractpo_parentid_link, mausanphamid_link, po_typeid_link);
	}

	@Override
	public List<PContract_PO> getPO_HavetoShip(Long orgrootid_link, Date shipdate_from, Date shipdate_to) {
		return repo.getPO_HavetoShip(orgrootid_link, shipdate_from, shipdate_to);
	}
	
	@Override
	public Integer getTotalProductinPcontract(Long pcontractid_link, Long productid_link) {
		return repo.getTotalProductinPcontract(pcontractid_link, productid_link);
	}

	@Override
	public Float getTotalPriceProductInPcontract(Long pcontractid_link, Long productid_link) {
		return null;
	}

	@Override
	public int getPOConfimNotLine(Long pcontractid_link) {
		// TODO Auto-generated method stub
		return repo.getPOConfimNotLine(pcontractid_link).size();
	}

	@Override
	public int getPOLineNotMaps(Long pcontractid_link) {
		return repo.getPOLineNotMaps(pcontractid_link).size();
	}

	@Override
	public List<PContract_PO> getPOConfirmNotMap(Long parentpoid_link, int type) {
		return repo.getby_parent_and_type_notmap(parentpoid_link, type);
	}

	@Override
	public List<PContract_PO> getByStockin(Long stockinid_link) {
		return repo.getByStockin(stockinid_link);
	}

	@Override
	public List<Long> getPoLineIdByStockin(Long stockinid_link) {
		return repo.getPoLineIdByStockin(stockinid_link);
	}

	@Override
	public List<PContract_PO> getBySearch_POLine_Stockin(String po_buyer, String productbuyercode, String pcontractcode,
			Date shipdateFrom, Date shipdateTo, List<Long> poLineIdList) {
		return repo.getBySearch_POLine_Stockin(po_buyer, productbuyercode, pcontractcode, shipdateFrom, shipdateTo, poLineIdList);
	}
	
	@Override
	public List<PContract_PO> getByStockout(Long stockoutid_link) {
		return repo.getByStockout(stockoutid_link);
	}

	@Override
	public List<Long> getPoLineIdByStockout(Long stockoutid_link) {
		return repo.getPoLineIdByStockout(stockoutid_link);
	}

	@Override
	public List<PContract_PO> getBySearch_POLine_Stockout(String po_buyer, String productbuyercode, String pcontractcode,
			Date shipdateFrom, Date shipdateTo, List<Long> poLineIdList) {
		return repo.getBySearch_POLine_Stockout(po_buyer, productbuyercode, pcontractcode, shipdateFrom, shipdateTo, poLineIdList);
	}

	@Override
	public List<PContract_PO> getPoLineByPcontract(Long pcontractid_link, Long productid_link) {
		return repo.getPoLineByPcontract(pcontractid_link, productid_link);
	}

	@Override
	public List<Long> getMaHangCanGiao(Long orgid_link, Date shipdateFrom, Date shipdateTo, String contractcode,
			String productbuyercode, String po_buyer, Long buyer, Long vendor) {
		return repo.getMaHangCanGiao(orgid_link, shipdateFrom, shipdateTo, contractcode, productbuyercode, po_buyer, buyer, vendor);
	}

	@Override
	public List<PContract_PO> getDsPoLineCanGiao(Long orgid_link, Date shipdateFrom, Date shipdateTo, String contractcode,
			String productbuyercode, String po_buyer, Long buyer, Long vendor) {
		return repo.getDsPoLineCanGiao(orgid_link, shipdateFrom, shipdateTo, contractcode, productbuyercode, po_buyer, buyer, vendor);
	}

	@Override
	public List<PContract_PO> getPoLineByPcontract_ProductInPair(Long pcontractid_link, Long productid_link) {
		return repo.getPoLineByPcontract_ProductInPair(pcontractid_link, productid_link);
	}

	@Override
	public List<PContract_PO> getPOLineChuaGiao(Long orgid_link, Date shipdateFrom, Date shipdateTo,
			String contractcode, Long productid_link, String po_buyer, Long buyer, Long vendor, Integer status) {
		return repo.getPOLineChuaGiao(orgid_link, shipdateFrom, shipdateTo, contractcode, productid_link, po_buyer, buyer, vendor, status);
	}

	@Override
	public List<PContract_PO> getPOLineChuaGiao_Cham(Long orgid_link, Date today, String contractcode,
			Long productid_link, String po_buyer, Long buyer, Long vendor, Integer status) {
		return repo.getPOLineChuaGiao_Cham(orgid_link, today, contractcode, productid_link, po_buyer, buyer, vendor, status);
	}

	@Override
	public List<PContract_PO> getbyPOrderGrant(Long pordergrantid_link) {
		return repo.getbyPOrderGrant(pordergrantid_link);
	}
}
