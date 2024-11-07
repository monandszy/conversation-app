package code.modules.accounts.data;

import code.modules.accounts.service.domain.AuthorityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AuthorityJpaRepo extends JpaRepository<AuthorityEntity, Integer> {
  Optional<AuthorityEntity> findByName(AuthorityName name);
}