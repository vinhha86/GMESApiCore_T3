package vn.gpay.gsmart.core.timesheet_sum_col;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="timesheet_sum_col")
@Entity
public class TimeSheet_Sum_Col implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "timesheet_sum_col_generator")
	@SequenceGenerator(name="timesheet_sum_col_generator", sequenceName = "timesheet_sum_col_id_seq", allocationSize=1)
	private Long id;
	private String code;
	private String name;
	private Long sumcoltypeid_link;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSumcoltypeid_link() {
		return sumcoltypeid_link;
	}
	public void setSumcoltypeid_link(Long sumcoltypeid_link) {
		this.sumcoltypeid_link = sumcoltypeid_link;
	}
	
	
}
