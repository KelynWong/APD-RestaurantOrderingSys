# Restaurant Ordering System

#### A multi-threaded program demonstrating and fixing race conditions -by Lynette, Kelyn and Rachel

---

## Context
The program simulates a restaurant where each waiters and chefs are represented by threads. Waiter threads place orders containing dishes and send them to the kitchen upadting the to make dish list. Chef threads perpares the dishes and update the kitchen with the dishes marked as made. To prepare a dish, chefs decrement ingredients from the inventory and increment ingredient counts in the dish. Race conditions are observed, when dish counts and ingredient counts don't tally. 

For simplicity, our program only implements two dishes, Steamed egg and Omelette. Both require various ingredients such as Eggs, Milk etc. Race conditions may happen here because multiple chefs peparing dishes access to the shared inventory of ingredients, leading to inconsistent inventory values.

In the kitchen class, both waiters and chefs may modify the dishes to make and made dishes list at the same time. Similarly, multiple waiters may modify the served dishes list. These ultimately leads to race conditions at the dish level.

To fix race conditions as well as prevent deadlocks, reentrantlocks and lock ordering are used.

## Running the program
1. cd into either fixed, notfixed(race condition) or partialfixed(deadlock) folder
2. Compile using `javac -d . *.java`
3. (optional - refer to configuration guide) Make configuration changes in config.txt
4. Run using `java apd.proj.<notfixed or partialfixed or fixed>.RestaurantMain`

## Configuration guide
### Order Configuration
---
**Format (note that each configuration is one line of the text  file):**  
`<dish name> <quantity per waiter>`

---

#### Dishes to be Submitted by Each Waiter

For example:

```txt
SteamedEgg 10  
Omelette 5
```

Each waiter will submit **10 orders of SteamedEgg** and **5 orders of Omelette** to the kitchen for the chefs to prepare.

### General Configurations
---
**Format (note that each configuration is one line of the text  file):**  
`<configuration name> <value>`

---

#### Waiter Configuration

These settings control the behavior of the waiter threads responsible for taking orders and serving dishes.

- **`MAX_SERVING_ATTEMPTS`** `<number>`  
  The maximum number of times a waiter will attempt to serve a dish. If this limit is reached, the waiter will stop attempting to serve the dish.

- **`SERVING_WAIT_TIME`** `<time in ms>`  
  The time (in milliseconds) a waiter will wait before attempting to serve another dish. This adds a delay between consecutive serving attempts.

---

#### Chef Configuration

These settings control the behavior of the chef threads responsible for preparing dishes in the kitchen.

- **`MAX_ATTEMPTS`** `<number>`  
  The maximum number of attempts a chef will make to prepare a dish before abandoning it. If a dish cannot be prepared after these attempts (due to ingredient shortages or other failures), it is marked as abandoned.

- **`MAX_EMPTY_CHECKS`** `<number>`  
  The number of times a chef can check the kitchen for new dishes before stopping. If no new dishes are available after this number of checks, the chef will stop working.

- **`MAKE_DISH_WAIT_TIME`:** `<time in ms>`  
  The time (in milliseconds) a chef will wait before attempting to prepare the next dish. This adds a delay between consecutive preparation attempts.

---

#### Thread Sleep Time

This setting controls the sleep time for both chefs and waiters to allow for more frequent context switching between threads. It increases the chances of concurrency issues such as deadlocks and race conditions for testing purposes and to make them more obvious.

- **`SLEEP_TIME`** `<time in ms>`  
  The time (in milliseconds) that threads (chefs, waiters, etc.) will sleep to simulate delays and help test thread synchronization.

---

#### Thread Counts

These settings define the number of chef and waiter threads in the simulation.

- **`NUMBER_OF_CHEFS`:** `<number>`  
  The total number of chef threads that will be spawned in the simulation.

- **`NUMBER_OF_WAITERS`:** `<number>`  
  The total number of waiter threads that will be spawned in the simulation.

---

#### Inventory Configuration

This setting controls whether the inventory has enough ingredients to fulfill all dish orders.

- **`MAKE_INVENTORY_LIMITED`:** `<0 or 1>`  
  Defines whether the inventory has limited ingredients or not:
  
  - `0`: The inventory contains exactly the amount of ingredients required for all the dishes, ensuring no shortage.
  - `1`: The inventory is limited, meaning there may not be enough ingredients to prepare all dishes, which could lead to some orders being abandoned.

---

Feel free to play around and changing the values of the configuration values to observe how the behavior of waiters, chefs, kitchen, and inventory in the restaurant simulation changes :)


##  Scenarios and their Observations (corresponding to folders in project directory)
### 1. `notfixed(race condition)`
**Observation 1:** Expected number of to make dishes (rare chance of this happening), made dishes, served dishes **do not** tally with actual number of to make dishes, made dishes and served dishes.

