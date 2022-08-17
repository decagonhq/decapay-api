package com.decagon.decapay.service.budget.category;

import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.UserInfo;
import com.decagon.decapay.utils.UserInfoUtills;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetCategoryServiceImpl implements BudgetCategoryService {
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final UserRepository userRepository;
    private final UserInfoUtills userInfoUtills;

    @Override
    public Optional<BudgetCategory> findCategoryByIdAndUser(Long budgetCategoryId, User user) {
        return this.budgetCategoryRepository.findByIdAndUser(budgetCategoryId, user);
    }

    @Override
    public List<BudgetCategoryResponseDto> getListOfBudgetCategories() {
        User user = this.getAuthenticatedUser();
        return budgetCategoryRepository.findCategoriesByUserId(user.getId());
    }

    public User getAuthenticatedUser() {

        UserInfo authenticatedUserInfo = this.userInfoUtills.authenticationUserInfo();
        if (authenticatedUserInfo == null){
            throw new UnAuthorizedException("Authenticated User not found");
        }
        Optional<User> user = userRepository.findUserByEmail(authenticatedUserInfo.getUsername());
        if (user.isEmpty()){
            throw  new ResourceNotFoundException("User not found");
        }
        return user.get();
    }

}
