package capstone.recipable.domain.refrigerator.service;

import capstone.recipable.domain.auth.jwt.SecurityContextProvider;
import capstone.recipable.domain.category.entity.Category;
import capstone.recipable.domain.category.repository.CategoryRepository;
import capstone.recipable.domain.ingredient.entity.Ingredient;
import capstone.recipable.domain.ingredient.repository.IngredientRepository;
import capstone.recipable.domain.ingredient.service.NaverSearchImageService;
import capstone.recipable.domain.refrigerator.dto.request.CreateIngredientListRequest;
import capstone.recipable.domain.refrigerator.entity.Refrigerator;
import capstone.recipable.domain.refrigerator.repository.RefrigeratorRepository;
import capstone.recipable.domain.user.entity.User;
import capstone.recipable.domain.user.repository.UserRepository;
import capstone.recipable.global.error.ApplicationException;
import capstone.recipable.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CreateIngredientService {
    private final IngredientRepository ingredientRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final NaverSearchImageService naverSearchImageService;


    @Transactional
    public void createIngredient(CreateIngredientListRequest createIngredientListRequest) {
        User user = userRepository.findById(SecurityContextProvider.getAuthenticatedUserId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Refrigerator refrigerator = refrigeratorRepository.findByUser(user)
                .orElseGet(() -> refrigeratorRepository.save(Refrigerator.of(null, user)));

        createIngredientListRequest.ingredients()
                .forEach(ingredientRequest -> {
                    Category category = categoryRepository.findByCategoryNameAndRefrigerator(ingredientRequest.ingredientCategory(), refrigerator)
                            .orElseGet(() ->
                                    categoryRepository.save(Category.of(null, ingredientRequest.ingredientCategory(), null, refrigerator))
                            );
                    String imageFromNaverSearchApi = naverSearchImageService.getImageFromNaverSearchApi(ingredientRequest.ingredientName());
                    ingredientRepository.save(
                            Ingredient.of(null, ingredientRequest.ingredientName(), imageFromNaverSearchApi, null, category, null)
                    );

                });
    }

}
