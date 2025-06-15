package org.huayu.infrastructure.entity;

public enum Operator {

    USER, ADMIN;

    public boolean needCheckUserId() {
        return this == Operator.USER;
    }
}
