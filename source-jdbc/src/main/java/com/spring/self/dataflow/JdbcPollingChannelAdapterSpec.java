package com.spring.self.dataflow;

import org.springframework.dao.DataAccessException;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jdbc.ExpressionEvaluatingSqlParameterSourceFactory;
import org.springframework.integration.jdbc.SqlParameterSourceFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.messaging.Message;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JdbcPollingChannelAdapterSpec extends IntegrationObjectSupport implements MessageSource<Object> {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final String selectQuery;
    private volatile RowMapper<?> rowMapper;
    private volatile SqlParameterSource sqlQueryParameterSource;
    private volatile boolean updatePerRow = false;
    //private volatile String updateSql;
    private volatile SqlParameterSourceFactory sqlParameterSourceFactory = new ExpressionEvaluatingSqlParameterSourceFactory();
    private volatile boolean sqlParameterSourceFactorySet;
    private volatile int maxRowsPerPoll = 0;

    public JdbcPollingChannelAdapterSpec(DataSource dataSource, String selectQuery) {
        this.jdbcOperations=new NamedParameterJdbcTemplate(dataSource);
        this.selectQuery = selectQuery;
    }

    public void setRowMapper(RowMapper<?> rowMapper) {
        this.rowMapper = rowMapper;
    }

   /* public void setUpdateSql(String updateSql) {
        this.updateSql = updateSql;
    }*/

    public void setUpdatePerRow(boolean updatePerRow) {
        this.updatePerRow = updatePerRow;
    }

    public void setUpdateSqlParameterSourceFactory(SqlParameterSourceFactory sqlParameterSourceFactory) {
        this.sqlParameterSourceFactory = sqlParameterSourceFactory;
        this.sqlParameterSourceFactorySet = true;
    }

    public void setSelectSqlParameterSource(SqlParameterSource sqlQueryParameterSource) {
        this.sqlQueryParameterSource = sqlQueryParameterSource;
    }

    public void setMaxRowsPerPoll(int maxRows) {
        this.maxRowsPerPoll = maxRows;
    }

    protected void onInit() throws Exception {
        super.onInit();
        if (!this.sqlParameterSourceFactorySet && this.getBeanFactory() != null) {
            ((ExpressionEvaluatingSqlParameterSourceFactory)this.sqlParameterSourceFactory).setBeanFactory(this.getBeanFactory());
        }

    }

    public Message<Object> receive() {
        Object payload = this.poll();
        return payload == null ? null : this.getMessageBuilderFactory().withPayload(payload).build();
    }

    private Object poll() {
        List<?> payload = this.doPoll(this.sqlQueryParameterSource);
        if (payload.size() < 1) {
            payload = null;
        }

       /* if (payload != null && this.updateSql != null) {
            if (this.updatePerRow) {
                Iterator var2 = payload.iterator();

                while(var2.hasNext()) {
                    Object row = var2.next();
                    this.executeUpdateQuery(row);
                }
            } else {
                this.executeUpdateQuery(payload);
            }
        }*/
        System.out.println("return payload.size="+payload.size());
        return payload;
    }

   /* private void executeUpdateQuery(Object obj) {
        SqlParameterSource updateParamaterSource = this.sqlParameterSourceFactory.createParameterSource(obj);
        this.jdbcOperations.update(this.updateSql, updateParamaterSource);
    }*/

    protected List<?> doPoll(SqlParameterSource sqlQueryParameterSource) {
        List<?> payload = null;
        final RowMapper<?> rowMapper = this.rowMapper == null ? new ColumnMapRowMapper() : this.rowMapper;
        Object resultSetExtractor;
        if (this.maxRowsPerPoll > 0) {
            resultSetExtractor = new ResultSetExtractor<List<Object>>() {
                public List<Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
                    List<Object> results = new ArrayList(JdbcPollingChannelAdapterSpec.this.maxRowsPerPoll);
                    int rowNum = 0;

                    while(rs.next() && rowNum < JdbcPollingChannelAdapterSpec.this.maxRowsPerPoll) {
                        results.add(((RowMapper)rowMapper).mapRow(rs, rowNum++));
                    }

                    return results;
                }
            };
        } else {
            ResultSetExtractor<List<Object>> temp = new RowMapperResultSetExtractor((RowMapper)rowMapper);
            resultSetExtractor = temp;
        }

        if (sqlQueryParameterSource != null) {
            payload = (List)this.jdbcOperations.query(this.selectQuery, sqlQueryParameterSource, (ResultSetExtractor)resultSetExtractor);
        } else {
            payload = (List)this.jdbcOperations.getJdbcOperations().query(this.selectQuery, (ResultSetExtractor)resultSetExtractor);
        }

        return payload;
    }

}
