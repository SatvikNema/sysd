package com.satvik.oms.service;

import com.satvik.oms.entity.Order;
import com.satvik.oms.entity.Payment;

public interface IPaymentService {
    Payment debit(Order order);
}
