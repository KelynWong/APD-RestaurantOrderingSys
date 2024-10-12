package apd.proj.fixed;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
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

    private final ReentrantLock toMakeLock = new ReentrantLock();
    private final ReentrantLock madeLock = new ReentrantLock();
    private final ReentrantLock servedLock = new ReentrantLock();
    private final ReentrantLock abandonedLock = new ReentrantLock();

    private final ReentrantLock dishCountLock = new ReentrantLock(); // New lock for shared resources

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
        toMakeLock.lock();  // Use toMakeLock for dishesToMake
        try {
            dishesToMake.add(dish);  
            totalDishesCount.incrementAndGet();
            incrementDishCount(dish, toMakeDishCountMap);
            allDishTypes.add(dish.getClass().getSimpleName());  // Track the dish type
        } finally {
            toMakeLock.unlock();
        }
    }    

    public Dish getDishToMake() {
        toMakeLock.lock();  // Use toMakeLock for dishesToMake
        try {
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
        } finally {
            toMakeLock.unlock();
        }
    }    

    public void markDishAsMade(Dish dish) {
        toMakeLock.lock();
        madeLock.lock();
        try {
            dishesToMake.remove(dish);
            madeDishes.add(dish);
        } finally {
            madeLock.unlock();
            toMakeLock.unlock();
        }

        // Update dish count and types (dishCountLock is used for shared resources)
        dishCountLock.lock();
        try {
            incrementDishCount(dish, madeDishCountMap);
            allDishTypes.add(dish.getClass().getSimpleName());
        } finally {
            dishCountLock.unlock();
        }
    }

    public void markDishAsAbandoned(Dish dish) {
        toMakeLock.lock();
        abandonedLock.lock();
        try {
            dishesToMake.remove(dish);
            abandonedDishes.add(dish);  // This should be abandonedDishes, not madeDishes
            totalAbandonedDishesCount.incrementAndGet();  // Atomic and thread-safe
        } finally {
            abandonedLock.unlock();
            toMakeLock.unlock();
        }
    
        // Update dish count and types (dishCountLock is used for shared resources)
        dishCountLock.lock();
        try {
            incrementDishCount(dish, abandonedDishCountMap);  // Increment abandonedDishCountMap
            allDishTypes.add(dish.getClass().getSimpleName());
        } finally {
            dishCountLock.unlock();
        }
    }    

    public void markDishAsServed(Dish dish) {
        madeLock.lock();   // Lock the madeDishes list
        servedLock.lock(); // Lock the servedDishes list
        try {
            madeDishes.remove(dish);  // Remove from madeDishes
            servedDishes.add(dish);   // Add to servedDishes
        } finally {
            servedLock.unlock();
            madeLock.unlock();
        }
    
        // Update dish count and types (dishCountLock is used for shared resources)
        dishCountLock.lock();
        try {
            incrementDishCount(dish, servedDishCountMap);  // Increment servedDishCountMap
            allDishTypes.add(dish.getClass().getSimpleName());
        } finally {
            dishCountLock.unlock();
        }
    }    

    public Dish getMadeDishToServe() {
        madeLock.lock();
        try {
            if (madeDishes.isEmpty()) {
                return null;
            } else {
                try {
                    Thread.sleep(sleepTime);  // Simulate delay in serving
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return madeDishes.remove(0);  // Serve the first made dish
            } 
        } finally {
            madeLock.unlock();
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
