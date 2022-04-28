package vn.gpay.gsmart.core.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long productid_link;

	private String text;
	private Long parent_id;
	private String code;
	private String info;
	private byte[] imgproduct;
	private Integer amount;
	private Float price;
	private String vendorcode;
	private Long pcontract_product_id;

	public String getIconCls() {
		return "x-tree-node-icon";
	}

	public byte[] getImgproduct() {
		return imgproduct;
	}

	public void setImgproduct(byte[] imgproduct) {
		this.imgproduct = imgproduct;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private List<ProductTree> children = new ArrayList<ProductTree>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public List<ProductTree> getChildren() {
		return children;
	}

	public void setChildren(List<ProductTree> children) {
		this.children = children;
	}

	public Long getProductid_link() {
		return productid_link;
	}

	public void setProductid_link(Long productid_link) {
		this.productid_link = productid_link;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getAmount() {
		return amount;
	}

	public Float getPrice() {
		return price;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getVendorcode() {
		return vendorcode;
	}

	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}

	public Long getPcontract_product_id() {
		return pcontract_product_id;
	}

	public void setPcontract_product_id(Long pcontract_product_id) {
		this.pcontract_product_id = pcontract_product_id;
	}
	
	
}
