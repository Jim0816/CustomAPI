package com.ljm.parseMongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.model.Condition;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.parseMongo.model.SortModel;
import com.ljm.util.DateUtil;
import com.ljm.util.JSONUtil;
import com.ljm.util.StringUtil;
import com.ljm.vo.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class SqlMongoDBParser{

    /**
     * 根据字段要求返回合法值 (数据中不存在当前字段)
     * @return 字段值
     * @author Jim
     */
    public static Object getValueByFieldInfo(Field field){
        switch (field.getType()){
            case "Array":
                return field.getDefaultVal() == null ? new ArrayList<>() : field.getDefaultVal();
            case "Object":
                return field.getDefaultVal() == null ? new HashMap<>() : field.getDefaultVal();
            default:
                return field.getDefaultVal() == null ? "" : field.getDefaultVal();
        }
    }

    /**
     * 根据字段要求校验字段数据 (数据中存在当前字段)
     * @return 字段值
     * @author Jim
     */
    public static boolean checkFieldDataByFieldInfo(Field field, Object data){
        boolean res = true;
        switch (field.getType()){
            case "String":
                try{
                    //类型校验
                    String str = String.valueOf(data);
                    //长度校验
                    if(str.length() > field.getLength()){
                        res = false;
                    }
                }catch (Exception e){
                    //转换异常
                    res = false;
                }
                return res;
            case "Int":
                try{
                    //类型校验 ""转换失败
                    Integer val = -1; //-1代表整型中null
                    String str = data.toString();
                    if(!str.equals("")){
                        val = Integer.valueOf(str);
                    }
                    //长度校验
                    if((val + "").length() > field.getLength()){
                        res = false;
                    }
                }catch (Exception e){
                    //转换异常
                    res = false;
                }
                return res;
            case "Float":
                try{
                    //类型校验
                    Float val = (Float) data;
                }catch (Exception e){
                    //转换异常
                    res = false;
                }
                return res;
            case "Double":
                try{
                    //类型校验
                    Double val = (Double) data;
                }catch (Exception e){
                    //转换异常
                    res = false;
                }
                return res;
            case "Object":
                try{
                    //类型校验
                    JSONObject jsonObject =  JSON.parseObject(data.toString());
                }catch (Exception e){
                    //转换异常
                    res = false;
                }
                return res;
            case "Array":
                try{
                    //类型校验
                    JSONArray jsonArray =  JSON.parseArray(data.toString());
                }catch (Exception e){
                    //转换异常
                    res = false;
                }
                return res;
            default:
                return true;
        }
    }

    /**
     * 根据表结构校验数据是否合理   如当前插入的数据字段不满足当前版本(最新)的表结构信息 数据中有字段，但是表结构中没有描述当前字段类型
     * 校验策略：以元数据为标准，校验数据。
     * ①如果数据存在冗余字段，则将冗余字段入库
     * ②如果数据存在字段缺失，则按照元数据规则补齐
     * ③如果字段不缺失，则按照元数据进行类型、格式校验
     * @param metaFieldsList 表示集合（表）的字段结构信息 data表示将要添加或者修改的数据
     * @return
     * @author Jim
     */
    public static Map<String,Object> checkOperateByMetaData(List<Map<String,Object>> metaFieldsList, Object data){
        //校验结果
        Map<String,Object> checkResult = new HashMap<>();
        JSONObject jsonObject = JSONUtil.parseBeanToJSONObject(data);
        //data数据对象
        Map<String, Object> dataMap = jsonObject.getInnerMap();
        //只考虑一级键值对
        int dataFieldsCount = dataMap.size();
        //记录已经校验过的数据
        List<String> checkedFields = new ArrayList<>();

        if(dataFieldsCount < 1){
            //当前data中没有数据字段, 不需要进行校验了，直接退出
            log.info("数据中没有任何字段，拒绝校验!");
            checkResult.put("result", false);
            return checkResult;
        }

        for(Map<String,Object> fieldMap : metaFieldsList){
            //字段对象转换
            Field field = null;
            try{
                field = JSONUtil.parseMapToBean(Field.class, fieldMap);
            }catch (Exception e){
                log.info("字段转换异常!");
                checkResult.put("result", false);
                return checkResult;
            }

            if(field == null){
                //filed转换不成功
                log.info("元数据字段信息转换失败，拒绝校验!");
                checkResult.put("result", false);
                return checkResult;
            }

            //查看data中是否有这个字段
            if(dataMap.containsKey(field.getName())){
                //data存在当前字段, 进行数据类型校验
                if(!checkFieldDataByFieldInfo(field, dataMap.get(field.getName()))){
                    //校验失败
                    log.info("字段校验失败，拒绝操作! == 字段："+field.getName());
                    checkResult.put("result", false);
                    return checkResult;
                }
            }else{
                //data不存在当前字段,进行填补
                if(field.getIsRequire() == 1){
                    //必须字段，不能缺失，拒绝操作，返回前端重新填写
                    log.info("必填字段缺失，拒绝操作!   == 字段: "+field.getName());
                    checkResult.put("result", false);
                    return checkResult;
                }
                //选填字段，自动补齐
                dataMap.put(field.getName(), getValueByFieldInfo(field));
            }
            checkedFields.add(field.getName());
        }

        //data中字段出现冗余，元数据中不存在该字段  策略：冗余字段也存入数据库
        if(checkedFields.size() < dataFieldsCount){
            for(String key : dataMap.keySet()){
                boolean isFind = false;
                for(String checkedField : checkedFields){
                   if(checkedField.equals(key)){
                       //字段匹配
                       isFind = true;
                       break;
                   }
                }
                //没有匹配
                if(!isFind){
                    log.info("接收到的数据中存在冗余字段: "+key);
                }
            }
        }
        //通过
        checkResult.put("result", true);
        checkResult.put("data", dataMap);
        return checkResult;
    }

    /**
     * 根据表结构格式化数据
     * @param tableMetaInfo 表示集合（表）的结构信息 data表示将要添加或者修改的数据
     * @return
     * @author Jim
     */
    public static Object formatDateByMetaData(Map tableMetaInfo, Object data){
        List<Map> fields = (List<Map>) tableMetaInfo.get("fields");
        JSONObject jsonObject = JSON.parseObject(data.toString());
        for(Map field : fields){
            //{"name": "uuid", "type": "String", "default": "", "remark": "user主键"}
            String fieldName = field.get("name").toString();
            if(jsonObject.get(fieldName) == null ){
                if("uuid".equals(fieldName)){
                    jsonObject.put(fieldName, StringUtil.generateUUID());
                }else if("isDelete".equals(fieldName)){
                    jsonObject.put(fieldName, 0);
                }else if(fieldName.equals("createTime") || fieldName.equals("updateTime")){
                    jsonObject.put(fieldName, LocalDateTime.now());
                }else{
                    jsonObject.put(fieldName, "");
                }
            }
        }
        return jsonObject;
    }

    /**
     * 解析用户json请求中的过滤条件
     * @param
     * @return
     * @author Jim
     */
    public static List<FilterModel> parseFilter(Map<String, Object> filterMap){
        List<FilterModel> filters = new ArrayList<>();
        for(String key : filterMap.keySet()){
            //"(name){<>,string,const,and}": "jim",
            //字段名称
            String fieldName = key.substring(key.indexOf("(") + 1, key.indexOf(")"));
            String rule = key.substring(key.indexOf("{") + 1, key.indexOf("}"));
            String[] splits = rule.split(",");
            //字段值
            String value = filterMap.get(key).toString();
            //比较规则
            String valueOperate = splits[0];
            //值的数据类型
            String valueType = splits[1];
            //值为常量还是变量  常量直接去配置值，变量取接口中数据
            String replace = splits[2];
            String logicRelation = splits[3];

            FilterModel filterModel = new FilterModel(fieldName, replace.equals("const") ? value : "$var", valueType, valueOperate, logicRelation);
            filters.add(filterModel);
        }
        return filters;
    }

    /**
     * 解析用户json请求中的排序条件
     * @param
     * @return
     * @author Jim
     */
    public static List<SortModel> parseSort(Map<String,Integer> sortMap){
        List<SortModel> sortModels = new LinkedList<>();
        if(sortMap.size() > 0){
            for(String field : sortMap.keySet()){
                SortModel sortModel = new SortModel(field, sortMap.get(field));
                sortModels.add(sortModel);
            }
        }
        return sortModels;
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
            String[] fields = updateFields.split(",");
            for(String field : fields){
                update.set(field, values.get(field));
            }
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
                        v = Integer.valueOf(value.toString());
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
