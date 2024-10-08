import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractDish implements Dish {
    protected Map<String, Integer> ingredients;
    protected String state = "toMake";  // Initial state is "toMake"

    public AbstractDish() {
        ingredients = new HashMap<>();
    }

    // Implementing the methods from the Dish interface
    @Override
    public Map<String, Integer> getIngredients() {
        return ingredients;
    }

    @Override
    public void addIngredient(String ingredient) {
        if (ingredients.containsKey(ingredient)) {
            // ingredients.put(ingredient, 1);  // Set ingredient to 1 (prepared)
            int count = ingredients.get(ingredient);
            count++;
            ingredients.put(ingredient, count);
        }
        checkIfMade();  // Check if all ingredients are prepared
    }

    // Check if all ingredients are set to 1, and update state
    protected void checkIfMade() {
        boolean allIngredientsPrepared = ingredients.values().stream().allMatch(i -> i == 1);
        if (allIngredientsPrepared) {
            state = "Made";  // Change state to Made
        }
    }

    @Override
    public String getState() {
        return state;
    }
}
