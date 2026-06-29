import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const outDir = path.resolve(__dirname, '../src/main/resources/mock');

/** 拒绝纯四字成语/古风短语 */
function isRejectedFourCharIdiom(s) {
  if (!/^[\u4e00-\u9fff]{4}$/.test(s)) return false;
  const poetic = new Set([
    '且听风吟', '半盏清茶', '寄予清风', '南风知意', '浮生若梦', '墨染流年', '诗与远方', '浅笑安然',
    '岁月静好', '温柔以待', '人间值得', '未来可期', '平安喜乐', '顺遂无忧', '喜乐长安', '素年锦时',
    '时光温柔', '清风徐来', '明月入怀', '浅夏微凉', '春风十里', '夏夜星辰', '冬雪入眠', '四季与你',
    '余生有你', '陪你到老', '执子之手', '与子偕老', '白首不离', '愿得一心', '两情相悦', '琴瑟和鸣',
    '比翼双飞', '一眼万年', '怦然心动', '小鹿乱撞', '脸红心跳', '只若初见', '南鸢北梦', '东篱西窗',
    '青衫烟雨', '画扇悲秋', '月落乌啼', '长安忆梦', '锦瑟年华', '浮生未歇', '红袖添香', '墨染青衣',
    '云鬓花颜', '烟雨江南', '素衣白裳', '醉卧红尘', '听风念旧', '月下独酌', '栀子花开', '旧巷拾梦',
    '书卷微凉', '素笺淡墨', '落笔成殇', '时光褶皱', '半盏流年', '青柠往事', '纸上烟云', '清欢渡',
    '月落星沉', '隐于山海', '心静如水', '画凉', '雪陌', '柚念', '辞旧', '挽风', '画卿', '醉卧', '墨染',
    '星眠', '雾屿', '浅夏', '月落', '浅笑', '初阳', '微风', '浅蓝', '书白', '云淡', '清川', '暮山',
    '归零', '孤城', '清酒', '余生', '挽月', '北岸', '南巷', '东篱', '西窗', '北辰', '南星', '旧颜', '清欢',
  ]);
  return poetic.has(s);
}

function acceptNick(s) {
  const t = s.trim();
  if (!t || t.length < 2 || t.length > 16) return false;
  if (/\d+$/.test(t)) return false;
  if (isRejectedFourCharIdiom(t)) return false;
  if (/^[a-zA-Z]+$/.test(t) && t.length < 3) return false;
  return true;
}

function addNick(seen, out, raw) {
  const s = raw.trim();
  if (!acceptNick(s) || seen.has(s)) return;
  seen.add(s);
  out.push(s);
}

function buildNickPool(seeds, generator) {
  const seen = new Set();
  const out = [];
  for (const line of seeds) addNick(seen, out, line);
  for (let round = 0; round < 80 && out.length < 500; round += 1) {
    for (const item of generator(round)) {
      if (out.length >= 500) break;
      addNick(seen, out, item);
    }
  }
  if (out.length < 500) throw new Error(`昵称不足 500，当前 ${out.length}`);
  return out.slice(0, 500);
}

/** 说说：去 AI 前缀，控制长度，统一句号结尾 */
function normalizeMoment(s) {
  let t = s.trim()
    .replace(/^其实[，,]\s*/, '')
    .replace(/^说实话[，,]\s*/, '')
    .replace(/^真心话[，,]\s*/, '')
    .replace(/^坦白讲[，,]\s*/, '')
    .replace(/^简单来说[，,]\s*/, '')
    .replace(/^总的来说[，,]\s*/, '')
    .replace(/（\d+）$/, '')
    .replace(/[。．.!！?？…]+$/g, '');
  if (!t) return null;
  if (t.length < 3 || t.length > 48) return null;
  if (/\d+$/.test(t)) return null;
  return `${t}。`;
}

function addMoment(seen, out, raw) {
  const s = normalizeMoment(raw);
  if (!s || seen.has(s)) return;
  seen.add(s);
  out.push(s);
}

