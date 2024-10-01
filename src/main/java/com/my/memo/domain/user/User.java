package com.my.memo.domain.user;

import com.my.memo.domain.base.BaseEntity;
import com.my.memo.dto.user.ReqDto;
import com.my.memo.service.UserService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.service.UserService.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    public void modify(UserModifyReqDto userModifyReqDto){
        if(userModifyReqDto.getEmail() != null)
            this.email = userModifyReqDto.getEmail();
        if(userModifyReqDto.getName() != null)
            this.name = userModifyReqDto.getName();
    }

}
