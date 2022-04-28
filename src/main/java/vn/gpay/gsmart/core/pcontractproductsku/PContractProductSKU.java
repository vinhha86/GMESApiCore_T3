package vn.gpay.gsmart.core.pcontractproductsku;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.sku.SKU;

@Table(name = "pcontract_product_skus")
@Entity
public class PContractProductSKU implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcontract_product_skus_generator")
	@SequenceGenerator(name = "pcontract_product_skus_generator", sequenceName = "pcontract_product_skus_id_seq", allocationSize = 1)
	private Long id;
	private Long orgrootid_link;
	private Long pcontractid_link;
	private Long productid_link;
	private Long skuid_link;
	private Integer pquantity_sample;// SL mau
	private Integer pquantity_porder;// SL don
	private Integer pquantity_total;// SL tong sx

	private Integer pquantity_granted;// SL da phan chuyen

	private Integer pquantity_production;// SL yeu cau sx
	private Long pcontract_poid_link;
	private Boolean ismap;

	@Transient
	private Integer pquantity_lenhsx = 0;// SL da tao lenh sx
	@Transient
	private Integer pquantity_onhand_begin = 0;// SL ton dau ky (cot 5 mau 15a)
	@Transient
	private Integer pquantity_stockin = 0;// SL da nhap kho thanh pham trong ky (cot 6 mau 15a)
	@Transient
	private Integer pquantity_changetarget = 0;// SL thay doi muc dich su dung (cot 7 mau 15a)
	@Transient
	private Integer pquantity_stockout = 0;// SL da xuat kho cho khach (cot 8 mau 15a)
	@Transient
	private Integer pquantity_stockout_other = 0;// SL da xuat kho muc dich khac (cot 9 mau 15a)
	@Transient
	private Integer pquantity_onhand_end = 0;// SL ton cuoi ky (cot 10 mau 15a)
	
	@Transient
	private String product_code = "";
	@Transient
	private String product_name = "";
	@Transient
	private String sku_code = "";
	@Transient
	private String unit_name = "";
	@Transient
	private String mausanpham = "";
	@Transient
	private String cosanpham = "";
	
	// truong thong tin cho dashboard mer
	@Transient
	private Integer pquantity_cut = 0;
	@Transient
	private Integer pquantity_vaoChuyen = 0;
	@Transient
	private Integer pquantity_raChuyen = 0;
	@Transient
	private Integer pquantity_hoanThien = 0;
	@Transient
	private Integer pquantity_tonKhoTp = 0;
	@Transient
	private Integer pquantity_daXuat = 0;


	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "skuid_link", insertable = false, updatable = false)
	private SKU sku;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productid_link", insertable = false, updatable = false)
	private Product product;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pcontract_poid_link", insertable = false, updatable = false)
	private PContract_PO po;

	@Transient
	public String getPo_buyer() {
		if (po != null)
			return po.getPo_buyer();
		return "";
	}

	@Transient
	public Integer getPquantity() {
		return pquantity_total;
	}

	@Transient
	public String getProductcode() {
		if (product != null)
			return product.getBuyercode();
		else
			return product_code;
	}
	
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	@Transient
	public String getProductname() {
		if (product != null)
			return product.getBuyername();
		else
			return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	
	@Transient
	public Long getUnitid_link() {
		if (product != null)
			return product.getUnitid_link();
		return null;
	}

	@Transient
	public String getUnitname() {
		if (product != null)
			return product.getUnitName();
		else
			return unit_name;
	}
	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	@Transient
	public String getSkuName() {
		if (sku != null) {
			return sku.getName();
		}
		return "";
	}

	@Transient
	public int getSort_value() {
		if (sku != null) {
			return sku.getSort_size();
		}
		return 0;
	}

	@Transient
	public String getSkuCode() {
		if (sku != null) {
			return sku.getCode();
		}
		return "";
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	@Transient
	public String getSkuBarCode() {
		if (sku != null) {
			return sku.getBarcode();
		}
		return "";
	}

	@Transient
	public String getMauSanPham() {
		if (sku != null)
			return sku.getMauSanPham();
		else
			return mausanpham;
	}
	public void setMausanpham(String mausanpham) {
		this.mausanpham = mausanpham;
	}

	@Transient
	public String getCoSanPham() {
		if (sku != null)
			return sku.getCoSanPham();
		else
			return cosanpham;
	}
	public void setCosanpham(String cosanpham) {
		this.cosanpham = cosanpham;
	}

	@Transient
	public Long getSizeid_link() {
		if (sku != null) {
			return sku.getSize_id();
		}
		return (long) 0;
	}

	@Transient
	public Long getColor_id() {
		if (sku != null) {
			return sku.getColor_id();
		}
		return (long) 0;
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

	public Long getProductid_link() {
		return productid_link;
	}

	public void setProductid_link(Long productid_link) {
		this.productid_link = productid_link;
	}

	public Long getSkuid_link() {
		return skuid_link;
	}

	public void setSkuid_link(Long skuid_link) {
		this.skuid_link = skuid_link;
	}

	public Integer getPquantity_sample() {
		return pquantity_sample;
	}

	public void setPquantity_sample(Integer pquantity_sample) {
		this.pquantity_sample = pquantity_sample;
	}

	public Integer getPquantity_porder() {
		return pquantity_porder;
	}

	public void setPquantity_porder(Integer pquantity_porder) {
		this.pquantity_porder = pquantity_porder;
	}

	public Integer getPquantity_total() {
		return pquantity_total;
	}

	public void setPquantity_total(Integer pquantity_total) {
		this.pquantity_total = pquantity_total;
	}

//	public SKU getSku() {
//		return sku;
//	}

	public Integer getPquantity_granted() {
		return pquantity_granted;
	}

	public void setPquantity_granted(Integer pquantity_granted) {
		this.pquantity_granted = pquantity_granted;
	}

	public Long getPcontract_poid_link() {
		return pcontract_poid_link;
	}

	public void setPcontract_poid_link(Long pcontract_poid_link) {
		this.pcontract_poid_link = pcontract_poid_link;
	}

	public Integer getPquantity_production() {
		return pquantity_production;
	}

	public void setPquantity_production(Integer pquantity_production) {
		this.pquantity_production = pquantity_production;
	}

	public Integer getPquantity_lenhsx() {
		return pquantity_lenhsx;
	}

	public void setPquantity_lenhsx(Integer pquantity_lenhsx) {
		this.pquantity_lenhsx = pquantity_lenhsx;
	}

	public Integer getPquantity_stockout() {
		return pquantity_stockout;
	}

	public void setPquantity_stockout(Integer pquantity_stockout) {
		this.pquantity_stockout = pquantity_stockout;
	}

	public Integer getPquantity_stockin() {
		return pquantity_stockin;
	}

	public void setPquantity_stockin(Integer pquantity_stockin) {
		this.pquantity_stockin = pquantity_stockin;
	}

	public Boolean getIsmap() {
		return ismap;
	}

	public void setIsmap(Boolean ismap) {
		this.ismap = ismap;
	}

	public Integer getPquantity_onhand_begin() {
		return pquantity_onhand_begin;
	}

	public void setPquantity_onhand_begin(Integer pquantity_onhand_begin) {
		this.pquantity_onhand_begin = pquantity_onhand_begin;
	}

	public Integer getPquantity_changetarget() {
		return pquantity_changetarget;
	}

	public void setPquantity_changetarget(Integer pquantity_changetarget) {
		this.pquantity_changetarget = pquantity_changetarget;
	}

	public Integer getPquantity_stockout_other() {
		return pquantity_stockout_other;
	}

	public void setPquantity_stockout_other(Integer pquantity_stockout_other) {
		this.pquantity_stockout_other = pquantity_stockout_other;
	}

	public Integer getPquantity_onhand_end() {
		return pquantity_onhand_end;
	}

	public void setPquantity_onhand_end(Integer pquantity_onhand_end) {
		this.pquantity_onhand_end = pquantity_onhand_end;
	}

	public Integer getPquantity_cut() {
		return pquantity_cut;
	}

	public void setPquantity_cut(Integer pquantity_cut) {
		this.pquantity_cut = pquantity_cut;
	}

	public Integer getPquantity_vaoChuyen() {
		return pquantity_vaoChuyen;
	}

	public void setPquantity_vaoChuyen(Integer pquantity_vaoChuyen) {
		this.pquantity_vaoChuyen = pquantity_vaoChuyen;
	}

	public Integer getPquantity_raChuyen() {
		return pquantity_raChuyen;
	}

	public void setPquantity_raChuyen(Integer pquantity_raChuyen) {
		this.pquantity_raChuyen = pquantity_raChuyen;
	}

	public Integer getPquantity_hoanThien() {
		return pquantity_hoanThien;
	}

	public void setPquantity_hoanThien(Integer pquantity_hoanThien) {
		this.pquantity_hoanThien = pquantity_hoanThien;
	}

	public Integer getPquantity_tonKhoTp() {
		return pquantity_tonKhoTp;
	}

	public void setPquantity_tonKhoTp(Integer pquantity_tonKhoTp) {
		this.pquantity_tonKhoTp = pquantity_tonKhoTp;
	}

	public Integer getPquantity_daXuat() {
		return pquantity_daXuat;
	}

	public void setPquantity_daXuat(Integer pquantity_daXuat) {
		this.pquantity_daXuat = pquantity_daXuat;
	}

}