function buildMomentPool(seeds, generators, target = 500) {
  const seen = new Set();
  const out = [];
  for (const line of seeds) addMoment(seen, out, line);
  for (let round = 0; round < 300 && out.length < target; round += 1) {
    for (const gen of generators) {
      for (const item of gen(round)) {
        if (out.length >= target) break;
        addMoment(seen, out, item);
      }
      if (out.length >= target) break;
    }
  }
  // 自然变体：同义改写，不用「说实话」类前缀
  const softPrefixes = ['平时', '最近', '一直', '大概就是', '算是', '算是那种', '属于', '典型'];
  const base = [...out];
  for (let round = 0; round < 50 && out.length < target; round += 1) {
    for (let i = 0; i < base.length && out.length < target; i += 1) {
      const core = base[i].replace(/。$/, '');
      if (core.length > 28) continue;
      addMoment(seen, out, `${softPrefixes[(i + round) % softPrefixes.length]}${core}`);
    }
  }
  // 双短句拼接
  for (let i = 0; i < base.length && out.length < target; i += 1) {
    for (let j = i + 1; j < base.length && j < i + 40 && out.length < target; j += 1) {
      const a = base[i].replace(/。$/, '');
      const b = base[j].replace(/。$/, '');
      if (a.length + b.length > 40) continue;
      addMoment(seen, out, `${a}，${b}`);
    }
  }
  if (out.length < target) throw new Error(`说说不足 ${target}，当前 ${out.length}`);
  return out.slice(0, target);
}

const femaleSeeds = `
草莓啵啵兔 月亮邮递员 软糖小星球 落日慢摇 云间小诗 偷走日落的猫 云边小卖部
贩卖蓝色黄昏 橘子味的风 云朵有点甜 躲进星星里 今日份甜妹 奶凶小野猫 软萌不软
奶茶半糖去冰 芋泥波波熊 芝士奶盖控 抹茶星冰乐 焦糖玛奇朵 椰椰拿铁女孩 桃气乌龙妹
薯片杀手 干饭不积极 摸鱼一级选手 摆烂小能手 熬夜冠军 失眠的猫 起床困难户
今天也要开心 元气少女Yo 甜度满分 可爱超标 官方小甜心 人间小甜瓜 快乐加载中
辣妹日记 氛围感美女 复古胶片风 法式慵懒风 韩系甜妹 社恐但话多 慢热型选手
直球选手 嘴硬心软 可盐可甜 又菜又爱玩 温柔且坚定 追星女孩 综艺下饭党
剧本杀爱好者 猫奴一枚 狗勾守护者 手帐少女 盲盒收藏家 电竞少女 音游废柴
摄影小白 修图达人 探店雷达 咖啡续命中 面包脑袋 失眠艺术家 浪漫收藏家
心动贩卖机 恋爱脑康复中 脱单进行时 等风也等你 Luna酱 小兔TuTu 星星泡饭
Cherry酱 蜜桃汽水 柠檬不酸 葡萄冻冻 布丁不加糖 蛋糕脑袋 曲奇碎屑 马卡龙控
提拉米苏 舒芙蕾本蕾 可颂小姐 周末睡不醒 周一综合症 下班倒计时 快递到了吗
`.trim().split(/\s+/);

const maleSeeds = `
阳光气质男 干饭王本王 摸鱼冠军 摆烂青年 熬夜选手 起床困难户 外卖鉴赏家
篮球少年 足球小子 健身达人 跑步爱好者 户外玩家 骑行少年 游泳健将
CityBoy 盐系男生 奶狗本狗 狼狗系 禁欲系学长 暖男一枚 理想型男友
程序员本员 码农日常 技术宅 极客玩家 设计狗 产品汪 运营喵
社恐直男 慢热理工男 直球选手 嘴硬心软 话少但靠谱 细节控 行动派
咖啡续命 面包脑袋 探店雷达 摄影爱好者 吉他少年 电影迷 音乐发烧友
北漂青年 沪漂选手 深漂打工人 小镇做题家 周末探险家 自驾爱好者
电竞少年 剧本杀高配 猫奴本奴 狗勾守护者 手办收藏家 球鞋控
LuckyBoy Max同学 Leo小哥 Ryan日常 今天也要加油 快乐加载中 快递到了吗
`.trim().split(/\s+/);

