package apd.proj.notfixed;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Kitchen {
    // Singleton instance
    private static Kitchen instance;

    private List<Dish> dishesToMake;  
    private List<Dish> madeDishes;
    private List<Dish> abandonedDishes;
    private List<Dish> servedDishes;
    
    private AtomicInteger totalDishesCount;
    private AtomicInteger totalAbandonedDishesCount;  

    private Map<String, AtomicInteger> toMakeDishCountMap;
    private Map<String, AtomicInteger> madeDishCountMap;
    private Map<String, AtomicInteger> servedDishCountMap;
    private Map<String, AtomicInteger> abandonedDishCountMap;

    private int sleepTime;

    // Set of all dish types
    private Set<String> allDishTypes;

    // Private constructor to prevent instantiation
    private Kitchen() {
        dishesToMake = new ArrayList<>();
        madeDishes = new ArrayList<>();
        abandonedDishes = new ArrayList<>();
        servedDishes = new ArrayList<>();
        totalDishesCount = new AtomicInteger(0);
        totalAbandonedDishesCount = new AtomicInteger(0);
        toMakeDishCountMap = new HashMap<>();
        madeDishCountMap = new HashMap<>();
        servedDishCountMap = new HashMap<>();
        abandonedDishCountMap = new HashMap<>();
        allDishTypes = new HashSet<>();

        // Load configuration values
        Config config = Config.getInstance();
        this.sleepTime = config.getIntValue("SLEEP_TIME");
    }

    // Method to get the single instance of Kitchen
    public static Kitchen getInstance() {
        if (instance == null) {
            instance = new Kitchen();
        }
        return instance;
    }
    
    public void addDishToMake(Dish dish) {
        dishesToMake.add(dish);  
        totalDishesCount.incrementAndGet();
        incrementDishCount(dish, toMakeDishCountMap);
        allDishTypes.add(dish.getClass().getSimpleName());  // Track the dish type
    }

    public Dish getDishToMake() {
        if (dishesToMake.isEmpty()) {
            return null;
        } else {
            try {
                Thread.sleep(sleepTime);  
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return dishesToMake.remove(0);
        } 
    }

    public void markDishAsMade(Dish dish) {
        dishesToMake.remove(dish);
        madeDishes.add(dish);
        incrementDishCount(dish, madeDishCountMap);
        allDishTypes.add(dish.getClass().getSimpleName());  // Track the dish type
    }

    public void markDishAsAbandoned(Dish dish) {
        dishesToMake.remove(dish);
        abandonedDishes.add(dish);
        totalAbandonedDishesCount.incrementAndGet();
        incrementDishCount(dish, abandonedDishCountMap);
        allDishTypes.add(dish.getClass().getSimpleName());  // Track the dish type
    }

    public void markDishAsServed(Dish dish) {
        madeDishes.remove(dish);
        servedDishes.add(dish);
        incrementDishCount(dish, servedDishCountMap);
        allDishTypes.add(dish.getClass().getSimpleName());  // Track the dish type
    }

    public Dish getMadeDishToServe() {
        if (madeDishes.isEmpty()) {
            return null;
        } else {
            try {
                Thread.sleep(sleepTime);  
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return madeDishes.remove(0);  
        }
    }

    public void displayDishHistory() {
        System.out.println("Total dishes to make: " + totalDishesCount.get());
        displayDishTypeCount("To-make dishes", toMakeDishCountMap);
        System.out.println();

        System.out.println("Total made dishes: " + getTotalDishCount(madeDishCountMap));
        displayDishTypeCount("Made dishes", madeDishCountMap);
        System.out.println();

        System.out.println("Total served dishes: " + getTotalDishCount(servedDishCountMap));
        displayDishTypeCount("Served dishes", servedDishCountMap);
        System.out.println();

        System.out.println("Total abandoned dishes: " + totalAbandonedDishesCount.get());
        displayDishTypeCount("Abandoned dishes", abandonedDishCountMap);
        System.out.println();
    }

    private void incrementDishCount(Dish dish, Map<String, AtomicInteger> dishCountMap) {
        String dishType = dish.getClass().getSimpleName();
        dishCountMap.putIfAbsent(dishType, new AtomicInteger(0));
        dishCountMap.get(dishType).incrementAndGet();
    }

    private void displayDishTypeCount(String label, Map<String, AtomicInteger> dishCountMap) {
        System.out.println(label + ":");
        for (String dishType : allDishTypes) { 
            int count = dishCountMap.getOrDefault(dishType, new AtomicInteger(0)).get();
            System.out.println(" - " + dishType + ": " + count);
        }
    }
    
    private int getTotalDishCount(Map<String, AtomicInteger> dishCountMap) {
        return dishCountMap.values().stream().mapToInt(AtomicInteger::get).sum();
    }

    public int getAbandonedDishCount(String dishType) {
        return abandonedDishCountMap.getOrDefault(dishType, new AtomicInteger(0)).get();
    }    
}
