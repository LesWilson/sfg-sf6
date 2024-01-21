package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.dto.BeerDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@NoArgsConstructor
public abstract class BeerMapperDecorator extends MapperDecoratorBase implements BeerMapper {

    @Autowired
    @Qualifier("delegate")
    private BeerMapper delegate;

    @Override
    public BeerDTO modelToDto(Beer beer) {
        BeerDTO dto = delegate.modelToDto(beer);
        return (BeerDTO)this.setBaseFieldsOnDtoFromModel(beer, dto);
    }

    public Beer dtoToModel(BeerDTO dto) {
        Beer model = delegate.dtoToModel(dto);
        return (Beer)this.setBaseFieldsOnModelFromDto(dto, model);
    }

}
