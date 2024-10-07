class Waiter implements Runnable {
    private int waiterId;
    private Kitchen kitchen;

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
    }
}
