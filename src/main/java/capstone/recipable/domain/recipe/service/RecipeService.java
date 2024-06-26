package capstone.recipable.domain.recipe.service;

import capstone.recipable.domain.auth.jwt.SecurityContextProvider;
import capstone.recipable.domain.bookmark.repository.BookmarkRepository;
import capstone.recipable.domain.ingredient.service.NaverSearchImageService;
import capstone.recipable.domain.recipe.dto.request.CreateRecipeRequest;
import capstone.recipable.domain.recipe.dto.response.CreateRecipeResponse;
import capstone.recipable.domain.recipe.dto.response.RecipeDetailsResponse;
import capstone.recipable.domain.recipe.dto.response.RecipeVideoResponse;
import capstone.recipable.domain.recipe.entity.Recipe;
import capstone.recipable.domain.recipe.entity.RecipeVideos;
import capstone.recipable.domain.recipe.repository.RecipeRepository;
import capstone.recipable.domain.recipe.repository.RecipeVideosRepository;
import capstone.recipable.domain.user.entity.User;
import capstone.recipable.domain.user.repository.UserRepository;
import capstone.recipable.domain.user.service.UserService;
import capstone.recipable.global.error.ApplicationException;
import capstone.recipable.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeVideosRepository recipeVideosRepository;
    private final YoutubeService youtubeService;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    //레시피 상세 조회
    public RecipeDetailsResponse getRecipeDetails(Long recipeId) {
        Long userId = SecurityContextProvider.getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        boolean isMarked = bookmarkRepository.isBookmarked(user, recipe);

        return RecipeDetailsResponse.of(recipe, isMarked);
    }

    //레시피 생성
    @Transactional
    public CreateRecipeResponse createRecipe(CreateRecipeRequest request) throws IOException {
        Long userId = SecurityContextProvider.getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        List<RecipeVideoResponse> recipeVideoResponses = youtubeService.searchVideo(request.getQuery());
        Recipe recipe = Recipe.of(request.getRecipeImg(), request.getRecipeName(), request.getIntroduce(),
                request.getIngredients(), request.getRecipeDetails(), user);

        recipeRepository.save(recipe);

        List<RecipeVideos> recipeVideos = recipeVideoResponses.stream()
                .map(videos -> RecipeVideos.of(videos.getVideoUrl(), videos.getTitle(), videos.getThumbnail(), recipe))
                .toList();

        recipeVideosRepository.saveAll(recipeVideos);

        recipe.updateVideo(recipeVideos);

        return CreateRecipeResponse.of(recipe);
    }
}
