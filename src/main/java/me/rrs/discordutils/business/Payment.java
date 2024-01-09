package me.rrs.discordutils.business;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;

import java.io.IOException;
import java.util.List;

public class Payment {

    public void onRequest(String value, String description) {
        String clientId = "";
        String clientSecret = "";

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode("USD")
                .value(value); // Set the total amount

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(amount)
                .description(description);

        // Additional setup for items, redirect URLs, etc.

        OrderRequest orderRequest = new OrderRequest()
                .checkoutPaymentIntent("CAPTURE") // Specify the payment intent
                .purchaseUnits(List.of(purchaseUnit));

        // Additional setup for payer, redirect URLs, etc.

        try {
            // Set up the PayPal HTTP client
            PayPalHttpClient client = new PayPalHttpClient(
                    new PayPalEnvironment.Sandbox(clientId, clientSecret)
            );

            // Create the order
            OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
            HttpResponse<Order> response = client.execute(request);

            // Get the approval URL from the response
            String approvalUrl = response.result().links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No approve link found"))
                    .href();

            // Redirect the buyer to the approval URL
            System.out.println("Redirect the buyer to: " + approvalUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}