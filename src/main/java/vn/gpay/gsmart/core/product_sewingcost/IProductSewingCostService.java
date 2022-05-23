package vn.gpay.gsmart.core.product_sewingcost;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;

public interface IProductSewingCostService extends Operations<ProductSewingCost> {
	List<ProductSewingCost> getby_product_and_workingprocess(Long productid_link, Long workingprocessid_link);
	List<ProductSewingCost> getby_workingprocess(Long workingprocessid_link);
	public List<ProductSewingCost> getByProductUnused(Long productid_link, Long pcontractid_link, List<Long> listProductBalanceProcessId);
	public List<ProductSewingCost> getByProductUnused(Long productid_link, Long pcontractid_link);
	List<ProductSewingCost> getbyProductPcontract(Long productid_link, Long pcontractid_link);


	// Lay danh sach theo ten Cong Doan ( nam trong va ngoai va cum cong doan)
	List<ProductSewingCost> findByProductPcontractName(Long productid_link, Long pcontractid_link, String name);

	// Lay danh sach theo ten Cong Doan ( nam trong cum cong doan co san tren DB)
	List <ProductSewingCost> findByProductPcontractNameInBalance(Long productid_link, Long pcontractid_link, String name);

	// Lay danh sanh theo ten Cong Doan ( khong nam trong cum cong doan co san trong DB)
	List <ProductSewingCost> findByProductPcontractNameOutBalance(Long productid_link, Long pcontractid_link, String name);
//    List<ProductSewingCost> findByProductPcontractName(Long productid_link, Long pcontractid_link, String name);
//	List<ProductSewingCost> findByProductPcontractName2(Long productid_link, Long pcontractid_link, String name, Long id);
}
