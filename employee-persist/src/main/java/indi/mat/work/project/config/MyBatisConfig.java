package indi.mat.work.project.config;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import indi.mat.work.project.model.BaseModel;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Date;

@Configuration
@MapperScan("indi.mat.work.project.dao.*")
public class MyBatisConfig {

    // https://www.cnblogs.com/dogdogwang/p/13019060.html

    @Bean
    public ModelInterceptor modelInterceptor() {
        return new ModelInterceptor();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }


    /**
     * 全局处理inUser, inDate, lastEditUser, lastEditDate字段
     */
    @Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
    public static class ModelInterceptor implements Interceptor {
        private static final String COLLECTION_IN_MAP = "collection";

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                Object parameter = invocation.getArgs()[1];
//                String user = WebUtil.currentUserEmail(DEFAULT_USER_EMAIL);
                String user = "admin";
                if (parameter instanceof BaseModel) {
                    updateModel(sqlCommandType, (BaseModel) parameter, user);
                } else if (parameter instanceof MapperMethod.ParamMap) {
                    MapperMethod.ParamMap<?> paramMap = ((MapperMethod.ParamMap<?>) parameter);
                    // see: org.apache.ibatis.reflection.ParamNameResolver.wrapToMapIfCollection
                    if (paramMap.containsKey(COLLECTION_IN_MAP) && paramMap.get(COLLECTION_IN_MAP) != null) {
                        Collection<?> values = (Collection<?>) (paramMap.get(COLLECTION_IN_MAP));
                        if (!values.isEmpty()) {
                            for (Object value : values) {
                                if (!(value instanceof BaseModel)) {
                                    break;
                                }
                                BaseModel model = (BaseModel) value;
                                updateModel(sqlCommandType, model, user);
                            }
                        }
                    }
                    //MybatisPlus's update function will add an attribute of Constants.ENTITY
                    else if (paramMap.containsKey(Constants.ENTITY) && ObjectUtils.isNotEmpty(paramMap.get(Constants.ENTITY)) && paramMap.get(Constants.ENTITY) instanceof BaseModel) {
                        updateModel(sqlCommandType, (BaseModel) paramMap.get(Constants.ENTITY), user);
                    }
                }
            }
            return invocation.proceed();
        }

        private void updateModel(SqlCommandType sqlCommandType, BaseModel model, String user) {
            Date now = new Date();
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                model.setOpUser(user);
                model.setOpDate(now);
                model.setLastOpUser(user);
                model.setLastOpDate(now);
            } else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                model.setLastOpUser(user);
                model.setLastOpDate(now);
            }
        }

        @Override
        public Object plugin(Object target) {
            return Plugin.wrap(target, this);
        }
    }
}
