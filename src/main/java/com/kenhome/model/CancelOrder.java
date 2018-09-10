package com.kenhome.model;

/**
 * @Author: cmk
 * @Description:
 * @Date: 2018\8\4 0004 23:27
 */
public class CancelOrder {

    //订单id
    private Long orderId;

    //撤销到期时间
    private Long overLine;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOverLine() {
        return overLine;
    }

    public void setOverLine(Long overLine) {
        this.overLine = overLine;
    }
}
