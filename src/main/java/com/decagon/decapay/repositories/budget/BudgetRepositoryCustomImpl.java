package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.DTO.SearchCriteria;
import com.decagon.decapay.DTO.budget.BudgetResponseDto;
import com.decagon.decapay.model.budget.BudgetState;
import com.decagon.decapay.utils.RepositoryHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

public class BudgetRepositoryCustomImpl implements BudgetRepositoryCustom{


    @PersistenceContext
    private EntityManager em;


    @Override
    public Page<BudgetResponseDto> findBudgetsByUserId(Pageable pageable, Long id, List<SearchCriteria> searchCriterias) {
        StringBuilder resultQuery = new StringBuilder("select new com.decagon.decapay.dto.budget.BudgetResponseDto(b.id, b.title, b.totalAmountSpentSoFar, b.projectedAmount, b.budgetPeriod) " +
                "from Budget b ");
        StringBuilder countQuery = new StringBuilder(" select count(*)  from Budget b ");


        String orderByCriteria = " order by b.budgetStartDate ";

        StringBuilder whereClauseQry = new StringBuilder(" WHERE b.user.id =:uid AND b.auditSection.delF <> '1' ");

        LocalDate now = null;

        if (CollectionUtils.isNotEmpty(searchCriterias)){
        for (SearchCriteria criteria : searchCriterias) {
                if (criteria.getKey().equals("state") && EnumUtils.isValidEnumIgnoreCase(BudgetState.class, String.valueOf(criteria.getValue()))){
                    now = LocalDate.now();
                    BudgetState budgetState = BudgetState.valueOf(String.valueOf(criteria.getValue()).toUpperCase());
                    switch (budgetState){
                        case CURRENT:
                            whereClauseQry.append(" and b.budgetStartDate<:currDate and b.budgetEndDate>:currDate ");
                            break;
                        case PAST:
                            whereClauseQry.append(" and b.budgetStartDate<:currDate and b.budgetEndDate<:currDate ");
                            break;
                        case UPCOMMING:
                            whereClauseQry.append(" and b.budgetStartDate>:currDate and b.budgetEndDate>:currDate ");
                        break;
                    }
                }
            }
        }
        resultQuery.append(whereClauseQry);
        countQuery.append(whereClauseQry);

        resultQuery.append(orderByCriteria);

        Query q = em.createQuery(resultQuery.toString()).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        Query cq = this.em.createQuery(countQuery.toString(), Long.class);


        RepositoryHelper.addQueriesParameter(q, cq, "uid", id);

        if (now != null) {
            RepositoryHelper.addQueriesParameter(q, cq, "currDate", now);
        }

        long maxResult = (long) cq.getSingleResult();

        return new PageImpl<BudgetResponseDto>(
                q.getResultList(),
                pageable, maxResult);
    }
}
