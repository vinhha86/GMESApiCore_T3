package vn.gpay.gsmart.core.stockin_poline;

import java.util.List;
import vn.gpay.gsmart.core.base.Operations;

public interface IStockinPolineService extends Operations<StockinPoline>{
	List<StockinPoline> getByStockin_Poline(Long stockinid_link, Long pcontract_poid_link);
}
