package team.project.Entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Member extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    protected Member() {

    }

    public Member(String email, String password, String name, String nickname, RoleType role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
    }
}
