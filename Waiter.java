import java.util.Random;

public class Waiter implements Runnable {
    private Kitchen kitchen;

    public Waiter(Kitchen kitchen, Order order) {
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        Random random = new Random();
        int maxOrders = 1;  // Limit the total number of orders
        for (int i = 0; i < maxOrders; i++) {
            Order order = new Order();
            // Create a random order with Steamed Egg and Omelette
            int steamedEggCount = random.nextInt(5) + 1; // Random quantity between 1 and 5
            int omeletteCount = random.nextInt(5) + 1;   // Random quantity between 1 and 5

            order.addDish("SteamedEgg", steamedEggCount);
            order.addDish("Omelette", omeletteCount);

            System.out.println("Waiter created: " + order);

            // Submit the order to the kitchen
            kitchen.processOrder(order);

            try {
                // Generate a random sleep duration between 0ms and 3000ms (3 seconds)
                int sleepDuration = random.nextInt(3000);
                Thread.sleep(sleepDuration);  // Wait before creating the next order
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
