package com.ljm.parseMongo;

import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.model.Condition;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.parseMongo.model.SortModel;
import com.ljm.util.DateUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.*;

public class SqlMongoDBParser{

    /*暂时没有使用到*/
    public static Map<String, String> splitSql(String sql) {
        Map<String,String> sqlSplits = new LinkedHashMap<>();
        try{
            String formatSql = sql.replaceAll("\\s{1,}", " ").toLowerCase();
            //提取where子语句
            int whereSqlStartIndex = formatSql.indexOf("where");
            if(whereSqlStartIndex != -1){
                //包含条件，提取条件
                int whereSqlEndIndex = formatSql.length();
                int orderSqlStartIndex = formatSql.indexOf("order");
                int limitSqlStartIndex = formatSql.indexOf("limit");
                if(orderSqlStartIndex  != -1 || limitSqlStartIndex != -1){
                    whereSqlEndIndex = Math.min(orderSqlStartIndex, limitSqlStartIndex);
                    //提取排序子句
                    if(orderSqlStartIndex != -1){
                        //存在order语句
                        int orderSqlEndIndex = formatSql.length();
                        if(limitSqlStartIndex != -1){
                            orderSqlEndIndex = Math.min(orderSqlEndIndex, limitSqlStartIndex);
                            String limitSql = formatSql.substring(limitSqlStartIndex, formatSql.length());
                            sqlSplits.put("limit", limitSql.trim());
                        }
                        String orderSql = formatSql.substring(orderSqlStartIndex, orderSqlEndIndex);
                        sqlSplits.put("order", orderSql.trim());
                    }
                }
                String whereSql = formatSql.substring(whereSqlStartIndex, whereSqlEndIndex);
                sqlSplits.put("where", whereSql.trim());
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("=====================================sql格式不正确==================================");
        }
        return sqlSplits;
    }

    /*暂时没有使用到*/
    public static Map getConditionInfo(JSONObject jsons){
        String jsonStr = "{\"(name)!={}\": \"jim\",\"(age)!{}\": [10,20,40,55],\"(email)!&{}\": \"%xxx%\",\"@relation\": \"name or (age and email)\"}";
        JSONObject json = JSONObject.parseObject(jsonStr);

        Map<String,Object> map = json.getInnerMap();
        String relation = null;
        Map<String,Condition> conditions = new HashMap<>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if(key.contains("@relation")){
                relation = value.toString();
            }else {
                String fieldName = key.substring(key.indexOf('(')+1, key.indexOf(')'));
                String operate = key.substring(key.indexOf(')')+1, key.indexOf('{'));
                Condition condition = new Condition(fieldName, operate, value);
                conditions.put(fieldName, condition);
            }

        }
        return null;
    }

    /**
     * 解析用户json请求中的过滤条件
     * @param
     * @return
     * @author Jim
     */
    public static List<FilterModel> parseRequestFilter(Map<String, Object> filter, JSONObject data){
        List<FilterModel> resList = new ArrayList<>();
        for (String key : filter.keySet()) {
            String fieldName = key.substring(key.indexOf('(') + 1,key.indexOf(')'));
            //绑定用户传入的条件数据,校验数据是否满足API格式要求
            Object requestVal = data.get(fieldName);
            String value = filter.get(key).toString();
            if(requestVal != null){
                value = requestVal.toString();
            }else{
                System.out.println("数据无效，过滤字段===" + fieldName + "===插入失败!");
            }
            String str = key.substring(key.indexOf('{')+1,key.indexOf('}'));
            String operate = str.split(",")[0];
            String valueType = str.split(",")[1];
            String relation = str.split(",")[2];
            FilterModel filterModel = new FilterModel(fieldName, value, valueType, operate, relation);
            resList.add(filterModel);
        }
        return resList;
    }

    /**
     * 解析用户json请求中的修改字段(用于修改操作)
     * @param
     * @return
     * @author Jim
     */
    public static UpdateDefinition parseRequestUpdate(String updateFields, JSONObject data){
        Update update = new Update();
        Map<String,Object> values = data.getInnerMap();
        if(updateFields == null || updateFields.equals("*") || updateFields.equals("")){
            //默认修改所有字段
            for(String key : values.keySet()){
                update.set(key, values.get(key));
            }
        }else{
            //修改部分字段
        }
        return update;
    }

    /**
     * 添加过滤条件
     * @param
     * @return
     * @author Jim
     */
    /*public static QueryModel transferToQueryModel(JSONObject condition){

    }*/

