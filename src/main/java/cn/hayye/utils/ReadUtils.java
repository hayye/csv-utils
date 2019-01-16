package cn.hayye.utils;


import cn.hayye.annotation.CsvColumn;
import com.csvreader.CsvReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * 读取CSV文件的工具类。
 * 需配合自定义注解@CsvColumn的支持，功能暂时不完善，仅支持字符串。
 */
public class ReadUtils {
        public void readCSV(FileInputStream fileInputStream, Charset charset ,Class clazz) throws Exception {
            CsvReader csvReader = new CsvReader(fileInputStream, charset);
            csvReader.readHeaders();
            Object obj = clazz.newInstance();
            List<Object> list =new LinkedList<>();
            while (csvReader.readRecord()){
                /*
                // 读一整行
                System.out.println(csvReader.getRawRecord());
                // 读这行的某一列
                System.out.println(csvReader.get("Link"));
                */
                list.add(this.setValueBatch(obj,csvReader));
            }
        }

        /**
         * 目前仅支持String类型
         * @param object
         * @param csvReader
         * @throws Exception
         */
        public Object setValueBatch(Object object, CsvReader csvReader) throws Exception {
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //判断属性上是否有注解
                if (field.isAnnotationPresent(CsvColumn.class)) {
                    String fieldName = field.getName();
                    //判断该属性值是否为空
                    if (get(object, fieldName) == null) {
                        //获取注解的值
                        CsvColumn csvColumn = field.getAnnotation(CsvColumn.class);
                        String columnName = csvColumn.name();
                        //获取csv中对应的值
                        String value = csvReader.get(columnName);
                        if (field.getType() == String.class) {
                            //如果是String类型，则赋值
                            set(object, fieldName, value);
                        }
                    }

                }
            }
            return object;
        }
        private Object get(Object obj,String fieldName) throws Exception {
            StringBuffer methodName = new StringBuffer();
            methodName.append("get");
            methodName.append(fieldName.substring(0, 1).toUpperCase());
            methodName.append(fieldName.substring(1));
            try {
                Method method = obj.getClass().getMethod(methodName.toString());
                return method.invoke(obj);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                throw new Exception("this field is not found");
            }
        }
        private Object set(Object obj,String fieldName,String value) throws Exception {
            StringBuffer methodName = new StringBuffer();
            methodName.append("set");
            methodName.append(fieldName.substring(0, 1).toUpperCase());
            methodName.append(fieldName.substring(1));
            try {
                Method method = obj.getClass().getMethod(methodName.toString(),String.class);
                return method.invoke(obj,value);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                throw new Exception("this field is not found");
            }
        }
}
