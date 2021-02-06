package pl.foxsaysderp.voucherstore.sales;

import pl.foxsaysderp.voucherstore.productcatalog.Product;
import pl.foxsaysderp.voucherstore.productcatalog.ProductCatalogFacade;
import pl.foxsaysderp.voucherstore.sales.basket.Basket;
import pl.foxsaysderp.voucherstore.sales.basket.InMemoryBasketStorage;
import pl.foxsaysderp.voucherstore.sales.offer.Offer;
import pl.foxsaysderp.voucherstore.sales.offer.OfferMaker;
import pl.foxsaysderp.voucherstore.sales.ordering.ClientData;
import pl.foxsaysderp.voucherstore.sales.ordering.OfferChangedException;
import pl.foxsaysderp.voucherstore.sales.ordering.Reservation;
import pl.foxsaysderp.voucherstore.sales.ordering.ReservationRepository;
import pl.foxsaysderp.voucherstore.sales.payment.PaymentDetails;
import pl.foxsaysderp.voucherstore.sales.payment.PaymentGateway;
import pl.foxsaysderp.voucherstore.sales.payment.PaymentUpdateStatusRequest;
import pl.foxsaysderp.voucherstore.sales.payment.PaymentVerificationException;

public class SalesFacade {

    ProductCatalogFacade productCatalogFacade;
    InMemoryBasketStorage basketStorage;
    CurrentCustomerContext currentCustomerContext;
    Inventory inventory;
    OfferMaker offerMaker;
    PaymentGateway paymentGateway;
    private final ReservationRepository reservationRepository;

    public SalesFacade(ProductCatalogFacade productCatalogFacade, InMemoryBasketStorage basketStorage, CurrentCustomerContext currentCustomerContext, Inventory inventory, OfferMaker offerMaker, PaymentGateway paymentGateway, ReservationRepository reservationRepository) {
        this.productCatalogFacade = productCatalogFacade;
        this.basketStorage = basketStorage;
        this.currentCustomerContext = currentCustomerContext;
        this.inventory = inventory;
        this.offerMaker = offerMaker;
        this.paymentGateway = paymentGateway;
        this.reservationRepository = reservationRepository;
    }

    public void addProduct(String productId1) {
        Product product = productCatalogFacade.getById(productId1);
        Basket basket = basketStorage.loadForCustomer(getCurrentCustomerId())
                .orElse(Basket.empty());

        basket.add(product, inventory);

        basketStorage.addForCustomer(getCurrentCustomerId(), basket);
    }

    private String getCurrentCustomerId() {
        return currentCustomerContext.getCurrentCustomerId();
    }

    public Offer getCurrentOffer() {
        Basket basket = basketStorage.loadForCustomer(getCurrentCustomerId())
                .orElse(Basket.empty());
        return offerMaker.calculateOffer(basket.getBasketItems());
    }

    public PaymentDetails acceptOffer(Offer seenOffer, ClientData clientData) {
        Basket basket = basketStorage.loadForCustomer(getCurrentCustomerId())
                .orElse(Basket.empty());

        Offer currentOffer = offerMaker.calculateOffer(basket.getBasketItems());

        if (!seenOffer.isEqual(currentOffer)) {
            throw new OfferChangedException();
        }

        Reservation reservation = Reservation.of(currentOffer, clientData);

        PaymentDetails reservationPaymentDetails = paymentGateway.register(reservation);

        reservation.fillWithPayment(reservationPaymentDetails);

        reservationRepository.save(reservation);

        return  reservationPaymentDetails;
    }

    public void handlePaymentStatusChanged(PaymentUpdateStatusRequest paymentUpdateStatusRequest) {
        if (!paymentGateway.isTrusted(paymentUpdateStatusRequest)) {
            throw new PaymentVerificationException();
        }
    }
}