    /**
     * 添加过滤条件
     * @param
     * @return
     * @author Jim
     */
    public static Query addFilters(Query query, List<FilterModel> filterList) {
        /***********字段条件过滤***********/
        if (filterList != null && filterList.size() > 0) {
            List<Criteria> andList = new ArrayList<>();
            List<Criteria> orList = new ArrayList<>();
            for (FilterModel filter : filterList) {
                Object value = filter.getValue();
                String type = filter.getValueType();
                Criteria criteria;
                Object v = value;

                //处理字段值类型
                switch (type){
                    case "string":
                        v = value.toString();
                        break;
                    case "date":
                        break;
                    case "int":
                        v = value;
                        break;
                    default:
                        break;
                }

                //处理操作类型
                switch (filter.getOper()) {
                    case "=":
                        criteria = Criteria.where(filter.getKey()).is(v);
                        break;
                    case ">":
                        criteria = Criteria.where(filter.getKey()).gt(v);
                        break;
                    case "<":
                        criteria = Criteria.where(filter.getKey()).lt(v);
                        break;
                    case ">=":
                        criteria = Criteria.where(filter.getKey()).gte(v);
                        break;
                    case "<=":
                        criteria = Criteria.where(filter.getKey()).lte(v);
                        break;
                    case "<>":
                        criteria = Criteria.where(filter.getKey()).ne(v);
                        break;
                    case "in":
                        //源数据
                        if (filter.getValue() == null) {
                            continue;
                        }
                        String valueStr = value.toString();
                        String[] values = valueStr.split(",");
                        Object[] para = values;
                        //日期in查询数组
                        Date[] dates = null;
                        //字符串类型
                        if (values != null && values[0].startsWith("\"")) {
                            String[] strs = new String[values.length];
                            for (int i = 0; i < values.length; i++) {
                                valueStr = valueStr.substring(1, valueStr.length() - 1);
                                strs[i] = valueStr;
                            }
                            para = strs;
                        }
                        //日期类型
                        else if (values != null && values[0].startsWith("'")) {
                            dates = new Date[values.length];
                            for (int i = 0; i < values.length; i++) {
                                //Date date = DateUtil.dateToISODate(values[i].substring(1, values[i].length() - 1), "yyyy-MM-dd");
                                //dates[i] = date;
                            }
                            para = dates;
                        }
                        //数字类型
                        else {
                            //logger.info("判断是否为数字类型");
                            Double[] doubles = new Double[values.length];
                            try {
                                for (int i = 0; i < values.length; i++) {
                                    Double dv = Double.parseDouble(values[i]);
                                    doubles[i] = dv;
                                }
                                para = doubles;
                                //logger.info("in 里的为数字类型");
                            } catch (Exception ex) {
                            }
                        }
                        if (para != null && para.length > 0) {
                            //logger.info(para[0].getClass() + "");
                        }
                        //添加in过滤条件
                        criteria = Criteria.where(filter.getKey()).in(para);
                        break;
                    default:
                        continue;
                }
                //或条件
                if (filter.getLogOper().equals("or")) {
                    orList.add(criteria);
                } else {
                    andList.add(criteria);
                }
            }
            //或条件数组,数组上线+1是为了把且条件集合加进去
            Criteria[] orC = new Criteria[1];
            if (orList.size() > 0) {
                orC = new Criteria[orList.size() + 1];
                for (int i = 0; i < orList.size(); i++) {
                    Criteria or = orList.get(i);
                    orC[i] = or;
                }
            }
            //且条件数组
            Criteria[] andC = new Criteria[0];
            if (andList.size() > 0) {
                andC = new Criteria[andList.size()];
                for (int i = 0; i < andList.size(); i++) {
                    Criteria and = andList.get(i);
                    andC[i] = and;
                }
            }
            //把全部且条件放在一起合成一个，再与其他的或条件一起组装
            orC[orC.length - 1] = new Criteria().andOperator(andC);
            query.addCriteria(new Criteria().orOperator(orC));
        }
        return query;
    }

    /**
     * 添加返回结果字段格式
     * @param
     * @return
     * @author Jim
     */
    public static Query addStys(Query query, String resFields) {
        /***********返回字段过滤***********/
        String[] stys = resFields.split(",");
        if (stys == null || stys.length == 0) {
            query = null;
            return query;
        }
        query.fields().exclude("_id");
        if (!(stys.length == 1 && stys[0].equals("*"))) {
            for (String sty : stys) {
                query.fields().include(sty.trim());
            }
        }
        return query;
    }

    /**
     * 添加排序规则
     * @param
     * @return
     * @author Jim
     */
    public static Query addSorts(Query query, List<SortModel> sortList) {
        /***************排序***************/
        if (sortList != null && sortList.size() > 0) {
            List<Sort.Order> orders = new ArrayList<>();
            //添加多个排序条件
            for (int i = 0; i < sortList.size(); i++) {
                SortModel sortModel = sortList.get(i);
                Sort.Direction direction = Sort.Direction.ASC;
                if (sortModel.getSortWay() < 0) {
                    direction = Sort.Direction.DESC;
                }
                Sort.Order order = new Sort.Order(direction, sortModel.getSortName());
                orders.add(order);
            }
            query.with(Sort.by(orders));
        }
        return query;
    }

    /**
     * 添加分页查询条件
     * @param
     * @return
     * @author Jim
     */
    public static Query addLimit(Query query, QueryModel model) {
        /***************分页*************/
        //设置页大小
        if (model.getPageSize() == null || model.getPageSize() <= 0) {
            model.setPageSize(50);
        }
        //分页
        if (model.getPageNow() != null && model.getPageNow() > 1) {
            query.skip((model.getPageNow() - 1) * model.getPageSize());
        }
        query.limit(model.getPageSize());
        return query;
    }

    /**
     * 格式化返回结果
     * @param
     * @return
     * @author Jim
     */
    public static List<Map> getRealResult(List<Map> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        //日期类型的字段
        List<Object> dateKeys = new ArrayList<>();
        for (Object key : list.get(0).keySet()) {
            Object value = list.get(0).get(key);
            if (value instanceof Date) {
                dateKeys.add(key);
            }
        }
        list.stream().parallel().forEach(map -> {
            for (Object key : dateKeys) {
                map.put(key, DateUtil.date2String((Date) map.get(key)));
            }
        });

        return list;
    }








}
