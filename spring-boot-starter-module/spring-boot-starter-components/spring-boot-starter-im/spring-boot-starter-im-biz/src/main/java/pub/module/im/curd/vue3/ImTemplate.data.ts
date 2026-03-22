import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '模板编码',
    align:"center",
    dataIndex: 'imTemplateCode'
   },
   {
    title: '消息名称',
    align:"center",
    dataIndex: 'imTemplateName'
   },
   {
    title: '消息类型',
    align:"center",
    dataIndex: 'imTemplateTypeCode'
   },
   {
    title: '消息内容',
    align:"center",
    dataIndex: 'imTemplateContent'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "模板编码",
      field: 'imTemplateCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "消息名称",
      field: 'imTemplateName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "消息类型",
      field: 'imTemplateTypeCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '模板编码',
    field: 'imTemplateCode',
    component: 'Input',
  },
  {
    label: '消息名称',
    field: 'imTemplateName',
    component: 'Input',
  },
  {
    label: '消息类型',
    field: 'imTemplateTypeCode',
    component: 'Input',
  },
  {
    label: '消息内容',
    field: 'imTemplateContent',
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
  imTemplateCode: {title: '模板编码',order: 0,view: 'text', type: 'string',},
  imTemplateName: {title: '消息名称',order: 1,view: 'text', type: 'string',},
  imTemplateTypeCode: {title: '消息类型',order: 2,view: 'text', type: 'string',},
  imTemplateContent: {title: '消息内容',order: 3,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}