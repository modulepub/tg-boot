package pub.module.generator.biz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import pub.module.generator.biz.domain.CxSelect;
import pub.module.generator.biz.domain.GenResult;
import pub.module.generator.biz.page.TableDataInfo;
import pub.module.generator.biz.util.StringUtils;
import pub.module.generator.biz.config.GenConfig;
import pub.module.generator.biz.entity.GenTable;
import pub.module.generator.biz.entity.GenTableColumn;
import pub.module.generator.biz.service.IGenTableColumnService;
import pub.module.generator.biz.service.IGenTableService;

/**
 * 代码生成 操作处理
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/pub/generator")
public class GeneratorController
{

    @Resource
    private IGenTableService genTableService;

    @Resource
    private IGenTableColumnService genTableColumnService;

    @GetMapping("/index")
    public String gen()
    {
        return "index";
    }
    /**
     * 导入表结构
     */
    @GetMapping("/importTable")
    public String importTable()
    {
        return "/importTable";
    }

    /**
     * 创建表结构
     */
    @GetMapping("/createTable")
    public String createTable()
    {
        return  "/createTable";
    }
    /**
     * 查询代码生成列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo genList(GenTable genTable)
    {
        TableDataInfo tableDataInfo = new TableDataInfo();
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        tableDataInfo.setRows(list);
        return tableDataInfo;
    }

    /**
     * 查询数据库列表
     */
    @PostMapping("/db/list")
    @ResponseBody
    public TableDataInfo dataList(GenTable genTable)
    {
        TableDataInfo tableDataInfo = new TableDataInfo();
        List<GenTable> list = genTableService.selectDbTableList(genTable);
        tableDataInfo.setRows(list);
        return tableDataInfo;
    }

    /**
     * 查询数据表字段列表
     */
    @PostMapping("/column/list")
    @ResponseBody
    public TableDataInfo columnList(GenTableColumn genTableColumn)
    {
        TableDataInfo dataInfo = new TableDataInfo();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(genTableColumn);
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }



    /**
     * 导入表结构（保存）
     */
    @PostMapping("/importTable")
    @ResponseBody
    public GenResult<?> importTableSave(String tables)
    {
        String[] tableNames = tables.split(",");
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList);
        return GenResult.ok();
    }

    /**
     * 修改代码生成业务
     */
    @GetMapping("/edit/{tableId}")
    public String edit(@PathVariable Long tableId, ModelMap modelMap)
    {
        GenTable table = genTableService.selectGenTableById(tableId);
        List<GenTable> genTables = genTableService.selectGenTableAll();
        List<CxSelect> cxSelect = new ArrayList<>();
        for (GenTable genTable : genTables)
        {
            if (!StringUtils.equals(table.getTableName(), genTable.getTableName()))
            {
                CxSelect cxTable = new CxSelect(genTable.getTableName(), genTable.getTableName() + '：' + genTable.getTableComment());
                List<CxSelect> cxColumns = new ArrayList<>();
                for (GenTableColumn tableColumn : genTable.getColumns())
                {
                    cxColumns.add(new CxSelect(tableColumn.getColumnName(), tableColumn.getColumnName() + '：' + tableColumn.getColumnComment()));
                }
                cxTable.setS(cxColumns);
                cxSelect.add(cxTable);
            }
        }
        modelMap.put("table", table);
        modelMap.put("data", JSONUtil.toJsonPrettyStr(cxSelect));
        return  "/edit";
    }

    /**
     * 修改保存代码生成业务
     */
    @PostMapping("/edit")
    @ResponseBody
    public GenResult<?> editSave(@Validated GenTable genTable)
    {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return GenResult.ok();
    }

    @PostMapping("/remove")
    @ResponseBody
    public GenResult<?> remove(String ids)
    {
        genTableService.deleteGenTableByIds(ids);
        return GenResult.ok();
    }

    @PostMapping("/createTable")
    @ResponseBody
    public GenResult<?> create(String sql)
    {
        try
        {
            List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
            List<String> tableNames = new ArrayList<>();
            for (SQLStatement sqlStatement : sqlStatements)
            {
                if (sqlStatement instanceof MySqlCreateTableStatement createTableStatement)
                {
                    if (genTableService.createTable(createTableStatement.toString()))
                    {
                        String tableName = createTableStatement.getTableName().replaceAll("`", "");
                        tableNames.add(tableName);
                    }
                }
            }
            List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames.toArray(new String[0]));
            genTableService.importGenTable(tableList);
            return GenResult.ok();
        }
        catch (Exception e)
        {
            return GenResult.error("创建表结构异常");
        }
    }

    /**
     * 预览代码
     */
    @GetMapping("/preview/{tableId}")
    @ResponseBody
    public GenResult<?> preview(@PathVariable Long tableId) throws IOException
    {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return GenResult.ok(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @GetMapping("/genCode/{tableName}")
    @ResponseBody
    public GenResult<?> genCode(@PathVariable String tableName)
    {
        if (!SpringUtil.getBean(GenConfig.class).getAllowOverwrite())
        {
            return GenResult.error("【系统预设】不允许生成文件覆盖到本地");
        }
        genTableService.generatorCode(tableName);
        return GenResult.ok();
    }

    /**
     * 同步数据库
     */
    @GetMapping("/synDb/{tableName}")
    @ResponseBody
    public GenResult<?> synDb(@PathVariable String tableName)
    {
        genTableService.synDb(tableName);
        return GenResult.ok();
    }

    /**
     * 批量生成代码
     */
    @GetMapping("/batchGenCode")
    @ResponseBody
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = tables.split(",");
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    private void genCode(HttpServletResponse response, byte[] data) throws IOException
    {
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"tg.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IoUtil.write( response.getOutputStream(),true,data);
    }
}