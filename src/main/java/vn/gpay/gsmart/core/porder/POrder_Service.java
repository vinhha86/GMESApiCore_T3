package vn.gpay.gsmart.core.porder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.github.wenhao.jpa.Sorts;
import com.github.wenhao.jpa.Specifications;

import vn.gpay.gsmart.core.base.AbstractService;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.pcontract_po.IPContract_POService;
import vn.gpay.gsmart.core.pcontract_po.PContract_PO;
import vn.gpay.gsmart.core.pcontract_po_productivity.IPContract_PO_Productivity_Service;
import vn.gpay.gsmart.core.porder_req.POrder_Req;
import vn.gpay.gsmart.core.product.IProductService;
import vn.gpay.gsmart.core.product.Product;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.GPAYDateFormat;
import vn.gpay.gsmart.core.utils.OrgType;
import vn.gpay.gsmart.core.utils.POStatus;
import vn.gpay.gsmart.core.utils.POType;
import vn.gpay.gsmart.core.utils.POrderStatus;

@Service
public class POrder_Service extends AbstractService<POrder> implements IPOrder_Service {
	@Autowired
	IPOrder_Repository repo;
	@Autowired
	IPOrder_AutoID_Service porder_AutoID_Service;
	@Autowired
	private IPContract_POService pcontract_POService;
	@Autowired
	private IPContract_PO_Productivity_Service poProductivityService;
//	@Autowired private IPOrder_Req_Service porder_reqService;
	@Autowired
	private Common commonService;
	@Autowired
	private IProductService productService;
	@Autowired
	private IOrgService orgService;

	@Override
	protected JpaRepository<POrder, Long> getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	@Override
	public List<POrder> getByContract(Long pcontractid_link) {
		return repo.getByContract(pcontractid_link);
	}

	@Override
	public POrder getById(Long id) {
		List<POrder> a = repo.getById(id);
		if (a.size() > 0)
			return a.get(0);
		else
			return null;
	}

