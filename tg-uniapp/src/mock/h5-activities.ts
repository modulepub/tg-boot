/** H5 活动列表 / 详情 mock（后续可替换为接口） */

export interface IMockActivity {
  id: string
  /** 活动名称 */
  title: string
  /** 开办时间（展示文案） */
  startTime: string
  /** 活动地点 */
  location: string
  /** 已报名人数 */
  enrolledCount: number
  /** 所属城市（用于检索） */
  city: string
  /** 活动内容描述 */
  description: string
}

export const MOCK_ACTIVITIES: IMockActivity[] = [
  {
    id: 'act-520-hz',
    title: '520 全国城市线下相亲专场 · 杭州站',
    startTime: '2026-05-20（周二）14:00 — 17:30',
    location: '杭州市西湖区某某文化中心 3F 多功能厅',
    enrolledCount: 86,
    city: '杭州',
    description:
      '本场为同城认证专场，红娘带队破冰互动 + 一对一座位轮换交流 + 心动互选环节。\n\n'
      + '流程说明：签到核验身份 → 破冰游戏 → 主题圆桌 → 自由交流 → 心动投递。\n\n'
      + '温馨提示：请准时到场，穿着得体；现场禁止推销与非婚恋相关商业行为。',
  },
  {
    id: 'act-521-sh',
    title: '初夏露台交友派对 · 上海',
    startTime: '2026-05-21（周三）18:30 — 21:00',
    location: '上海市静安区某某创意园露台花园',
    enrolledCount: 54,
    city: '上海',
    description:
      '轻松社交向线下活动，适合希望拓展交际圈的单身青年。\n\n'
      + '含饮品小食、互动卡牌与小组话题讨论；不设强制配对，尊重个人意愿。\n\n'
      + '如需自驾请提前报备车牌；雨天活动将移至室内同址 B1。',
  },
  {
    id: 'act-528-sz',
    title: '程序员 × 教师主题下午茶 · 深圳',
    startTime: '2026-05-28（周三）15:00 — 18:00',
    location: '深圳市南山区某某书店二楼活动区',
    enrolledCount: 32,
    city: '深圳',
    description:
      '定向主题沙龙，现场核验职业信息后入场。\n\n'
      + '环节包含自我介绍上限、话题盲盒、心动便利贴。\n\n'
      + '名额有限，报满即止；报名成功后将有短信提醒。',
  },
  {
    id: 'act-606-cd',
    title: '咖啡馆慢相亲 · 成都',
    startTime: '2026-06-06（周六）10:00 — 12:30',
    location: '成都市锦江区某某咖啡馆包厢区',
    enrolledCount: 41,
    city: '成都',
    description:
      '小规模精品场次，每场不超过 40 人，注重深度沟通。\n\n'
      + '提供纸质资料袋与心动问卷；活动结束后可选择是否交换联系方式。',
  },
  {
    id: 'act-613-nj',
    title: '玄武湖畔徒步交友 · 南京',
    startTime: '2026-06-13（周六）09:00 — 11:30',
    location: '南京市玄武湖公园情侣园入口集合',
    enrolledCount: 67,
    city: '南京',
    description:
      '轻运动向交友活动，约 5 公里平缓路线；请穿着运动鞋并自备饮用水。\n\n'
      + '中途设置补给与合影环节；身体不适者请勿勉强参与。',
  },
]

export function getMockActivityById(id: string): IMockActivity | undefined {
  const key = String(id ?? '').trim()
  return MOCK_ACTIVITIES.find(a => a.id === key)
}

export function getMockActivityCities(): string[] {
  const set = new Set<string>()
  for (const a of MOCK_ACTIVITIES) {
    const c = String(a.city ?? '').trim()
    if (c)
      set.add(c)
  }
  return Array.from(set).sort((a, b) => a.localeCompare(b, 'zh-Hans-CN'))
}