const flavors = ['草莓', '蜜桃', '葡萄', '柠檬', '抹茶', '焦糖', '芋泥', '椰椰', '芝士', '可可', '蓝莓', '樱花'];
const sweets = ['泡芙', '奶茶', '布丁', '蛋糕', '饼干', '马卡龙', '蛋挞', '司康', '贝果', '可颂', '麻薯', '雪媚娘'];
const cuteAdj = ['软萌', '奶凶', '甜系', '治愈', '元气', '盐系', '慵懒', '高冷', '社恐', '佛系', '温柔', '清醒'];
const cuteNoun = ['小兔', '小猫', '小鹿', '团子', '泡芙', '奶茶', '薯片', '月亮', '星星', '布丁', '熊仔', '鸭鸭'];
const humorV = ['摸鱼', '干饭', '摆烂', '熬夜', '躺平', '冲浪', '吃瓜', '潜水', '划水', '追剧', '探店', '撸猫'];
const humorN = ['选手', '冠军', '达人', '王者', '青年', '少女', '本喵', '玩家', '爱好者', '研究员', '专家', '队长'];
const today = ['今日', '今天', '此刻', '今晚', '周末', '假日'];
const styleF = ['辣妹', '甜妹', '酷girl', '软妹', '宅女', '学姐', '学妹', '打工人', '考研党', '上班族'];
const styleM = ['少年', '男孩', '青年', '学长', '学弟', '打工人', '考研党', '上班族', '程序员', '设计师'];
const connectors = ['的', '与', '和', '遇上', '爱上', '遇见'];
const mood = ['快乐', '治愈', '温柔', '元气', '慵懒', '清醒', '浪漫', '自由', '安静', '热烈', '慢热', '直球'];
const place = ['海边', '山顶', '街角', '便利店', '咖啡馆', '书店', '花店', '地铁口', '天台', '公园'];

function femaleGenerator(round) {
  const out = [];
  for (let i = 0; i < flavors.length; i++) {
    out.push(`${flavors[i]}${sweets[(i + round) % sweets.length]}`);
    out.push(`${flavors[(i + 1) % flavors.length]}味${cuteNoun[i % cuteNoun.length]}`);
  }
  for (let i = 0; i < cuteAdj.length; i++) {
    out.push(`${cuteAdj[i]}${cuteNoun[(i + round) % cuteNoun.length]}`);
    out.push(`${humorV[i]}${humorN[(i + round) % humorN.length]}`);
    out.push(`${today[i % today.length]}${cuteAdj[(i + round) % cuteAdj.length]}`);
    out.push(`${cuteNoun[i]}的${cuteAdj[(i + 1) % cuteAdj.length]}`);
    out.push(`${styleF[i % styleF.length]}${cuteNoun[(i + round) % cuteNoun.length]}`);
    out.push(`${mood[i % mood.length]}${cuteNoun[(i + round) % cuteNoun.length]}`);
    out.push(`${place[i % place.length]}${cuteNoun[(i + round) % cuteNoun.length]}`);
  }
  for (let i = 0; i < mood.length; i++) {
    for (let j = 0; j < 3; j++) {
      out.push(`${mood[i]}${connectors[(i + j + round) % connectors.length]}${cuteNoun[(i + j) % cuteNoun.length]}`);
    }
  }
  const en = ['Luna', 'Cherry', 'Peach', 'Momo', 'Coco', 'Milk', 'Honey', 'Sugar', 'Star', 'Moon', 'Cloud', 'Rain'];
  const enSuf = ['酱', '同学', '小姐', '本喵', '殿下', '宝宝', '姐姐', '姑娘'];
  for (let i = 0; i < en.length; i++) {
    out.push(`${en[i]}${enSuf[(i + round) % enSuf.length]}`);
    out.push(`${en[i]}的${cuteNoun[i % cuteNoun.length]}`);
    out.push(`${en[i]}${cuteAdj[(i + round) % cuteAdj.length]}`);
  }
  return out;
}

