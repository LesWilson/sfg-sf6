package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.ModelBase;
import guru.springframework.spring6restmvc.dto.DtoBase;

public abstract class MapperDecoratorBase {

    public DtoBase setBaseFieldsOnDtoFromModel(ModelBase model, DtoBase dto) {
        if(model != null) {
            dto.setId(model.getId());
            dto.setVersion(model.getVersion());
            dto.setCreatedDate(model.getCreateDate());
            dto.setUpdateDate(model.getUpdateDate());
        }
        return dto;
    }

    public ModelBase setBaseFieldsOnModelFromDto(DtoBase dto, ModelBase model) {
        if(dto != null) {
            model.setId(dto.getId());
            model.setVersion(dto.getVersion());
            model.setCreateDate(dto.getCreatedDate());
            model.setUpdateDate(dto.getUpdateDate());
        }
        return model;
    }

}
