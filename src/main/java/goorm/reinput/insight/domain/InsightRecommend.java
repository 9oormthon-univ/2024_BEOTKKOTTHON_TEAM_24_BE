package goorm.reinput.insight.domain;

import goorm.reinput.user.domain.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static goorm.reinput.user.domain.Job.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsightRecommend {
    private String insightMainImage;
    private String insightTitle;
    private String insightSummary;
    private List<String> hashTagList;

    public static List<InsightRecommend> getRecommendInsight(Job job) {
        switch (job) {
            case PLANNER:
                return Arrays.asList(
                        InsightRecommend.builder()
                                .insightMainImage("https://yozm.wishket.com/media/news/2422/image1.png")
                                .insightTitle("커뮤니티 서비스 만들지 마세요, 어차피 실패합니다")
                                .insightSummary("커뮤니티 서비스의 시작과 유지에 관한 어려움을 강조합니다.")
                                .hashTagList(Arrays.asList("#커뮤니티개발", "#스타트업실패", "#서비스창업전략"))
                                .build(),
                        InsightRecommend.builder()
                                .insightMainImage("img1.daumcdn.net/thumb/R1280x0/?fname=http://t1.daumcdn.net/brunch/service/user/1J9E/image/T6kKEP_L15wndi_-Wy_rPghATDU.png")
                                .insightTitle("네이버와 알토스벤처스의 이커머스 풀필먼트 시장 전략 차이 분석")
                                .insightSummary("네이버와 알토스벤처스의 이커머스 풀필먼트 시장 접근 방식 차이를 탐구합니다.")
                                .hashTagList(Arrays.asList("#이커머스풀필먼트", "#네이버vs알토스", "#스타트업투자전략"))
                                .build(),
                        InsightRecommend.builder()
                                .insightMainImage("img1.daumcdn.net/thumb/R1280x0/?fname=http://t1.daumcdn.net/brunch/service/user/5qLd/image/rfdCFkK7Fgts5BWaAh3drxBviY0.jpg")
                                .insightTitle("좋은 유저 저니맵 만드는 방법")
                                .insightSummary("좋은 유저 저니맵의 정의, 목적, 제작 과정을 단계별로 설명합니다.")
                                .hashTagList(Arrays.asList("#유저저니맵", "#UX디자인", "#프로덕트개발"))
                                .build()
                );

            case DESIGNER:
                return Arrays.asList(
                        InsightRecommend.builder()
                                .insightMainImage("https://yozm.wishket.com/media/news/2412/1__애플_Spatial_ui.png")
                                .insightTitle("2024년 UX/UI 디자인 트렌드 훑어보기")
                                .insightSummary("2024년에 주목할 UX/UI 디자인 트렌드를 소개합니다.")
                                .hashTagList(Arrays.asList("#UXUITrends2024", "#디자인트렌드", "#AI디자인"))
                                .build(),
                        InsightRecommend.builder()
                                .insightMainImage("https://reinput.s3.ap-northeast-2.amazonaws.com/uploads/recommend/des2.jpg")
                                .insightTitle("개발자 모드 공개한 ‘피그마 Config 2023’ 주요 리뷰")
                                .insightSummary("'피그마 Config 2023' 컨퍼런스의 주요 업데이트 및 디자인-개발 협업 강화 기능 소개.")
                                .hashTagList(Arrays.asList("#피그마Config2023", "#디자인개발협업", "#피그마업데이트"))
                                .build(),
                        InsightRecommend.builder()
                                .insightMainImage("https://yozm.wishket.com/media/news/2401/image1.jpg")
                                .insightTitle("UX 심리학: 타인의 눈을 의식하는 '호손효과'")
                                .insightSummary("호손효과와 그것이 디지털 환경에서 어떻게 나타나는지 탐구합니다.")
                                .hashTagList(Arrays.asList("#호손효과", "#UX심리학", "#행동변화"))
                                .build()
                );

            case DEVELOPER:
                return Arrays.asList(
                        InsightRecommend.builder()
                                .insightMainImage("https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fc47Ilc%2FbtsEZom8Gno%2FH0mZLxRD6qA2NpFHu3JD5k%2Fimg.png")
                                .insightTitle("분산 시스템에서의 일관성(Consistency) 이야기")
                                .insightSummary("분산 시스템 환경에서 데이터의 일관성을 유지하는 방법을 설명합니다.")
                                .hashTagList(Arrays.asList("#분산시스템", "#일관성", "#CAP이론"))
                                .build(),
                        InsightRecommend.builder()
                                .insightMainImage("https://static.toss.im/assets/payments/contents/progressive-thumb.jpg")
                                .insightTitle("프론트엔드 배포 시스템의 진화 (1) - 결제 SDK에 카나리 배포 적용하기")
                                .insightSummary("프론트엔드 제품에 카나리 배포를 적용한 토스페이먼츠 결제 SDK 팀의 과정 소개.")
                                .hashTagList(Arrays.asList("#카나리배포", "#프론트엔드개발", "#결제SDK"))
                                .build(),
                        InsightRecommend.builder()
                                .insightMainImage("https://oliveyoung.tech/static/4e77f72fe3e694ae36246109c4901426/1ef10/thumbnail.png")
                                .insightTitle("Next.js에서 MSW(Mock Service Worker)로 네트워크 Mocking하기")
                                .insightSummary("Next.js 프로젝트에서 MSW를 사용해 네트워크 요청 모의 과정 소개.")
                                .hashTagList(Arrays.asList("#MSW", "#Nextjs", "#네트워크Mocking"))
                                .build()
                );

            case ETC:
                return Collections.emptyList();

            default:
                return Collections.emptyList();
        }
    }
}
