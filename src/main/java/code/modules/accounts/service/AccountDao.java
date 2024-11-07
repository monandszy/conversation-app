package code.modules.accounts.service;

import code.modules.accounts.service.domain.Account;
import code.modules.accounts.service.domain.AuthorityName;
import java.util.Optional;

public interface AccountDao {
  Optional<Account> findByEmail(String email);

  Account create(Account account, AuthorityName authority);

}