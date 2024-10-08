package com.my.memo.domain.schedule;


import com.my.memo.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

interface Dao {
    List<Schedule> findPublicSchedulesWithFilters(long limit, long page, String modifiedAt,
                                                  String authorName, String startModifiedAt, String endModifiedAt);

    List<Schedule> findAllByUserIdWithPagination(User user, long limit, long page, String modifiedAt, String startModifiedAt, String endModifiedAt);

}

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements Dao {
    private final EntityManager em;

    //TODO 테스트하기
    @Override
    public List<Schedule> findAllByUserIdWithPagination(User user, long limit, long page, String modifiedAt, String startModifiedAt, String endModifiedAt) {
        StringBuilder sql = new StringBuilder("select s from Schedule s where s.user = :user ");
        if (modifiedAt != null && !modifiedAt.isEmpty()) {
            switch (modifiedAt) {
                case "30m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 30 MINUTE ");
                    break;
                case "1h":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 HOUR ");
                    break;
                case "1d":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 DAY ");
                    break;
                case "1w":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 WEEK ");
                    break;
                case "1m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 MONTH ");
                    break;
                case "3m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 3 MONTH ");
                    break;
                case "6m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 6 MONTH ");
                    break;
            }
        } else if (startModifiedAt != null && endModifiedAt != null) {
            sql.append("and s.lastModifiedAt between :startModifiedAt and :endModifiedAt ");
        } else if (startModifiedAt != null) {
            sql.append("and s.lastModifiedAt >= :startModifiedAt ");
        } else if (endModifiedAt != null) {
            sql.append("and s.lastModifiedAt <= :endModifiedAt ");
        }

        sql.append("order by  s.lastModifiedAt desc"); //수정일 기준 내림차순 정렬

        TypedQuery<Schedule> query = em.createQuery(sql.toString(), Schedule.class);
        query.setParameter("user", user);

        if (startModifiedAt != null && endModifiedAt != null) {
            query.setParameter("startModifiedAt", startModifiedAt + " 00:00:00");
            query.setParameter("endModifiedAt", endModifiedAt + " 23:59:59");
        } else if (startModifiedAt != null) {
            query.setParameter("startModifiedAt", startModifiedAt + " 00:00:00");
        } else if (endModifiedAt != null) {
            query.setParameter("endModifiedAt", endModifiedAt + " 23:59:59");
        }

        query.setFirstResult((int) (page * limit));
        query.setMaxResults((int) (limit + 1));

        return query.getResultList();

    }

    @Override
    public List<Schedule> findPublicSchedulesWithFilters(long limit, long page, String modifiedAt, String authorName, String startModifiedAt, String endModifiedAt) {
        StringBuilder sql = new StringBuilder("select s from Schedule s " +
                "left join fetch s.user u " +
                "where s.isPublic = true ");

        if (modifiedAt != null && !modifiedAt.isEmpty()) {
            switch (modifiedAt) {
                case "30m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 30 MINUTE ");
                    break;
                case "1h":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 HOUR ");
                    break;
                case "1d":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 DAY ");
                    break;
                case "1w":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 WEEK ");
                    break;
                case "1m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 1 MONTH ");
                    break;
                case "3m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 3 MONTH ");
                    break;
                case "6m":
                    sql.append("and s.lastModifiedAt >= NOW() - INTERVAL 6 MONTH ");
                    break;
            }
        } else if (startModifiedAt != null && endModifiedAt != null) {
            sql.append("and s.lastModifiedAt between :startModifiedAt and :endModifiedAt ");
        } else if (startModifiedAt != null) {
            sql.append("and s.lastModifiedAt >= :startModifiedAt ");
        } else if (endModifiedAt != null) {
            sql.append("and s.lastModifiedAt <= :endModifiedAt ");
        }

        if (authorName != null && !authorName.isEmpty()) {
            sql.append("and u.name like :authorName ");
        }

        sql.append("order by  s.lastModifiedAt desc"); //수정일 기준 내림차순 정렬

        TypedQuery<Schedule> query = em.createQuery(sql.toString(), Schedule.class);

        if (startModifiedAt != null && endModifiedAt != null) {
            query.setParameter("startModifiedAt", startModifiedAt + " 00:00:00");
            query.setParameter("endModifiedAt", endModifiedAt + " 23:59:59");
        } else if (startModifiedAt != null) {
            query.setParameter("startModifiedAt", startModifiedAt + " 00:00:00");
        } else if (endModifiedAt != null) {
            query.setParameter("endModifiedAt", endModifiedAt + " 23:59:59");
        }

        if (authorName != null && !authorName.isEmpty()) {
            query.setParameter("authorName", "%" + authorName + "%");
        }

        query.setFirstResult((int) (page * limit));
        query.setMaxResults((int) (limit + 1));

        return query.getResultList();
    }
}
