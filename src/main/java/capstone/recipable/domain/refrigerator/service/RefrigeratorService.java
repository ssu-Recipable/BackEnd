package capstone.recipable.domain.refrigerator.service;

import capstone.recipable.domain.auth.jwt.SecurityContextProvider;
import capstone.recipable.domain.category.entity.Category;
import capstone.recipable.domain.category.repository.CategoryRepository;
import capstone.recipable.domain.expiration.entity.Expiration;
import capstone.recipable.domain.expiration.repository.ExpirationRepository;
import capstone.recipable.domain.ingredient.controller.dto.response.IngredientDetailResponse;
import capstone.recipable.domain.ingredient.entity.Ingredient;
import capstone.recipable.domain.ingredient.repository.IngredientRepository;
import capstone.recipable.domain.refrigerator.dto.response.RefrigeratorDetailResponse;
import capstone.recipable.domain.refrigerator.dto.response.RefrigeratorListResponse;
import capstone.recipable.domain.refrigerator.dto.response.RefrigeratorResponse;
import capstone.recipable.domain.refrigerator.entity.Refrigerator;
import capstone.recipable.domain.refrigerator.repository.RefrigeratorRepository;
import capstone.recipable.domain.user.entity.User;
import capstone.recipable.domain.user.repository.UserRepository;
import capstone.recipable.global.error.ApplicationException;
import capstone.recipable.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class RefrigeratorService {
    private final UserRepository userRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final ExpirationRepository expirationRepository;


    public RefrigeratorListResponse getAllIngredientsByRefrigerator() {
        User user = userRepository.findById(SecurityContextProvider.getAuthenticatedUserId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Refrigerator refrigerator = refrigeratorRepository.findByUserId(user)
                .orElseThrow(() -> new ApplicationException(ErrorCode.REFRIGERATOR_NOT_FOUND));

        List<Category> allCategoryByRefrigerator = categoryRepository.findAllByRefrigeratorId(refrigerator);
        List<RefrigeratorResponse> refrigeratorResponses = allCategoryByRefrigerator.stream()
                .map(category -> {
                    List<Ingredient> ingredients = ingredientRepository.findAllByCategoryId(category);

                    List<RefrigeratorDetailResponse> refrigeratorDetails = ingredients.stream()
                            .map(ingredient -> {
                                Expiration expiration = expirationRepository.findByIngredientId(ingredient)
                                        .orElseThrow(() -> new ApplicationException(ErrorCode.EXPIRATION_NOT_FOUND));

                                Long remainingExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expiration.getExpireDate());
                                RefrigeratorDetailResponse refrigeratorDetail = RefrigeratorDetailResponse.of(ingredient.getIngredientName(), remainingExpiration, ingredient.getIngredientImage());

                                return refrigeratorDetail;
                            }).toList();
                    return RefrigeratorResponse.of(category.getCategoryName(), category.getDetails(), refrigeratorDetails);
                }).toList();

        return RefrigeratorListResponse.of(refrigeratorResponses);
    }

    public IngredientDetailResponse getIngredient(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INGREDIENT_NOT_FOUND));

        Expiration expiration = expirationRepository.findByIngredientId(ingredient)
                .orElseThrow(() -> new ApplicationException(ErrorCode.EXPIRATION_NOT_FOUND));

        return IngredientDetailResponse.of(ingredient.getIngredientName(), ingredient.getCategoryId().getCategoryName(),
                expiration.getExpireDate(), ingredient.getMemo());
    }
}