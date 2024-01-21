package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.dto.BeerDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(BeerMapperDecorator.class)
public interface BeerMapper {
    Beer dtoToModel(BeerDTO dto);
    BeerDTO modelToDto(Beer beer);
}
