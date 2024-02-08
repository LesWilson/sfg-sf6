package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.dto.BeerStyle;
import guru.springframework.spring6restmvc.entities.Beer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BeerSpecifications {
    public static Specification<Beer> hasBeerNameLike(String name) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("beerName"), "%"+name+"%"));
    }
    public static Specification<Beer> hasBeerStyle(List<BeerStyle> beerStyles) {
        return ((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("beerStyle"));
            for(BeerStyle style : beerStyles) {
                inClause.value(String.valueOf(style));
            }
            return inClause;
        });
    }
    public static Specification<Beer> hasBeerNameIn(List<String> names) {
        return ((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("beerName"));
            for(String str : names) {
                inClause.value(str);
            }
            return inClause;
        });
    }
/*
criteriaBuilder.in(root.get(input.getField()))
          .value(castToRequiredType(
                  root.get(input.getField()).getJavaType(),
                  input.getValues()));
 */
}