function maleGenerator(round) {
  const out = [];
  const style = ['阳光', '盐系', '奶狗', '狼狗', '复古', '极简', '街头', '机能', '日系', '韩系', '运动', '文艺', '沉稳', '清爽'];
  const role = ['少年', '男孩', '青年', '选手', '达人', '玩家', '本哥', '同学', '小哥', '直男', '暖男'];
  const humor = ['干饭', '摸鱼', '摆烂', '熬夜', '躺平', '冲浪', '吃瓜', '潜水', '划水', '加班', '健身', '打球', '撸铁', '跑步'];
  const humorTail = ['王', '冠军', '达人', '选手', '青年', '本男', '高手', '爱好者', '队长', '男孩', '小哥'];
  for (let i = 0; i < style.length; i++) {
    out.push(`${style[i]}${role[(i + round) % role.length]}`);
    out.push(`${humor[i]}${humorTail[(i + round) % humorTail.length]}`);
    out.push(`${role[i]}${style[(i + 1) % style.length]}`);
    out.push(`${styleM[i % styleM.length]}${role[(i + round) % role.length]}`);
    out.push(`${today[i % today.length]}${humor[i]}`);
    out.push(`${place[i % place.length]}${role[(i + round) % role.length]}`);
  }
  const en = ['Leo', 'Max', 'Ryan', 'Ethan', 'Alex', 'Jack', 'Luke', 'Noah', 'Owen', 'Eric', 'Mike', 'Tom'];
  const enSuf = ['同学', '小哥', '本哥', '男孩', '青年', '先生'];
  for (let i = 0; i < en.length; i++) {
    out.push(`${en[i]}${enSuf[(i + round) % enSuf.length]}`);
    out.push(`${en[i]}的日常`);
    out.push(`${en[i]}${style[(i + round) % style.length]}`);
  }
  const jobs = ['产品', '设计', '运营', '测试', '前端', '后端', '算法', '架构', '运维', '数据'];
  const jobTail = ['汪', '猿', '仔', '君', '同学', '小哥', '达人', '选手'];
  for (let i = 0; i < jobs.length; i++) {
    out.push(`${jobs[i]}${jobTail[(i + round) % jobTail.length]}`);
    out.push(`${jobs[i]}${role[(i + round) % role.length]}`);
  }
  return out;
}

// —— 女生说说：网络常见个性签名 + 婚恋场景 ——
const femaleMomentSeeds = `
爱自己是终生浪漫的开始
温柔半两，从容一生
心里住着小星星，生活才能亮晶晶
减少依赖，降低期待
人间烟火气，最抚凡人心
给自己买花，陪自己长大
知足而上进，温柔且坚定
满怀希望就会所向披靡
愿你历遍山河，仍觉得人间值得
好好生活，慢慢相遇
接受普通，努力出众
不慌不忙，心之所向
有所为，有所爱，有所期待
自律且努力，别让生活太安逸
及时清醒，也要事事甘心
请保持微笑的眼睛，才能看到更美的风景
遇见是福气，不遇见也是
五颜六色的生活，不能乱七八糟的过
不去期待，就不会失望
总听别人的声音很难做自己
顺遂无虞
靠近我就是晴天
不被情绪裹挟才是更高级的自由
我不着急，我要爱对人
我的世界我做主
看世界，听自己
多塑造自己，不为别人的评价所累
爱国爱家和爱自己
生而自由，爱而无畏
无心风月，钟情自己
生人勿近
及时止损
仅你可见
保持冷漠
废话少说
偶尔可恶，永远可爱
内心向阳，无谓悲伤
独立清醒，放荡不羁
人间散客，一切随缘
情出自愿，何来亏欠
落落大方，不负众望
认真找对象，慢热但专一
喜欢旅行和美食，期待真诚相遇
工作稳定，愿以结婚为目的交往
养了一只猫，也喜欢爱小动物的人
周末喜欢探店，希望有人一起分享
可盐可甜，熟悉后话很多
圈子小，想认真认识一个人
不养鱼，只想谈一段安稳感情
希望遇见三观相合的人
比起浪漫惊喜，更在意相处舒服
性格温和，家庭观念传统
爱干净也爱生活，期待双向奔赴
偷得浮生半日闲
要及时清醒，也要事事甘心
遇见是福气，不遇见也是
有人骂你野心勃勃，也有人独爱你灵魂有火
五颜六色生活，不能乱七八糟过
想多了全是问题，做多了都是答案
永远二十赶朝暮
先是我，才能是任何
无关风月，我题我写
别看了，过得蛮好
平凡至极又可爱非常
爱与不爱皆自由
爱人先爱己
两手空空才能无限拥有
看世界，听自己
勇敢成为别人的过去
永住太阳里
我的世界围绕我自己转
愿赌服输，好好活下去
`.trim().split('\n').map((l) => l.trim()).filter(Boolean);

