package vn.gpay.gsmart.core.pcontractproductsku;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "pcontract_product_skus")
@Entity
public class PContractProductSKU_NoLink implements Serializable {
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

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getMausanpham() {
		return mausanpham;
	}

	public void setMausanpham(String mausanpham) {
		this.mausanpham = mausanpham;
	}

	public String getCosanpham() {
		return cosanpham;
	}

	public void setCosanpham(String cosanpham) {
		this.cosanpham = cosanpham;
	}

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

	
}
