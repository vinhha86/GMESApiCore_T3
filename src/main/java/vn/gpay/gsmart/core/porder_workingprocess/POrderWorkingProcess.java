package vn.gpay.gsmart.core.porder_workingprocess;

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

import vn.gpay.gsmart.core.category.LaborLevel;
import vn.gpay.gsmart.core.devices_type.Devices_Type;

@Table(name="porders_workingprocess")
@Entity
public class POrderWorkingProcess implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "porders_workingprocess_generator")
	@SequenceGenerator(name="porders_workingprocess_generator", sequenceName = "porders_workingprocess_id_seq", allocationSize=1)
	protected Long id;
	
	@Column(name ="orgrootid_link")
    private Long orgrootid_link;
	
	@Column(name ="name",length=100)
    private String name;
	
	@Column(name ="code",length=100)
    private String code;
	
	@Column(name ="parentid_link")
    private Long parentid_link;

	@Column(name ="productid_link")
    private Long productid_link;
	
	@Column(name ="porderid_link")
    private Long porderid_link;

	@Column(name ="timespent_standard")
    private Integer timespent_standard;
	
	@Column(name ="devicerequiredid_link")
    private Long devicerequiredid_link;
	
	@Column(name ="devicerequired_desc",length=200)
    private String devicerequired_desc;	
	
	@Column(name ="laborrequiredid_link")
    private Long laborrequiredid_link;	
	
	@Column(name ="laborrequired_desc", length=200)
    private String laborrequired_desc;
	
	@Column(name ="techcomment", length=500)
    private String techcomment;	
	
	@Column(name ="process_type")
    private Integer process_type;

	@Column(name ="status")
    private Integer status;	
	
	@Column(name ="usercreatedid_link")
    private Long usercreatedid_link;	
	
	@Column(name ="timecreated")
    private Date timecreated;

	@Column(name ="isselected")
    private Boolean isselected;
	
	private Float lastcost;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
    @JoinColumn(name="devicerequiredid_link",insertable=false,updatable =false)
    private Devices_Type device;	
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
    @JoinColumn(name="laborrequiredid_link",insertable=false,updatable =false)
    private LaborLevel laborlevel;
	
	@Transient
	public String getDevice_name() {
		if(device != null) {
			return device.getName();
		}
		return "";
	}	
	
	@Transient
	public String getLaborlevel_name() {
		if(laborlevel != null) {
			return laborlevel.getName();
		}
		return "";
	}
	
	public Boolean getIsselected() {
		return isselected;
	}

	public void setIsselected(Boolean isselected) {
		this.isselected = isselected;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getParentid_link() {
		return parentid_link;
	}

	public void setParentid_link(Long parentid_link) {
		this.parentid_link = parentid_link;
	}

	public Long getProductid_link() {
		return productid_link;
	}

	public void setProductid_link(Long productid_link) {
		this.productid_link = productid_link;
	}
	
	public Long getPorderid_link() {
		return porderid_link;
	}

	public void setPorderid_link(Long porderid_link) {
		this.porderid_link = porderid_link;
	}

	public Integer getTimespent_standard() {
		return timespent_standard;
	}

	public void setTimespent_standard(Integer timespent_standard) {
		this.timespent_standard = timespent_standard;
	}

	public Long getDevicerequiredid_link() {
		return devicerequiredid_link;
	}

	public void setDevicerequiredid_link(Long devicerequiredid_link) {
		this.devicerequiredid_link = devicerequiredid_link;
	}

	public String getDevicerequired_desc() {
		return devicerequired_desc;
	}

	public void setDevicerequired_desc(String devicerequired_desc) {
		this.devicerequired_desc = devicerequired_desc;
	}

	public Long getLaborrequiredid_link() {
		return laborrequiredid_link;
	}

	public void setLaborrequiredid_link(Long laborrequiredid_link) {
		this.laborrequiredid_link = laborrequiredid_link;
	}

	public String getLaborrequired_desc() {
		return laborrequired_desc;
	}

	public void setLaborrequired_desc(String laborrequired_desc) {
		this.laborrequired_desc = laborrequired_desc;
	}

	public String getTechcomment() {
		return techcomment;
	}

	public void setTechcomment(String techcomment) {
		this.techcomment = techcomment;
	}

	public Integer getProcess_type() {
		return process_type;
	}

	public void setProcess_type(Integer process_type) {
		this.process_type = process_type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getUsercreatedid_link() {
		return usercreatedid_link;
	}

	public void setUsercreatedid_link(Long usercreatedid_link) {
		this.usercreatedid_link = usercreatedid_link;
	}

	public Date getTimecreated() {
		return timecreated;
	}

	public void setTimecreated(Date timecreated) {
		this.timecreated = timecreated;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Float getLastcost() {
		return lastcost;
	}

	public void setLastcost(Float lastcost) {
		this.lastcost = lastcost;
	}
}
