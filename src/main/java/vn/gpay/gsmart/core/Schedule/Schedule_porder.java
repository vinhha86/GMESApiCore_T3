package vn.gpay.gsmart.core.Schedule;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Schedule_porder {
	@JsonProperty("ResourceId")
	private long ResourceId;

	@JsonProperty("Name")
	private String Name;

	@JsonProperty("StartDate")
	private Date StartDate;

	@JsonProperty("EndDate")
	private Date EndDate;

	private String mahang;

	private String vendorname;
	private String buyername;
	private String pordercode;
	private Integer totalpackage;
	private Long parentid_origin;
	public long porder_grantid_link;
	private Integer status;
	private long productid_link;
	private long pcontract_poid_link;
	private long pcontractid_link;
	private long porderid_link;
	private String productbuyercode;
	private int grant_type; // 0 : kip giao hang, 1 qua ngay giao hang
	private byte[] img;
	@JsonProperty("Cls")
	private String Cls;

	private long id_origin;
	private Boolean is_show_image;

	private Integer duration;
	private Integer productivity;// NS xưởng
	private Integer productivity_po;// NS cua PO
	private Integer productivity_porder;// NS Target (Khi tao lenh sx, lay tu NS Target tai chao gia vao)
	private Integer productivity_line;// NS tổ (tính trung bình tiến độ ra chuyền)
	private String lineinfo;
	@JsonProperty("icon")
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(int grant_type) {
		this.grant_type = grant_type;
	}

	public Integer getProductivity_po() {
		return productivity_po;
	}

	public void setProductivity_po(Integer productivity_po) {
		this.productivity_po = productivity_po;
	}

	public Integer getProductivity_porder() {
		return productivity_porder;
	}

	public void setProductivity_porder(Integer productivity_porder) {
		this.productivity_porder = productivity_porder;
	}

	public Long getParentid_origin() {
		return parentid_origin;
	}

	public void setParentid_origin(Long parentid_origin) {
		this.parentid_origin = parentid_origin;
	}

	public String getPordercode() {
		return pordercode;
	}

	public void setPordercode(String pordercode) {
		this.pordercode = pordercode;
	}

	public long getResourceId() {
		return ResourceId;
	}

	public String getName() {
		return Name;
	}

	public Date getStartDate() {
		return StartDate;
	}

	public Date getEndDate() {
		return EndDate;
	}

	public void setResourceId(long resourceId) {
		ResourceId = resourceId;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setStartDate(Date startDate) {
		StartDate = startDate;
	}

	public void setEndDate(Date endDate) {
		EndDate = endDate;
	}

	public String getMahang() {
		return mahang;
	}

	public String getCls() {
		return Cls;
	}

	public void setMahang(String mahang) {
		this.mahang = mahang;
	}

	public void setCls(String cls) {
		Cls = cls;
	}

	public long getId_origin() {
		return id_origin;
	}

	public void setId_origin(long id_origin) {
		this.id_origin = id_origin;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getProductivity() {
		return productivity;
	}

	public void setProductivity(Integer productivity) {
		this.productivity = productivity;
	}

	public String getVendorname() {
		return vendorname;
	}

	public String getBuyername() {
		return buyername;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public void setBuyername(String buyername) {
		this.buyername = buyername;
	}

	public Integer getTotalpackage() {
		return totalpackage;
	}

	public void setTotalpackage(Integer totalpackage) {
		this.totalpackage = totalpackage;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public long getPorder_grantid_link() {
		return porder_grantid_link;
	}

	public void setPorder_grantid_link(long porder_grantid_link) {
		this.porder_grantid_link = porder_grantid_link;
	}

	public long getProductid_link() {
		return productid_link;
	}

	public long getPcontract_poid_link() {
		return pcontract_poid_link;
	}

	public void setProductid_link(long productid_link) {
		this.productid_link = productid_link;
	}

	public void setPcontract_poid_link(long pcontract_poid_link) {
		this.pcontract_poid_link = pcontract_poid_link;
	}

	public long getPcontractid_link() {
		return pcontractid_link;
	}

	public void setPcontractid_link(long pcontractid_link) {
		this.pcontractid_link = pcontractid_link;
	}

	public long getPorderid_link() {
		return porderid_link;
	}

	public void setPorderid_link(long porderid_link) {
		this.porderid_link = porderid_link;
	}

	public String getProductbuyercode() {
		return productbuyercode;
	}

	public void setProductbuyercode(String productbuyercode) {
		this.productbuyercode = productbuyercode;
	}

	public Integer getProductivity_line() {
		return productivity_line;
	}

	public void setProductivity_line(Integer productivity_line) {
		this.productivity_line = productivity_line;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	public Boolean getIs_show_image() {
		return is_show_image;
	}

	public void setIs_show_image(Boolean is_show_image) {
		this.is_show_image = is_show_image;
	}

	public String getLineinfo() {
		return lineinfo;
	}

	public void setLineinfo(String lineinfo) {
		this.lineinfo = lineinfo;
	}

}
