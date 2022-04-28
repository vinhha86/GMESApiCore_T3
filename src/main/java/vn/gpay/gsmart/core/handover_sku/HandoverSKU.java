package vn.gpay.gsmart.core.handover_sku;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import vn.gpay.gsmart.core.sku.SKU;

@Table(name="handover_sku")
@Entity
public class HandoverSKU implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "handover_sku_generator")
	@SequenceGenerator(name="handover_sku_generator", sequenceName = "handover_sku_id_seq", allocationSize=1)
	protected Long id;
	
	@Column(name ="orgrootid_link")
	private Long orgrootid_link;
	
	@Column(name ="handoverid_link")
	private Long handoverid_link;
	
	@Column(name ="handoverproductid_link")
	private Long handoverproductid_link;
	
	@Column(name ="productid_link")
	private Long productid_link;
	
	@Column(name ="skuid_link")
	private Long skuid_link;
	
	@Column(name ="totalpackage")
	private Integer totalpackage;
	
	@Column(name ="totalpackagecheck")
	private Integer totalpackagecheck;
	
	@Column(name ="status")
	private Integer status;
	
	@Column(name ="usercreateid_link")
	private Long usercreateid_link;
	
	@Column(name ="lastuserupdateid_link")
	private Long lastuserupdateid_link;
	
	@Column(name ="timecreate")
	private Date timecreate;
	
	@Column(name ="lasttimeupdate")
	private Date lasttimeupdate;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
    @JoinColumn(name="skuid_link",insertable=false,updatable =false)
	private SKU sku;
	
	@Transient
	public String getSkuCode() {
		if(sku!=null) {
			if(sku.getCode() != null)
				return sku.getCode();
		}
		return "";
	}
	
	@Transient
	public String getSkuColor() {
		if(sku!=null) {
			if(sku.getColor_name() != null)
				return sku.getColor_name();
		}
		return "";
	}
	
	@Transient
	public String getSkuSize() {
		if(sku!=null) {
			if(sku.getSize_name() != null)
				return sku.getSize_name();
		}
		return "";
	}
	
	@Transient
	public Integer getSkuSizeSortVal() {
		if(sku!=null) {
				return sku.getSort_size();
		}
		return 0;
	}
	
	@Transient
	public String getBarcode() {
		if(sku!=null) {
			if(sku.getBarcode() != null)
				return sku.getBarcode();
		}
		return "";
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

	public Long getHandoverid_link() {
		return handoverid_link;
	}

	public void setHandoverid_link(Long handoverid_link) {
		this.handoverid_link = handoverid_link;
	}

	public Long getHandoverproductid_link() {
		return handoverproductid_link;
	}

	public void setHandoverproductid_link(Long handoverproductid_link) {
		this.handoverproductid_link = handoverproductid_link;
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

	public Integer getTotalpackage() {
		return totalpackage;
	}

	public void setTotalpackage(Integer totalpackage) {
		this.totalpackage = totalpackage;
	}

	public Integer getTotalpackagecheck() {
		return totalpackagecheck;
	}

	public void setTotalpackagecheck(Integer totalpackagecheck) {
		this.totalpackagecheck = totalpackagecheck;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getUsercreateid_link() {
		return usercreateid_link;
	}

	public void setUsercreateid_link(Long usercreateid_link) {
		this.usercreateid_link = usercreateid_link;
	}

	public Long getLastuserupdateid_link() {
		return lastuserupdateid_link;
	}

	public void setLastuserupdateid_link(Long lastuserupdateid_link) {
		this.lastuserupdateid_link = lastuserupdateid_link;
	}

	public Date getTimecreate() {
		return timecreate;
	}

	public void setTimecreate(Date timecreate) {
		this.timecreate = timecreate;
	}

	public Date getLasttimeupdate() {
		return lasttimeupdate;
	}

	public void setLasttimeupdate(Date lasttimeupdate) {
		this.lasttimeupdate = lasttimeupdate;
	}
	
	@Transient
	private String skuCodeString;
	
	@Transient
	private String skuColorString;
	
	@Transient
	private String skuSizeString;
	
	@Transient
	private Integer skuSizeSortValInt;

	public String getSkuCodeString() {
		return skuCodeString;
	}

	public void setSkuCodeString(String skuCodeString) {
		this.skuCodeString = skuCodeString;
	}

	public String getSkuColorString() {
		return skuColorString;
	}

	public void setSkuColorString(String skuColorString) {
		this.skuColorString = skuColorString;
	}

	public String getSkuSizeString() {
		return skuSizeString;
	}

	public void setSkuSizeString(String skuSizeString) {
		this.skuSizeString = skuSizeString;
	}

	public Integer getSkuSizeSortValInt() {
		return skuSizeSortValInt;
	}

	public void setSkuSizeSortValInt(Integer skuSizeSortValInt) {
		this.skuSizeSortValInt = skuSizeSortValInt;
	}
	
	
}
