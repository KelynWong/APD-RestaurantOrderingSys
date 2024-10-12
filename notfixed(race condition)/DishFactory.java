package apd.proj.notfixed;

public class DishFactory {
    public static Dish createDish(String dishType) {
        switch (dishType.toLowerCase()) {
            case "steamedegg":
                return new SteamedEgg();
            case "omelette":
                return new Omelette();
            default:
                throw new IllegalArgumentException("Unknown dish type: " + dishType);
        }
    }
}
