package vn.gpay.gsmart.core.api.Report;

import vn.gpay.gsmart.core.base.ResponseBase;

public class NhanSu_Excel_response extends ResponseBase {
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
