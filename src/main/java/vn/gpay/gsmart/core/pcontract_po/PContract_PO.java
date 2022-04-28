package vn.gpay.gsmart.core.pcontract_po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import vn.gpay.gsmart.core.category.Port;
import vn.gpay.gsmart.core.category.ShipMode;
import vn.gpay.gsmart.core.currency.Currency;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.pcontract.PContract;
import vn.gpay.gsmart.core.pcontract_po_productivity.PContract_PO_Productivity;
import vn.gpay.gsmart.core.pcontract_price.PContract_Price;
import vn.gpay.gsmart.core.pcontractproductsku.PContractProductSKU;
import vn.gpay.gsmart.core.porder_req.POrder_Req;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.utils.OrgType;
import vn.gpay.gsmart.core.utils.POType;

@Table(name = "pcontract_po")
@Entity
public class PContract_PO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcontract_po_generator")
	@SequenceGenerator(name = "pcontract_po_generator", sequenceName = "pcontract_po_id_seq", allocationSize = 1)
	private Long id;
	private Long orgrootid_link;
	private Long pcontractid_link;
	private String code;
	private String po_buyer;
	private String po_vendor;
	private Long productid_link;
	private Integer po_quantity;
	private Long unitid_link;
	private Date shipdate;
	private Date matdate;
	private Float actual_quantity;
	private Date actual_shipdate;
	private Float price_cmp;
	private Float price_fob;
	private Float price_sweingtarget;
	private Float price_sweingfact;
	private Float price_add;
	private Float price_commission;
	private Float salaryfund;
	private Long currencyid_link;
	private Float exchangerate;
	private Date productiondate;
	private String packingnotice;
	private Long qcorgid_link;
	private String qcorgname;
	private Integer etm_from;
	private Integer etm_to;
	private Integer etm_avr;
	private Long usercreatedid_link;
	private Date datecreated;
	private Integer status;
	private Integer productiondays;
	private Integer productiondays_ns;
	private Long orgmerchandiseid_link;
	private Long merchandiserid_link;
	private Long parentpoid_link;
	private Long orggrantid_link;
	private Boolean is_tbd;
	private Float sewtarget_percent;
	private Long portfromid_link;
	private Long porttoid_link;
	private Boolean isauto_calculate;
	private Long shipmodeid_link;
	private Date date_importdata;
	private Integer plan_productivity;
	private Float plan_linerequired;
	private Integer po_typeid_link;
	private String comment;
	private Boolean ismap;
	private String dc;
	private String code_extra;

	@Transient
	private String phuongThucDongGoi;
	@Transient
	private Integer po_quantity_total; // sum po_quantity po con thuc te
	@Transient
	private Integer po_quantity_difference; // hieu so po_quantity_total - po_quantity
	@Transient
	private int po_quantity_sp;
	@Transient
	private int totalpair;
	@Transient
	private String fob_worklist;//Danh sach dau muc fob (nha may tu chi tra)

	public Integer getPo_typeid_link() {
		return po_typeid_link;
	}

	public void setPo_typeid_link(Integer po_typeid_link) {
		this.po_typeid_link = po_typeid_link;
	}

	@Transient
	private String productionlines;

//	@NotFound(action = NotFoundAction.IGNORE)
//	@OneToMany
//	@JoinColumn(name = "pcontract_poid_link", insertable = false, updatable = false)
//	private List<PContractProductSKU> pcontractProductSKUs = new ArrayList<>();

