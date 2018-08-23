package io.choerodon.wiki.infra.common.enums;

/**
 * Created by Zenger on 2018/7/27.
 */
public enum SpaceStatus {

    /**
     * 处理中
     */
    OPERATIING("operating"),
    /**
     * 处理成功
     */
    SUCCESS("success"),
    /**
     * 删除
     */
    DELETED("deleted"),
    /**
     * 删除
     */
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
