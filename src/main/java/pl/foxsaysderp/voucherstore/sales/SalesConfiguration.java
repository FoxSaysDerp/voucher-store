package pl.foxsaysderp.voucherstore.sales;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.foxsaysderp.payu.http.JavaHttpPayUApiClient;
import pl.foxsaysderp.payu.PayU;
import pl.foxsaysderp.payu.PayUCredentials;
import pl.foxsaysderp.voucherstore.productcatalog.ProductCatalogFacade;
import pl.foxsaysderp.voucherstore.sales.basket.InMemoryBasketStorage;
import pl.foxsaysderp.voucherstore.sales.offer.OfferMaker;
import pl.foxsaysderp.voucherstore.sales.ordering.ReservationRepository;
import pl.foxsaysderp.voucherstore.sales.payment.PayUPaymentGateway;
import pl.foxsaysderp.voucherstore.sales.payment.PaymentGateway;
import pl.foxsaysderp.voucherstore.sales.product.ProductCatalogProductDetailsProvider;
import pl.foxsaysderp.voucherstore.sales.product.ProductDetailsProvider;

@Configuration
public class SalesConfiguration {

    @Bean
    SalesFacade salesFacade(ProductCatalogFacade productCatalogFacade, OfferMaker offerMaker, PaymentGateway paymentGateway, ReservationRepository reservationRepository) {
        return new SalesFacade(
                productCatalogFacade,
                new InMemoryBasketStorage(),
                () -> "customer_1",
                (productId) -> true,
                offerMaker,
                paymentGateway,
                reservationRepository);
    }

    @Bean
    PaymentGateway payUPaymentGateway() {
        return new PayUPaymentGateway(new PayU(
                PayUCredentials.productionOfEnv(),
                new JavaHttpPayUApiClient()
        ));
    }

    @Bean
    OfferMaker offerMaker(ProductDetailsProvider productDetailsProvider) {
        return new OfferMaker(productDetailsProvider);
    }

    @Bean
    ProductDetailsProvider productDetailsProvider(ProductCatalogFacade productCatalogFacade) {
        return new ProductCatalogProductDetailsProvider(productCatalogFacade);
    }
}
