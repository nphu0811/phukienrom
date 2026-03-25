package com.example.demo.controller.web;

import com.example.demo.domain.entity.Order;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class StaffWebController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public StaffWebController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/staff/orders";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) OrderStatus status,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        model.addAttribute("orders", orderService.getAllOrders(status, pageable));
        model.addAttribute("status", status);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        return "staff/orders";
    }

    @GetMapping("/orders/{id}")
    @Transactional(readOnly = true)
    public String orderDetail(@PathVariable Long id, Model model) {
        try {
            Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
            order.getItems().size();
            model.addAttribute("order", order);
            model.addAttribute("statuses", OrderStatus.values());
            model.addAttribute("pageTitle", "Chi tiết đơn hàng " + order.getOrderCode());
            return "staff/order-detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Đơn hàng không tồn tại ID: " + id);
            return "redirect:/staff/orders?error=order_not_found";
        }
    }
}
