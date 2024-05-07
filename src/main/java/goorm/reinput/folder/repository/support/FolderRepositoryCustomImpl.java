package goorm.reinput.folder.repository.support;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.folder.domain.dto.InsightSearchDto;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.QInsight;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static goorm.reinput.folder.domain.QFolder.folder;
import static goorm.reinput.insight.domain.QHashTag.hashTag;
import static goorm.reinput.insight.domain.QInsight.insight;

@Slf4j
@Repository
public class FolderRepositoryCustomImpl implements FolderRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Autowired
    public FolderRepositoryCustomImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<List<FolderResponseDto>> getFolderList(Long userId) {

        List<FolderResponseDto> results = queryFactory
                .select(folder.folderId, folder.folderName, folder.folderColor, insight.count())
                .from(folder)
                .leftJoin(folder.insightList, insight)
                .where(folder.user.userId.eq(userId))
                .groupBy(folder.folderId)
                .orderBy(folder.folderId.asc())
                .fetch()
                .stream()
                .map(tuple ->
                        new FolderResponseDto(tuple.get(folder.folderId),
                                tuple.get(folder.folderName),
                                tuple.get(folder.folderColor),
                                tuple.get(insight.count())))
                .collect(Collectors.toList());

        return Optional.of(results);
    }

    @Override
    public void updateFolder(Long userId, FolderDto folderDto) {

        // 업데이트 쿼리 시작
        var updateClause = queryFactory.update(folder)
                .where(folder.folderId.eq(folderDto.getFolderId()).and(folder.user.userId.eq(userId)));

        // 조건에 따라 필드 업데이트 설정
        if (folderDto.getFolderName() != null) {
            updateClause.set(folder.folderName, folderDto.getFolderName());
        }
        if (folderDto.getFolderColor() != null) {
            updateClause.set(folder.folderColor, folderDto.getFolderColor());
        }

        long affectedRows = updateClause.execute();

        if (affectedRows == 0) {
            log.error("[CustomFolderRepository] Folder not found with id {}", folderDto.getFolderId());
            throw new EntityNotFoundException("Folder not found with id ");
        }
    }

    @Override
    public List<Long> searchInsightInFolder(Long userId, String searchWord) {
        // 우선순위 1. 제목, 2. 인사이트 summary,3.  tag, 4. memo
        return queryFactory
                .select(insight.insightId)
                .from(insight)
                .leftJoin(insight.hashTagList, hashTag) // 여기서 `hashTagList`는 Insight 엔티티 내의 HashTag 엔티티 리스트를 참조하는 필드입니다.
                .where(
                        insight.folder.user.userId.eq(userId)
                                .and(
                                        insight.insightTitle.contains(searchWord)
                                                .or(insight.insightSummary.contains(searchWord))
                                                .or(hashTag.hashTagName.contains(searchWord))
                                                .or(insight.insightMemo.contains(searchWord))
                                )
                )
                .fetch();
    }
    @Override
    public List<InsightSearchDto> searchInsight(List<Long> insightIds) {
        // Insight ID 리스트를 이용하여 각 Insight에 대한 간단한 정보 조회
        //todo : insight로 책임 분리
        // 모든 태그를 한 번에 조회하여 Map<Long, List<String>> 형태로 매핑
        Map<Long, List<String>> tagsByInsightId = queryFactory
                .select(hashTag.insight.insightId, hashTag.hashTagName)
                .from(hashTag)
                .where(hashTag.insight.insightId.in(insightIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.get(hashTag.insight.insightId), // Insight ID로 그룹핑
                        Collectors.mapping(r -> r.get(hashTag.hashTagName), Collectors.toList()) // 태그 이름 수집
                ));

        // 인사이트 데이터를 조회
        List<InsightSearchDto> insights = queryFactory
                .select(Projections.fields(InsightSearchDto.class,
                        insight.insightId,
                        insight.insightMainImage,
                        insight.insightTitle,
                        insight.insightSummary,
                        insight.insightMemo))
                .from(insight)
                .where(insight.insightId.in(insightIds))
                .fetch();

        // 빌더를 사용하여 각 Insight에 태그 리스트 설정
        return insights.stream().map(dto -> InsightSearchDto.builder()
                        .insightId(dto.getInsightId())
                        .insightMainImage(dto.getInsightMainImage())
                        .insightTitle(dto.getInsightTitle())
                        .insightSummary(dto.getInsightSummary())
                        .insightMemo(dto.getInsightMemo())
                        .insightTagList(tagsByInsightId.getOrDefault(dto.getInsightId(), Collections.emptyList())) // 메모리에서 태그 리스트 매핑
                        .build())
                .collect(Collectors.toList());
    }

    public List<InsightSearchDto> searchInsights(Long userId, String searchWord) {
        // 모든 인사이트 정보와 태그를 조인하여 조회
        List<Tuple> results = getSearchTuples(userId, searchWord);

        // 결과를 Insight ID 별로 DTO와 태그 리스트로 매핑
        Map<Long, InsightSearchDto> insightMap = new HashMap<>();
        Map<Long, List<String>> tagsMap = new HashMap<>();

        results.forEach(result -> {
            Insight insight = result.get(QInsight.insight);
            String tagName = result.get(hashTag.hashTagName);

            InsightSearchDto dto = insightMap.computeIfAbsent(insight.getInsightId(), id -> InsightSearchDto.builder()
                    .insightId(insight.getInsightId())
                    .insightMainImage(insight.getInsightMainImage())
                    .insightTitle(insight.getInsightTitle())
                    .insightSummary(insight.getInsightSummary())
                    .insightMemo(insight.getInsightMemo())
                    .build());

            tagsMap.computeIfAbsent(insight.getInsightId(), k -> new ArrayList<>()).add(tagName);
        });

        // DTO에 태그 정보 설정
        insightMap.forEach((id, dto) -> dto.setInsightTagList(tagsMap.getOrDefault(id, Collections.emptyList())));

        return new ArrayList<>(insightMap.values());
    }

    private List<Tuple> getSearchTuples(Long userId, String searchWord) {
        List<Tuple> results = queryFactory
                .select(insight, hashTag.hashTagName)
                .from(insight)
                .leftJoin(insight.hashTagList, hashTag) // 태그와 조인
                .where(insight.folder.user.userId.eq(userId)
                        .and(insight.insightTitle.contains(searchWord)
                                .or(insight.insightSummary.contains(searchWord))
                                .or(hashTag.hashTagName.contains(searchWord))
                                .or(insight.insightMemo.contains(searchWord))
                        ))
                .fetch();
        return results;
    }
}