**Observation 2:**
Expected inventory of ingredients of butter, salt, egg, milk and water **does not** match with actual inventory of ingredients of butter, salt, egg, milk and water.

**Observation 3:** Looking at the outputs when waiter and chef is preparing and serving dishes, multiple different chefs can be seen preparing the same dish order as well as multiple different waiters can be seen serving the same dish order.
E.g. Multiple different chef preparing same dish orders
```txt
Chef 5 is preparing: SteamedEgg (item: 1994625982)
Chef 1 is preparing: SteamedEgg (item: 1994625982)
Chef 2 is preparing: SteamedEgg (item: 1183290361)
Chef 6 is preparing: SteamedEgg (item: 1183290361)
```

E.g. Multiple different waiters serving same dish orders
```txt
Waiter 7 served: SteamedEgg (item: 674644657)
Waiter 9 served: SteamedEgg (item: 674644657)
Waiter 5 served: SteamedEgg (item: 674644657)
Waiter 1 served: SteamedEgg (item: 674644657)
Waiter 6 served: SteamedEgg (item: 250533076)
Waiter 4 served: SteamedEgg (item: 250533076)
```
**Observation 4:**
Abandoned dishes are most of the time 0 regardless of configuration being MAKE_INVENTORY_LIMITED 0 or 1. Race conditions of dish arraylists and modification in inventory of ingredients may lead to underutilization of ingredients so no dish is abandoned. 

program output e.g.
```txt
Parsed config: SteamedEgg = 10
Parsed config: Omelette = 5
Parsed config: MAX_SERVING_ATTEMPTS = 1000
Parsed config: SERVING_WAIT_TIME = 10
Parsed config: MAX_ATTEMPTS = 5
Parsed config: MAX_EMPTY_CHECKS = 1000
Parsed config: MAKE_DISH_WAIT_TIME = 10
Parsed config: SLEEP_TIME = 10
Parsed config: NUMBER_OF_CHEFS = 10
Parsed config: NUMBER_OF_WAITERS = 10
Parsed config: MAKE_INVENTORY_LIMITED = 0

<waiter ordering and chef preparing outputs>

--------------------------------
        The Kitchen
--------------------------------
--------------------
!!! ACTUAL !!!
--------------------
Total dishes to make: 150
To-make dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total made dishes: 133
Made dishes:
 - SteamedEgg: 81
 - Omelette: 52

Total served dishes: 124
Served dishes:
 - SteamedEgg: 75
 - Omelette: 49

Total abandoned dishes: 0
Abandoned dishes:
 - SteamedEgg: 0
 - Omelette: 0

--------------------
!!! EXPECTED !!!
--------------------
Total dishes to make: 150
To-make dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total made dishes: 150
Made dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total served dishes: 150
Served dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total abandoned dishes: 0
Abandoned dishes:
 - SteamedEgg: 0
 - Omelette: 0

--------------------------------
        Inventory
--------------------------------
Current Inventory: {butter=38, salt=116, egg=118, milk=36, water=76}
Expected Inventory: {butter=0, salt=0, egg=0, milk=0, water=0}
```

### 2.`partialfixed(deadlock)
**Obervation 1:** The program hangs forever and no output of actual and expected inventory of ingredients and kitchen lists is observed.

**Observation 2:** Multiple different ingredients are being locked in random order by multiple chefs while preparing dishes and does not release the lock on ingredients until they have gotten all the locks of all the ingredients required for the dish which causes a circular dependency

program output e.g.
```txt
Parsed config: SteamedEgg = 10
Parsed config: Omelette = 5
Parsed config: MAX_SERVING_ATTEMPTS = 1000
Parsed config: SERVING_WAIT_TIME = 10
Parsed config: MAX_ATTEMPTS = 5
Parsed config: MAX_EMPTY_CHECKS = 1000
Parsed config: MAKE_DISH_WAIT_TIME = 10
Parsed config: SLEEP_TIME = 10
Parsed config: NUMBER_OF_CHEFS = 10
Parsed config: NUMBER_OF_WAITERS = 10
Parsed config: MAKE_INVENTORY_LIMITED = 0

<waiter ordering and chef preparing outputs>

pool-1-thread-56 locking water
pool-1-thread-18 locking water
pool-1-thread-48 locking egg
pool-1-thread-11 locking salt
pool-1-thread-12 locking salt
pool-1-thread-19 locking salt
pool-1-thread-49 locking water
pool-1-thread-25 locking water
pool-1-thread-55 locking egg
pool-1-thread-34 locking salt
pool-1-thread-35 locking water
pool-1-thread-32 locking egg
pool-1-thread-47 locking egg
pool-1-thread-44 locking salt
pool-1-thread-50 locking egg
pool-1-thread-31 locking salt
pool-1-thread-17 locking egg
pool-1-thread-36 locking salt
pool-1-thread-57 locking egg
pool-1-thread-40 locking egg
pool-1-thread-45 locking egg
pool-1-thread-30 locking water
pool-1-thread-27 locking salt
pool-1-thread-39 locking water
pool-1-thread-38 locking egg
pool-1-thread-29 locking salt
pool-1-thread-24 locking egg
<program hang>
```