const fHobby = ['咖啡', '奶茶', '探店', '旅行', '健身', '追剧', '撸猫', '摄影', '烘焙', '阅读', '徒步', '瑜伽', '插花', '画画', '听歌'];
const fTrait = ['慢热', '开朗', '内向', '直球', '佛系', '清醒', '温柔', '独立', '黏人', '社恐', '乐观', '细腻'];
const fWish = [
  '想认真谈一场恋爱', '希望遇见靠谱的人', '期待三观合拍', '等一个能聊得来的人',
  '愿以结婚为目的交往', '想找能一起过日子的人', '相信缘分也相信真诚', '不喜欢快餐式恋爱',
  '更看重相处舒服', '希望彼此都能主动一点', '喜欢稳定踏实的感情', '想找个懂我的人',
];
const fShort = [
  '今日甜度已满', '贩卖可爱', '快乐加载中', '恋爱脑康复中', '脱单进行时', '等风也等你',
  '人间小甜瓜', '甜度满分', '可爱超标', '元气满满', '保持可爱', '温柔且坚定',
  '先爱自己', '慢慢来', '别赶路感受路', '生活亮晶晶', '今天也要开心', '做自己的光',
];
const fTail = ['周末常出门走走', '也爱宅家看电影', '生活习惯简单', '不抽烟不喝酒', '愿意一起规划未来', '讨厌冷暴力'];

function femaleMomentGenerators(round) {
  return [
    function gen() {
      const out = [];
      for (let i = 0; i < fHobby.length; i++) {
        out.push(`${fHobby[i]}爱好者，${fWish[(i + round) % fWish.length]}`);
        out.push(`平时爱${fHobby[i]}，${fTail[(i + round) % fTail.length]}`);
        out.push(`${fTrait[i % fTrait.length]}型女生，${fWish[(i + 1 + round) % fWish.length]}`);
        out.push(`${fShort[i % fShort.length]}，${fWish[(i + 2 + round) % fWish.length]}`);
      }
      return out;
    },
    function gen() {
      const out = [];
      const city = ['日照', '青岛', '本地'];
      const job = ['教师', '护士', '会计', '设计', '运营', '销售', '文员', '药师', '人事', '客服'];
      for (let i = 0; i < job.length; i++) {
        out.push(`${city[i % city.length]}上班，${fWish[(i + round) % fWish.length]}`);
        out.push(`做${job[i]}的，${fTail[(i + round) % fTail.length]}`);
        out.push(`${job[i]}一枚，性格${fTrait[(i + round) % fTrait.length]}`);
      }
      return out;
    },
    function gen() {
      const out = [];
      const lines = [
        '朋友都说我脾气好', '家人催婚但不想将就', '谈过恋爱更懂自己要什么',
        '喜欢有分寸感的靠近', '聊天舒服比条件更重要', '愿意为了对的人慢慢了解',
        '不喜欢暧昧拖太久', '见面比网聊更能看出合不合', '希望对方也真诚专一',
        '可以接受慢热不能接受敷衍', '讨厌已读不回', '重视细节和态度',
      ];
      for (let i = 0; i < lines.length; i++) {
        out.push(lines[i]);
        out.push(`${lines[i]}，${fWish[(i + round) % fWish.length]}`);
      }
      return out;
    },
  ];
}

