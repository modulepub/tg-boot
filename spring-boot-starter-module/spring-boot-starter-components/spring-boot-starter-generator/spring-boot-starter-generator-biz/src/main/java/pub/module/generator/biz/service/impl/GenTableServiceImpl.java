package pub.module.generator.biz.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.generator.biz.constant.GenConstants;
import pub.module.generator.biz.util.StringUtils;
import pub.module.generator.biz.config.GenConfig;
import pub.module.generator.biz.entity.GenTable;
import pub.module.generator.biz.entity.GenTableColumn;
import pub.module.generator.biz.mapper.GenTableColumnMapper;
import pub.module.generator.biz.mapper.GenTableMapper;
import pub.module.generator.biz.service.IGenTableService;
import pub.module.generator.biz.util.VelocityUtil;
import pub.module.generator.biz.util.VmFilePathUtil;

/**
 * 业务 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class GenTableServiceImpl implements IGenTableService
{
    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    @Resource
    private GenTableMapper genTableMapper;

    @Resource
    private GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务信息
     * 
     * @return 业务信息
     */
    @Override
    public GenTable selectGenTableById(Long id)
    {
        GenTable genTable = genTableMapper.selectOne(new QueryWrapper<GenTable>().lambda().eq(GenTable::getTableId, id));
        List<GenTableColumn> columns = genTableColumnMapper.selectList(new QueryWrapper<GenTableColumn>().lambda().eq(GenTableColumn::getTableId, id));
        genTable.setColumns(columns);
        setTableFromOptions(genTable);
        return genTable;
    }

    public GenTable selectGenTableByName(String tableName){
        GenTable genTable = genTableMapper.selectOne(new QueryWrapper<GenTable>().lambda().eq(GenTable::getTableName, tableName));
        List<GenTableColumn> columns = genTableColumnMapper.selectList(new QueryWrapper<GenTableColumn>().lambda().eq(GenTableColumn::getTableId, genTable.getTableId()));
        genTable.setColumns(columns);
        setTableFromOptions(genTable);
        return genTable;
    }

    /**
     * 查询业务列表
     * 
     * @param genTable 业务信息
     * @return 业务集合
     */
    @Override
    public List<GenTable> selectGenTableList(GenTable genTable)
    {
        QueryWrapper<GenTable> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotEmpty(genTable.getTableName())){
            queryWrapper.lambda().like(GenTable::getTableName, genTable.getTableName());
        }
        return genTableMapper.selectList(queryWrapper);
    }

    /**
     * 查询据库列表
     * 
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    @Override
    public List<GenTable> selectDbTableList(GenTable genTable)
    {
        return genTableMapper.selectDbTableList(genTable);
    }

    /**
     * 查询据库列表
     * 
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames)
    {
        return genTableMapper.selectDbTableListByNames(tableNames);
    }

    /**
     * 查询所有表信息
     * 
     * @return 表信息集合
     */
    @Override
    public List<GenTable> selectGenTableAll()
    {
        List<GenTable> tables = genTableMapper.selectList(new QueryWrapper<GenTable>().lambda().orderByAsc(GenTable::getId));
        for (GenTable table : tables){
            List<GenTableColumn> columns = genTableColumnMapper.selectList(new QueryWrapper<GenTableColumn>().lambda().eq(GenTableColumn::getTableId, table.getTableId()));
            table.setColumns(columns);
        }
        return tables;
    }

    /**
     * 修改业务
     * 
     * @param genTable 业务信息
     */
    @Override
    @Transactional
    public void updateGenTable(GenTable genTable)
    {
        int row = genTableMapper.updateById(genTable);
        if (row > 0)
        {
            for (GenTableColumn genTableColumn : genTable.getColumns())
            {
                genTableColumnMapper.updateById(genTableColumn);
            }
        }
    }

    /**
     * 删除业务对象
     * 
     * @param ids 需要删除的数据 ID
     */
    @Override
    @Transactional
    public void deleteGenTableByIds(List<String> ids)
    {
        QueryWrapper<GenTable> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(GenTable::getTableId,ids);
        genTableMapper.delete(queryWrapper1);
        QueryWrapper<GenTableColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(GenTableColumn::getTableId,ids);
        genTableColumnMapper.delete(queryWrapper);
    }

    /**
     * 创建表
     *
     * @param sql 创建表语句
     * @return 结果
     */
    @Override
    public boolean createTable(String sql)
    {
        SqlRunner SQL_RUNNER = SqlRunner.db();
        SQL_RUNNER.update(sql, (Object) null);
        return true;
    }

    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     */
    @Override
    @Transactional
    public void importGenTable(List<GenTable> tableList)
    {
        String packageName = SpringUtil.getBean(GenConfig.class).getPackageName();
        String moduleName = SpringUtil.getBean(GenConfig.class).getModuleName();

            for (GenTable table : tableList)
            {
                table.setId(IdUtil.getSnowflakeNextIdStr());
                String tableName = table.getTableName();
                table.setCreateBy("admin");
                table.setFunctionName(table.getTableComment());
                table.setPackageName(packageName);
                table.setModuleName(moduleName);
                table.setFunctionAuthor(SpringUtil.getBean(GenConfig.class).getAuthor());
                String className = StrUtil.toCamelCase(table.getTableName());
                table.setClassName(StrUtil.upperFirst(className));
                int lastIndex = tableName.indexOf("_");
                int nameLength = tableName.length();
                String businessName =  StrUtil.toCamelCase(StringUtils.substring(tableName, lastIndex+1, nameLength));
                table.setBusinessName(businessName);
                table.setTableId(IdUtil.getSnowflakeNextIdStr());
                int row = genTableMapper.insert(table);
                if (row > 0)
                {
                    // 保存列信息
                    List<GenTableColumn> genTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
                    for (GenTableColumn column : genTableColumns)
                    {
                        column.setColumnId(IdUtil.getSnowflakeNextIdStr());
                        column.setId(IdUtil.getSnowflakeNextIdStr());
                        this.initColumnField(column, table);
                        genTableColumnMapper.insert(column);
                    }
                }
        }

    }
    public static boolean arraysContains(String[] arr, String targetValue)
    {
        return Arrays.asList(arr).contains(targetValue);
    }
    public void initColumnField(GenTableColumn column, GenTable table)
    {
        String columnType = column.getColumnType();
        String dataType = columnType.contains("(")?columnType.substring(0,columnType.indexOf("(")):columnType;
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        column.setCreateBy(table.getCreateBy());
        // 设置java 字段名
        column.setJavaField(StrUtil.toCamelCase(columnName));
        // 设置默认类型
        column.setJavaType(GenConstants.TYPE_STRING);
        column.setQueryType(GenConstants.QUERY_EQ);

        if (arraysContains(GenConstants.COLUMN_TYPE_STR, dataType) || arraysContains(GenConstants.COLUMN_TYPE_TEXT, dataType))
        {
            // 字符串长度超过500设置为文本域
            column.setHtmlType(GenConstants.HTML_INPUT);
        }
        else if (arraysContains(GenConstants.COLUMN_TYPE_TIME, dataType))
        {
            column.setJavaType(GenConstants.TYPE_DATE);
            column.setHtmlType(GenConstants.HTML_DATETIME);
        }
        else if (arraysContains(GenConstants.COLUMN_TYPE_NUMBER, dataType))
        {
            column.setHtmlType(GenConstants.HTML_INPUT);

            // 如果是浮点型 统一用BigDecimal
            String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0)
            {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10)
            {
                column.setJavaType(GenConstants.TYPE_INTEGER);
            }
            // 长整形
            else
            {
                column.setJavaType(GenConstants.TYPE_LONG);
            }
        }

        // 插入字段（默认所有字段都需要插入）
        column.setIsInsert(GenConstants.REQUIRE);

        // 编辑字段
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_EDIT, columnName) && !column.isPk())
        {
            column.setIsEdit(GenConstants.REQUIRE);
        }
        // 列表字段
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_LIST, columnName) && !column.isPk())
        {
            column.setIsList(GenConstants.REQUIRE);
        }
        // 查询字段
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_QUERY, columnName) && !column.isPk())
        {
            column.setIsQuery(GenConstants.REQUIRE);
        }

        // 查询字段类型
        if (StringUtils.endsWithIgnoreCase(columnName, "name"))
        {
            column.setQueryType(GenConstants.QUERY_LIKE);
        }
        // 状态字段设置单选框
        if (StringUtils.endsWithIgnoreCase(columnName, "status"))
        {
            column.setHtmlType(GenConstants.HTML_RADIO);
        }
        // 类型&性别字段设置下拉框
        else if (StringUtils.endsWithIgnoreCase(columnName, "type")
                || StringUtils.endsWithIgnoreCase(columnName, "sex"))
        {
            column.setHtmlType(GenConstants.HTML_SELECT);
        }
        // 文件字段设置上传控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "file"))
        {
            column.setHtmlType(GenConstants.HTML_UPLOAD);
        }
        // 内容字段设置富文本控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "content"))
        {
            column.setHtmlType(GenConstants.HTML_SUMMERNOTE);
        }
    }


    /**
     * 预览代码
     * 
     * @param tableId 表编号
     * @return 预览数据列表
     */
    @Override
    public Map<String, String> previewCode(Long tableId)
    {
        Map<String, String> dataMap = new LinkedHashMap<>();
        // 查询表信息
        GenTable table = this.selectGenTableById(tableId);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);
        VelocityUtil.setProperties();
        VelocityContext context = VelocityUtil.prepareContext(table);

        // 获取模板列表
        List<String> templates = VmFilePathUtil.getAllVmFileRelativePaths();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     * 
     * @param tableName 表名称
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String tableName)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableName, zip,new HashSet<>());
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     */
    @Override
    public void generatorCode(String tableName)
    {
        // 查询表信息
        GenTable table = this.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);


        VelocityContext context = VelocityUtil.prepareContext(table);

        // 获取模板列表
        List<String> templates = VmFilePathUtil.getAllVmFileRelativePaths();
        for (String template : templates)
        {
            if (!StringUtils.contains(template, "sql.vm"))
            {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, "UTF-8");
                tpl.merge(context, sw);
                String path = getGenPath(table, template);
                FileUtil.writeBytes(sw.toString().getBytes(StandardCharsets.UTF_8),new File(path));
            }
        }
    }

    /**
     * 同步数据库
     * 
     * @param tableName 表名称
     */
    @Override
    @Transactional
    public void synDb(String tableName)
    {
        GenTable table = this.selectGenTableByName(tableName);
        List<GenTableColumn> tableColumns = table.getColumns();
        Map<String, GenTableColumn> tableColumnMap = tableColumns.stream().collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity()));

        List<GenTableColumn> dbTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
        Assert.isFalse(dbTableColumns.isEmpty(),"表列爲空");
        List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).toList();

        dbTableColumns.forEach(column -> {
            this.initColumnField(column, table);
            if (tableColumnMap.containsKey(column.getColumnName()))
            {
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
                column.setColumnId(prevColumn.getColumnId());
                if (column.isList())
                {
                    // 如果是列表，继续保留查询方式/字典类型选项
                    column.setDictType(prevColumn.getDictType());
                    column.setQueryType(prevColumn.getQueryType());
                }
                if (StringUtils.isNotEmpty(prevColumn.getIsRequired()) && !column.isPk()
                        && (column.isInsert() || column.isEdit())
                        && ((column.isUsableColumn()) || (!column.isSuperColumn())))
                {
                    // 如果是(新增/修改&非主键/非忽略及父属性)，继续保留必填/显示类型选项
                    column.setIsRequired(prevColumn.getIsRequired());
                    column.setHtmlType(prevColumn.getHtmlType());
                }
                genTableColumnMapper.updateById(column);
            }
            else
            {
                genTableColumnMapper.insert(column);
            }
        });

        List<GenTableColumn> delColumns = tableColumns.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName())).toList();
        for (GenTableColumn column : delColumns){
            genTableColumnMapper.deleteById(column);
        }
    }

    /**
     * 批量生成代码（下载方式）
     * 
     * @param tableNames 表数组
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String[] tableNames)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        Set<String> handledFileNames = new HashSet<>();
        for (String tableName : tableNames)
        {
            generatorCode(tableName, zip,handledFileNames);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(String tableName, ZipOutputStream zip,Set<String> handledFileNames)
    {
        // 查询表信息
        GenTable table = this.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);


        VelocityContext context = VelocityUtil.prepareContext(table);

        // 获取模板列表
        List<String> templates = VmFilePathUtil.getAllVmFileRelativePaths();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            VelocityUtil.setProperties();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try
            {
                String fileName = VelocityUtil.getFileName(template, table);
                if(handledFileNames.contains(fileName)){
                    continue;
                }
                // 添加到 zip
                zip.putNextEntry(new ZipEntry(fileName));
                IoUtil.write(zip, false,sw.toString().getBytes(StandardCharsets.UTF_8));
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
                handledFileNames.add(fileName);
            }
            catch (IOException e)
            {
                log.error("渲染模板失败，表名：{}", table.getTableName(), e);
            }
        }
    }

    /**
     * 修改保存参数校验
     * 
     * @param genTable 业务信息
     */
    @Override
    public void validateEdit(GenTable genTable)
    {

         if (GenConstants.TPL_SUB.equals(genTable.getTplCategory()))
        {
            if (StringUtils.isEmpty(genTable.getSubTableName()))
            {
                throw new RuntimeException("关联子表的表名不能为空");
            }
            else if (StringUtils.isEmpty(genTable.getSubTableFkName()))
            {
                throw new RuntimeException("子表关联的外键名不能为空");
            }
        }
    }

    /**
     * 设置主键列信息
     * 
     * @param table 业务表信息
     */
    public void setPkColumn(GenTable table)
    {
        for (GenTableColumn column : table.getColumns())
        {
            if (column.isPk())
            {
                table.setPkColumn(column);
                break;
            }
        }
        if (StringUtils.isNull(table.getPkColumn()))
        {
            table.setPkColumn(table.getColumns().getFirst());
        }
        if (GenConstants.TPL_SUB.equals(table.getTplCategory()))
        {
            for (GenTableColumn column : table.getSubTable().getColumns())
            {
                if (column.isPk())
                {
                    table.getSubTable().setPkColumn(column);
                    break;
                }
            }
            if (StringUtils.isNull(table.getSubTable().getPkColumn()))
            {
                table.getSubTable().setPkColumn(table.getSubTable().getColumns().getFirst());
            }
        }
    }

    /**
     * 设置主子表信息
     * 
     * @param table 业务表信息
     */
    public void setSubTable(GenTable table)
    {
        String subTableName = table.getSubTableName();
        if (StringUtils.isNotEmpty(subTableName))
        {
            table.setSubTable(this.selectGenTableByName(subTableName));
        }
    }

    /**
     * 设置代码生成其他选项值
     * 
     * @param genTable 设置后的生成对象
     */
    public void setTableFromOptions(GenTable genTable)
    {
        JSONObject paramsObj = JSONUtil.parseObj(genTable.getOptions());
        String treeCode = paramsObj.getStr(GenConstants.TREE_CODE);
        String treeParentCode = paramsObj.getStr(GenConstants.TREE_PARENT_CODE);
        String treeName = paramsObj.getStr(GenConstants.TREE_NAME);
        String parentMenuId = paramsObj.getStr(GenConstants.PARENT_MENU_ID);
        String parentMenuName = paramsObj.getStr(GenConstants.PARENT_MENU_NAME);
        genTable.setTreeCode(treeCode);
        genTable.setTreeParentCode(treeParentCode);
        genTable.setTreeName(treeName);
        genTable.setParentMenuId(parentMenuId);
        genTable.setParentMenuName(parentMenuName);
    }

    /**
     * 获取代码生成地址
     * 
     * @param table 业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template)
    {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/"))
        {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtil.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtil.getFileName(template, table);
    }
}