package io.choerodon.wiki.infra.common.enums;

/**
 * Created by Zenger on 2018/7/27.
 */
public enum SpaceStatus {
    OPERATIING("operating"),
    SUCCESS("success"),
    DELETED("deleted"),
    FAILED("failed");

    private String value;

    SpaceStatus(String value) {
        this.value = value;
    }

    public static SpaceStatus forString(String value) {
        switch (value) {
            case "operating":
                return SpaceStatus.OPERATIING;
            case "success":
                return SpaceStatus.SUCCESS;
            case "deleted":
                return SpaceStatus.DELETED;
            case "failed":
                return SpaceStatus.FAILED;
            default:
                return null;
        }
    }

    public String getSpaceStatus() {
        return value;
    }
}