// —— 男生说说：网络常见个性签名 + 婚恋场景 ——
const maleMomentSeeds = `
上善若水，安之若素
人生海海，不过尔尔
快乐当下，不忧未来
肆意生长，无惧风浪
保持沉默，静观其变
眼里有光，清澈善良
不卑不亢，光芒万丈
悲喜自渡，他人难悟
真心很贵，谁也不给
跋山涉水，努力前行
接受普通，努力出众
因为喜欢，可迎万难
不慌不忙，心之所向
好好生活，慢慢相遇
顺遂无虞
别烂在回忆里，该大步往前走了
少说话，多喝水
看世界，听自己
少研究别人，多塑造自己
低头要有勇气，抬头要有底气
凡事要三思，更要三思而行
过去的事可以不记得，但一定要放下
路在自己脚下，没人能决定我的方向
成熟不是心变老，而是眼泪打转还能笑
以结婚为目的认真交往
性格稳重，喜欢户外运动
工作稳定，家庭观念传统
不抽烟少喝酒，生活习惯简单
周末健身或打球，也乐意在家做饭
慢热理工男，熟悉后很靠谱
想找个能一起规划未来的人
相信缘分，更相信用心经营
圈子不大，愿意把时间留给对的人
踏实过日子，浪漫主义也有
话不多但做事靠谱
不喜欢冷战，有问题愿意沟通
心怀慈悲，平静向暖
只说温暖，不谈悲伤
日子清闲，无欲无求
光明本身，偶尔凉薄
淡然一笑，简单随缘
偶尔可恶，永远可爱
及时行乐，及时止损
内心向阳，无谓悲伤
独立清醒，放荡不羁
人间散客，一切随缘
人生如酒，至死方休
人生苦短，倒满倒满
烟火人间，遗憾万千
人间百态，看清看轻
禁止矫情，洒脱随性
来时不惧风雨，去时何谓人言
不困于心，不乱于情
做喜欢的事，奔赴真实简单生活
希望拼命争取的最后都能如愿
`.trim().split('\n').map((l) => l.trim()).filter(Boolean);

const mHobby = ['篮球', '健身', '跑步', '骑行', '钓鱼', '摄影', '做饭', '自驾', '徒步', '看电影', '打游戏', '吉他', '喝茶', '露营'];
const mTrait = ['稳重', '内敛', '直爽', '慢热', '幽默', '踏实', '理性', '细腻', '佛系', '上进', '靠谱', '务实'];
const mWish = [
  '想认真找个对象', '希望遇见合适的人', '愿以结婚为目的', '想找个能聊得来的人',
  '更看重三观和性格', '不喜欢折腾式恋爱', '相信日久见人心', '愿意主动但不喜欢卑微',
  '想找个能一起过日子的人', '期待双向奔赴', '讨厌玩弄感情', '喜欢简单直接的关系',
];
const mShort = [
  '顺遂无虞', '保持真实', '慢慢来', '别赶路', '干饭人', '摸鱼选手', '行动派',
  '话少靠谱', '继续向前', '活在当下', '稳住', '简单生活', '认真做事',
];
const mTail = ['周末常运动', '也爱安静待着', '作息规律', '不泡吧不夜店', '愿意分担家务', '希望彼此坦诚'];