### 3.`fixed
**Observation 1:** Expected number of to make dishes (rare chance of this happening), made dishes, served dishes **do** tally with actual number of to make dishes, made dishes and served dishes.

**Observation 2:** Expected inventory of ingredients of butter, salt, egg, milk and water **does** match with actual inventory of ingredients of butter, salt, egg, milk and water.

**Observation 3:** Looking at the outputs when waiter and chef is preparing and serving dishes, multiple different chefs **does not** prepare the same dish order and multiple different waiters **does not** serve the same dish order.

program output e.g.
```txt
Parsed config: SteamedEgg = 10
Parsed config: Omelette = 5
Parsed config: MAX_SERVING_ATTEMPTS = 1000
Parsed config: SERVING_WAIT_TIME = 10
Parsed config: MAX_ATTEMPTS = 5
Parsed config: MAX_EMPTY_CHECKS = 1000
Parsed config: MAKE_DISH_WAIT_TIME = 10
Parsed config: SLEEP_TIME = 10
Parsed config: NUMBER_OF_CHEFS = 10
Parsed config: NUMBER_OF_WAITERS = 10
Parsed config: MAKE_INVENTORY_LIMITED = 0

<waiter ordering and chef preparing outputs>

--------------------------------
        The Kitchen
--------------------------------
--------------------
!!! ACTUAL !!!
--------------------
Total dishes to make: 150
To-make dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total made dishes: 150
Made dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total served dishes: 150
Served dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total abandoned dishes: 0
Abandoned dishes:
 - SteamedEgg: 0
 - Omelette: 0

--------------------
!!! EXPECTED !!!
--------------------
Total dishes to make: 150
To-make dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total made dishes: 150
Made dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total served dishes: 150
Served dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total abandoned dishes: 0
Abandoned dishes:
 - SteamedEgg: 0
 - Omelette: 0

--------------------------------
        Inventory
--------------------------------
Current Inventory: {butter=0, salt=0, egg=0, milk=0, water=0}
Expected Inventory: {butter=0, salt=0, egg=0, milk=0, water=0}
```

### Edge case: Abandoned dishes
In the fixed folder, change `config.txt` MAKE_INVENTORY_LIMITED value to 1.

**Observation:** Chefs abandon dishes when the inventory contains insufficient ingredients for the dishes

program output e.g
```txt
Parsed config: SteamedEgg = 10
Parsed config: Omelette = 5
Parsed config: MAX_SERVING_ATTEMPTS = 1000
Parsed config: SERVING_WAIT_TIME = 10
Parsed config: MAX_ATTEMPTS = 5
Parsed config: MAX_EMPTY_CHECKS = 1000
Parsed config: MAKE_DISH_WAIT_TIME = 10
Parsed config: SLEEP_TIME = 10
Parsed config: NUMBER_OF_CHEFS = 10
Parsed config: NUMBER_OF_WAITERS = 10
Parsed config: MAKE_INVENTORY_LIMITED = 1

<waiter ordering and chef preparing outputs>

--------------------------------
        The Kitchen
--------------------------------
--------------------
!!! ACTUAL !!!
--------------------
Total dishes to make: 150
To-make dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total made dishes: 23
Made dishes:
 - SteamedEgg: 11
 - Omelette: 12

Total served dishes: 23
Served dishes:
 - SteamedEgg: 11
 - Omelette: 12

Total abandoned dishes: 127
Abandoned dishes:
 - SteamedEgg: 89
 - Omelette: 38

--------------------
!!! EXPECTED !!!
--------------------
Total dishes to make: 150
To-make dishes:
 - SteamedEgg: 100
 - Omelette: 50

Total made dishes: 23
Made dishes:
 - SteamedEgg: 11
 - Omelette: 12

Total served dishes: 23
Served dishes:
 - SteamedEgg: 11
 - Omelette: 12

Total abandoned dishes: 127
Abandoned dishes:
 - SteamedEgg: 89
 - Omelette: 38

--------------------------------
        Inventory
--------------------------------
Current Inventory: {butter=10, salt=71, egg=63, milk=0, water=0}
Expected Inventory: {butter=10, salt=71, egg=63, milk=0, water=0}
```


## Restaurant Simulation Configuration Guide

The `config.txt` file provides easy-to-change settings used in the restaurant ordering system simulation. These settings define the behavior of the waiters, chefs, kitchen, and the inventory system in handling dish orders and preparation.


