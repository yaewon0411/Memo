package com.my.memo.domain.user;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.schedule.Schedule;
import com.my.memo.dto.user.req.UserModifyReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    /*
     * 해당 부분은 JPA 스펙상 원칙적으로 CascadeType.PERSIST이 없어도 orphanRemoval만으로 삭제되어야 하는 것이 맞습니다.
     * 하이버네이트 구현체에서는 해당 기능에 버그가 있고, 그래서 CascadeType.PERSIST(또는 ALL)이 함께 적용되어야 orphanRemoval이 동작합니다.
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 12)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user")
    private List<Schedule> scheduleList = new ArrayList<>();


    @Builder
    public User(Long id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void modify(UserModifyReqDto userModifyReqDto) {
        if (userModifyReqDto.getEmail() != null)
            this.email = userModifyReqDto.getEmail();
        if (userModifyReqDto.getName() != null)
            this.name = userModifyReqDto.getName();
        if (userModifyReqDto.getPassword() != null)
            this.password = userModifyReqDto.getEncodedPassword();

    }

    public boolean isAdmin() {
        return this.role.equals(Role.ADMIN);
    }

}
