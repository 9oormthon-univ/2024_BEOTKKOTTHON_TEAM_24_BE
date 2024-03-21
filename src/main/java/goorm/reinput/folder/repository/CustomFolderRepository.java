package goorm.reinput.folder.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
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
}
