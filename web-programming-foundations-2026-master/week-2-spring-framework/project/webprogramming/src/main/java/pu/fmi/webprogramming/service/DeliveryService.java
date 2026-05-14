package pu.fmi.webprogramming.service;

import org.springframework.stereotype.Service;
import pu.fmi.webprogramming.model.Courier;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.Warehouse;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.CourierRepository;
import pu.fmi.webprogramming.repository.DeliveryRepository;
import pu.fmi.webprogramming.repository.WarehouseRepository;

import java.time.LocalDateTime;

@Service
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final CourierRepository courierRepository;
  private final WarehouseRepository warehouseRepository;
  private final DeliveryEstimator deliveryEstimator;

  public DeliveryService(
          DeliveryRepository deliveryRepository,
          CourierRepository courierRepository,
          WarehouseRepository warehouseRepository,
          DeliveryEstimator deliveryEstimator) {
    this.deliveryRepository = deliveryRepository;
    this.courierRepository = courierRepository;
    this.warehouseRepository = warehouseRepository;
    this.deliveryEstimator = deliveryEstimator;
  }

  public Delivery createDelivery(Customer customer) {
    Warehouse warehouse = warehouseRepository.findByCustomerCity(customer);
    Courier courier = courierRepository.findAvailableCourier();

    Delivery delivery = new Delivery();
    delivery.setCreatedAt(LocalDateTime.now());
    delivery.setCustomer(customer);
    delivery.setWarehouse(warehouse);
    delivery.setCourier(courier);

    if (courier != null) {
      delivery.setDeliveryStatus(DeliveryStatusEnum.ASSIGNED);
    } else {
      delivery.setDeliveryStatus(DeliveryStatusEnum.CREATED);
    }

    delivery.setEstimatedArrivalAt(deliveryEstimator.estimateArrivalTime(delivery));

    return delivery;
  }
}