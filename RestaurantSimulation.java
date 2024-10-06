public class RestaurantSimulation {
    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Inventory inventory = new Inventory();

        // Add some ingredients to the inventory
        inventory.addIngredient("egg", 5);
        inventory.addIngredient("milk", 3);
        inventory.addIngredient("butter", 2);
        inventory.addIngredient("water", 2);
        inventory.addIngredient("salt", 2);

        // Add dishes to the kitchen
        kitchen.addDish(new SteamedEgg());
        kitchen.addDish(new Omelette());

        // Display the initial inventory
        inventory.displayInventory();

        // Create and start the Chef (in a separate thread)
        Chef chef = new Chef(kitchen, inventory);
        Thread chefThread = new Thread(chef);
        chefThread.start();

        // Wait for the chef to finish
        try {
            chefThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Display the final inventory
        inventory.displayInventory();
    }
}
