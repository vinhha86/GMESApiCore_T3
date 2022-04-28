package vn.gpay.gsmart.core.pcontractmarket;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import vn.gpay.gsmart.core.markettype.MarketType;
import vn.gpay.gsmart.core.pcontract.PContract;
import vn.gpay.gsmart.core.product.Product;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "pcontract_market" )
@Entity
public class PContractMarket implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcontract_market_generator")
    @SequenceGenerator(name = "pcontract_market_generator", sequenceName = "pcontract_market_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private Long pcontractid_link;

    @Column
    private Long marketid_link;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "pcontractid_link", insertable = false, updatable = false)
    private PContract contract;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "marketid_link", insertable = false, updatable = false)
    private MarketType market;
    
	@Transient
	public String getMarketTypeName() {
		if(market != null) {
			return market.getName();
		}
		return "";
	}

    public PContractMarket() {
    }

    public PContractMarket(Long pcontractid_link, Long marketid_link) {
        this.pcontractid_link = pcontractid_link;
        this.marketid_link = marketid_link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPcontractid() {
        return pcontractid_link;
    }

    public void setPcontractid(Long pcontractid_link) {
        this.pcontractid_link = pcontractid_link;
    }

    public Long getMarketid() {
        return marketid_link;
    }

    public void setMarketid(Long marketid_link) {
        this.marketid_link = marketid_link;
    }
}