function maleMomentGenerators(round) {
  return [
    function gen() {
      const out = [];
      for (let i = 0; i < mHobby.length; i++) {
        out.push(`喜欢${mHobby[i]}，${mWish[(i + round) % mWish.length]}`);
        out.push(`${mHobby[i]}爱好者，${mTail[(i + round) % mTail.length]}`);
        out.push(`${mTrait[i % mTrait.length]}男生，${mWish[(i + 1 + round) % mWish.length]}`);
        out.push(`${mShort[i % mShort.length]}，${mWish[(i + 2 + round) % mWish.length]}`);
      }
      return out;
    },
    function gen() {
      const out = [];
      const city = ['日照', '青岛', '本地'];
      const job = ['工程师', '程序员', '销售', '教师', '医生', '设计', '运营', '财务', '公务员', '个体'];
      for (let i = 0; i < job.length; i++) {
        out.push(`${city[i % city.length]}工作，${mWish[(i + round) % mWish.length]}`);
        out.push(`做${job[i]}的，${mTail[(i + round) % mTail.length]}`);
        out.push(`${job[i]}一枚，性格${mTrait[(i + round) % mTrait.length]}`);
      }
      return out;
    },
    function gen() {
      const out = [];
      const lines = [
        '有车有房但不是炫耀型', '父母开明，尊重我的选择', '谈过恋爱更懂经营感情',
        '不喜欢忽冷忽热', '见面比网聊实在', '愿意为了对的人花时间',
        '讨厌吊着人不表态', '希望对方也能真诚', '可以接受慢热', '重视沟通和担当',
        '不喜欢冷战', '认定了就会认真',
      ];
      for (let i = 0; i < lines.length; i++) {
        out.push(lines[i]);
        out.push(`${lines[i]}，${mWish[(i + round) % mWish.length]}`);
      }
      return out;
    },
  ];
}

// —— 红娘展业签名：传统婚介语 + 现代展业短句 ——
const matchmakerMomentSeeds = `
真心实意帮您找个有缘人
真诚为您牵线搭桥，成就美好姻缘
欣当月老牵红线，乐作良媒搭鹊桥
千里姻缘一线牵，有情人终成眷属
您的幸福，我的祝福
你的佳偶，我来成就
牵线搭桥，缔造幸福
让有缘人相遇，相知，相伴
有缘千里来相会
一生成佳缘，一世归美眷
幸福婚恋，美好生活
情深似海，缘定终生
美满的纽带，幸福的桥梁
良缘一世同地久，佳偶百年共天长
爱在这里，有你才幸福
爱情很近，幸福不远
本地实名嘉宾，一对一精准匹配
专注严肃婚恋，只帮真心找对象的朋友
同城资源更新，欢迎私信了解
不推销不套路，合适再深入服务
见面前帮你把关，提高每次见面质量
深耕本地婚恋，熟悉各区单身圈子
会员牵手订婚结婚，是我最大的成就感
红娘相助美姻缘，月老牵成红丝绳
搭建鹊桥，缔造婚姻
可结交佳伴侣，莫错过好姻缘
这里，让爱纯净无暇
携手传达真爱，滋润你心中的他
线引真成助，情牵总是缘
让有缘人相遇相知相伴
牵起你的手，我们路过春夏秋冬
恋爱心已合，结婚情更浓
浪漫爱情，漫步人生
结成平等果，开出自由花
婚姻自主恩爱重，家庭和睦幸福多
互敬互爱好伴侣，同心同德好姻缘
红线摇摆情欲动，有缘来这好相逢
并肩同步长征路，齐心共谱幸福歌
百年恩爱双心结，千里姻缘一线牵
纯真的爱，就在这里
成一段姻缘，结百年好合
微缘牵手，本土婚恋倡导者
缘来挡不住，牵线就圆满
`.trim().split('\n').map((l) => l.trim()).filter(Boolean);

const mkService = [
  '一对一牵线', '实名嘉宾推荐', '同城精准匹配', '择偶方向梳理', '见面反馈跟进',
  '关系破冰辅导', '家长沟通协助', '本地资源对接', '严肃婚恋服务', '会员专属顾问',
];
const mkPromise = [
  '只服务真心找对象的朋友', '帮你少走弯路', '见面前先帮你把关', '合适再安排深入接触',
  '隐私保护到位', '不勉强不凑合', '全程有人跟进', '让每次见面更有价值',
  '把时间和缘分留给对的人', '欢迎私信聊聊你的标准',
];
const mkShort = [
  '您的幸福我的祝福', '你的佳偶我来成就', '有缘人来找我', '本地红娘在线',
  '真心实意做红娘', '专业牵线搭桥', '同城脱单顾问', '认真帮大家找对象',
  '不玩套路只讲真诚', '靠谱缘分在这里', '专注婚恋匹配', '愿有情人终成眷属',
];
const mkScene = ['本周新嘉宾已更新', '想脱单的可以私我', '先聊需求再推荐', '适合才安排见面', '本地土著更懂本地圈子'];

