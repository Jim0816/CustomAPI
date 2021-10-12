package com.ljm;

import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.parseMongo.model.SortModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MyTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    //private MongoDBUtil mongoDBUtil;




/*    @Test
    public void insertTest(){
        for(int i=100 ; i<200 ; i++){
            mongoDBUtil.insertDocument("{\n" +
                    "\t\"name\" : \"学生"+i+"\",\n" +
                    "\t\"sex\" : \"男\",\n" +
                    "\t\"email\" : \"12345@qq.com\",\n" +
                    "\t\"belong\" : \"马克思学院\",\n" +
                    "\t\"emailVerified\" : false\n" +
                    "}", "tb_student");
        }
    }*/

    /*@Test
    public void test(){
        QueryModel model = new QueryModel();
        model.setType("tb_student");
        model.setSty("name,sex,email,belong");
        model.setPs(200);
        model.setP(0);

        FilterModel filterModel1 = new FilterModel("sex", "男", "=", "and");
        FilterModel filterModel2 = new FilterModel("belong", "马克思学院", "=", "and");
        FilterModel filterModel3 = new FilterModel("_id", "61220459296b6c635a4c82dd", "=", "or");
        List<FilterModel> filterList = new ArrayList<>();
        filterList.add(filterModel3);
        filterList.add(filterModel1);
        filterList.add(filterModel2);
        model.setFilter(filterList);

        List<Map> list = getByMongo(model);

        for (Map item : list) {
            System.out.println(item);
        }
    }

    public List<Map> getByMongo(QueryModel model) {
        String currentType = model.getType() ;
        //logger.info("查询的表名为 " + currentType);
        List<Map> result = null;
        try {
            //检查集合是否存在
            if (!mongoTemplate.collectionExists(currentType)) {
                return null;
            }
            long qstart = System.currentTimeMillis();
            //查询条件
            Query query = new Query();
            addStys(query, model);//添加返回字段
            if (query == null) {
                return null;
            }
            addFilters(query, model);//添加过滤条件
            System.out.println(query);
            //addSorts(query, model);//添加排序条件
            //long qend = System.currentTimeMillis();
            //System.out.println("构建query耗时：" + (qend - qstart) + " ms");
            //long countStart = System.currentTimeMillis();

            //不分页的总数量
            long totalCount = mongoTemplate.count(query, currentType);
            if (totalCount == 0) {
                return null;
            }
            addLimit(query, model);//添加分页条件
            *//*long countEnd = System.currentTimeMillis();
            System.out.println("count耗时：" + (countEnd - countStart) + " ms");
            *//*

            //计算总页数
            long totalPage = totalCount / model.getPs();
            if (totalCount % model.getPs() > 0) {
                totalPage += 1;
            }

            long start = System.currentTimeMillis();
            //查询
            List<Map> list = mongoTemplate.find(query, Map.class, currentType);
            result = list;

           *//* long end = System.currentTimeMillis();
            System.out.println("查询数据耗时：" + (end - start) + " ms");
            long rstart = System.currentTimeMillis();
            List<Map> realResult = getRealResult(list);
            long rend = System.currentTimeMillis();
            System.out.println("Date格式化耗时：" + (rend - rstart) + " ms");

            result = JSONObject.toJSONString(realResult);
            long reTime = System.currentTimeMillis();
            System.out.println("结果转JSON耗时：" + (reTime - rend) + " ms");*//*

        } catch (Exception ex) {

            //logger.error("查询时出错", ex);
        }

        return result;
    }

    private Query addFilters(Query query, QueryModel model) {
        List<FilterModel> filterList = model.getFilter();
        *//***********字段条件过滤***********//*
        if (filterList != null && filterList.size() > 0) {
            List<Criteria> andList = new ArrayList<>();
            List<Criteria> orList = new ArrayList<>();
            for (FilterModel filter : filterList) {
                String value = filter.getValue();
                Criteria criteria;
                Object v = value;
                //字符串类型
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                    v = value;
                }
                //日期类型
                else if (value.startsWith("'") && value.endsWith("'") && !"in".equals(filter.getOper())) {
                    value = value.substring(1, value.length() - 1);
                    *//*Date date = DateUtil.dateToISODate(value);
                    if (date == null) {
                        date = DateUtil.dateToISODate(value, "yyyy-MM-dd");
                    }
                    v = date;*//*
                }
                //数字类型
                else {
                    try {
                        Double dv = Double.parseDouble(value);
                        v = dv;
                    } catch (Exception ex) {

                    }
                }
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
                        String[] values = filter.getValue().split(",");
                        Object[] para = values;
                        //日期in查询数组
                        Date[] dates = null;
                        //字符串类型
                        if (values != null && values[0].startsWith("\"")) {
                            String[] strs = new String[values.length];
                            for (int i = 0; i < values.length; i++) {
                                value = value.substring(1, value.length() - 1);
                                strs[i] = value;
                            }
                            para = strs;
                        }
                        //日期类型
                        else if (values != null && values[0].startsWith("'")) {
                            //logger.info("判断是否为日期类型");
                            dates = new Date[values.length];
                            for (int i = 0; i < values.length; i++) {
                                //Date date = DateUtil.dateToISODate(values[i].substring(1, values[i].length() - 1), "yyyy-MM-dd");
                                //dates[i] = date;
                            }
                            para = dates;
                            //logger.info("in 里的为日期类型");
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

    private Query addStys(Query query, QueryModel model) {
        *//***********返回字段过滤***********//*
        String[] stys = model.getSty().split(",");
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


    private Query addSorts(Query query, QueryModel model) {
        *//***************排序***************//*
        List<SortModel> sortList = model.getSort();
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

    private Query addLimit(Query query, QueryModel model) {
        *//***************分页*************//*
        //设置页大小
        if (model.getPs() == null || model.getPs() <= 0) {
            model.setPs(50);
        }
        //分页
        if (model.getP() != null && model.getP() > 1) {
            query.skip((model.getP() - 1) * model.getPs());
        }
        query.limit(model.getPs());
        return query;
    }

    private List<Map> getRealResult(List<Map> list) {
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
                map.put(key, date2String((Date) map.get(key)));
            }
        });

        return list;
    }


    public static String date2String(Date date) {
        try {
            if (date == null)
                return null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = sdf.format(date);
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }*/




}
