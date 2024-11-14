package code.modules.accounts.data;

import code.modules.accounts.service.AccountDao;
import code.modules.accounts.service.domain.Account;
import code.modules.accounts.service.domain.AuthorityName;
import code.modules.accounts.util.AccountMapper;
import code.util.ReadRepositoryAdapter;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;

@ReadRepositoryAdapter
@AllArgsConstructor
public class AccountRepo implements AccountDao {

  private AccountMapper accountMapper;
  private AccountJpaRepo accountJpaRepo;
  private AuthorityJpaRepo authorityJpaRepo;

  @Override
  public Optional<Account> findByEmail(String email) {
    Optional<AccountEntity> byUserName = accountJpaRepo.findByEmail(email);
    return byUserName.map(e -> accountMapper.entityToDomain(e));
  }

  @Override
  public Account create(Account account, AuthorityName authority) {
    AuthorityEntity initialAuthority = authorityJpaRepo.findByName(authority).orElseThrow();
    AccountEntity entity = accountMapper.domainToEntity(account);
    entity.setAuthorities(Set.of(initialAuthority));
    AccountEntity save = accountJpaRepo.save(entity);
    return accountMapper.entityToDomain(save);
  }

}