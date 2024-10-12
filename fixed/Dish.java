package apd.proj.fixed;

import java.util.Map;

public interface Dish {
    Map<String, Integer> getIngredients();  // Returns the ingredients with their current amounts
    void addIngredient(String ingredient);  // Adds or increments an ingredient
}
