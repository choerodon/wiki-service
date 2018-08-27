package io.choerodon.wiki.infra.common.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Zenger on 2018/8/26.
 */
public class NetworkRequestStatusCodeException extends RuntimeException {
    private final transient Object[] parameters;
    private String code;

    public NetworkRequestStatusCodeException(String code, Object... parameters) {
        super(code);
        this.parameters = parameters;
        this.code = code;
    }

    public NetworkRequestStatusCodeException(String code, Throwable cause, Object... parameters) {
        super(code, cause);
        this.parameters = parameters;
        this.code = code;
    }

    public NetworkRequestStatusCodeException(String code, Throwable cause) {
        super(code, cause);
        this.code = code;
        this.parameters = new Object[0];
    }

    public NetworkRequestStatusCodeException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public String getCode() {
        return this.code;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new LinkedHashMap();
        map.put("code", this.code);
        map.put("message", super.getMessage());
        return map;
    }
}
