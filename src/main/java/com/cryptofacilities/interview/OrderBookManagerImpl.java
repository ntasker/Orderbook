package com.cryptofacilities.interview;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CF-8 on 6/27/2017.
 */
public class OrderBookManagerImpl implements OrderBookManager {

    //Sorts by lowest for asks and by highest for bids
    private Comparator<OrderLevelKey> compareLevels = new Comparator<OrderLevelKey>() {
        @Override
        public int compare(OrderLevelKey o1, OrderLevelKey o2) {

            if (o1.getSide().equals(o2.getSide()) &&
                    o1.getPrice().equals(o2.getPrice())) return 0;

            else if (o1.getSide().equals(Side.buy)) {
                return o1.getPrice() > o2.getPrice() ? -1 : 1;
            }
            else {
                return o1.getPrice() > o2.getPrice() ? 1 : -1;
            }

        }
    };

    //Sorts orders as they are input, to maintain insertion order
    private Comparator<Order> compareOrders = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getTimeCreated() == o2.getTimeCreated()) return 0;
            else return o1.getTimeCreated() > o2.getTimeCreated() ? 1 : -1;
        }
    };

    //Map orderIds to correct level
    private Map<String, OrderLevelKey> ordersToLevels;
    //Store orders by price level
    private Map<OrderLevelKey, Set<Order>> orders;

    public OrderBookManagerImpl(){
        ordersToLevels = new HashMap<>();
        orders = new TreeMap(compareLevels);
    }

    public void addOrder(Order order) {

        Long price = order.getPrice();
        Side side = order.getSide();
        String orderID = order.getOrderId();
        OrderLevelKey orderLevelKey = new OrderLevelKey(price, side);
        Set<Order> priceLevel = orders.get(orderLevelKey);

        if (priceLevel == null || priceLevel.size() == 0) {
            priceLevel = new TreeSet(compareOrders);
        }

        priceLevel.add(order);
        orders.put(orderLevelKey,priceLevel);

        if (ordersToLevels.get(orderID) == null || priceLevel.size() == 0) {
            ordersToLevels.put(orderID,
                    new OrderLevelKey(price, side));
        }
    }

    public void modifyOrder(String orderId, long newQuantity) {

        try {

            Set<Order> ordersFromLevel = orders.get(ordersToLevels.get(orderId));
            if (ordersFromLevel == null) throw new Exception("Order not found 1 ");

            for (Order order : ordersFromLevel) {
                if (order.getOrderId().equals(orderId)) {
                    //placing order at end if quantity has been increased
                    if(order.getQuantity() < newQuantity) {
                        order.setQuantity(newQuantity);
                        ordersFromLevel.remove(order);
                        order.newTimeStamp();
                        ordersFromLevel.add(order);
                        return;
                    } else {
                        //Order maintains position if quanitity has decresed
                        order.setQuantity(newQuantity);
                        return;
                    }
                }
            }

            throw new Exception();
        }
        catch(Exception e){
            System.err.println("Order " + "ID#" + orderId + " not found. Therefore could not be modified");
        }
    }

    public void deleteOrder(String orderId) {
        try {
            Set<Order> ordersFromLevel = orders.get(ordersToLevels.get(orderId));
            for (Order order : ordersFromLevel) {

                if (order.getOrderId().equals(orderId)) {
                    ordersFromLevel.remove(order);
                    ordersToLevels.remove(orderId);
                    return;
                }
            }
                throw new Exception();
        }

        catch(Exception e){
                System.err.println("Order " + "ID#" + orderId + " not found. Therefore could not be deleted");
        }
    }

    public long getBestPrice(String instrument, Side side) {

        if (instrument == "") {
            return -1;
        }

        List<Order> allOrdersList  = new ArrayList<>();

        for (Set<Order> order : orders.values()) {
            allOrdersList.addAll(order);
        }

        switch (side) {
            case buy:
                List<Order> buyList = new ArrayList<>();

                for (Set<Order> order : orders.values()) {
                    allOrdersList.addAll(order);
                }

                for(Order order : allOrdersList) {
                    if((order.getInstrument().equals(instrument)) && (order.getSide().equals(Side.buy))) {
                        return order.getPrice();

                    }
                }

                if (buyList.size() == 0) {
                    return -1;
                }

            case sell:
                List<Order> sellList = new ArrayList<>();

                for (Set<Order> order : orders.values()) {
                    allOrdersList.addAll(order);
                }

                for(Order order : allOrdersList) {
                    if((order.getInstrument().equals(instrument)) && (order.getSide().equals(Side.sell))) {
                        return order.getPrice();

                    }
                }

                if (sellList.size() == 0) {
                    return -1;
                }
            }

        return -1;
    }

    public long getOrderNumAtLevel(String instrument, Side side, long price) {
        Set<Order> ordersFromLevel = orders.get(new OrderLevelKey(price, side));
        long count = 0;

        if (ordersFromLevel != null) {
            for (Order order : ordersFromLevel) {
                if (order.getInstrument().equals(instrument)) {
                    count++;
                }
            }
        }

        if(count > 0){
            return count;
        }
        else {
            return -1;
        }
    }

    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {

        Set<Order> orderSet = orders.get(new OrderLevelKey(price, side));

        if (orderSet == null) {
            return -1;
        }

        long totalQuantity = 0;

        for (Order order : orderSet) {

            if(order.getInstrument().equals(instrument) && order.getSide().equals(side)) {
                totalQuantity = totalQuantity + order.getQuantity();
            }
        }

        if (totalQuantity == 0) {
            return -1;
        }
        return totalQuantity;

    }

    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {

        Set<Order> orderSet = orders.get(new OrderLevelKey(price, side));

        long totalVolume = 0;

        if (orderSet == null) {
            return -1;
        }

        for (Order order : orderSet) {

            if(order.getInstrument().equals(instrument) && order.getSide().equals(side)) {
                totalVolume = totalVolume + order.getQuantity() * order.getPrice();
            }
        }

        if (totalVolume == 0) {
            return -1;
        }
        return totalVolume;
    }

    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {

        Set<Order> orderSet = orders.get(new OrderLevelKey(price, side));

        ArrayList orderList = new ArrayList();

        for (Order order : orderSet) {
            if(order.getInstrument().equals(instrument) && order.getSide().equals(side)) {
                orderList.add(order);
            }
        }

        return orderList;
    }


    class OrderLevelKey {

        private Long price;
        private Side side;

        public OrderLevelKey(Long price, Side side) {
            this.price = price;
            this.side = side;
        }

        public Side getSide() {
            return side;
        }

        public Long getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OrderLevelKey)) return false;
            OrderLevelKey orderLevelKey = (OrderLevelKey) o;
            return getPrice().equals(orderLevelKey.getPrice()) &&
                    getSide() == orderLevelKey.getSide();
        }

        @Override
        public int hashCode() {
            int hash = getPrice() != null ? getPrice().hashCode() : 0;
            hash = 31 * hash + (getSide() != null ? getSide().hashCode() : 0);
            return hash;
        }
    }
}