	@Override
//	@Transactional(rollbackFor = RuntimeException.class)
	public POrder createPOrder(POrder_Req porder_req, GpayUser user) {
		Long orgrootid_link = user.getRootorgid_link();
		try {
			POrder porder = new POrder();

			// Kiem tra xem POrder_req da duoc tao lenhsx hay chua?
			List<POrder> lsPOrder = getByPOrder_Req(porder_req.getPcontract_poid_link(), porder_req.getId());
			if (lsPOrder.size() > 0) {
				porder = lsPOrder.get(0);
				porder.setGranttoorgid_link(porder_req.getGranttoorgid_link());
				porder.setTotalorder(porder_req.getTotalorder());
				porder = this.save(porder);
				return porder;

			} else {

				porder.setPorderreqid_link(porder_req.getId());
				porder.setGranttoorgid_link(porder_req.getGranttoorgid_link());
				porder.setPcontractid_link(porder_req.getPcontractid_link());
				porder.setPcontract_poid_link(porder_req.getPcontract_poid_link());
				porder.setProductid_link(porder_req.getProductid_link());
				porder.setTotalorder_req(porder_req.getTotalorder());
				porder.setTotalorder(porder_req.getTotalorder());
				// Lay thong tin PO
				PContract_PO thePO = pcontract_POService.findOne(porder.getPcontract_poid_link());

				// Chi tao lenh cho Chao gia/ Khong tao lenh cho PO Line
//				if (null !=thePO && thePO.getParentpoid_link() == null){
				if (thePO != null & thePO.getPo_typeid_link() == POType.PO_LINE_PLAN
						&& thePO.getParentpoid_link() != null) {
					String po_code = null != thePO.getPo_vendor() && thePO.getPo_vendor().length() > 0
							? thePO.getPo_vendor()
							: thePO.getPo_buyer();

					if (porder.getId() == null || porder.getId() == 0) {

						porder.setPcontract_poid_link(thePO.getId());
						porder.setGolivedate(thePO.getShipdate());
						porder.setProductiondate(thePO.getProductiondate());

						porder.setFinishdate_plan(thePO.getShipdate());
						porder.setProductiondate_plan(thePO.getProductiondate());

						porder.setOrgrootid_link(orgrootid_link);
						porder.setOrderdate(new Date());
						porder.setUsercreatedid_link(user.getId());
						porder.setStatus(null != thePO.getStatus() && thePO.getStatus() == POStatus.PO_STATUS_UNCONFIRM
								? POrderStatus.PORDER_STATUS_UNCONFIRM
								: POrderStatus.PORDER_STATUS_FREE);
						porder.setTimecreated(new Date());

						// Lay thong tin NS target tu chao gia
						porder.setPlan_productivity(poProductivityService
								.getProductivityByPOAndProduct(thePO.getParentpoid_link(), porder.getProductid_link()));
					}
					Product theProduct = productService.findOne(porder.getProductid_link());
					// Sinh mã lệnh theo Mã SP(Buyer)
					if (null != theProduct) {
						String theCode = theProduct.getBuyercode() + "-"
								+ Common.Date_ToString(thePO.getShipdate(), "dd/MM/yy");
						porder = savePOrder(calPlan_FinishDate(orgrootid_link, porder), theCode);
					} else
						porder = savePOrder(calPlan_FinishDate(orgrootid_link, porder), po_code);

					// Update lai trng thai cua Porder_req ve da tao lenh
//					List<POrder_Req> lsPOrder_Req = porder_reqService.getByOrg_PO_Product(porder.getPcontract_poid_link(), porder.getProductid_link(), porder.getGranttoorgid_link());
//					for(POrder_Req thePOrder_Req: lsPOrder_Req){
//						thePOrder_Req.setStatus(POrderReqStatus.STATUS_PORDERED);
//						porder_reqService.save(thePOrder_Req);
//					}

//					//Tao Task
//					long userid_link = user.getId();
//					long pcontractid_link = porder.getPcontractid_link();
//					long pcontract_poid_link = porder.getPcontract_poid_link();
//					long porder_req_id_link = porder.getPorderreqid_link();
//					long porderid_link = porder.getId();
//					long productid_link = porder.getProductid_link();
//					long granttoorgid_link = porder.getGranttoorgid_link();
//					createTask_AfterPorderCreating(
//							orgrootid_link,
//							userid_link,
//							pcontractid_link,
//							pcontract_poid_link,
//							porder_req_id_link,
//							porderid_link,
//							productid_link,
//							granttoorgid_link						
//						);
					return porder;
				} else {
					return null;
				}
			}

		} catch (RuntimeException e) {
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return null;
		}
	}

	private POrder calPlan_FinishDate(Long orgrootid_link, POrder porder) {
		if (null != porder.getTotalorder() && 0 != porder.getTotalorder() && null != porder.getPlan_productivity()
				&& 0 != porder.getPlan_productivity()) {
			// Tinh toan SL chuyen yeu cau
			Float totalorder = (float) porder.getTotalorder();
			Float plan_productivity = (float) porder.getPlan_productivity();

			Integer plan_duration = Math.round(totalorder / plan_productivity);
			Date finishdate_plan = commonService.Date_Add_with_holiday(porder.getProductiondate_plan(), plan_duration,
					orgrootid_link);

			porder.setFinishdate_plan(finishdate_plan);
			porder.setPlan_duration(plan_duration);
//			porder.setPlan_linerequired(Precision.round(iSLNgaySX/productiondays,1));
		} else {
			porder.setPlan_linerequired(null);
		}
		return porder;
	}

