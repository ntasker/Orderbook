package com.cryptofacilities.interview;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class SampleTest {

    @Test
    public void testAddingOrder() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        //initialize order
        Order buyOrder = new Order("1", "VOD.L", Side.buy, 102, 5);
        //add order to book
        orderBookManager.addOrder(buyOrder);
        long expectedOrderId = 1;
        long orderId = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 102);
        assertEquals("There is 1 order in the book", expectedOrderId, orderId);
    }

    @Test
    public void testModifyOrder() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        //initialize orders
        Order buyOrder1 = new Order("1", "VOD.L", Side.buy, 88, 10);
        Order buyOrder2 = new Order("2", "VOD.L", Side.buy, 45, 7);
        Order buyOrder3 = new Order("3", "VOD.L", Side.buy, 45, 12);
        Order buyOrder4 = new Order("4", "VOD.L", Side.buy, 39, 4);
        Order buyOrder5 = new Order("5", "VOD.L", Side.buy, 45, 25);
        Order buyOrder6 = new Order("6", "VOD.L", Side.buy, 45, 30);
        Order buyOrder7 = new Order("7", "VOD.L", Side.buy, 45, 12);


        //place orders
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(buyOrder5);
        orderBookManager.addOrder(buyOrder6);
        orderBookManager.addOrder(buyOrder7);


        long expectedNum = 5;
        long numOfOrders = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 45);
        assertEquals("There are 4 orders in the book, before any modification", expectedNum, numOfOrders);

        long newQuantity = 5;
        orderBookManager.modifyOrder("6", newQuantity);

        List<Order> orderList = orderBookManager.getOrdersAtLevel("VOD.L", Side.buy, 45);
        assertEquals("OrderID#6 is still in  position 3 within the level after quantity decrease",
                orderList.get(3).getOrderId(), "6");

        long totalQuantity = 61;
        long quantity = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 45);
        assertEquals(String.format("Quantity at level has been updated after modification"), totalQuantity, quantity);

        newQuantity = 55;
        orderBookManager.modifyOrder("3", newQuantity);
        orderList = orderBookManager.getOrdersAtLevel("VOD.L", Side.buy, 45);
        assertEquals("OrderID#3 placed at end of que in its level after quantity increase.",
                "3", orderList.get(orderList.size()-1).getOrderId());
    }

    @Test
    public void testDeleteOrder() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        //initialize order
        Order buy = new Order("1", "VOD.L", Side.buy, 120, 30);
        //add order to book
        orderBookManager.addOrder(buy);

        long expectedNum = 1;
        long numOfOrders = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 120);
        assertEquals("There is exactly 1 order before deletion", expectedNum, numOfOrders);
        //delete the order
        orderBookManager.deleteOrder("1");

        expectedNum = -1;
        numOfOrders = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 120);
        assertEquals("There are no orders after the deletion", expectedNum, numOfOrders);

    }


    @Test
    public void testHighestBidPrice() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        //initialize orders
        Order buyOrder = new Order("1", "VOD.L", Side.buy, 200, 7);
        Order buyOrder2 = new Order("2", "VOD.L", Side.buy, 300, 10);
        Order buyOrder3 = new Order("3", "VOD.L", Side.buy, 850, 10);
        Order buyOrder4 = new Order("4", "VOD.L", Side.buy, 500, 24);
        Order buyOrder5 = new Order("5", "VOD.L", Side.buy, 200, 100);

        //add orders to book
        orderBookManager.addOrder(buyOrder);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(buyOrder5);

        //check that highest price is 850
        long expectedPrice = 850;
        long actualPrice = orderBookManager.getBestPrice("VOD.L", Side.buy);
        assertEquals("The highest bid price for VOD.L is 850", expectedPrice, actualPrice);
    }

    @Test
    public void testLowestAskPrice() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        //initialize orders
        Order sellOrder = new Order("1", "VOD.L", Side.sell, 200, 29);
        Order sellOrder2 = new Order("2", "VOD.L", Side.sell, 300, 10);
        Order sellOrder3 = new Order("3", "VOD.L", Side.sell, 400, 49);
        Order sellOrder4 = new Order("4", "VOD.L", Side.sell, 100, 8);
        Order sellOrder5 = new Order("5", "VOD.L", Side.sell, 600, 20);

        //Add orders to book
        orderBookManager.addOrder(sellOrder);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);
        orderBookManager.addOrder(sellOrder5);


        //check that the lowest price is 100
        long expectedPrice = 100;
        long actualPrice = orderBookManager.getBestPrice("VOD.L", Side.sell);
        assertEquals("Lowest ask price is 100", expectedPrice, actualPrice);
    }


    @Test
    public void testTotalQuantityAtLevel() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        //initialize orders
        Order buyOrder = new Order("1", "VOD.L", Side.buy, 124, 8);
        Order buyOrder2 = new Order("2", "VOD.L", Side.buy, 124, 16);
        Order buyOrder3 = new Order("3", "VOD.L", Side.buy, 250, 20);
        Order buyOrder4 = new Order("4", "VOD.L", Side.buy, 88, 5);

        Order sellOrder = new Order("5", "VOD.L", Side.sell, 136, 7);
        Order sellOrder2 = new Order("6", "VOD.L", Side.sell, 121, 25);
        Order sellOrder3 = new Order("7", "VOD.L", Side.sell, 121, 30);
        Order sellOrder4 = new Order("8", "VOD.L", Side.sell, 121, 5);
        Order sellOrder5 = new Order("9", "VOD.L", Side.sell, 50, 3);

        //add orders to book
        orderBookManager.addOrder(buyOrder);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);

        orderBookManager.addOrder(sellOrder);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);
        orderBookManager.addOrder(sellOrder5);

        long expectedQuantity = -1;
        long actualQuantity = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 500);
        assertEquals("Total Quantity is 0 at this level", expectedQuantity, actualQuantity);

        expectedQuantity = 24;
        actualQuantity = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 124);
        assertEquals("Total Quantity is 24 at this level", expectedQuantity, actualQuantity);

        expectedQuantity = 60;
        actualQuantity = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.sell, 121);
        assertEquals("Total Quantity is 60 at this level",expectedQuantity, actualQuantity);
    }

    @Test
    public void testTotalTradeableVol() {
        //initialize order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        //initialize orders
        Order buyOrder = new Order("1", "VOD.L", Side.buy, 124, 8);
        Order buyOrder2 = new Order("2", "VOD.L", Side.buy, 124, 16);
        Order buyOrder3 = new Order("3", "VOD.L", Side.buy, 250, 20);
        Order buyOrder4 = new Order("4", "VOD.L", Side.buy, 88, 5);

        Order sellOrder = new Order("5", "VOD.L", Side.sell, 136, 7);
        Order sellOrder2 = new Order("6", "VOD.L", Side.sell, 121, 25);
        Order sellOrder3 = new Order("7", "VOD.L", Side.sell, 121, 30);
        Order sellOrder4 = new Order("8", "VOD.L", Side.sell, 121, 5);
        Order sellOrder5 = new Order("9", "VOD.L", Side.sell, 50, 3);

        //add orders to book
        orderBookManager.addOrder(buyOrder);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);

        orderBookManager.addOrder(sellOrder);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);
        orderBookManager.addOrder(sellOrder5);


        long expectedVolume = -1;
        long actualVolume = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.buy, 800);
        assertEquals("Total volume is 0 for this level", expectedVolume, actualVolume);

        expectedVolume = 7260;
        actualVolume = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.sell, 121);
        assertEquals("Total volume is 7260 for this level", expectedVolume, actualVolume);
    }


}