//	@NotFound(action = NotFoundAction.IGNORE)
//	@OneToMany
//    @JoinColumn(name="pcontract_poid_link",insertable=false,updatable =false)
//	private List<POrder_POLine> list_porder_poline = new ArrayList<>();
//	
//	@Transient
//	public String getOrdercode() {
//		if(list_porder_poline.size()>0) {
//			return list_porder_poline.get(0).getOrderCode();
//		}
//		return "";
//	}

	@Transient
	public Integer getPcontractPoProductSkuQuantityTotal() { // sl sp sku trong po line
		Integer sum = 0;
		if (pcontract_po_sku != null && pcontract_po_sku.size() > 0) {
			for (PContractProductSKU PContractProductSKU : pcontract_po_sku) {
				if (PContractProductSKU.getPquantity_total() != null && PContractProductSKU.getPquantity_total() != 0) {
					sum += PContractProductSKU.getPquantity_total();
				}
			}
		}
		return sum;
	}

	@Transient
	public Long getId_MinPOShipdate() {
		if (sub_po.size() > 0) {

			List<PContract_PO> list_line = sub_po.stream()
					.filter(item -> null != item.po_typeid_link && item.po_typeid_link == POType.PO_LINE_PLAN)
					.collect(Collectors.toList());
			Date min = null;
			int i = -1;
			for (int a = 0; a < list_line.size(); a++) {
				if (a == 0) {
					min = list_line.get(a).getShipdate();
					i = a;
				} else {
					if (min.after(list_line.get(a).getShipdate())) {
						min = list_line.get(a).getShipdate();
						i = a;
					}
				}
			}
			if (i > -1)
				return list_line.get(i).getId();
		}
		return null;
	}

	public Integer getPlan_productivity() {
		return plan_productivity;
	}

	public void setPlan_productivity(Integer plan_productivity) {
		this.plan_productivity = plan_productivity;
	}

	public Float getPlan_linerequired() {
		return plan_linerequired;
	}

	public void setPlan_linerequired(Float plan_linerequired) {
		this.plan_linerequired = plan_linerequired;
	}

	public Long getShipmodeid_link() {
		return shipmodeid_link;
	}

	public void setShipmodeid_link(Long shipmodeid_link) {
		this.shipmodeid_link = shipmodeid_link;
	}

	public Integer getProductiondays() {
		return productiondays;
	}

	public void setProductiondays(Integer productiondays) {
		this.productiondays = productiondays;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany
	@JoinColumn(name = "pcontract_poid_link", insertable = false, updatable = false)
	private List<POrder_Req> porder_req = new ArrayList<POrder_Req>();

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "usercreatedid_link", insertable = false, updatable = false)
	private GpayUser usercreated;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "productid_link", insertable = false, updatable = false)
	private Product product;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "parentpoid_link", insertable = false, updatable = false)
	private PContract_PO parent;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany
	@JoinColumn(name = "pcontract_poid_link", insertable = false, updatable = false)
	private List<PContract_Price> pcontract_price = new ArrayList<PContract_Price>();

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany
	@JoinColumn(name = "pcontract_poid_link", insertable = false, updatable = false)
	private List<PContract_PO_Productivity> pcontract_po_productivity = new ArrayList<PContract_PO_Productivity>();

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany
	@JoinColumn(name = "pcontract_poid_link", insertable = false, updatable = false)
	private List<PContractProductSKU> pcontract_po_sku = new ArrayList<PContractProductSKU>();

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany
	@JoinColumn(name = "parentpoid_link", insertable = false, updatable = false)
	private List<PContract_PO> sub_po = new ArrayList<PContract_PO>();

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "merchandiserid_link", insertable = false, updatable = false)
	private GpayUser merchandiser;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "currencyid_link", insertable = false, updatable = false)
	private Currency currency;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "orgmerchandiseid_link", insertable = false, updatable = false)
	private Org org_factory;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "shipmodeid_link", insertable = false, updatable = false)
	private ShipMode shipmode;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "porttoid_link", insertable = false, updatable = false)
	private Port port_to;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "portfromid_link", insertable = false, updatable = false)
	private Port port_from;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "pcontractid_link", insertable = false, updatable = false)
	private PContract pcontract;
	
	@Transient
	public String getContractcode() {
		if (pcontract != null)
			return pcontract.getContractcode();
		return "";
	}

	@Transient
	public long getProduct_poparent() {
		if (parent != null)
			return parent.getProductid_link();
		return 0;
	}
	
	@Transient
	public Long getOrg_inchargeid_link() {
		if (parent != null)
			return parent.getOrgmerchandiseid_link();
		return null;
	}

	@Transient
	public String getPortTo() {
		if (port_to != null)
			return port_to.getCode();
		return "";
	}

	@Transient
	public String getPortFrom() {
		if (port_from != null)
			return port_from.getCode();
		return "";
	}

	@Transient
	public String getShipMode() {
		if (shipmode != null)
			return shipmode.getName();
		return "";
	}

	@Transient
	public Boolean getCheckamount() {
		int amount_sku = 0;
		for (PContractProductSKU sku : pcontract_po_sku) {
			amount_sku += sku.getPquantity_porder() == null ? 0 : sku.getPquantity_porder();
		}
		int quantity = po_quantity == null ? 0 : po_quantity;
		if (quantity == amount_sku)
			return true;
		return false;
	}

	@Transient
	public Float getTotalprice() {
		Float price = (float) 0;
		if (pcontract_price != null) {
			for (PContract_Price thePrice : pcontract_price) {
				if (thePrice.getProductid_link().compareTo(productid_link) == 0)
					if (thePrice.getSizesetid_link() != null && thePrice.getSizesetid_link() == 1) {
						if (thePrice.getTotalprice() != null)
							price = thePrice.getTotalprice();
					}
			}
		}
		return price;
	}

	@Transient
	public Integer getProductivity_byproduct(Long productid_link) {
		if (parent != null) {
			for (PContract_PO_Productivity productivity : parent.getPcontract_po_productivity()) {
				if (productivity.getProductid_link().equals(productid_link)) {
					return productivity.getPlan_productivity();
				}
			}
		} else if (pcontract_po_productivity != null) {
			for (PContract_PO_Productivity pContract_PO_Productivity2 : pcontract_po_productivity) {
				if (pContract_PO_Productivity2.getProductid_link() != null) {
					if (pContract_PO_Productivity2.getProductid_link().equals(productid_link)) {
						return pContract_PO_Productivity2.getPlan_productivity() == null ? 0
								: pContract_PO_Productivity2.getPlan_productivity();
					}
				}
			}
		}
		return 0;
	}

	@Transient
	public String getFactory_name() {
		if (org_factory != null)
			return org_factory.getName();
		return "";
	}

	@Transient
	public String getMerchandiser_name() {
		if (merchandiser != null) {
			return merchandiser.getFullname();
		}
		return "";
	}

	@Transient
	public String getUsercreatedName() {
		if (usercreated != null) {
			return usercreated.getFullName();
		}
		return "";
	}

	@Transient
	public String getCurrencyCode() {
		if (currency != null)
			return currency.getCode();
		return "$";
	}

	@Transient
	public String getCurrencyName() {
		if (currency != null)
			return currency.getName();
		return "US Dollar";
	}

	@Transient
	public String getFactories() {
		String name = "";
		for (POrder_Req req : porder_req) {
			if (name.contains(req.getGranttoorgcode()))
				continue;
			if (name == "")
				name += req.getGranttoorgcode();
			else
				name += ", " + req.getGranttoorgcode();
		}
		return name;
	}
	
	@Transient
	public List<Long> getFactories_Id() {
		List<Long> orgIds = new ArrayList<Long>();
		for (POrder_Req req : porder_req) {
			if(req.getGranttoorgid_link() != null) {
				if (orgIds.contains(req.getGranttoorgid_link()))
					continue;
				if(req.getGranttoorgtype().equals(OrgType.ORG_TYPE_XUONGSX))
					orgIds.add(req.getGranttoorgid_link());
			}
		}
		return orgIds;
	}

	public List<PContract_Price> getPcontract_price() {
		return pcontract_price;
	}

	public void setPcontract_price(List<PContract_Price> pcontract_price) {
		this.pcontract_price = pcontract_price;
	}
