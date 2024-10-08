import java.util.Map;

public interface Dish {
    Map<String, Integer> getIngredients();  // Returns the ingredients with their current amounts
    void addIngredient(String ingredient);  // Adds or increments an ingredient
    String getState();  // Returns the state of the dish ("toMake" or "Made")
    // int incrementTimesPicked();
    // int getTimesPicked();
}
