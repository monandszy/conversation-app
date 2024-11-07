package code.modules.accounts.service;

import code.modules.accounts.service.domain.Account;
import code.modules.accounts.service.domain.Authority;
import jakarta.servlet.http.HttpSession;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService implements UserDetailsService {

  private AccountDao accountDAO;
  private HttpSession session;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Account account = accountDAO.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    session.setAttribute("accountId", account.getId());
    return buildAccountForAuthentication(account, account.getAuthorities());
  }

  private UserDetails buildAccountForAuthentication(
    Account account, Set<Authority> authorities) {
    return new User(
      account.getId().toString(),
      account.getPassword(),
      account.getEnabled(),
      true,
      true,
      true,
      authorities.stream()
        .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority.getName().name()))
        .toList()
    );
  }
}