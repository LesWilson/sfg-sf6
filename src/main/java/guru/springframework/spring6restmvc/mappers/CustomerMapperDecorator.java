package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.dto.CustomerDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@NoArgsConstructor
public abstract class CustomerMapperDecorator extends MapperDecoratorBase implements CustomerMapper {

    @Autowired
    @Qualifier("delegate")
    private CustomerMapper delegate;

    @Override
    public CustomerDTO modelToDto(Customer model) {
        CustomerDTO dto = delegate.modelToDto(model);
        return (CustomerDTO) this.setBaseFieldsOnDtoFromModel(model, dto);
    }

    public Customer dtoToModel(CustomerDTO dto) {
        Customer model = delegate.dtoToModel(dto);
        return (Customer) this.setBaseFieldsOnModelFromDto(dto, model);
    }

}
