package com.fiap.br.services;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fiap.br.models.enums.CRUDOperation;
import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.Required;
import com.fiap.br.util.connection.DatabaseConnection;

public class QueryExecutor {

    private Connection connection = DatabaseConnection.getConnection();

    public <T> List<T> execute(Class<T> entityClass, String sql, Object[] params, CRUDOperation operation,
            Optional<Integer> id) throws SQLException {
        List<T> results = new ArrayList<>();

        try (PreparedStatement pstm = prepareStatement(sql, params, id)) {
            if (operation == CRUDOperation.READ) {
                results = executeRead(pstm, entityClass);
            } else {
                executeUpdate(pstm);
            }
        }

        return results;
    }

    private PreparedStatement prepareStatement(String sql, Object[] params, Optional<Integer> id) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement(sql);

        if (id.isPresent()) {
            Integer idValue = id.get();
            params = (params == null) ? new Object[] { idValue } : Arrays.copyOf(params, params.length + 1);
            params[params.length - 1] = idValue;
        }

        if (params != null) {
            setParameters(pstm, params);
        }

        return pstm;
    }

    private void setParameters(PreparedStatement statement, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Enum) {
                statement.setString(i + 1, ((Enum<?>) param).name());
            } else {
                statement.setObject(i + 1, param);
            }
        }
    }

    private <T> List<T> executeRead(PreparedStatement pstm, Class<T> entityClass) throws SQLException {
        List<T> results = new ArrayList<>();
        try (ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                T entity = mapResultSetToEntity(rs, entityClass);
                results.add(entity);
            }
        }
        return results;
    }

    private void executeUpdate(PreparedStatement pstm) throws SQLException {
        int affectedRows = pstm.executeUpdate();
        System.out.println("Rows affected: " + affectedRows);
    }

    private <T> T mapResultSetToEntity(ResultSet rs, Class<T> entityClass) throws SQLException {
        T entity = null;
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();
            Map<String, String> columnNames = getColumnNames(entityClass);

            for (Map.Entry<String, String> entry : columnNames.entrySet()) {
                String fieldName = entry.getKey();
                String columnName = entry.getValue();
                Field field = entityClass.getDeclaredField(fieldName);
                field.setAccessible(true);

                Object value = rs.getObject(columnName);
                handleSpecialCases(field, entity, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void handleSpecialCases(Field field, Object entity, Object value) throws IllegalAccessException {
        /* Por algum motivo o driver do DB ta retornando todo numero como BigDecimal */
        if (value instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) value;
            if (field.getType().isAssignableFrom(double.class)) {
                field.set(entity, bigDecimalValue.doubleValue());
            } else if (field.getType().isAssignableFrom(int.class)) {
                field.set(entity, bigDecimalValue.intValue());
            }
        } else if (field.getType().isEnum()) {
            @SuppressWarnings("unchecked")
            Class<Enum<?>> enumType = (Class<Enum<?>>) field.getType();
            Enum<?> enumValue = Enum.valueOf(enumType.asSubclass(Enum.class), value.toString());
            field.set(entity, enumValue);
        } else if (field.getType() == LocalDate.class && value instanceof Timestamp) {
            Timestamp timestampValue = (Timestamp) value;
            LocalDate localDateValue = timestampValue.toLocalDateTime().toLocalDate();
            field.set(entity, localDateValue);
        } else {
            field.set(entity, value);
        }
    }

    public <T> Map<String, String> getRequiredColumnNames(Class<T> entityClass) {
        Map<String, String> columnNames = new LinkedHashMap<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CollumnName.class) && field.isAnnotationPresent(Required.class)) {
                CollumnName annotation = field.getAnnotation(CollumnName.class);
                columnNames.put(field.getName(), annotation.value());
            }
        }
        return columnNames;
    }

    private <T> Map<String, String> getColumnNames(Class<T> entityClass) {
        Map<String, String> columnNames = new LinkedHashMap<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CollumnName.class)) {
                CollumnName annotation = field.getAnnotation(CollumnName.class);
                columnNames.put(field.getName(), annotation.value());
            }
        }
        return columnNames;
    }
}
