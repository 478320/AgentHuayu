package org.huayu.infrastructure.converter;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import org.huayu.domain.llm.model.config.ProviderConfig;
import org.huayu.infrastructure.utils.JsonUtils;
import org.huayu.infrastructure.utils.ValidationUtils;


import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 服务商配置转换器 处理加密存储的配置信息 */
@MappedTypes(ProviderConfig.class)
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.LONGVARCHAR, JdbcType.OTHER})
public class ProviderConfigConverter extends BaseTypeHandler<ProviderConfig> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProviderConfig parameter, JdbcType jdbcType)
            throws SQLException {
        String jsonStr = JsonUtils.toJsonString(parameter);
        String encryptedStr = ValidationUtils.EncryptUtils.encrypt(jsonStr);
        ps.setString(i, encryptedStr);
    }

    @Override
    public ProviderConfig getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String encryptedStr = rs.getString(columnName);
        return parseEncryptedJson(encryptedStr);
    }

    @Override
    public ProviderConfig getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String encryptedStr = rs.getString(columnIndex);
        return parseEncryptedJson(encryptedStr);
    }

    @Override
    public ProviderConfig getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encryptedStr = cs.getString(columnIndex);
        return parseEncryptedJson(encryptedStr);
    }

    private ProviderConfig parseEncryptedJson(String encryptedStr) throws SQLException {
        if (encryptedStr == null || encryptedStr.isEmpty()) {
            return new ProviderConfig();
        }

        String jsonStr = ValidationUtils.EncryptUtils.decrypt(encryptedStr);;

        return JsonUtils.parseObject(jsonStr, ProviderConfig.class);

    }
}