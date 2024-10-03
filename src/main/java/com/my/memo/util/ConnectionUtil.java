package com.my.memo.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DB 커넥션과 트랜잭션 처리를 담당하는 유틸 클래스입니다
 */
@Component
@RequiredArgsConstructor
public class ConnectionUtil {

    private final DataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(ConnectionUtil.class);

    /**
     * 자동 커밋 비활성화:
     * 트랜잭션이 필요한 연결을 가져옵니다
     */
    public Connection getConnectionForTransaction() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    /**
     * 자동 커밋 활성화:
     * 트랜잭션이 필요 없는 연결을 가져옵니다
     */
    public Connection getConnectionForReadOnly() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }

    /**
     * 트랜잭션을 커밋합니다
     *
     * @param connection 트랜잭션이 시작된 Connection 객체
     * @throws SQLException 커밋 중 오류 발생 시
     */
    public void commit(Connection connection) throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    /**
     * 트랜잭션을 롤백합니다
     *
     * @param connection 트랜잭션이 시작된 Connection 객체
     */
    public void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                log.info("트랜잭션 롤백 완료");
            } catch (SQLException e) {
                log.error("트랜잭션 롤백 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 커넥션을 닫습니다
     *
     * @param connection 닫을 Connection 객체
     */
    public void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                log.info("커넥션 종료 완료");
            } catch (SQLException e) {
                log.error("커넥션 닫기 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

}
