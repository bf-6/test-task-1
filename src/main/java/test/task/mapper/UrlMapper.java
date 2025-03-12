package test.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import test.task.dto.UrlCreateDTO;
import test.task.dto.UrlDTO;
import test.task.dto.UrlUpdateDTO;
import test.task.model.Url;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UrlMapper {
    public abstract Url map(UrlCreateDTO model);
    public abstract UrlDTO map(Url model);
    public abstract Url map(UrlDTO model);
    public abstract void update(UrlUpdateDTO update, @MappingTarget Url model);
}
