package com.sandesh.paymentgatewaydemo.enums;

import java.util.HashMap;
import java.util.Map;

public enum AppId {
    ECOM1("1", "eCom1"),
    ECOM2("2", "eCom2"),
    ECOM3("3", "eCom3");

    private final String id;
    private final String name;

    private static final Map<String, AppId> BY_ID = new HashMap<>();
    private static final Map<String, AppId> BY_NAME = new HashMap<>();

    static {
        for (AppId appId : values()) {
            BY_ID.put(appId.id, appId);
            BY_NAME.put(appId.name, appId);
        }
    }

    AppId(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static AppId fromId(String id) {
        AppId appId = BY_ID.get(id);
        if (appId == null) {
            throw new IllegalArgumentException("Invalid AppId: " + id);
        }
        return appId;
    }

    public static AppId fromName(String name) {
        AppId appId = BY_NAME.get(name);
        if (appId == null) {
            throw new IllegalArgumentException("Invalid AppId name: " + name);
        }
        return appId;
    }

    public static String getNameFromId(String id) {
        AppId appId = fromId(id);
        return appId.getName();
    }

}