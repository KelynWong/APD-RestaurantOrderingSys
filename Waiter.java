import java.util.Random;

public class Waiter implements Runnable {
    private Kitchen kitchen;

    public Waiter(Kitchen kitchen) {
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        Random random = new Random();
        Order order = new Order();
        
        // Create a random order with Steamed Egg and Omelette
        int steamedEggCount = random.nextInt(5) + 1; // Random quantity between 1 and 5
        int omeletteCount = random.nextInt(5) + 1;   // Random quantity between 1 and 5

        order.addDish("SteamedEgg", steamedEggCount);
        order.addDish("Omelette", omeletteCount);

        System.out.println("Waiter created: " + order);

        // Submit the order to the kitchen
        kitchen.processOrder(order);
        while (true) {
            if (kitchen.isOrderComplete(order)) {  // Check without synchronization
                System.out.println("Order complete! Waiter is done.");
                break;
            }

            // Simulate some delay or waiting period
            try {
                Thread.sleep(100);  // Waiter keeps checking the kitchen every 100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
