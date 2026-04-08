package com.washplatform.service.impl;

import com.washplatform.entity.Order;
import com.washplatform.entity.OrderDetail;
import com.washplatform.repository.OrderRepository;
import com.washplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (order.getShop() != null) {
                order.setShopName(order.getShop().getName());
            }
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    if (detail.getService() != null) {
                        detail.setServiceName(detail.getService().getName());
                    }
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> findByCustomerId(Integer customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        // 设置shopName字段
        for (Order order : orders) {
            if (order.getShop() != null) {
                order.setShopName(order.getShop().getName());
            }
            // 设置orderDetails的serviceName字段
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    if (detail.getService() != null) {
                        detail.setServiceName(detail.getService().getName());
                    }
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> findByShopId(Integer shopId) {
        List<Order> orders = orderRepository.findByShopId(shopId);
        // 设置shopName字段
        for (Order order : orders) {
            if (order.getShop() != null) {
                order.setShopName(order.getShop().getName());
            }
            // 设置orderDetails的serviceName字段
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    if (detail.getService() != null) {
                        detail.setServiceName(detail.getService().getName());
                    }
                }
            }
        }
        return orders;
    }

    @Override
    public Order findById(Integer id) {
        try {
            System.out.println("Finding order by id: " + id);
            Order order = orderRepository.findById(id).orElse(null);
            System.out.println("Order found: " + order);
            // 设置shopName字段
            if (order != null && order.getShop() != null) {
                order.setShopName(order.getShop().getName());
            }
            // 设置orderDetails的serviceName字段
            if (order != null && order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    if (detail.getService() != null) {
                        detail.setServiceName(detail.getService().getName());
                    }
                }
            }
            return order;
        } catch (Exception e) {
            System.err.println("Error finding order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Order save(Order order) {
        Date now = new Date();
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(now);
        }
        if (order.getStatus() == null) {
            order.setStatus("ordered");
        }
        order.setUpdatedAt(now);
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> searchCustomerOrders(Integer customerId, String status, String phone) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByCustomerIdAndStatusContaining(customerId, status);
        } else if (phone != null && !phone.isEmpty()) {
            orders = orderRepository.findByCustomerIdAndContactPhoneContaining(customerId, phone);
        } else {
            orders = orderRepository.findByCustomerId(customerId);
        }
        // 设置shopName字段
        for (Order order : orders) {
            if (order.getShop() != null) {
                order.setShopName(order.getShop().getName());
            }
            // 设置orderDetails的serviceName字段
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    if (detail.getService() != null) {
                        detail.setServiceName(detail.getService().getName());
                    }
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> searchShopOrders(Integer shopId, String status, String name) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByShopIdAndStatusContaining(shopId, status);
        } else if (name != null && !name.isEmpty()) {
            orders = orderRepository.findByShopIdAndContactNameContaining(shopId, name);
        } else {
            orders = orderRepository.findByShopId(shopId);
        }
        // 设置shopName字段
        for (Order order : orders) {
            if (order.getShop() != null) {
                order.setShopName(order.getShop().getName());
            }
            // 设置orderDetails的serviceName字段
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    if (detail.getService() != null) {
                        detail.setServiceName(detail.getService().getName());
                    }
                }
            }
        }
        return orders;
    }
}