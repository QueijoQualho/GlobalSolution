
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
import com.fiap.br.util.annotations.JoinTable;
import com.fiap.br.util.connection.DatabaseConnection;

import jakarta.validation.constraints.NotNull;

public class QueryExecutor {

    private final Connection connection = DatabaseConnection.getConnection();

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
        setParameters(pstm, params);
        return pstm;
    }

    private void setParameters(PreparedStatement statement, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof Enum<?>) {
                    statement.setString(i + 1, ((Enum<?>) param).name());
                } else {
                    statement.setObject(i + 1, param);
                }
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
        System.out.println("Linhas afetadas: " + affectedRows);
    }

    @SuppressWarnings("unchecked")
    private <T> T mapResultSetToEntity(ResultSet rs, Class<T> entityClass) throws SQLException {
        T entity;
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();
            Map<String, String> columnNames = getColumnNames(entityClass);

            for (Map.Entry<String, String> entry : columnNames.entrySet()) {
                String fieldName = entry.getKey();
                String columnName = entry.getValue();
                Field field = entityClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = rs.getObject(columnName);
                setFieldValue(field, entity, value);
            }

            // Handle joins
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(JoinTable.class)) {
                    Class<?> joinClass = field.getAnnotation(JoinTable.class).value();
                    Object joinEntity = mapResultSetToEntity(rs, joinClass);

                    // Initialize the list if necessary
                    if (List.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        if (field.get(entity) == null) {
                            field.set(entity, new ArrayList<>());
                        }
                        ((List<Object>) field.get(entity)).add(joinEntity);
                    } else {
                        field.setAccessible(true);
                        field.set(entity, joinEntity);
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao mapear ResultSet para a entidade " + entityClass.getName(), e);
        }
        return entity;
    }

    private void setFieldValue(Field field, Object entity, Object value) throws IllegalAccessException {
        if (value instanceof BigDecimal) {
            setBigDecimalField(field, entity, (BigDecimal) value);
        } else if (field.getType().isEnum()) {
            setEnumField(field, entity, value);
        } else if (field.getType() == LocalDate.class && value instanceof Timestamp) {
            setLocalDateField(field, entity, (Timestamp) value);
        } else {
            field.set(entity, value);
        }
    }

    private void setBigDecimalField(Field field, Object entity, BigDecimal value) throws IllegalAccessException {
        if (field.getType().isAssignableFrom(double.class)) {
            field.set(entity, value.doubleValue());
        } else if (field.getType().isAssignableFrom(int.class)) {
            field.set(entity, value.intValue());
        }
    }

    private void setEnumField(Field field, Object entity, Object value) throws IllegalAccessException {
        @SuppressWarnings("unchecked")
        Class<Enum<?>> enumType = (Class<Enum<?>>) field.getType();
        Enum<?> enumValue = Enum.valueOf(enumType.asSubclass(Enum.class), value.toString());
        field.set(entity, enumValue);
    }

    private void setLocalDateField(Field field, Object entity, Timestamp value) throws IllegalAccessException {
        LocalDate localDateValue = value.toLocalDateTime().toLocalDate();
        field.set(entity, localDateValue);
    }

    public <T> Map<String, String> getRequiredColumnNames(Class<T> entityClass) {
        return getColumnNames(entityClass, true);
    }

    private <T> Map<String, String> getColumnNames(Class<T> entityClass) {
        return getColumnNames(entityClass, false);
    }

    private <T> Map<String, String> getColumnNames(Class<T> entityClass, boolean requiredOnly) {
        Map<String, String> columnNames = new LinkedHashMap<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CollumnName.class)
                    && (!requiredOnly || field.isAnnotationPresent(NotNull.class))) {
                CollumnName annotation = field.getAnnotation(CollumnName.class);
                columnNames.put(field.getName(), annotation.value());
            }
        }
        return columnNames;
    }
}