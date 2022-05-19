package vn.gpay.gsmart.core.api.product;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.product.Product;

import java.util.List;

public class Product_getProductByBuyercode_response extends ResponseBase {
    public List<Product> data;
}
