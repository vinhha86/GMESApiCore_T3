package vn.gpay.gsmart.core.cutplan;

public class CutPlan_Dinhmuc {
	private Long id;
	private Long materialskuid_link;
	private Integer productsku_amount = 0;
	private Float material_amount = (float)0;
	private Float cutplan_bom = (float)0;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMaterialskuid_link() {
		return materialskuid_link;
	}
	public void setMaterialskuid_link(Long materialskuid_link) {
		this.materialskuid_link = materialskuid_link;
	}
	public Integer getProductsku_amount() {
		return productsku_amount;
	}
	public void setProductsku_amount(Integer productsku_amount) {
		this.productsku_amount = productsku_amount;
	}
	public Float getMaterial_amount() {
		return material_amount;
	}
	public void setMaterial_amount(Float material_amount) {
		this.material_amount = material_amount;
	}
	public Float getCutplan_bom() {
		return cutplan_bom;
	}
	public void setCutplan_bom(Float cutplan_bom) {
		this.cutplan_bom = cutplan_bom;
	}
	
	public void calculateBOM() {
		if (productsku_amount > 0)
			cutplan_bom = material_amount/productsku_amount;
	}
}
