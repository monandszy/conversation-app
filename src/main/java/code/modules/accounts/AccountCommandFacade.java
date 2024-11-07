package code.modules.accounts;

import code.modules.accounts.service.AccountDao;
import code.modules.accounts.service.domain.Account;
import code.modules.accounts.service.domain.AuthorityName;
import code.modules.accounts.util.AccountMapper;
import code.util.Facade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Facade
@Slf4j
@AllArgsConstructor
public class AccountCommandFacade {

  private AccountDao accountDao;
  private PasswordEncoder passwordEncoder;
  private AccountMapper accountMapper;

  public AccountReadDto register(@Valid AccountCreateDto accountDto) {
    log.info("Registering account [{}]", accountDto); // TODO also a bit sus
    Account account = accountDao.create(accountMapper.createDtoToDomain(accountDto)
      .withEmail(accountDto.email())
      .withPassword(passwordEncoder.encode(accountDto.password()))
      .withEnabled(true), AuthorityName.ROLE_USER);
    log.info("Registered account [{}]", account);
    return accountMapper.domainToReadDto(account);
  }

  public record AccountReadDto(
    String email,
    String password,
    Boolean enabled,
    Set<String> authorities
  ) {
  }

  public record AccountCreateDto(
    @NotBlank
    @Email
    String email,
    @NotBlank
    @Size(min = 6, max = 32)
    String password
  ) {
  }
}