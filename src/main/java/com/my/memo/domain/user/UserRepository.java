package com.my.memo.domain.user;

import com.my.memo.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);


    public Long save(Connection connection, User user) throws SQLException {
        String sql = "insert into users (name, password, email, created_at, last_modified_at) values (?, ?, ?, now(), now())";

        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
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
            throw new SQLException("유저 저장 중 오류 발생");
        }

    }

    public boolean existsByEmail (Connection connection, String email) {
        String sql = "select exists(select 1 from users where email = ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

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

    public Optional<User> findByEmail(Connection connection, String email) throws SQLException {
        String sql = "select * from Users where email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try(ResultSet resultSet = pstmt.executeQuery()){
                if(resultSet.next()){
                   return Optional.of(User.builder()
                            .id(resultSet.getLong("user_id"))
                            .name(resultSet.getString("name"))
                            .email(resultSet.getString("email"))
                            .password(resultSet.getString("password"))
                            .build());
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("이메일로 유저 조회 중 오류 발생: " + e.getMessage());
            throw new SQLException("이메일로 유저 조회 중 오류 발생");
        }
    }


    public Optional<User> findById(Connection connection, Long id) throws SQLException {
        String sql = "select * from Users where user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try(ResultSet resultSet = pstmt.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(User.builder()
                            .id(resultSet.getLong("user_id"))
                            .name(resultSet.getString("name"))
                            .email(resultSet.getString("email"))
                            .password(resultSet.getString("password"))
                            .build());
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("아이디로 유저 조회 중 오류 발생: " + e.getMessage());
            throw new SQLException("아이디로 유저 조회 중 오류 발생");
        }
    }
}
