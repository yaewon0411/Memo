package com.my.memo.domain.user;

import com.my.memo.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Override
    public Long save(User user) {
        String sql = "insert into user (name, password, email, created_at, last_modified_at) values (?, ?, ?, now(), now())";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());

            int createdRow = pstmt.executeUpdate();

            if(createdRow == 0)
                throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 저장 실패");

            try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
               if(generatedKeys.next()){
                   return generatedKeys.getLong(1);
               }else
                   throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 저장 실패. 기본키가 생성되지 않음");
            }

        }catch (SQLException e){
            log.error("유저 저장 중 오류 발생: " + e.getMessage());
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유저 저장 중 오류 발생");
        }

    }

    @Override
    public boolean existsByEmail (String email) {
        String sql = "select exists(select 1 from user where email = ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                } else return false;
            }
        } catch (SQLException e) {
            log.error("이메일 중복 검사 중 오류 발생: " + e.getMessage());
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이메일 중복 검사 중 오류 발생");
        }

    }
}
