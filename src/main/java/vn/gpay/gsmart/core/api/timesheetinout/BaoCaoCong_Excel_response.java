package vn.gpay.gsmart.core.api.timesheetinout;

import vn.gpay.gsmart.core.base.ResponseBase;

public class BaoCaoCong_Excel_response extends ResponseBase {
    private byte[] data;
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
}
