package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.project.Entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
