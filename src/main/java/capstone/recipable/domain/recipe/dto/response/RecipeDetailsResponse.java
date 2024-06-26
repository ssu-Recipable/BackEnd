package capstone.recipable.domain.recipe.dto.response;

import capstone.recipable.domain.bookmark.repository.BookmarkRepository;
import capstone.recipable.domain.recipe.entity.Recipe;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class RecipeDetailsResponse {

    private Long recipeId;

    private String recipeName;

    private String introduce;

    private String recipeImg;

    private String ingredients;

    private String recipeDetails;

    private List<RecipeVideoResponse> recipeVideoResponses;

    private boolean bookmark;

    public static RecipeDetailsResponse of(Recipe recipe, boolean isMarked) {
        List<RecipeVideoResponse> responses = recipe.getRecipeVideos().stream()
                .map(recipeVideos -> RecipeVideoResponse.of(recipeVideos.getVideoUrl(),
                        recipeVideos.getTitle(),
                        recipeVideos.getThumbnail()))
                .toList();

        return RecipeDetailsResponse.builder()
                .recipeId(recipe.getId())
                .recipeName(recipe.getRecipeName())
                .introduce(recipe.getIntroduce())
                .recipeImg(recipe.getRecipeImg())
                .ingredients(recipe.getIngredients())
                .recipeDetails(recipe.getRecipeDetails())
                .recipeVideoResponses(responses)
                .bookmark(isMarked)
                .build();
    }
}
