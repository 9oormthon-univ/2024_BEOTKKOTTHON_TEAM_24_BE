package goorm.reinput.folder.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.insight.domain.QHashTag;
import goorm.reinput.insight.domain.dto.InsightResponseDto;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static goorm.reinput.folder.domain.QFolder.folder;
import static goorm.reinput.insight.domain.QHashTag.hashTag;
import static goorm.reinput.insight.domain.QInsight.insight;

@RequiredArgsConstructor
@Repository
@Slf4j
public class CustomFolderRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    @Autowired
    public CustomFolderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(this.entityManager);
    }

    public Optional<List<FolderResponseDto>> getFolderList(Long userId) {
        //insight count는 folder 테이블에 없는 컬럼이므로, folderId를 이용하여 insight 테이블에서 count를 가져와야 한다.
        List<FolderResponseDto> results = queryFactory
                .select(folder.folderId, folder.folderName, insight.count())
                .from(folder)
                .leftJoin(folder.insightList, insight)
                .groupBy(folder.folderId)
                .orderBy(folder.folderId.asc())
                .fetch()
                .stream()
                .map(tuple -> new FolderResponseDto(tuple.get(folder.folderId), tuple.get(folder.folderName),tuple.get(folder.folderColor), tuple.get(insight.count())))
                .collect(Collectors.toList());

        return Optional.of(results);

    }

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
    // user의 모든 폴더내 insight 검색,
    /* \
     우선순위
     1. 제목
     2. 인사이트 요약
     3. 태그
     4. 메모
     */
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

    public List<InsightSimpleResponseDto> searchInsight(List<Long> insightIds) {
        // Insight ID 리스트를 이용하여 각 Insight에 대한 간단한 정보 조회
        //todo : insight로 책임 분리
        List<InsightSimpleResponseDto> insights = queryFactory
                .select(Projections.fields(InsightSimpleResponseDto.class,
                        insight.insightId,
                        insight.insightMainImage,
                        insight.insightTitle,
                        insight.insightSummary))
                .from(insight)
                .where(insight.insightId.in(insightIds))
                .fetch();

        // 빌더를 사용하여 각 Insight에 대한 태그 리스트 설정
        //todo: 쿼리 최적화, 한번에 조회후 insightId로 매핑
        return insights.stream().map(dto -> {
            List<String> tags = queryFactory
                    .select(hashTag.hashTagName)
                    .from(hashTag)
                    .where(hashTag.insight.insightId.eq(dto.getInsightId()))
                    .fetch();

            return InsightSimpleResponseDto.builder()
                    .insightId(dto.getInsightId())
                    .insightMainImage(dto.getInsightMainImage())
                    .insightTitle(dto.getInsightTitle())
                    .insightSummary(dto.getInsightSummary())
                    .hashTagList(tags) // 태그 리스트 설정
                    .build();
        }).collect(Collectors.toList());
    }
}
