package team.project.Entity;

/**
 * 개발 5단계
 * 1. 요구사항 분석
 * 2. 설계
 * 3. 프로그래밍
 * 4. 테스트
 * 5. 유지보수
 * */
public enum Progress {

    /**
     * 요구사항 분석
    * */
    Requirements_Analysis,

    /**
     * 설계 (구체적 설계)
     * */
    Design,

    /**
     * 프로그래밍 (개발 단계)
     * 설계에 맞도록 시스템을 구현
     * */
    Programming,

    /**
     * 테스트
     * 구현된 시스템이 정상 작동 하는지, 요건과 부합하는지 테스트
     * */
    Testing,

    /**
     * 유지보수
     * 배포 및 운영
     * */
    Maintenance,
}