function matchmakerMomentGenerators(round) {
  return [
    function gen() {
      const out = [];
      for (let i = 0; i < mkService.length; i++) {
        out.push(`${mkService[i]}，${mkPromise[(i + round) % mkPromise.length]}`);
        out.push(`${mkShort[i % mkShort.length]}，${mkScene[(i + round) % mkScene.length]}`);
      }
      return out;
    },
    function gen() {
      const out = [];
      const area = ['日照', '青岛', '本地', '同城'];
      const focus = ['80后90后', '本科及以上', '稳定工作族', '认真婚恋族', '再婚人群', '大龄单身'];
      for (let i = 0; i < area.length; i++) {
        for (let j = 0; j < focus.length; j++) {
          out.push(`${area[i]}红娘，擅长${focus[j]}匹配`);
          out.push(`熟悉${area[i]}${focus[j]}嘉宾资源`);
        }
      }
      return out;
    },
    function gen() {
      const out = [];
      const lines = [
        '嘉宾资料真实可查', '牵线前先沟通双方意愿', '见面后及时收集反馈',
        '帮你避开无效社交', '减少相亲内耗', '让脱单更有章法',
        '重视三观也看相处', '不催婚但会推一把', '成功案例持续更新',
        '服务透明口碑说话', '欢迎老会员推荐朋友', '新嘉宾持续入库',
      ];
      for (let i = 0; i < lines.length; i++) {
        out.push(lines[i]);
        out.push(`${lines[i]}，${mkPromise[(i + round) % mkPromise.length]}`);
      }
      return out;
    },
  ];
}

const female = buildNickPool(femaleSeeds, femaleGenerator);
const male = buildNickPool(maleSeeds, maleGenerator);
const femaleMoments = buildMomentPool(femaleMomentSeeds, femaleMomentGenerators(), 500);
const maleMoments = buildMomentPool(maleMomentSeeds, maleMomentGenerators(), 500);
const matchmakerMoments = buildMomentPool(matchmakerMomentSeeds, matchmakerMomentGenerators(), 500);

function assertNoDupAndNoTrailingDigit(list, label) {
  const seen = new Set();
  for (const s of list) {
    if (/\d+$/.test(s)) throw new Error(`${label} 含末尾数字: ${s}`);
    if (seen.has(s)) throw new Error(`${label} 含重复: ${s}`);
    seen.add(s);
  }
}

assertNoDupAndNoTrailingDigit(female, 'female');
assertNoDupAndNoTrailingDigit(male, 'male');
assertNoDupAndNoTrailingDigit(femaleMoments, 'femaleMoments');
assertNoDupAndNoTrailingDigit(maleMoments, 'maleMoments');
assertNoDupAndNoTrailingDigit(matchmakerMoments, 'matchmakerMoments');

fs.mkdirSync(outDir, { recursive: true });
fs.writeFileSync(path.join(outDir, 'female-nicknames.txt'), female.join('\n') + '\n', 'utf8');
fs.writeFileSync(path.join(outDir, 'male-nicknames.txt'), male.join('\n') + '\n', 'utf8');
fs.writeFileSync(path.join(outDir, 'female-moments.txt'), femaleMoments.join('\n') + '\n', 'utf8');
fs.writeFileSync(path.join(outDir, 'male-moments.txt'), maleMoments.join('\n') + '\n', 'utf8');
fs.writeFileSync(path.join(outDir, 'matchmaker-moments.txt'), matchmakerMoments.join('\n') + '\n', 'utf8');

const oldMoments = path.join(outDir, 'moments.txt');
if (fs.existsSync(oldMoments)) fs.unlinkSync(oldMoments);

console.log('female', female.length, 'male', male.length);
console.log('femaleMoments', femaleMoments.length, 'maleMoments', maleMoments.length, 'matchmakerMoments', matchmakerMoments.length);
console.log('sample female:', femaleMoments.slice(0, 3).join(' | '));
console.log('sample male:', maleMoments.slice(0, 3).join(' | '));
console.log('sample matchmaker:', matchmakerMoments.slice(0, 3).join(' | '));
