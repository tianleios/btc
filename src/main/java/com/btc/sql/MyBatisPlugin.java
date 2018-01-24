package com.btc.sql;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * Created by tianlei on 2017/十二月/17.
 */
/*
 可以拦截以下类的方法
1. Executor (update, query, flushStatements, commit, rollback,
getTransaction, close, isClosed)
2. ParameterHandler (getParameterObject, setParameters)
3. ResultSetHandler (handleResultSets, handleOutputParameters)
4. StatementHandler (prepare, parameterize, batch, update, query)
* */
@Intercepts(
        {
                @Signature(
                        type = Executor.class,
                        method = "update",
                        args = {MappedStatement.class, Object.class}),
                @Signature(
                        type = StatementHandler.class,
                        method = "prepare",
                        args = {Connection.class, Integer.class}),
                @Signature(
                        type = ParameterHandler.class,
                        method = "setParameters",
                        args = {PreparedStatement.class})
        }
)
public class MyBatisPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if (invocation.getTarget() instanceof StatementHandler) {

            //        RoutingStatementHandler
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();

        }

        if (invocation.getTarget() instanceof DefaultParameterHandler) {
            DefaultParameterHandler parameterHandler = (DefaultParameterHandler)invocation.getTarget();
        }



        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {


    }
}
