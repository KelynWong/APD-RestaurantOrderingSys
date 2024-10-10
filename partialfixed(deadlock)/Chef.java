import java.util.concurrent.atomic.AtomicInteger;

public class Chef implements Runnable {
    private Kitchen kitchen;
    private Inventory inventory;
    private int chefId;  
    private int maxAttempts;
    private int maxEmptyChecks;
    private int sleepTime;
    private static AtomicInteger activeChefsCount = new AtomicInteger(0);

    public Chef(int chefId, Kitchen kitchen, Inventory inventory) {
        this.chefId = chefId; 
        this.kitchen = kitchen;
        this.inventory = inventory;

        // Load configuration values
        Config config = Config.getInstance();
        this.maxAttempts = config.getIntValue("MAX_ATTEMPTS");
        this.maxEmptyChecks = config.getIntValue("MAX_EMPTY_CHECKS");
        this.sleepTime = config.getIntValue("SLEEP_TIME");
    }

    @Override
    public void run() {
        activeChefsCount.incrementAndGet();
        int emptyCheckCount = 0;

        while (true) {
            Dish dish = kitchen.getDishToMake();

            if (dish == null) {
                emptyCheckCount++;
                if (emptyCheckCount >= maxEmptyChecks) {
                    System.out.println("Chef " + chefId + " has waited " + maxEmptyChecks + " times without receiving a dish. Chef is terminating.");
                    break;
                }
                try {
                    Thread.sleep(sleepTime);  // Wait before checking again
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            emptyCheckCount = 0;  // Reset empty check counter if a dish is found

            System.out.println("Chef " + chefId + " is preparing: " + dish.getClass().getSimpleName() + " (item: " + dish.hashCode() + ")");

            boolean prepared = false;
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                if (inventory.useIngredientsForDish(dish)) {
                    prepared = true;
                    break;
                }
                System.out.println("Chef " + chefId + " failed to prepare " + dish.getClass().getSimpleName() + " (item: " + dish.hashCode() + ")" + ". Attempt " + (attempt + 1) + " of " + maxAttempts);
            }

            if (prepared) {
                System.out.println("Chef " + chefId + ": " + dish.getClass().getSimpleName() + " (item: " + dish.hashCode() + ")" + " is ready!");
                kitchen.markDishAsMade(dish);
            } else {
                System.out.println("Chef " + chefId + " gave up on preparing " + dish.getClass().getSimpleName() + " after " + maxAttempts + " attempts.");
                kitchen.markDishAsAbandoned(dish);
            }
        }

        activeChefsCount.decrementAndGet();
    }

    public static boolean areAllChefsDone() {
        return activeChefsCount.get() == 0;
    }
}
