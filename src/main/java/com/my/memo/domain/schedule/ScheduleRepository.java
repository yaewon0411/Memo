package com.my.memo.domain.schedule;

import com.my.memo.domain.user.User;
import com.my.memo.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.my.memo.dto.schedule.ReqDto.*;

@Service
@RequiredArgsConstructor
public class ScheduleRepository {

    private static final Logger log = LoggerFactory.getLogger(ScheduleRepository.class);

    public Optional<Schedule> findById(Connection connection, Long id) throws SQLException {
        String sql = "select s.schedule_id, s.content, s.start_at, s.end_at, s.is_public, s.created_at, s.last_modified_at, " +
                "u.user_id, u.name, u.email " +
                "from schedules s join users u on s.user_id = u.user_id " +
                "where s.schedule_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try(ResultSet resultSet = pstmt.executeQuery()){
                if(resultSet.next()){
                    User user = User.builder()
                            .id(resultSet.getLong("user_id"))
                            .name(resultSet.getString("name"))
                            .email(resultSet.getString("email"))
                            .build();

                    Schedule schedule = Schedule.builder()
                            .id(resultSet.getLong("schedule_id"))
                            .content(resultSet.getString("content"))
                            .startAt(resultSet.getTimestamp("start_at").toLocalDateTime())
                            .endAt(resultSet.getTimestamp("end_at").toLocalDateTime())
                            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                            .lastModifiedAt(resultSet.getTimestamp("last_modified_at").toLocalDateTime())
                            .isPublic(resultSet.getBoolean("is_public"))
                            .user(user)
                            .build();

                    return Optional.of(schedule);
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("일정 조회 중 오류 발생: " + e.getMessage());
            throw new SQLException("일정 조회 중 오류 발생", e);
        }
    }