	@Override
	public POrder savePOrder(POrder porder, String po_code) {
		try {
			if (porder.getId() == null || porder.getId() == 0) {
				porder.setOrdercode(porder_AutoID_Service.getLastID(po_code));
			}
			porder = this.save(porder);
			return porder;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<POrder> getByContractAndProduct(Long pcontractid_link, Long productid_link) {
		return repo.getByContractAndProduct(pcontractid_link, productid_link);
	}

	@Override
	public List<POrder> getByProductIdLink(Long productid_link) {
		return repo.getByProductIdLink(productid_link);
	}

	@Override
	public List<POrder> getByPOAndProduct(Long pcontract_poid_link, Long productid_link) {
		return repo.getByPOAndProduct(pcontract_poid_link, productid_link);
	}

	@Override
	public List<POrder> getByContractAndPO(Long pcontractid_link, Long pcontract_poid_link) {
		return repo.getByContractAndPO(pcontractid_link, pcontract_poid_link);
	}

	@Override
	public List<POrder> getByContractAndPO_Granted(Long pcontractid_link, Long pcontract_poid_link) {
//		// TODO Auto-generated method stub
//		Specification<POrder> specification = Specifications.<POrder>and()
//				.ge("status", 0)
//				.eq("pcontractid_link", pcontractid_link)
//	            .eq("pcontract_poid_link", pcontract_poid_link)
//	            .build();
//		List<POrder> a = repo.findAll(specification);
//		return a;
		return repo.getByContractAndPO_Granted(pcontractid_link, pcontract_poid_link);
	}

	@Override
	public List<POrder> getByPOrder_Req(Long pcontract_poid_link, Long porderreqid_link) {
		return repo.getByPOrder_Req(pcontract_poid_link, porderreqid_link);
	}

	@Override
	public List<POrder> getByStatus(Integer status) {
		return repo.getByStatus(status);
	}

	@Override
	public Integer getMaxPriority() {
		return repo.getMaxPriority();
	}

	@Override
	public List<POrder> getFilter(String ordercode, Integer status, Long granttoorgid_link, String collection,
			String season, Integer salaryyear, Integer salarymonth, Date processingdate_from, Date processingdate_to) {
		try {
			if (null != salarymonth) {
				Specification<POrder> specification = Specifications.<POrder>and()
						.eq(null != status && status != -1, "status", status)
						.eq(null != granttoorgid_link && granttoorgid_link != -1, "granttoorgid_link",
								granttoorgid_link)
						.like(null != ordercode && ordercode != "", "ordercode", "%" + ordercode + "%")
						.like(null != collection && collection != "", "collection", "%" + collection + "%")
						.like(null != season && season != "", "season", "%" + season + "%")
						.eq(Objects.nonNull(salaryyear), "salaryyear", salaryyear)
						.eq(null != salarymonth && salarymonth != -1, "salarymonth", salarymonth)
						.ge(Objects.nonNull(processingdate_from), "orderdate",
								GPAYDateFormat.atStartOfDay(processingdate_from))
						.le(Objects.nonNull(processingdate_to), "orderdate",
								GPAYDateFormat.atEndOfDay(processingdate_to))
						.between(processingdate_from != null && processingdate_to != null, "orderdate",
								GPAYDateFormat.atStartOfDay(processingdate_from),
								GPAYDateFormat.atEndOfDay(processingdate_to))
						.build();
				Sort sort = Sorts.builder().desc("ordercode").build();
				List<POrder> a = repo.findAll(specification, sort);
				return a;
			} else {
				Specification<POrder> specification = Specifications.<POrder>and()
						.eq(null != status && status != -1, "status", status)
						.eq(null != granttoorgid_link && granttoorgid_link != -1, "granttoorgid_link",
								granttoorgid_link)
						.like(null != ordercode && ordercode != "", "ordercode", "%" + ordercode + "%")
						.like(null != collection && collection != "", "collection", "%" + collection + "%")
						.like(null != season && season != "", "season", "%" + season + "%")
						.eq(Objects.nonNull(salaryyear), "salaryyear", salaryyear)
//			            .eq(null!=salarymonth && salarymonth != -1, "salarymonth", salarymonth)
						.eq("salarymonth", salarymonth)
						.ge(Objects.nonNull(processingdate_from), "orderdate",
								GPAYDateFormat.atStartOfDay(processingdate_from))
						.le(Objects.nonNull(processingdate_to), "orderdate",
								GPAYDateFormat.atEndOfDay(processingdate_to))
						.between(processingdate_from != null && processingdate_to != null, "orderdate",
								GPAYDateFormat.atStartOfDay(processingdate_from),
								GPAYDateFormat.atEndOfDay(processingdate_to))
						.build();
				Sort sort = Sorts.builder().desc("ordercode").build();
				List<POrder> a = repo.findAll(specification, sort);
				return a;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<POrder> get_by_org(long orgid_link) {
		// TODO Auto-generated method stub
		Specification<POrder> specification = Specifications.<POrder>and().ne("status", -1).le("status", 5)
				.eq("granttoorgid_link", orgid_link).build();
		Sort sort = Sorts.builder().desc("ordercode").build();
		List<POrder> a = repo.findAll(specification, sort);
		return a;
	}

	@Override
	// Lay Porder duy nhat theo tung PO, san pham va dai co
	public POrder get_oneby_po_price(long orgrootid_link, long granttoorgid_link, long pcontract_poid_link,
			long productid_link, long sizesetid_link) {
		Specification<POrder> specification = Specifications.<POrder>and().eq("orgrootid_link", orgrootid_link)
				.eq("granttoorgid_link", granttoorgid_link).eq("pcontract_poid_link", pcontract_poid_link)
				.eq("productid_link", productid_link).eq("sizesetid_link", sizesetid_link).build();
		List<POrder> a = repo.findAll(specification);
		if (a.size() > 0)
			return a.get(0);
		else
			return null;
	}

	@Override
	// Lay Porder duy nhat theo tung PO, san pham va dai co
	public POrder get_oneby_po_org_product(long orgrootid_link, long granttoorgid_link, long pcontract_poid_link,
			long productid_link) {
		Specification<POrder> specification = Specifications.<POrder>and().eq("orgrootid_link", orgrootid_link)
				.eq("granttoorgid_link", granttoorgid_link).eq("pcontract_poid_link", pcontract_poid_link)
				.eq("productid_link", productid_link).build();
		List<POrder> a = repo.findAll(specification);
		if (a.size() > 0)
			return a.get(0);
		else
			return null;
	}

	@Override
	// Danh sach cac lenh duoc phan cho Phan xuong nhung chua duoc phan chuyen
	public List<POrder> get_free_bygolivedate(Date golivedate_from, Date golivedate_to, Long granttoorgid_link,
			String PO_code, Long orgbuyerid_link, Long orgvendorid_link, List<Long> vendors, List<Long> buyers) {
		vendors = vendors.size() == 0 ? null : vendors;
		buyers = buyers.size() == 0 ? null : buyers;
		return repo.getfree_groupby_product(granttoorgid_link, vendors, buyers);
	}

	@Override
	public List<POrder> getPOrderListBySearch(String style, Long buyerid, Long vendorid, Long factoryid, Long status,
			Long granttoorgid_link) {

		Specification<POrder> specification = Specifications.<POrder>and()
				.eq(Objects.nonNull(buyerid), "pcontract.orgbuyerid_link", buyerid)
				.eq(Objects.nonNull(vendorid), "pcontract.orgvendorid_link", vendorid)
				.eq(Objects.nonNull(factoryid), "org.id", factoryid)
//				.ge(Objects.nonNull(orderdatefrom),"orderdate",GPAYDateFormat.atStartOfDay(orderdatefrom))
//              .le(Objects.nonNull(orderdateto),"orderdate",GPAYDateFormat.atEndOfDay(orderdateto))
				.eq(Objects.nonNull(status), "porderstatus.id", status).ne("porderstatus.id", -1)
				.ne("porderstatus.id", -3)
				.eq(Objects.nonNull(granttoorgid_link), "granttoorgid_link", granttoorgid_link).build();

		return repo.findAll(specification);
	}

	@Override
	public List<POrder> get_by_code(String ordercode, long orgrootid_link) {
		// TODO Auto-generated method stub
		return repo.get_by_code(orgrootid_link, ordercode);
	}

	@Override
	public List<POrder> getPOrderByOrdercode(String ordercode, Long granttoorgid_link) {
		// TODO Auto-generated method stub
		return repo.getPOrderByOrdercode(ordercode, granttoorgid_link);
	}

	@Override
	public List<POrder> getPOrderByExactOrdercode(String ordercode) {
		// TODO Auto-generated method stub
		return repo.getPOrderByExactOrdercode(ordercode);
	}

	@Override
	public List<POrderBinding> getForNotInProductionChart() {

		List<POrderBinding> data = new ArrayList<POrderBinding>();
		Map<String, POrderBinding> mapTmp = new HashMap<>();
		List<Object[]> objects = repo.getForNotInProductionChart();

		for (Object[] row : objects) {
//			System.out.println("---");
//			System.out.println((Long) row[0]);
//			System.out.println((String) row[1]);
//			System.out.println((Integer) row[2]);
			Long sum = (Long) row[0];
			String name = (String) row[1];
			Integer status = (Integer) row[2];
			Long id = (Long) row[3];
			String code = (String) row[4];

			if (mapTmp.containsKey(name)) {
				POrderBinding temp = mapTmp.get(name);
				switch (status) {
				case 0:
					temp.setSumChuaPhanChuyen(sum);
					break;
				case 1:
					temp.setSumChuaSanXuat(sum);
					break;
				}
				mapTmp.put(name, temp);
			} else {
				POrderBinding temp = new POrderBinding();
				temp.setOrgName(name);
				temp.setOrgId(id);
				temp.setOrgCode(code);
				temp.setSumChuaPhanChuyen(0L);
				temp.setSumChuaSanXuat(0L);
				switch (status) {
				case 0:
					temp.setSumChuaPhanChuyen(sum);
					break;
				case 1:
					temp.setSumChuaSanXuat(sum);
					break;
				}
				mapTmp.put(name, temp);
			}
		}
		data = new ArrayList<POrderBinding>(mapTmp.values());
		Collections.sort(data, new Comparator<POrderBinding>() {
			public int compare(POrderBinding o1, POrderBinding o2) {
				return o1.getOrgId().compareTo(o2.getOrgId());
			}
		});
		return data;
	}

	@Override
	public List<POrderBinding> getPOrderStatusChart() {
		List<POrderBinding> data = new ArrayList<POrderBinding>();

		// ds trang thai lenh
		data = addPOrderBinding_TrangThai(data);

		// ds phan xuong
		List<Integer> orgtype = new ArrayList<Integer>();
		orgtype.add(OrgType.ORG_TYPE_FACTORY);
		List<Org> px_list = orgService.findOrgByOrgType(orgtype); // System.out.println("px_list " + px_list.size());

		// lenh sx truoc 6 thang -> hien tai -> sau 1 thang
		Calendar calfrom = Calendar.getInstance();
		calfrom.add(Calendar.MONTH, -1);
//		calfrom.set(Calendar.DAY_OF_MONTH, 1);
		Date dateFrom = GPAYDateFormat.atStartOfDay(calfrom.getTime());
		Calendar calto = Calendar.getInstance();
		calto.add(Calendar.MONTH, +12);
//		calto.set(Calendar.DAY_OF_MONTH, 1);
		Date dateTo = GPAYDateFormat.atEndOfDay(calto.getTime());

//		List<POrder> porder_list = repo.findByGolivedate(dateFrom, dateTo); System.out.println("porder_list " + porder_list.size());

		for (Org px : px_list) {
			List<Integer> statuses = new ArrayList<Integer>();
			// free
			statuses.add(POrderStatus.PORDER_STATUS_FREE);
			Long sum_free = repo.findTotalByGolivedate(dateFrom, dateTo, px.getId(), statuses);

			// granted
			statuses = new ArrayList<Integer>();
			statuses.add(POrderStatus.PORDER_STATUS_GRANTED);
			Long sum_granted = repo.findTotalByGolivedate(dateFrom, dateTo, px.getId(), statuses);

			// running
			statuses = new ArrayList<Integer>();
			statuses.add(POrderStatus.PORDER_STATUS_RUNNING);
			statuses.add(POrderStatus.PORDER_STATUS_DONE);
			Long sum_running = repo.findTotalByGolivedate(dateFrom, dateTo, px.getId(), statuses);

			// finished
			statuses = new ArrayList<Integer>();
			statuses.add(POrderStatus.PORDER_STATUS_FINISHED);
			Long sum_finished = repo.findTotalByGolivedate(dateFrom, dateTo, px.getId(), statuses);

			for (POrderBinding porderBinding : data) {
				List<POrderBinding> porderBinding_list = porderBinding.getPorderBinding_list();
				Long sum = porderBinding.getSum();
				POrderBinding newPOrderBinding = new POrderBinding();
				Integer status = porderBinding.getStatus();
				String statusName = porderBinding.getStatusName();
				switch (status) {
				case 0:
					newPOrderBinding.setOrgName(px.getName());
					newPOrderBinding.setSum(sum_free);
					sum += sum_free;
					break;
				case 1:
					newPOrderBinding.setOrgName(px.getName());
					newPOrderBinding.setSum(sum_granted);
					sum += sum_granted;
					break;
				case 4:
					newPOrderBinding.setOrgName(px.getName());
					newPOrderBinding.setSum(sum_running);
					sum += sum_running;
					break;
				case 6:
					if (statusName.equals("Chậm GH (ít)")) {
						statuses = new ArrayList<Integer>();
						statuses.add(POrderStatus.PORDER_STATUS_FINISHED);
						Long sum_slow = repo.findTotalByGolivedate_SlowSmall(dateFrom, dateTo, px.getId(), statuses);
						newPOrderBinding.setOrgName(px.getName());
						newPOrderBinding.setSum(sum_slow);
						sum += sum_slow;
						break;
					}
					if (statusName.equals("Chậm GH (vừa)")) {
						statuses = new ArrayList<Integer>();
						statuses.add(POrderStatus.PORDER_STATUS_FINISHED);
						Long sum_slow = repo.findTotalByGolivedate_SlowMedium(dateFrom, dateTo, px.getId(), statuses);
						newPOrderBinding.setOrgName(px.getName());
						newPOrderBinding.setSum(sum_slow);
						sum += sum_slow;
						break;
					}
					if (statusName.equals("Chậm GH (nhiều)")) {
						statuses = new ArrayList<Integer>();
						statuses.add(POrderStatus.PORDER_STATUS_FINISHED);
						Long sum_slow = repo.findTotalByGolivedate_SlowBig(dateFrom, dateTo, px.getId(), statuses);
						newPOrderBinding.setOrgName(px.getName());
						newPOrderBinding.setSum(sum_slow);
						sum += sum_slow;
						break;
					}
					newPOrderBinding.setOrgName(px.getName());
					newPOrderBinding.setSum(sum_finished);
					sum += sum_finished;
					break;
				}
				porderBinding_list.add(newPOrderBinding);
				porderBinding.setSum(sum);
			}
		}

		return data;
	}

	public List<POrderBinding> addPOrderBinding_TrangThai(List<POrderBinding> data) {
		POrderBinding statusFree = new POrderBinding();
		POrderBinding statusGranted = new POrderBinding();
		POrderBinding statusRunning = new POrderBinding();
		POrderBinding statusFinished = new POrderBinding();
		POrderBinding statusSlow_small = new POrderBinding();
		POrderBinding statusSlow_medium = new POrderBinding();
		POrderBinding statusSlow_big = new POrderBinding();

		statusFree.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusFree.setStatus(POrderStatus.PORDER_STATUS_FREE);
		statusFree.setStatusName("Chưa phân chuyền");
		statusFree.setSum((long) 0);

		statusGranted.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusGranted.setStatus(POrderStatus.PORDER_STATUS_GRANTED);
		statusGranted.setStatusName("Đã phân chuyền");
		statusGranted.setSum((long) 0);

		statusRunning.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusRunning.setStatus(POrderStatus.PORDER_STATUS_RUNNING);
		statusRunning.setStatusName("Đang sản xuất");
		statusRunning.setSum((long) 0);

		statusFinished.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusFinished.setStatus(POrderStatus.PORDER_STATUS_FINISHED);
		statusFinished.setStatusName("Đã hoàn thành");
		statusFinished.setSum((long) 0);

		statusSlow_small.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusSlow_small.setStatus(POrderStatus.PORDER_STATUS_FINISHED);
		statusSlow_small.setStatusName("Chậm GH (ít)");
		statusSlow_small.setSum((long) 0);

		statusSlow_medium.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusSlow_medium.setStatus(POrderStatus.PORDER_STATUS_FINISHED);
		statusSlow_medium.setStatusName("Chậm GH (vừa)");
		statusSlow_medium.setSum((long) 0);

		statusSlow_big.setPorderBinding_list(new ArrayList<POrderBinding>());
		statusSlow_big.setStatus(POrderStatus.PORDER_STATUS_FINISHED);
		statusSlow_big.setStatusName("Chậm GH (nhiều)");
		statusSlow_big.setSum((long) 0);

		data.add(statusFree);
		data.add(statusGranted);
		data.add(statusRunning);
		data.add(statusFinished);
		data.add(statusSlow_small);
		data.add(statusSlow_medium);
		data.add(statusSlow_big);

		return data;
	}

	@Override
	public List<POrder> getPOrderBySearch(Long buyerid, Long vendorid, Long factoryid, String pobuyer,
			String stylebuyer, String contractcode, List<Integer> statuses, Long granttoorgid_link, Date golivedatefrom,
			Date golivedateto) {
		// TODO Auto-generated method stub
		statuses = statuses.size() > 0 ? statuses : null;
		List<POrder> page = repo.getPOrderBySearch(buyerid, vendorid, factoryid, pobuyer, stylebuyer, contractcode,
				statuses, granttoorgid_link, golivedatefrom, golivedateto);
		return page;

//		List<POrder> a= repo.getPOrderBySearch(buyerid, vendorid, factoryid, 
//				pobuyer, 
////				stylebuyer, 
//				contractcode, 
//				statuses, 
//				granttoorgid_link, golivedatefrom, golivedateto);
//		return a;
	}

//	@Override
//	public List<POrder> getPOrderBySearch(Long buyerid, Long vendorid, Long factoryid, String pobuyer,
//			String stylebuyer, String contractcode, Long granttoorgid_link, Date golivedatefrom, Date golivedateto) {
//		return repo.getPOrderBySearch(buyerid, vendorid, factoryid, pobuyer, stylebuyer, contractcode,
//				granttoorgid_link, golivedatefrom, golivedateto);
//	}

	@Override
	public List<POrder> get_free_bygolivedate_groupby_product(Date golivedate_from, Date golivedate_to,
			Long granttoorgid_link, String PO_code, Long orgbuyerid_link, Long orgvendorid_link) {
		return null;
	}

	@Override
	public List<POrder> getby_offer(Long pcontract_poid_link, Long productid_link, Long orgid_link) {
		return repo.getby_offer(orgid_link, pcontract_poid_link, productid_link);
	}

	@Override
	public List<POrder> getby_offer_and_orgs(Long pcontract_poid_link, List<Long> orgs) {
		return repo.getall_by_offer_and_orgs(pcontract_poid_link, orgs);
	}

	@Override
	public List<POrder> getPorderByOrdercodeAndOrg(String ordercode, Long granttoorgid_link) {
		return repo.getPorderByOrdercodeAndOrg(ordercode, granttoorgid_link);
	}

	@Override
	public List<POrder> getPOrderByOrderCodeAndProductBuyerCode(Long granttoorgid_link, String ordercode,
			String buyercode) {
		return repo.getPOrderByOrderCodeAndProductBuyerCode(granttoorgid_link, ordercode, buyercode);
	}

	@Override
	public List<POrder> getByPcontractPO(Long pcontract_poid_link) {
		return repo.getByPcontractPO(pcontract_poid_link);
	}

	@Override
	public Integer getTotalOrder(Long porderid_link) {
		// TODO Auto-generated method stub
		return repo.getTotalOrder(porderid_link);
	}
}
