import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '删除标识',
    align:"center",
    dataIndex: 'deleted'
   },
   {
    title: '合同模板编码',
    align:"center",
    dataIndex: 'ctTemplateCode'
   },
   {
    title: '合同模板名称',
    align:"center",
    dataIndex: 'ctTemplateName'
   },
   {
    title: '合同模板文件',
    align:"center",
    dataIndex: 'ctTemplateFile'
   },
   {
    title: '合同模板分类',
    align:"center",
    dataIndex: 'ctClassificationCode'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '删除标识',
    field: 'deleted',
    component: 'Input',
  },
  {
    label: '合同模板编码',
    field: 'ctTemplateCode',
    component: 'Input',
  },
  {
    label: '合同模板名称',
    field: 'ctTemplateName',
    component: 'Input',
  },
  {
    label: '合同模板文件',
    field: 'ctTemplateFile',
    component: 'Input',
  },
  {
    label: '合同模板分类',
    field: 'ctClassificationCode',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];

// 高级查询数据
export const superQuerySchema = {
  deleted: {title: '删除标识',order: 0,view: 'text', type: 'string',},
  ctTemplateCode: {title: '合同模板编码',order: 1,view: 'text', type: 'string',},
  ctTemplateName: {title: '合同模板名称',order: 2,view: 'text', type: 'string',},
  ctTemplateFile: {title: '合同模板文件',order: 3,view: 'text', type: 'string',},
  ctClassificationCode: {title: '合同模板分类',order: 4,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}