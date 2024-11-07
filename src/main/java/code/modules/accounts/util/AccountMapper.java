package code.modules.accounts.util;

import code.configuration.SpringMapperConfig;
import code.modules.accounts.AccountCommandFacade.AccountCreateDto;
import static code.modules.accounts.AccountCommandFacade.AccountReadDto;
import code.modules.accounts.data.AccountEntity;
import code.modules.accounts.service.domain.Account;
import code.modules.accounts.service.domain.Authority;
import code.util.Generated;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface AccountMapper {

  Account entityToDomain(AccountEntity entity);

  AccountEntity domainToEntity(Account domain);

  Account createDtoToDomain(AccountCreateDto createDto);

  @Mapping(target = "authorities", source = "authorities", qualifiedByName = "authorityMapping")
  AccountReadDto domainToReadDto(Account domain);

  @Named("authorityMapping")
  default Set<String> authorityMapping(Set<Authority> authorities) {
    if (Objects.isNull(authorities)) return null;
    return authorities.stream().map(e -> e.getName().name()).collect(Collectors.toSet());
  }
}