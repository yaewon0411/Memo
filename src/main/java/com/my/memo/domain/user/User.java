package com.my.memo.domain.user;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.domain.schedule.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.my.memo.dto.user.ReqDto.UserModifyReqDto;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity {

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
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

}
