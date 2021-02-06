package pl.foxsaysderp.voucherstore.sales.product;

public interface ProductDetailsProvider {
    ProductDetails getByProductId(String productId);
}
