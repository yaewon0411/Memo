package com.my.memo.domain.schedule;


import com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto;
import com.my.memo.domain.user.User;
import com.my.memo.dto.schedule.req.PublicScheduleFilter;
import com.my.memo.dto.schedule.req.UserScheduleFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

interface Dao {
    List<ScheduleWithCommentAndUserCountsDto> findPublicSchedulesWithFilters(PublicScheduleFilter publicScheduleFilter);

    List<ScheduleWithCommentAndUserCountsDto> findUserSchedulesWithFilters(User user, UserScheduleFilter userScheduleFilter);

}

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements Dao {
    private final EntityManager em;
    private final Clock clock;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //TODO 테스트하기
    @Override
    public List<ScheduleWithCommentAndUserCountsDto> findUserSchedulesWithFilters(User user, UserScheduleFilter filter) {

        String jpql =
                "select new com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto(s, count(distinct c.id) as commentCnt, count(distinct su.id) as assignedUserCnt) " +
                        "from Schedule s " +
                        "left join Comment c on s.id = c.schedule.id " +
                        "left join ScheduleUser su on s.id = su.schedule.id " +
                        "where s.user = :user ";

        StringBuilder sql = new StringBuilder(jpql);

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
        sql.append("group by s ");
        sql.append("order by s.lastModifiedAt desc");

        TypedQuery<ScheduleWithCommentAndUserCountsDto> query = em.createQuery(sql.toString(), ScheduleWithCommentAndUserCountsDto.class);
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
    public List<ScheduleWithCommentAndUserCountsDto> findPublicSchedulesWithFilters(PublicScheduleFilter filter) {

        String jpql =
                "select new com.my.memo.domain.schedule.dto.ScheduleWithCommentAndUserCountsDto(s, count(distinct c.id) as commentCnt, count(distinct su.id) as assignedUserCnt) " +
                        "from Schedule s " +
                        "left join Comment c on s.id = c.schedule.id " +
                        "left join ScheduleUser su on s.id = su.schedule.id " +
                        "left join fetch s.user u " +
                        "where s.isPublic = true ";

        StringBuilder sql = new StringBuilder(jpql);

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
        sql.append("group by s ");
        sql.append("order by  s.lastModifiedAt desc");

        TypedQuery<ScheduleWithCommentAndUserCountsDto> query = em.createQuery(sql.toString(), ScheduleWithCommentAndUserCountsDto.class);

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
        LocalDateTime now = LocalDateTime.now(clock);
        log.info("현재 시각: {}", now);
        return switch (modifiedAt) {
            case "30m" -> now.minusMinutes(30);
            case "1h" -> now.minusHours(1);
            case "1d" -> now.minusDays(1);
            case "1w" -> now.minusWeeks(1);
            case "1m" -> now.minusMonths(1);
            case "3m" -> now.minusMonths(3);
            case "6m" -> now.minusMonths(6);
            default -> null;
        };
    }
}
