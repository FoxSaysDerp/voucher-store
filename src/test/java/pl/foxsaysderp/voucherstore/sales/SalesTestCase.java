package pl.foxsaysderp.voucherstore.sales;

import pl.foxsaysderp.voucherstore.productcatalog.Product;
import pl.foxsaysderp.voucherstore.productcatalog.ProductCatalogConfiguration;
import pl.foxsaysderp.voucherstore.productcatalog.ProductCatalogFacade;
import pl.foxsaysderp.voucherstore.sales.basket.InMemoryBasketStorage;
import pl.foxsaysderp.voucherstore.sales.offer.OfferMaker;
import pl.foxsaysderp.voucherstore.sales.ordering.InMemoryReservationRepository;
import pl.foxsaysderp.voucherstore.sales.ordering.ReservationRepository;
import pl.foxsaysderp.voucherstore.sales.payment.DummyPaymentGateway;
import pl.foxsaysderp.voucherstore.sales.payment.PaymentGateway;
import pl.foxsaysderp.voucherstore.sales.product.ProductDetails;

import java.math.BigDecimal;
import java.util.UUID;

public class SalesTestCase {

    ProductCatalogFacade productCatalog;
    InMemoryBasketStorage basketStorage;
    CurrentCustomerContext currentCustomerContext;
    Inventory inventory;
    String customerId;
    OfferMaker offerMaker;
    PaymentGateway paymentGateway;
    ReservationRepository reservationRepository;

    protected CurrentCustomerContext thereIsCurrentCustomerContext() {
        return () -> customerId;
    }

    protected Inventory therIsInventory() {
        return productId -> true;
    }

    protected OfferMaker thereIsOfferMaker(ProductCatalogFacade productCatalogFacade) {
        return new OfferMaker(productId -> {
            Product product = productCatalogFacade.getById(productId);

            return new ProductDetails(productId, product.getDescription(), product.getPrice());
        });
    }

    protected InMemoryBasketStorage thereIsBasketStorage() {
        return new InMemoryBasketStorage();
    }

    protected PaymentGateway thereIsPaymentGateway() {
        return new DummyPaymentGateway();
    }

    protected ProductCatalogFacade thereIsProductCatalog() {
        return new ProductCatalogConfiguration().productCatalogFacade();
    }

    protected String thereIsCustomerWhoIsDoingSomeShoping() {
        customerId = UUID.randomUUID().toString();
        return new String(customerId);
    }

    protected String thereIsProductAvailable() {
        var id = productCatalog.createProduct();
        productCatalog.applyPrice(id, BigDecimal.valueOf(10));
        productCatalog.updateProductDetails(id, "desc", "http://image");
        return id;
    }

    protected ReservationRepository thereIsInMemoryReservationsRepository() {
        return new InMemoryReservationRepository();
    }

    protected SalesFacade thereIsSalesModule() {
        return new SalesFacade(
                productCatalog,
                basketStorage,
                currentCustomerContext,
                inventory,
                offerMaker,
                paymentGateway,
                reservationRepository
        );
    }
}