    public Long save(Connection connection, Schedule schedule) throws SQLException {
        String sql = "insert into schedules (content, start_at, end_at, user_id, is_public, created_at, last_modified_at) values (?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1, schedule.getContent());
            pstmt.setTimestamp(2, Timestamp.valueOf(schedule.getStartAt()));
            pstmt.setTimestamp(3, Timestamp.valueOf(schedule.getEndAt()));
            pstmt.setLong(4, schedule.getUser().getId());
            pstmt.setBoolean(5, schedule.isPublic());
            pstmt.setTimestamp(6, Timestamp.valueOf(schedule.getCreatedAt()));
            pstmt.setTimestamp(7, Timestamp.valueOf(schedule.getLastModifiedAt()));
            int createdRow = pstmt.executeUpdate();

            if(createdRow == 0)
                throw new SQLException("일정 저장 실패: 행이 생성되지 않았습니다");

            try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                if(generatedKeys.next()){
                    return generatedKeys.getLong(1);
                }else
                    throw new SQLException("일정 저장 실패: 기본키가 생성되지 않았습니다");
            }

        }catch (SQLException e){
            log.error("일정 저장 중 오류 발생: " + e.getMessage());
            throw new SQLException("일정 저장 중 오류 발생", e);
        }

    }


    public List<Schedule> findAllByUserIdWithPagination(Connection connection, Long userId, long limit, long offset, String modifiedAt, String startModifiedAt, String endModifiedAt) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from schedules where user_id = ? ");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        if (modifiedAt != null && !modifiedAt.isEmpty()) {
            switch (modifiedAt) {
                case"30m" :
                    sql.append("and last_modified_at >= NOW() - INTERVAL 30 MINUTE ");
                    break;
                case "1h":
                    sql.append("and last_modified_at >= NOW() - INTERVAL 1 HOUR ");
                    break;
                case "1d":
                    sql.append("and last_modified_at >= NOW() - INTERVAL 1 DAY ");
                    break;
                case "1w":
                    sql.append("and last_modified_at >= NOW() - INTERVAL 1 WEEK ");
                    break;
                case "1m":
                    sql.append("and last_modified_at >= NOW() - INTERVAL 1 MONTH ");
                    break;
                case "3m":
                    sql.append("and last_modified_at >= NOW() - INTERVAL 3 MONTH ");
                    break;
                case "6m":
                    sql.append("and last_modified_at >= NOW() - INTERVAL 6 MONTH ");
                    break;
            }
        }

        else if (startModifiedAt != null && endModifiedAt != null) {
            sql.append("and last_modified_at between ? and ? ");
            parameters.add(startModifiedAt + " 00:00:00");
            parameters.add(endModifiedAt + " 23:59:59");
        } else if (startModifiedAt != null) {
            sql.append("and last_modified_at >= ? ");
            parameters.add(startModifiedAt + " 00:00:00");
        } else if (endModifiedAt != null) {
            sql.append("and last_modified_at <= ? ");
            parameters.add(endModifiedAt + " 23:59:59");
        }

        sql.append("order by last_modified_at desc limit ? offset ?");
        parameters.add(limit);
        parameters.add(offset);

        List<Schedule> scheduleList = new ArrayList<>();

        try(PreparedStatement pstmt = connection.prepareStatement(sql.toString())
        ){
            for (int i = 0; i < parameters.size(); i++) {
                if (parameters.get(i) instanceof Long) {
                    pstmt.setLong(i + 1, (Long) parameters.get(i));
                } else if (parameters.get(i) instanceof String) {
                    pstmt.setString(i + 1, (String) parameters.get(i));
                }
            }

            try(ResultSet resultSet = pstmt.executeQuery()){
                while (resultSet.next()) {
                    Schedule schedule = Schedule.builder()
                            .id(resultSet.getLong("schedule_id"))
                            .content(resultSet.getString("content"))
                            .startAt(resultSet.getTimestamp("start_at").toLocalDateTime())
                            .endAt(resultSet.getTimestamp("end_at").toLocalDateTime())
                            .isPublic(resultSet.getBoolean("is_public"))
                            .lastModifiedAt(resultSet.getTimestamp("last_modified_at").toLocalDateTime())
                            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                            .build();
                    scheduleList.add(schedule);
                }
                return scheduleList;
            }
        }catch (SQLException e){
            log.error("일정 조회 중 오류 발생: " + e.getMessage());
            throw new SQLException("일정 조회 중 오류 발생");
        }
    }

    public void update(Connection connection, Long scheduleId, ScheduleModifyReqDto scheduleModifyReqDto) throws SQLException {
        StringBuilder sql = new StringBuilder("update schedules set ");
        List<Object> parameters = new ArrayList<>();


        if (scheduleModifyReqDto.getContent() != null) {
            sql.append("content = ?, ");
            parameters.add(scheduleModifyReqDto.getContent());
        }
        if (scheduleModifyReqDto.getStartAt() != null) {
            sql.append("start_at = ?, ");
            parameters.add(scheduleModifyReqDto.getStartAt());
        }
        if (scheduleModifyReqDto.getEndAt() != null) {
            sql.append("end_at = ?, ");
            parameters.add(scheduleModifyReqDto.getEndAt());
        }
        if(scheduleModifyReqDto.getIsPublic() != null){
            sql.append("is_public = ?, ");
            parameters.add(scheduleModifyReqDto.getIsPublic());
        }
        sql.append("last_modified_at = now() ");
        sql.append("where schedule_id = ?");

        parameters.add(scheduleId);

        try(PreparedStatement pstmt = connection.prepareStatement(sql.toString())){

            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            pstmt.executeUpdate();
            log.info("일정 ID: {} 수정 완료", scheduleId);

        }catch(SQLException e){
            log.error("일정 수정 중 오류 발생: "+e.getMessage());
            throw new SQLException("일정 수정 중 오류 발생");
        }
    }

    public void deleteById(Connection connection,  Long scheduleId) {
        String sql = "delete from schedules where schedule_id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setLong(1, scheduleId);

            pstmt.executeUpdate();
            log.info("일정 ID: {} 삭제 완료", scheduleId);

        }catch (SQLException e){
            log.error("일정 삭제 중 오류 발생: "+e.getMessage());
            throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일정 삭제 중 오류 발생");
        }
    }

    public List<Schedule> findPublicSchedulesWithFilters(Connection connection, long limit, long offset, String modifiedAt, String authorName, String startModifiedAt, String endModifiedAt) throws SQLException {
        StringBuilder sql = new StringBuilder("select s.schedule_id, s.content, s.start_at, s.end_at, s.is_public, s.last_modified_at, s.created_at, " +
                "u.user_id, u.name, u.email " +
                "from schedules s join users u on s.user_id = u.user_id " +
                "where is_public = ? ");
        List<Object> parameters = new ArrayList<>();

        parameters.add(true);

        if (modifiedAt != null && !modifiedAt.isEmpty()) {
            switch (modifiedAt) {
                case"30m" :
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 30 MINUTE ");
                    break;
                case "1h":
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 1 HOUR ");
                    break;
                case "1d":
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 1 DAY ");
                    break;
                case "1w":
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 1 WEEK ");
                    break;
                case "1m":
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 1 MONTH ");
                    break;
                case "3m":
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 3 MONTH ");
                    break;
                case "6m":
                    sql.append("and s.last_modified_at >= NOW() - INTERVAL 6 MONTH ");
                    break;
            }
        }

        else if (startModifiedAt != null && endModifiedAt != null) {
            sql.append("and s.last_modified_at between ? and ? ");
            parameters.add(startModifiedAt + " 00:00:00");
            parameters.add(endModifiedAt + " 23:59:59");
        } else if (startModifiedAt != null) {
            sql.append("and s.last_modified_at >= ? ");
            parameters.add(startModifiedAt + " 00:00:00");
        } else if (endModifiedAt != null) {
            sql.append("and s.last_modified_at <= ? ");
            parameters.add(endModifiedAt + " 23:59:59");
        }

        if (authorName != null && !authorName.isEmpty()) {
            sql.append("and u.name like ? ");
            parameters.add("%" + authorName + "%");
        }

        sql.append("order by  s.last_modified_at desc limit ? offset ?"); //수정일 기준 내림차순 정렬
        parameters.add(limit);
        parameters.add(offset);

        log.info("조회 쿼리: {}",sql.toString());

        List<Schedule> scheduleList = new ArrayList<>();

        try(PreparedStatement pstmt = connection.prepareStatement(sql.toString())
        ){
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            try(ResultSet resultSet = pstmt.executeQuery()){
                while (resultSet.next()) {
                    User user = User.builder()
                            .id(resultSet.getLong("user_id"))
                            .name(resultSet.getString("name"))
                            .email(resultSet.getString("email"))
                            .build();

                    Schedule schedule = Schedule.builder()
                            .id(resultSet.getLong("schedule_id"))
                            .content(resultSet.getString("content"))
                            .startAt(resultSet.getTimestamp("start_at").toLocalDateTime())
                            .endAt(resultSet.getTimestamp("end_at").toLocalDateTime())
                            .isPublic(resultSet.getBoolean("is_public"))
                            .lastModifiedAt(resultSet.getTimestamp("last_modified_at").toLocalDateTime())
                            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                            .user(user)
                            .build();
                    scheduleList.add(schedule);
                }
                return scheduleList;
            }
        }catch (SQLException e){
            log.error("전체 공개 일정 조회 중 오류 발생: " + e.getMessage());
            throw new SQLException("전체 일정 조회 중 오류 발생");
        }
    }

    public void deleteByUser(Connection connection, User userPS) throws SQLException {
        String sql = "delete from schedules where user_id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1, userPS.getId());

            pstmt.executeUpdate();
            log.info("유저 전체 일정 삭제 완료: 유저 ID {}", userPS.getId());

        }catch (SQLException e){
            log.error("유저 삭제 중 오류 발생: "+e.getMessage());
            throw new SQLException("유저 삭제 중 오류 발생");
        }

    }
}
