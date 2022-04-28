package vn.gpay.gsmart.core.stockin_poline;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="stockin_poline")
@Entity
public class StockinPoline implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stockin_poline_generator")
	@SequenceGenerator(name="stockin_poline_generator", sequenceName = "stockin_poline_id_seq", allocationSize=1)
	protected Long id;
	
	@Column(name = "stockinid_link")
	private Long stockinid_link;
	
	@Column(name = "pcontract_poid_link")
	private Long pcontract_poid_link;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStockinid_link() {
		return stockinid_link;
	}

	public void setStockinid_link(Long stockinid_link) {
		this.stockinid_link = stockinid_link;
	}

	public Long getPcontract_poid_link() {
		return pcontract_poid_link;
	}

	public void setPcontract_poid_link(Long pcontract_poid_link) {
		this.pcontract_poid_link = pcontract_poid_link;
	}
	
}
