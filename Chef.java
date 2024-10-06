// public class Chef implements Runnable {  // Runnable to allow multithreading
//     private Kitchen kitchen;
//     private Inventory inventory;

//     public Chef(Kitchen kitchen, Inventory inventory) {
//         this.kitchen = kitchen;
//         this.inventory = inventory;
//     }

//     @Override
//     public void run() {
//         while (true) {
//             Dish dish = kitchen.getDishToMake();  // Get a dish to prepare
//             if (dish == null) {
//                 System.out.println("No more dishes to make, Chef is done.");
//                 break;
//             }

//             System.out.println("Chef is preparing: " + dish.getClass().getSimpleName());
//             for (String ingredient : dish.getIngredients().keySet()) {
//                 while (dish.getIngredients().get(ingredient) == 0) {  // While ingredient is not prepared
//                     if (inventory.useIngredient(ingredient)) {
//                         dish.addIngredient(ingredient);  // Add ingredient to the dish
//                         System.out.println("Added " + ingredient + " to " + dish.getClass().getSimpleName());
//                     } else {
//                         System.out.println("Insufficient " + ingredient + " for " + dish.getClass().getSimpleName());
//                         return;  // Exit if the ingredient is not available
//                     }
//                 }
//             }

//             // Mark dish as Made once all ingredients are added
//             if (dish.getState().equals("Made")) {
//                 System.out.println(dish.getClass().getSimpleName() + " is ready!");
//                 kitchen.markDishAsMade(dish);
//             }
//         }
//     }
// }

public class Chef implements Runnable {  // Runnable to allow multithreading
    private Kitchen kitchen;
    private Inventory inventory;

    public Chef(Kitchen kitchen, Inventory inventory) {
        this.kitchen = kitchen;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            Dish dish = kitchen.getDishToMake();  // Get a dish to prepare
            if (dish == null) {
                System.out.println("No more dishes to make, Chef is done.");
                break;
            }

            System.out.println("Chef is preparing: " + dish.getClass().getSimpleName());
            boolean allIngredientsAvailable = true;  // Track if all ingredients can be added
            
            for (String ingredient : dish.getIngredients().keySet()) {
                while (dish.getIngredients().get(ingredient) == 0) {  // While ingredient is not prepared
                    if (inventory.useIngredient(ingredient)) {
                        dish.addIngredient(ingredient);  // Add ingredient to the dish
                        System.out.println("Added " + ingredient + " to " + dish.getClass().getSimpleName());
                    } else {
                        System.out.println("Insufficient " + ingredient + " for " + dish.getClass().getSimpleName());
                        allIngredientsAvailable = false; // Track if we can't finish the dish
                        break; // Exit the while loop for this ingredient
                    }
                }
                if (!allIngredientsAvailable) {
                    break; // Exit the for loop if an ingredient is missing
                }
            }

            // If all ingredients are available, mark the dish as Made
            if (allIngredientsAvailable && dish.getState().equals("toMake")) {
                System.out.println(dish.getClass().getSimpleName() + " is ready!");
                kitchen.markDishAsMade(dish);
            }
        }
    }
}

