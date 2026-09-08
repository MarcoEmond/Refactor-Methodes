package main.service;

import main.domain.Customer;
import main.domain.Planet;
import main.domain.Shipment;

import java.time.LocalDate;

public class PricingService {
    public double increaseByFivePercent(double price) { return price + price * 0.05; }
    public double increaseByTenPercent(double price) { return price + price * 0.10; }
    public double increaseByTwentyPercent(double price) { return price + price * 0.20; }

    // a fix was made here , there was like 1 million arguments
    public double calculatePrice(double weight, double declaredValue, boolean hazardous, Shipment shipment) {
        double result = weight * 2.25;
        if (declaredValue > 10000) result += declaredValue * 0.015;
        if (hazardous) result = increaseByTwentyPercent(result);
        if (shipment.getOrigin().getSecurityLevel() >= 4 || shipment.getDestination().getSecurityLevel() >= 4) result += 125;
        if (!shipment.getDestination().getSector().equals(shipment.getOrigin().getSector())) result += 80;
        if (shipment.getDepartureDate().getMonthValue() == 12 || shipment.getDepartureDate().getMonthValue() <= 2) result += 45;
        if (shipment.getCustomer().getLoyaltyYears() >= 5 && shipment.getCustomer().isActive() && !shipment.getCustomer().isSuspended()) result *= 0.90;
        return result;
    }

    public double calculateInsurance(double value, boolean hazardous, Customer customer) {
        value = value * 0.02;
        if (hazardous) value += 75;
        if (customer.getLoyaltyYears() >= 10) value -= 10;
        return Math.max(value, 0);
    }

    public String priceCategory(double price) {
        double temporaryPrice = price;
        return temporaryPrice > 1000 ? "HIGH" : "STANDARD";
    }

    public double calculateRouteSurcharge(Planet origin, Planet destination) {
        int temporary = origin.getSecurityLevel() + destination.getSecurityLevel();
        double surcharge = temporary * 12.5;
        temporary = origin.getSector().equals(destination.getSector()) ? 0 : 1;
        surcharge += temporary * 80;
        return surcharge;
    }

    public Object pricingSummary(double total) {
        return total >= 2000 ? "PRIORITY" : "REGULAR";
    }
}
