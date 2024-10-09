class Waiter implements Runnable {
    private int waiterId;
    private Kitchen kitchen;
    private static final int MAX_SERVING_ATTEMPTS = 800;
    private static final int SERVING_WAIT_TIME = 10;  // Wait 0.01 second between checks

    public Waiter(int waiterId, Kitchen kitchen) {
        this.waiterId = waiterId;
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        // Each waiter creates an order and submits it to the kitchen
        Order order = new Order();
        order.addDish("SteamedEgg", 15);
        order.addDish("Omelette", 15);

        // Submitting the dishes to the kitchen
        for (String dishName : order.getDishes().keySet()) {
            for (int j = 0; j < order.getDishes().get(dishName); j++) {
                if (dishName.equals("SteamedEgg")) {
                    kitchen.addDishToMake(new SteamedEgg());
                } else if (dishName.equals("Omelette")) {
                    kitchen.addDishToMake(new Omelette());
                }
            }
        }
        System.out.println("Waiter " + waiterId + " submitted order: " + order);

        // Waiter serves the made dishes after submitting the order
        serveDishes();
    }

    private void serveDishes() {
        int attempts = 0;
        while (attempts < MAX_SERVING_ATTEMPTS) {
            Dish dish = kitchen.getMadeDishToServe();
            if (dish != null) {
                System.out.println("Waiter " + waiterId + " served: " + dish.getClass().getSimpleName());
                kitchen.markDishAsServed(dish);
            } else {
                attempts++;
                try {
                    Thread.sleep(SERVING_WAIT_TIME);  // Wait before checking again
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        System.out.println("Waiter " + waiterId + " finished serving after " + attempts + " attempts.");
    }
}
