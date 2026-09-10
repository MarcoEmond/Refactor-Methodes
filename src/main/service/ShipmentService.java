package main.service;

import main.domain.Cargo;
import main.domain.Shipment;

public class ShipmentService {
    private final PricingService pricingService;
    private final PermissionService permissionService;
    private final ManifestRepository repository;
    private final NotificationService notificationService;

    public ShipmentService(PricingService pricingService, PermissionService permissionService,
                           ManifestRepository repository, NotificationService notificationService) {
        this.pricingService = pricingService;
        this.permissionService = permissionService;
        this.repository = repository;
        this.notificationService = notificationService;
    }


    public String validateCalculatePrintSaveAndNotify(Shipment shipment) {

        // fix made here , 3 if to 1 if with every condition
        if (shipment.getCustomer().isActive() && !shipment.getCustomer().isSuspended() && !shipment.getCargo().isEmpty()) {
            double totalWeight = 0;
            double totalValue = 0;
            boolean hazardous = false;
            for (Cargo item : shipment.getCargo()) {
                totalWeight += item.getWeight();
                totalValue += item.getDeclaredValue();
                if (item.isHazardous()) hazardous = true;
            }
            if (totalWeight > shipment.getShip().getCapacity()) return "ERROR_CAPACITY";
            if (hazardous && !permissionService.canCarryHazardous(shipment.getShip())) return "ERROR_PERMISSION";

            double total = pricingService.calculatePrice(totalWeight, totalValue, hazardous, shipment);
            total += pricingService.calculateInsurance(totalValue, hazardous, shipment.getCustomer());

            shipment.setTotal(total);
            shipment.setStatus("READY");

        } else if (shipment.getCargo().isEmpty()){
            return "ERROR CARGO IS EMPTY";

        } else if (shipment.getCustomer().isSuspended()){
            return "ERROR CUSTOMER IS SUSPENDED";

        } else if (!shipment.getCustomer().isActive()){
            return "ERROR CUSTOMER IS NOT ACTIVE";
        }

        return "ERROR METHOD IS NOT METHODING";
    }

    public String NotifyShipmentTotal (Shipment shipment) {
        String output;
        if (shipment.getTotal() > 2000) {
            output = "PRIORITY | " + shipment.getReference() + " | " + String.format("%.2f", shipment.getTotal());
            repository.save(shipment);
            output += " | " + notificationService.confirmationFor(shipment);
        } else {
            output = "REGULAR | " + shipment.getReference() + " | " + String.format("%.2f", shipment.getTotal());
            repository.save(shipment);
            output += " | " + notificationService.confirmationFor(shipment);
        }
        return output;
    }
}
