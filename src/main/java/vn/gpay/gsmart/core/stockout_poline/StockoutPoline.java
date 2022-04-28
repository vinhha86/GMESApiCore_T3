package vn.gpay.gsmart.core.stockout_poline;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="stockout_poline")
@Entity
public class StockoutPoline implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stockout_poline_generator")
	@SequenceGenerator(name="stockout_poline_generator", sequenceName = "stockout_poline_id_seq", allocationSize=1)
	protected Long id;
	
	@Column(name = "stockoutid_link")
	private Long stockoutid_link;
	
	@Column(name = "pcontract_poid_link")
	private Long pcontract_poid_link;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStockoutid_link() {
		return stockoutid_link;
	}

	public void setStockoutid_link(Long stockoutid_link) {
		this.stockoutid_link = stockoutid_link;
	}

	public Long getPcontract_poid_link() {
		return pcontract_poid_link;
	}

	public void setPcontract_poid_link(Long pcontract_poid_link) {
		this.pcontract_poid_link = pcontract_poid_link;
	}
	
}
