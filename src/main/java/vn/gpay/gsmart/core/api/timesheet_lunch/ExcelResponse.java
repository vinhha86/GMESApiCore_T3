package vn.gpay.gsmart.core.api.timesheet_lunch;

import vn.gpay.gsmart.core.base.ResponseBase;

public class ExcelResponse extends ResponseBase {
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
