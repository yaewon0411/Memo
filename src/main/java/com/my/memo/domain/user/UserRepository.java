package com.my.memo.domain.user;

import com.my.memo.dto.user.ReqDto;
import com.my.memo.ex.CustomApiException;
import com.my.memo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.my.memo.dto.user.ReqDto.*;
import static com.my.memo.service.UserService.*;
/**
 * 유저 데이터를 관리하는 리포지토리 클래스입니다
 *
 * 유저 정보를 데이터베이스에 저장, 조회, 수정, 삭제하는 기능을 제공합니다
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    /**
     * 새로운 유저를 데이터베이스에 저장합니다
     *
     * @param connection 데이터베이스 커넥션
     * @param user 저장할 유저 객체
     * @return 생성된 유저의 ID
     * @throws SQLException 유저 저장 중 오류가 발생한 경우
     */
    public Long save(Connection connection, User user) throws SQLException {
        String sql = "insert into users (name, password, email, created_at, last_modified_at) values (?, ?, ?, now(), now())";

        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());

            int createdRow = pstmt.executeUpdate();

            if(createdRow == 0)
                throw new SQLException("유저 저장 실패: 추가된 레코드가 없음");

            try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
               if(generatedKeys.next()){
                   return generatedKeys.getLong(1);
               }else
                   throw new SQLException("유저 저장 실패: 기본키 생성 실패");
            }

        }catch (SQLException e){
            log.error("유저 저장 중 오류 발생: " + e.getMessage());
            throw e;
        }

    }

    /**
     * 주어진 이메일이 데이터베이스에 존재하는지 확인합니다
     *
     * @param connection 데이터베이스 커넥션
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 존재하지 않으면 false
     */
    public boolean existsByEmail (Connection connection, String email) throws SQLException {
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
            throw new SQLException("이메일 중복 검사 중 오류 발생: "+e.getMessage());
        }

    }

    /**
     * 주어진 이메일로 유저 정보를 조회합니다
     *
     * @param connection 데이터베이스 커넥션
     * @param email 조회할 유저의 이메일
     * @return 유저 객체를 포함한 Optional 객체. 유저가 존재하지 않으면 Optional.empty()
     * @throws SQLException 유저 조회 중 오류가 발생한 경우
     */
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

    /**
     * 주어진 ID로 유저 정보를 조회합니다
     *
     * @param connection 데이터베이스 커넥션
     * @param id 조회할 유저의 ID
     * @return 유저 객체를 포함한 Optional 객체. 유저가 존재하지 않으면 Optional.empty()
     * @throws SQLException 유저 조회 중 오류가 발생한 경우
     */
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
                            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                            .build());
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("아이디로 유저 조회 중 오류 발생: " + e.getMessage());
            throw new SQLException("아이디로 유저 조회 중 오류 발생");
        }
    }

    /**
     * 유저 정보를 수정합니다
     *
     * 주어진 유저 수정 요청 DTO의 값을 사용하여 데이터베이스에서 해당 유저 정보를 업데이트합니다
     *
     * @param connection 데이터베이스 커넥션
     * @param userModifyReqDto 수정할 유저 정보를 담은 DTO
     * @param userId 수정할 유저의 ID
     * @throws SQLException 유저 정보 수정 중 오류가 발생한 경우
     */
    public void update(Connection connection, UserModifyReqDto userModifyReqDto, Long userId) throws SQLException {
        StringBuilder sql = new StringBuilder("update users set ");
        List<Object> parameters = new ArrayList<>();

        if(userModifyReqDto.getName() != null){
            sql.append("name = ?, ");
            parameters.add(userModifyReqDto.getName());
        }
        if(userModifyReqDto.getEmail() != null){
            sql.append("email = ?, ");
            parameters.add(userModifyReqDto.getEmail());
        }
        if(userModifyReqDto.getPassword() != null){
            sql.append("password = ?, ");
            parameters.add(userModifyReqDto.getEncodedPassword());
        }

        sql.append("last_modified_at = now() ");
        sql.append("where user_id = ?");

        parameters.add(userId);

        try(PreparedStatement pstmt = connection.prepareStatement(sql.toString())){

            for(int i = 0;i<parameters.size();i++){
                pstmt.setObject(i+1, parameters.get(i));
            }

            pstmt.executeUpdate();
            log.info("유저 ID: {} 수정 완료",userId);

        }catch (SQLException e){
            log.error("유저 정보 수정 중 오류 발생: "+e.getMessage());
            throw new SQLException("유저 정보 수정 중 오류 발생");
        }

    }

    /**
     * 유저를 데이터베이스에서 삭제합니다
     *
     * @param connection 데이터베이스 커넥션
     * @param userPS 삭제할 유저 객체
     * @throws SQLException 유저 삭제 중 오류가 발생한 경우
     */
    public void delete(Connection connection, User userPS) throws SQLException {
        String sql = "delete from users where user_id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setLong(1, userPS.getId());

            pstmt.executeUpdate();
            log.info("유저 삭제 완료: 유저 ID {}", userPS.getId());

        }catch (SQLException e){
            log.error("유저 삭제 중 오류 발생: "+e.getMessage());
            throw new SQLException("유저 삭제 중 오류 발생");
        }
    }
}
