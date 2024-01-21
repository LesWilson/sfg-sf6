package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.dto.CustomerDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(CustomerMapperDecorator.class)
public interface CustomerMapper {
    Customer dtoToModel(CustomerDTO dto);
    CustomerDTO modelToDto(Customer customer);
}
