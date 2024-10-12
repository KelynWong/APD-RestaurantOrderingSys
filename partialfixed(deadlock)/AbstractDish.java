package apd.proj.partialfixed;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDish implements Dish {
    protected Map<String, Integer> ingredients;

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
    }
}
