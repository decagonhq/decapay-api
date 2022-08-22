package com.decagon.decapay.service.budget.category;

import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetCategoryDto;
import com.decagon.decapay.dto.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.utils.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class BudgetCategoryServiceImpl implements BudgetCategoryService{

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final UserInfoUtil userInfoUtil;

    @Override
    public Optional<BudgetCategory> findCategoryByIdAndUser(Long budgetCategoryId, User user) {
        return this.budgetCategoryRepository.findByIdAndUser(budgetCategoryId, user);
    }

    @Override
    public List<BudgetCategoryResponseDto> getListOfBudgetCategories() {
        User currentUser = this.userInfoUtil.getCurrAuthUser();
        return budgetCategoryRepository.findCategoriesByUserId(currentUser.getId());
    }

    @Override
    public CreateBudgetResponseDTO createBudgetCategory(CreateBudgetCategoryDto request) {
        BudgetCategory category= createCategoryModelEntity(request.getTitle());
        budgetCategoryRepository.save(category);
        return new CreateBudgetResponseDTO(category.getId());
    }

    @Override
    @Transactional
    public void updateBudgetCategory(Long categoryId, CreateBudgetCategoryDto updateRequestDto) {
        User user = this.getAuthenticatedUser();

        Optional<BudgetCategory> optionalBudgetCategory = this.budgetCategoryRepository.findById(categoryId);
        if (optionalBudgetCategory.isEmpty()){
            throw new ResourceNotFoundException("Budget Category not found");
        }
        BudgetCategory budgetCategory = optionalBudgetCategory.get();
        if (!isCurrentUserOwnerOfBudgetCategory(user, budgetCategory)){
            throw new InvalidRequestException("Invalid Request");
        }
        this.mapRequestToEntity(budgetCategory, updateRequestDto);
    }

    private void mapRequestToEntity(BudgetCategory budgetCategory, CreateBudgetCategoryDto updateRequestDto) {
        budgetCategory.setTitle(updateRequestDto.getTitle());
    }

    private boolean isCurrentUserOwnerOfBudgetCategory(User user, BudgetCategory category) {
            return user.getId().equals(category.getUser().getId());
    }

    private BudgetCategory createCategoryModelEntity(String title) {
        BudgetCategory category=new BudgetCategory();
        User currentUser = this.userInfoUtil.getCurrAuthUser();
        category.setTitle(title);
        category.setUser(currentUser);
        return category;
    }

}
