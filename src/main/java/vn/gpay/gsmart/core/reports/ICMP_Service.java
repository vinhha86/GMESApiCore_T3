package vn.gpay.gsmart.core.reports;

import java.util.List;

public interface ICMP_Service {

	List<CMP_Data> getData_ByMonth(Long userrootorgid_link, Long userorgid_link, int month, int year, int reportmonths);

	List<CMP_Data> getData_ByMonth_ToSX(Long userrootorgid_link, Long userorgid_link, int month, int year,
			int reportmonths);

}
