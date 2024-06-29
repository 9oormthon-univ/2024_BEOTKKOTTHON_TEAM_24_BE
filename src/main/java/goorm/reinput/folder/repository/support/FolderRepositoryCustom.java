package goorm.reinput.folder.repository.support;

import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.folder.domain.dto.InsightSearchDto;

import java.util.List;
import java.util.Optional;

public interface FolderRepositoryCustom {
    Optional<List<FolderResponseDto>> getFolderList(Long userId);
    void updateFolder(Long userId, FolderDto folderDto);
    List<Long> searchInsightInFolder(Long userId, String searchWord);
    List<InsightSearchDto> searchInsight(List<Long> insightIds);
}
