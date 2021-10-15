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

    /**
     * 解析用户json请求中的过滤条件
     * @param
     * @return
     * @author Jim
     */
    public static List<FilterModel> parseFilter(Map<String, Object> filterMap){
        List<FilterModel> filters = new ArrayList<>();
        for(String key : filterMap.keySet()){
            //"(name){<>,string,and}": "jim",
            String fieldName = key.substring(key.indexOf("(") + 1, key.indexOf(")"));
            String rule = key.substring(key.indexOf("{") + 1, key.indexOf("}"));
            String[] splits = rule.split(",");

            String value = filterMap.get(key).toString();
            String valueOperate = splits[0];
            String valueType = splits[1];
            String logicRelation = splits[2];
            FilterModel filterModel = new FilterModel(fieldName, value, valueType, valueOperate, logicRelation);
            filters.add(filterModel);
        }
        return filters;
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
