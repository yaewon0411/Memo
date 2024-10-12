package com.my.memo.domain.schedule;


import com.my.memo.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.my.memo.dto.schedule.ReqDto.PublicScheduleFilter;
import static com.my.memo.dto.schedule.ReqDto.UserScheduleFilter;

interface Dao {
    List<Schedule> findPublicSchedulesWithFilters(PublicScheduleFilter publicScheduleFilter);

    List<Schedule> findUserSchedulesWithFilters(User user, UserScheduleFilter userScheduleFilter);

}

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements Dao {
    private final EntityManager em;

    //TODO 테스트하기
    @Override
    public List<Schedule> findUserSchedulesWithFilters(User user, UserScheduleFilter filter) {
        StringBuilder sql = new StringBuilder("select s from Schedule s " +
                "left join fetch s.assignedUserList al " +
                "where s.user = :user ");

        LocalDateTime modifiedTime = null;

        //modifiedTime 계산
        if (filter.getModifiedAt() != null && !filter.getModifiedAt().isEmpty()) {
            modifiedTime = calculateModifiedTime(filter.getModifiedAt());

            if (modifiedTime != null) {
                sql.append("and s.lastModifiedAt >= :modifiedTime ");
            }
        }
        //startModifiedAt, endModifiedAt 필터링
        else if (filter.getStartModifiedAt() != null && filter.getEndModifiedAt() != null) {
            sql.append("and s.lastModifiedAt between :startModifiedAt and :endModifiedAt ");
        } else if (filter.getStartModifiedAt() != null) {
            sql.append("and s.lastModifiedAt >= :startModifiedAt ");
        } else if (filter.getEndModifiedAt() != null) {
            sql.append("and s.lastModifiedAt <= :endModifiedAt ");
        }

        //수정일 기준 내림차순 정렬
        sql.append("order by  s.lastModifiedAt desc");

        TypedQuery<Schedule> query = em.createQuery(sql.toString(), Schedule.class);
        query.setParameter("user", user);

        if (modifiedTime != null) {
            query.setParameter("modifiedTime", modifiedTime);
        }

        if (filter.getStartModifiedAt() != null && filter.getEndModifiedAt() != null) {
            query.setParameter("startModifiedAt", filter.getStartModifiedAt() + " 00:00:00");
            query.setParameter("endModifiedAt", filter.getEndModifiedAt() + " 23:59:59");
        } else if (filter.getStartModifiedAt() != null) {
            query.setParameter("startModifiedAt", filter.getStartModifiedAt() + " 00:00:00");
        } else if (filter.getEndModifiedAt() != null) {
            query.setParameter("endModifiedAt", filter.getEndModifiedAt() + " 23:59:59");
        }

        query.setFirstResult((int) (filter.getPage() * filter.getLimit()));
        query.setMaxResults((int) (filter.getLimit() + 1));

        return query.getResultList();

    }

    @Override
    public List<Schedule> findPublicSchedulesWithFilters(PublicScheduleFilter filter) {
        StringBuilder sql = new StringBuilder("select s from Schedule s " +
                "left join fetch s.user u " +
                "left join fetch s.assignedUserList al " +
                "where s.isPublic = true ");

        LocalDateTime modifiedTime = null;

        //modifiedTime 계산
        if (filter.getModifiedAt() != null && !filter.getModifiedAt().isEmpty()) {
            modifiedTime = calculateModifiedTime(filter.getModifiedAt());

            if (modifiedTime != null) {
                sql.append("and s.lastModifiedAt >= :modifiedTime ");
            }
        }
        //startModifiedAt, endModifiedAt 필터링
        else if (filter.getStartModifiedAt() != null && filter.getEndModifiedAt() != null) {
            sql.append("and s.lastModifiedAt between :startModifiedAt and :endModifiedAt ");
        } else if (filter.getStartModifiedAt() != null) {
            sql.append("and s.lastModifiedAt >= :startModifiedAt ");
        } else if (filter.getEndModifiedAt() != null) {
            sql.append("and s.lastModifiedAt <= :endModifiedAt ");
        }

        //작성자 필터링
        if (filter.getAuthorName() != null && !filter.getAuthorName().isEmpty()) {
            sql.append("and u.name like :authorName ");
        }

        //수정일 기준 내림차순 정렬
        sql.append("order by  s.lastModifiedAt desc");

        TypedQuery<Schedule> query = em.createQuery(sql.toString(), Schedule.class);

        if (modifiedTime != null) {
            query.setParameter("modifiedTime", modifiedTime);
        }

        if (filter.getStartModifiedAt() != null && filter.getEndModifiedAt() != null) {
            query.setParameter("startModifiedAt", filter.getStartModifiedAt() + " 00:00:00");
            query.setParameter("endModifiedAt", filter.getEndModifiedAt() + " 23:59:59");
        } else if (filter.getStartModifiedAt() != null) {
            query.setParameter("startModifiedAt", filter.getStartModifiedAt() + " 00:00:00");
        } else if (filter.getEndModifiedAt() != null) {
            query.setParameter("endModifiedAt", filter.getEndModifiedAt() + " 23:59:59");
        }

        if (filter.getAuthorName() != null && !filter.getAuthorName().isEmpty()) {
            query.setParameter("authorName", "%" + filter.getAuthorName() + "%");
        }

        query.setFirstResult((int) (filter.getPage() * filter.getLimit()));
        query.setMaxResults((int) (filter.getLimit() + 1));

        return query.getResultList();
    }

    private LocalDateTime calculateModifiedTime(String modifiedAt) {
        return switch (modifiedAt) {
            case "30m" -> LocalDateTime.now().minusMinutes(30);
            case "1h" -> LocalDateTime.now().minusHours(1);
            case "1d" -> LocalDateTime.now().minusDays(1);
            case "1w" -> LocalDateTime.now().minusWeeks(1);
            case "1m" -> LocalDateTime.now().minusMonths(1);
            case "3m" -> LocalDateTime.now().minusMonths(3);
            case "6m" -> LocalDateTime.now().minusMonths(6);
            default -> null;
        };
    }
}
