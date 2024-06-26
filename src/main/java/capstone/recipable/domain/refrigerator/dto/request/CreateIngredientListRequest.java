package capstone.recipable.domain.refrigerator.dto.request;

import capstone.recipable.domain.category.entity.Category;
import capstone.recipable.domain.ingredient.entity.Ingredient;

import java.util.List;

public record CreateIngredientListRequest(
        List<IngredientRequest> ingredients
) {
    public record IngredientRequest(String ingredientCategory, String ingredientName) {

    }
}
