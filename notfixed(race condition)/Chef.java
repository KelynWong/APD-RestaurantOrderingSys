import java.util.concurrent.atomic.AtomicInteger;

public class Chef implements Runnable {
    private Kitchen kitchen;
    private Inventory inventory;
    private static final int MAX_ATTEMPTS = 5;
    private static AtomicInteger activeChefsCount = new AtomicInteger(0);

    public Chef(Kitchen kitchen, Inventory inventory) {
        this.kitchen = kitchen;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        activeChefsCount.incrementAndGet();
        while (true) {
            Dish dish = kitchen.getDishToMake();  // Race condition occurs here
            if (dish == null) {
                if (kitchen.noMoreDishesToMake()) {
                    System.out.println(Thread.currentThread().getName() + " is done.");
                    break;
                }
                continue;
            }

            // Log when the dish is picked, showing how many chefs have picked it
            int timesPicked = dish.incrementTimesPicked();
            System.out.println(Thread.currentThread().getName() + " is preparing: " + dish.getClass().getSimpleName() + " (picked " + timesPicked + " times)");

            boolean prepared = false;
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                if (inventory.useIngredientsForDish(dish)) {
                    prepared = true;
                    break;
                }
                System.out.println(Thread.currentThread().getName() + " failed to prepare " + dish.getClass().getSimpleName() + ". Attempt " + (attempt + 1) + " of " + MAX_ATTEMPTS);
            }

            if (prepared) {
                System.out.println(Thread.currentThread().getName() + ": " + dish.getClass().getSimpleName() + " is ready!");
                kitchen.markDishAsMade(dish);
            } else {
                System.out.println(Thread.currentThread().getName() + " gave up on preparing " + dish.getClass().getSimpleName() + " after " + MAX_ATTEMPTS + " attempts.");
                kitchen.markDishAsAbandoned(dish);
            }
        }
        activeChefsCount.decrementAndGet();
    }

    private boolean tryPrepareDish(Dish dish) {
        for (String ingredient : dish.getIngredients().keySet()) {
            if (dish.getIngredients().get(ingredient) == 0) {
                if (!inventory.useIngredient(ingredient)) {
                    return false;
                }
                dish.addIngredient(ingredient);
                System.out.println(Thread.currentThread().getName() + " added " + ingredient + " to " + dish.getClass().getSimpleName());
            }
        }
        return true;
    }

    public static boolean areAllChefsDone() {
        return activeChefsCount.get() == 0;
    }
}