//	public List<PContract_PO> getSub_po() {
////		sub_po.removeIf(c->c.getPo_typeid_link() != 10);
//		return sub_po;
//	}

//	public List<PContract_PO> getSub_po_plan() {
//		List<PContract_PO> list_line = sub_po.stream().filter(item -> null!=item.po_typeid_link && item.po_typeid_link==POType.PO_LINE_PLAN).collect(Collectors.toList());
//		Comparator<PContract_PO> compareBySortValue = (PContract_PO a1, PContract_PO a2) -> a1.getShipdate().compareTo( a2.getShipdate());
//		Collections.sort(list_line, compareBySortValue);
//		return list_line;
//	}
//	public List<PContract_PO> getSub_po_confirm() {
//		List<PContract_PO> list_line = sub_po.stream().filter(item -> null!=item.po_typeid_link && item.po_typeid_link==POType.PO_LINE_CONFIRMED).collect(Collectors.toList());
//		Comparator<PContract_PO> compareBySortValue = (PContract_PO a1, PContract_PO a2) -> a1.getShipdate().compareTo( a2.getShipdate());
//		Collections.sort(list_line, compareBySortValue);
//		return list_line;
//	}

	public void setSub_po(List<PContract_PO> sub_po) {
		this.sub_po = sub_po;
	}

	@Transient
	public int getProduct_typeid_link() {
		if (product != null) {
			return product.getProducttypeid_link();
		}
		return 0;
	}

	@Transient
	public int getAmount_org() {
		int sum_product = 0;
		for (PContract_PO po : sub_po) {
			if (po.getPo_typeid_link() == POType.PO_LINE_PLAN) {
				sum_product += po.getPo_quantity();
			}
		}

		return sum_product;
	}

	@Transient
	public String getProductbuyercode() {
		if (product != null) {
			return product.getBuyercode();
		}
		return "";
	}

	@Transient
	public List<PContract_PO_Productivity> getpo_productivity_parent() {
		if (parent != null)
			return parent.getPcontract_po_productivity();
		return new ArrayList<PContract_PO_Productivity>();
	}

	@Transient
	public String getBuyerName() {
		if (pcontract != null) {
			return pcontract.getBuyername();
		}
		return "";
	}

	@Transient
	public String getVendorName() {
		if (pcontract != null) {
			return pcontract.getVendorname();
		}
		return "";
	}

	@Transient
	public String getProductvendorcode() {
		if (product != null) {
			return product.getVendorcode();
		}
		return "";
	}

	@Transient
	public String getProduct_code() {
		if (product != null) {
			return product.getBuyercode();
		}
		return "";
	}

	@Transient
	public Integer getparent_quantity() {
		if (parent != null) {
			return parent.getPo_quantity();
		}
		return 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgrootid_link() {
		return orgrootid_link;
	}

	public void setOrgrootid_link(Long orgrootid_link) {
		this.orgrootid_link = orgrootid_link;
	}

	public Long getPcontractid_link() {
		return pcontractid_link;
	}

	public void setPcontractid_link(Long pcontractid_link) {
		this.pcontractid_link = pcontractid_link;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPo_buyer() {
		return po_buyer;
	}

	public void setPo_buyer(String po_buyer) {
		this.po_buyer = po_buyer;
	}

	public String getPo_vendor() {
		return po_vendor;
	}

	public void setPo_vendor(String po_vendor) {
		this.po_vendor = po_vendor;
	}

	public Long getProductid_link() {
		return productid_link;
	}

	public void setProductid_link(Long productid_link) {
		this.productid_link = productid_link;
	}

	public Integer getPo_quantity() {
		return po_quantity;
	}

	public void setPo_quantity(Integer po_quantity) {
		this.po_quantity = po_quantity;
	}

	public Long getUnitid_link() {
		return unitid_link;
	}

	public void setUnitid_link(Long unitid_link) {
		this.unitid_link = unitid_link;
	}

	public Date getShipdate() {
		return shipdate;
	}

	public void setShipdate(Date shipdate) {
		this.shipdate = shipdate;
	}

	public Date getMatdate() {
		return matdate;
	}

	public void setMatdate(Date matdate) {
		this.matdate = matdate;
	}

	public Float getActual_quantity() {
		return actual_quantity;
	}

	public void setActual_quantity(Float actual_quantity) {
		this.actual_quantity = actual_quantity;
	}

	public Date getActual_shipdate() {
		return actual_shipdate;
	}

	public void setActual_shipdate(Date actual_shipdate) {
		this.actual_shipdate = actual_shipdate;
	}

	public Float getPrice_cmp() {
		return price_cmp;
	}

	public void setPrice_cmp(Float price_cmp) {
		this.price_cmp = price_cmp;
	}

	public Float getPrice_fob() {
		return price_fob;
	}

	public void setPrice_fob(Float price_fob) {
		this.price_fob = price_fob;
	}

	public Float getPrice_sweingtarget() {
		return price_sweingtarget;
	}

	public void setPrice_sweingtarget(Float price_sweingtarget) {
		this.price_sweingtarget = price_sweingtarget;
	}

	public Float getPrice_sweingfact() {
		return price_sweingfact;
	}

	public void setPrice_sweingfact(Float price_sweingfact) {
		this.price_sweingfact = price_sweingfact;
	}

	public Float getPrice_add() {
		return price_add;
	}

	public void setPrice_add(Float price_add) {
		this.price_add = price_add;
	}

	public Float getPrice_commission() {
		return price_commission;
	}

	public void setPrice_commission(Float price_commission) {
		this.price_commission = price_commission;
	}

	public Float getSalaryfund() {
		return salaryfund;
	}

	public void setSalaryfund(Float salaryfund) {
		this.salaryfund = salaryfund;
	}

	public Long getCurrencyid_link() {
		return currencyid_link;
	}

	public void setCurrencyid_link(Long currencyid_link) {
		this.currencyid_link = currencyid_link;
	}

	public Float getExchangerate() {
		return exchangerate;
	}

	public void setExchangerate(Float exchangerate) {
		this.exchangerate = exchangerate;
	}

	public Date getProductiondate() {
		return productiondate;
	}

	public void setProductiondate(Date productiondate) {
		this.productiondate = productiondate;
	}

	public String getPackingnotice() {
		return packingnotice;
	}

	public void setPackingnotice(String packingnotice) {
		this.packingnotice = packingnotice;
	}

	public Long getQcorgid_link() {
		return qcorgid_link;
	}

	public void setQcorgid_link(Long qcorgid_link) {
		this.qcorgid_link = qcorgid_link;
	}

	public Integer getEtm_from() {
		return etm_from;
	}

	public void setEtm_from(Integer etm_from) {
		this.etm_from = etm_from;
	}

	public Integer getEtm_to() {
		return etm_to;
	}

	public void setEtm_to(Integer etm_to) {
		this.etm_to = etm_to;
	}

	public Integer getEtm_avr() {
		return etm_avr;
	}

	public void setEtm_avr(Integer etm_avr) {
		this.etm_avr = etm_avr;
	}

	public Long getUsercreatedid_link() {
		return usercreatedid_link;
	}

	public void setUsercreatedid_link(Long usercreatedid_link) {
		this.usercreatedid_link = usercreatedid_link;
	}

	public Date getDatecreated() {
		return datecreated;
	}

	public void setDatecreated(Date datecreated) {
		this.datecreated = datecreated;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getQcorgname() {
		return qcorgname;
	}

	public void setQcorgname(String qcorgname) {
		this.qcorgname = qcorgname;
	}

	public Long getOrgmerchandiseid_link() {
		return orgmerchandiseid_link;
	}

	public Long getMerchandiserid_link() {
		return merchandiserid_link;
	}

	public void setOrgmerchandiseid_link(Long orgmerchandiseid_link) {
		this.orgmerchandiseid_link = orgmerchandiseid_link;
	}

	public void setMerchandiserid_link(Long merchandiserid_link) {
		this.merchandiserid_link = merchandiserid_link;
	}

	public Long getParentpoid_link() {
		return parentpoid_link;
	}

	public void setParentpoid_link(Long parentpoid_link) {
		this.parentpoid_link = parentpoid_link;
	}

	public Boolean getIs_tbd() {
		return is_tbd;
	}

	public void setIs_tbd(Boolean is_tbd) {
		this.is_tbd = is_tbd;
	}

	public Float getSewtarget_percent() {
		return sewtarget_percent;
	}

	public void setSewtarget_percent(Float sewtarget_percent) {
		this.sewtarget_percent = sewtarget_percent;
	}

	public Long getPortfromid_link() {
		return portfromid_link;
	}

	public void setPortfromid_link(Long portfromid_link) {
		this.portfromid_link = portfromid_link;
	}

	public Long getPorttoid_link() {
		return porttoid_link;
	}

	public void setPorttoid_link(Long porttoid_link) {
		this.porttoid_link = porttoid_link;
	}

	public Boolean getIsauto_calculate() {
		return isauto_calculate;
	}

	public void setIsauto_calculate(Boolean isauto_calculate) {
		this.isauto_calculate = isauto_calculate;
	}

	public Date getDate_importdata() {
		return date_importdata;
	}

	public void setDate_importdata(Date date_importdata) {
		this.date_importdata = date_importdata;
	}

	public List<PContract_PO_Productivity> getPcontract_po_productivity() {
		return pcontract_po_productivity;
	}

	public void setPcontract_po_productivity(List<PContract_PO_Productivity> pcontract_po_productivity) {
		this.pcontract_po_productivity = pcontract_po_productivity;
	}

	public Integer getProductiondays_ns() {
		return productiondays_ns;
	}

	public void setProductiondays_ns(Integer productiondays_ns) {
		this.productiondays_ns = productiondays_ns;
	}

	public String getProductionlines() {
		return productionlines;
	}

	public void setProductionlines(String productionlines) {
		this.productionlines = productionlines;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsmap() {
		return ismap == null ? false : ismap;
	}

	public void setIsmap(Boolean ismap) {
		this.ismap = ismap;
	}

	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public int getPo_quantity_sp() {
		return po_quantity_sp;
	}

	public void setPo_quantity_sp(int po_quantity_sp) {
		this.po_quantity_sp = po_quantity_sp;
	}

	public int getTotalpair() {
		return totalpair;
	}

	public void setTotalpair(int totalpair) {
		this.totalpair = totalpair;
	}

	public List<PContractProductSKU> getPcontract_po_sku() {
		return pcontract_po_sku;
	}

	public String getFob_worklist() {
		return fob_worklist;
	}

	public void setFob_worklist(String fob_worklist) {
		this.fob_worklist = fob_worklist;
	}

	public Integer getPo_quantity_total() {
		return po_quantity_total;
	}

	public void setPo_quantity_total(Integer po_quantity_total) {
		this.po_quantity_total = po_quantity_total;
	}

	public Integer getPo_quantity_difference() {
		return po_quantity_difference;
	}

	public void setPo_quantity_difference(Integer po_quantity_difference) {
		this.po_quantity_difference = po_quantity_difference;
	}

	public String getCode_extra() {
		return code_extra;
	}

	public void setCode_extra(String code_extra) {
		this.code_extra = code_extra;
	}

	public String getPhuongThucDongGoi() {
		return phuongThucDongGoi;
	}

	public void setPhuongThucDongGoi(String phuongThucDongGoi) {
		this.phuongThucDongGoi = phuongThucDongGoi;
	}

	public Long getOrggrantid_link() {
		return orggrantid_link;
	}

	public void setOrggrantid_link(Long orggrantid_link) {
		this.orggrantid_link = orggrantid_link;
	}
	
	
}
